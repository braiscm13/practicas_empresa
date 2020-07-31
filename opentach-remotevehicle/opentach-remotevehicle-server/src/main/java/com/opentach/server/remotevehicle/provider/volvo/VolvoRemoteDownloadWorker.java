package com.opentach.server.remotevehicle.provider.volvo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ibm.icu.util.Calendar;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ThreadTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.util.OpentachCheckingTools;
import com.opentach.server.remotevehicle.dao.RemoteCompanySetupDao;
import com.opentach.server.remotevehicle.provider.common.RemoteVehicleErrorCodes;
import com.opentach.server.remotevehicle.provider.common.event.IEventRegister;
import com.opentach.server.remotevehicle.provider.volvo.client.api.VolvoApi;
import com.opentach.server.remotevehicle.provider.volvo.client.model.DriverCardFilesObject;
import com.opentach.server.remotevehicle.provider.volvo.client.model.TachoFileResponseObject;
import com.opentach.server.remotevehicle.provider.volvo.client.model.TachoFilesObject;
import com.opentach.server.remotevehicle.uploader.IUploader;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.spring.ILocatorReferencer;

public class VolvoRemoteDownloadWorker extends AbstractDelegate implements Runnable {

	private static final Logger			logger					= LoggerFactory.getLogger(VolvoRemoteDownloadWorker.class);
	private static final String			PROP_LAST_SYNC			= "last_sync";
	private static final String			PROP_API_PASSWORD		= "api_password";
	private static final String			PROP_API_USER			= "api_user";
	private static final Date			DEFAULT_START_DATE		= DateTools.createDate(2018, 1, 1);
	private static final long			SYNC_PERIOD				= 3l * 60l * 1000l;											// 3 minutos
	private static final String			SYNC_DATE_FORMAT		= "yyyy-MM-dd_HH:mm:ss";
	private static final long			MAX_MINUTES_PER_COMPANY	= 10;

	private final Thread				execThread;
	private final ApplicationContext	context;
	private final Object				providerId;
	private final VolvoApi				volvoApi;
	private final IUploader				uploader;
	private final IEventRegister		eventRegister;

	public VolvoRemoteDownloadWorker(ApplicationContext context, VolvoApi volvoApi, Object providerId) {
		super(context.getBean(ILocatorReferencer.class).getLocator());
		this.providerId = providerId;
		this.context = context;
		this.volvoApi = volvoApi;
		this.uploader = this.context.getBean(IUploader.class);
		this.eventRegister = this.context.getBean(IEventRegister.class);
		this.execThread = new Thread(this, "Volvo download Synch");
		this.execThread.setDaemon(true);
		this.execThread.start();
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();
			try {
				this.doSynch();
				this.waitNextExecution(startTime, System.currentTimeMillis());
			} catch (Exception error) {
				VolvoRemoteDownloadWorker.logger.error(null, error);
				this.waitNextExecution(startTime, System.currentTimeMillis());
			}
		}
	}

	private void doSynch() throws Exception {
		// 1.- consultamos las empresas con sincronizacion volvo
		List<CompanyVolvoInfo> companies = this.queryCompaniesToSync();
		VolvoRemoteDownloadWorker.logger.info("Retrieved {} companies to Volvo sync", companies.size());
		// 2.- iteramos por cada una e ellas
		ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("VOLVO_SYNC-%d").build());
		try {
			for (CompanyVolvoInfo info : companies) {
				try {
					SimpleTimeLimiter.create(executor).runWithTimeout(() -> this.processCompany(info), Duration.ofMinutes(VolvoRemoteDownloadWorker.MAX_MINUTES_PER_COMPANY));
				} catch (Exception err) {
					VolvoRemoteDownloadWorker.logger.error("Error synching company {}", info.cif, err);
					this.eventRegister.saveEvent(this.providerId, info.cif, null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR,
							String.format("Error synching company %s", info.cif), err);
				}
			}
		} finally {
			executor.shutdown();
		}
	}

	private void processCompany(CompanyVolvoInfo info) {
		try {
			VolvoRemoteDownloadWorker.logger.info("Updating company {} ", info.cif);
			TachoFileResponseObject response = this.volvoApi.tachofilesGet(info.user, info.pass, info.lastSynch, System.currentTimeMillis() + "", null, null, null, null, "ES",
					Boolean.TRUE);
			if ((response == null) || (response.getTachoFilesResponse() == null)) {
				VolvoRemoteDownloadWorker.logger.warn("Empty response from volvo api for company {}", info.cif);
				return;
			}
			Date maxDate = info.lastSynch;
			maxDate = this.processDriverFiles(info, response, maxDate);
			maxDate = this.processVehicleFiles(info, response, maxDate);
			// Actualizamos la fecha
			this.updateCompanySyncDate(info, maxDate);
			VolvoRemoteDownloadWorker.logger.info("Volvo Company {} synched ", info.cif);
		} catch (Exception err) {
			VolvoRemoteDownloadWorker.logger.error("Error synching company {}", info.cif, err);
			this.eventRegister.saveEvent(this.providerId, info.cif, null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR, String.format("Error synching company %s", info.cif),
					err);
		}
	}

	private Date processVehicleFiles(CompanyVolvoInfo info, TachoFileResponseObject response, Date maxDate) {
		List<TachoFilesObject> vehicleFiles = response.getTachoFilesResponse().getTachoFiles();
		if (vehicleFiles == null) {
			return maxDate;
		}
		VolvoRemoteDownloadWorker.logger.debug("Found {} vehicle files for company {}", vehicleFiles.size(), info.cif);
		for (TachoFilesObject vehicleFile : vehicleFiles) {
			try {
				byte[] fileContent = vehicleFile.getFileContent();
				TachoFileUploadRequest parameters = new TachoFileUploadRequest();
				parameters.setCif(info.cif);
				parameters.setAnalyze(false);
				parameters.setSourceType(UploadSourceType.REMOTA_VOLVO.toString());
				VolvoRemoteDownloadWorker.logger.debug("Sending card file {} for company {}", vehicleFile.getReceivedDateTime(), info.cif);
				this.uploader.uploadFile(fileContent, vehicleFile.getFilename(), parameters);
				VolvoRemoteDownloadWorker.logger.debug("File {} uploaded for company {}", vehicleFile.getReceivedDateTime(), info.cif);
				maxDate = maxDate.after(vehicleFile.getReceivedDateTime()) ? maxDate : vehicleFile.getReceivedDateTime();
				this.eventRegister.saveEvent(this.providerId, info.cif, vehicleFile.getVin(), new Date(), RemoteVehicleErrorCodes.OK,
						String.format("File %s uploaded for company %s", vehicleFile.getFilename(), info.cif));
			} catch (Exception err) {
				VolvoRemoteDownloadWorker.logger.error("Error uploading vehicle file {} of company {}", vehicleFile.getFilename(), info.cif, err);
			}
		}
		return maxDate;
	}

	private Date processDriverFiles(CompanyVolvoInfo info, TachoFileResponseObject response, Date maxDate) {
		List<DriverCardFilesObject> driverCardFiles = response.getTachoFilesResponse().getDriverCardFiles();
		if (driverCardFiles == null) {
			return maxDate;
		}
		VolvoRemoteDownloadWorker.logger.debug("Found {} card files for company {}", driverCardFiles.size(), info.cif);
		for (DriverCardFilesObject driverCardFile : driverCardFiles) {
			try {
				byte[] fileContent = driverCardFile.getFileContent();
				TachoFileUploadRequest parameters = new TachoFileUploadRequest();
				parameters.setCif(info.cif);
				parameters.setAnalyze(false);
				parameters.setSourceType(UploadSourceType.REMOTA_VOLVO.toString());
				VolvoRemoteDownloadWorker.logger.debug("Sending card file {} for company {}", driverCardFile.getReceivedDateTime(), info.cif);
				this.uploader.uploadFile(fileContent, driverCardFile.getFilename(), parameters);
				VolvoRemoteDownloadWorker.logger.debug("File {} uploaded for company {}", driverCardFile.getFilename(), info.cif);
				maxDate = maxDate.after(driverCardFile.getReceivedDateTime()) ? maxDate : driverCardFile.getReceivedDateTime();
				this.eventRegister.saveEvent(this.providerId, info.cif, driverCardFile.getDriverId().getTachoDriverIdentification().getDriverIdentification(), new Date(),
						RemoteVehicleErrorCodes.OK,
						String.format("File %s uploaded for company %s", driverCardFile.getFilename(), info.cif));
			} catch (Exception err) {
				VolvoRemoteDownloadWorker.logger.error("Error uploading file {} of company {}", driverCardFile.getFilename(), info.cif, err);
				this.eventRegister.saveEvent(this.providerId, info.cif, null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR,
						String.format("Error uploading driver file %s of company %s", driverCardFile.getFilename(), info.cif), err);
			}
		}
		return maxDate;
	}

	private void updateCompanySyncDate(CompanyVolvoInfo info, Date maxDate) throws RemoteVehicleException {
		try {
			Properties prop = new Properties();
			prop.setProperty(VolvoRemoteDownloadWorker.PROP_API_USER, info.user);
			prop.setProperty(VolvoRemoteDownloadWorker.PROP_API_PASSWORD, info.pass);
			prop.setProperty(VolvoRemoteDownloadWorker.PROP_LAST_SYNC, this.formatSyncDate(maxDate));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			prop.store(baos, null);
			this.context.getBean(RemoteCompanySetupDao.class).update(EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_EXTRA_INFO, new String(baos.toByteArray(), "UTF-8")),
					EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_ID, info.ccfId));
		} catch (IOException err) {
			throw new RemoteVehicleException("Error updating sync date", err);
		}
	}

	private List<CompanyVolvoInfo> queryCompaniesToSync() throws RemoteVehicleException {
		List<CompanyVolvoInfo> res = new ArrayList<>();
		RemoteCompanySetupDao dao = this.context.getBean(RemoteCompanySetupDao.class);
		EntityResult er = dao.query(EntityResultTools.keysvalues(RemoteVehicleNaming.RDP_ID, this.providerId, RemoteVehicleNaming.CCF_ACTIVE,
				"S"),
				Arrays.asList(CompanyNaming.CIF, RemoteVehicleNaming.CCF_EXTRA_INFO, RemoteVehicleNaming.CCF_ID), null, "default");
		OpentachCheckingTools.checkValidEntityResult(er, RemoteVehicleException.class);
		int nrecords = er.calculateRecordNumber();
		for (int i = 0; i < nrecords; i++) {
			String cif = (String) ((List<?>) er.get(CompanyNaming.CIF)).get(i);
			Object ccfId = ((List<?>) er.get(RemoteVehicleNaming.CCF_ID)).get(i);
			try {
				String properties = (String) ((List<?>) er.get(RemoteVehicleNaming.CCF_EXTRA_INFO)).get(i);
				Properties prop = new Properties();
				prop.load(new ByteArrayInputStream(properties.getBytes()));
				Date parseSyncDate = this.parseSyncDate(prop.getProperty(VolvoRemoteDownloadWorker.PROP_LAST_SYNC));
				parseSyncDate = DateTools.add(parseSyncDate, Calendar.SECOND, 1);
				res.add(new CompanyVolvoInfo(cif, prop.getProperty(VolvoRemoteDownloadWorker.PROP_API_USER), prop.getProperty(VolvoRemoteDownloadWorker.PROP_API_PASSWORD),
						parseSyncDate, ccfId));
			} catch (Exception err) {
				VolvoRemoteDownloadWorker.logger.error("Error loading company settings for company {}", cif, err);
			}
		}
		return res;
	}

	private Date parseSyncDate(String str) throws RemoteVehicleException {
		if ((str == null) || str.isEmpty()) {
			return VolvoRemoteDownloadWorker.DEFAULT_START_DATE;
		}
		try {
			return new SimpleDateFormat(VolvoRemoteDownloadWorker.SYNC_DATE_FORMAT).parse(str);
		} catch (ParseException ex) {
			throw new RemoteVehicleException("Error processing sync date format", ex);
		}
	}

	private String formatSyncDate(Date maxDate) {
		return new SimpleDateFormat(VolvoRemoteDownloadWorker.SYNC_DATE_FORMAT).format(maxDate);
	}

	private void waitNextExecution(long startTime, long endTime) {
		if ((endTime - startTime) < VolvoRemoteDownloadWorker.SYNC_PERIOD) {
			VolvoRemoteDownloadWorker.logger.debug("waiting for {} ms", VolvoRemoteDownloadWorker.SYNC_PERIOD - (endTime - startTime));
			ThreadTools.sleep(VolvoRemoteDownloadWorker.SYNC_PERIOD - (endTime - startTime));
		}
		VolvoRemoteDownloadWorker.logger.info("no need to wait");
	}

	private class CompanyVolvoInfo {
		String	cif;
		String	user;
		String	pass;
		Date	lastSynch;
		Object	ccfId;

		public CompanyVolvoInfo(String cif, String user, String pass, Date lastSynch, Object ccfId) {
			super();
			this.cif = cif;
			this.user = user;
			this.pass = pass;
			this.lastSynch = lastSynch;
			this.ccfId = ccfId;
		}

	}

}
