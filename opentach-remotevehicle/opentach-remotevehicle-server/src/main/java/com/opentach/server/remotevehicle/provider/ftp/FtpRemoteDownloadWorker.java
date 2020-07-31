package com.opentach.server.remotevehicle.provider.ftp;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.ThreadTools;
import com.opentach.client.util.directorywatcher.ftp.FtpEngine;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.ftp.IFtpEngineListener;
import com.opentach.client.util.directorywatcher.ftp.IFtpFile;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.util.OpentachCheckingTools;
import com.opentach.server.remotevehicle.dao.RemoteCompanySetupDao;
import com.opentach.server.remotevehicle.provider.common.RemoteVehicleErrorCodes;
import com.opentach.server.remotevehicle.provider.common.event.IEventRegister;
import com.opentach.server.remotevehicle.uploader.IUploader;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.spring.ILocatorReferencer;

public class FtpRemoteDownloadWorker extends AbstractDelegate implements Runnable {

	private static final Logger			logger					= LoggerFactory.getLogger(FtpRemoteDownloadWorker.class);
	private static final long			SYNC_PERIOD				= 3l * 60l * 1000l;											// 3 minutos
	private static final long			MAX_MINUTES_PER_COMPANY	= 10;

	private final Thread				execThread;
	private final ApplicationContext	context;
	private final Object				providerId;
	private final FtpEngine				ftpEngine;
	private final IUploader				uploader;
	private final IEventRegister		eventRegister;

	public FtpRemoteDownloadWorker(ApplicationContext context, Object providerId) {
		super(context.getBean(ILocatorReferencer.class).getLocator());
		this.providerId = providerId;
		this.context = context;
		this.uploader = this.context.getBean(IUploader.class);
		this.eventRegister = this.context.getBean(IEventRegister.class);
		this.ftpEngine = new FtpEngine(new IFtpEngineListener() {

			@Override
			public void onFileToUpload(Path path, IFtpFile ftpFile, FtpWatcherSettings info) {
				FtpRemoteDownloadWorker.this.onFileToUpload(path, ftpFile, info);
			}

			@Override
			public void onError(FtpWatcherSettings info, IFtpFile ftpFile, Exception ex) {
				FtpRemoteDownloadWorker.this.eventRegister.saveEvent(providerId, info.getCompanyId(), null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR,
						String.format("Error synching company %s file {}", info.getCompanyId(), ftpFile == null ? "none" : ftpFile.getName()), ex);
			}
		});
		this.execThread = new Thread(this, "FTP download Synch");
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
				FtpRemoteDownloadWorker.logger.error(null, error);
				this.waitNextExecution(startTime, System.currentTimeMillis());
			}
		}
	}

	private void doSynch() throws Exception {
		// 1.- consultamos las empresas con sincronizacion ftp
		List<FtpWatcherSettings> companies = this.queryCompaniesToSync();
		FtpRemoteDownloadWorker.logger.info("Retrieved {} companies to Ftp sync", companies.size());
		// 2.- iteramos por cada una e ellas
		ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("FTP_SYNC-%d").build());
		try {
			for (FtpWatcherSettings info : companies) {
				try {
					SimpleTimeLimiter.create(executor).runWithTimeout(() -> this.processCompany(info), Duration.ofMinutes(FtpRemoteDownloadWorker.MAX_MINUTES_PER_COMPANY));
				} catch (Exception err) {
					FtpRemoteDownloadWorker.logger.error("Error synching company {}", info.getCompanyId(), err);
					this.eventRegister.saveEvent(this.providerId, info.getCompanyId(), null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR,
							String.format("Error synching company %s", info.getCompanyId()), err);
				}
			}
		} finally {
			executor.shutdown();
		}
	}

	private void processCompany(FtpWatcherSettings info) {
		try {
			FtpRemoteDownloadWorker.logger.info("Updating company {} ", info.getCompanyId());
			this.ftpEngine.checkFtp(info);
			FtpRemoteDownloadWorker.logger.info("FTP Company {} synched ", info.getCompanyId());
		} catch (Exception err) {
			FtpRemoteDownloadWorker.logger.error("Error synching company {}", info.getCompanyId(), err);
			this.eventRegister.saveEvent(this.providerId, info.getCompanyId(), null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR,
					String.format("Error synching company %s", info.getCompanyId()), err);
		}
	}

	private void onFileToUpload(Path file, IFtpFile ftpFile, FtpWatcherSettings info) {
		try {
			FtpRemoteDownloadWorker.logger.debug("Found FTP file {} for company {}", ftpFile.getName(), info.getCompanyId());
			byte[] bytesFromFile = FileTools.getBytesFromFile(file.toFile());
			TachoFileUploadRequest parameters = new TachoFileUploadRequest();
			parameters.setCif(info.getCompanyId());
			parameters.setAnalyze(false);
			parameters.setSourceType(UploadSourceType.FTP_SERVER.toString());
			FtpRemoteDownloadWorker.logger.debug("Sending card file {} for company {}", ftpFile.getName(), info.getCompanyId());
			this.uploader.uploadFile(bytesFromFile, ftpFile.getName(), parameters);
			FtpRemoteDownloadWorker.logger.debug("File {} uploaded for company {}", ftpFile.getName(), info.getCompanyId());
			this.eventRegister.saveEvent(this.providerId, info.getCompanyId(), null, new Date(), RemoteVehicleErrorCodes.OK,
					String.format("File %s uploaded for company %s", ftpFile.getName(), info.getCompanyId()));
			this.ftpEngine.backupFile(info, ftpFile);
		} catch (Exception err) {
			FtpRemoteDownloadWorker.logger.error("Error uploading file {} of company {}", ftpFile.getName(), info.getCompanyId(), err);
			this.eventRegister.saveEvent(this.providerId, ftpFile.getName(), null, new Date(), RemoteVehicleErrorCodes.UNKNOW_ERROR,
					String.format("Error uploading ftp file %s of company %s", ftpFile.getName(), info.getCompanyId()), err);
		}
	}

	private List<FtpWatcherSettings> queryCompaniesToSync() throws RemoteVehicleException {
		List<FtpWatcherSettings> res = new ArrayList<>();

		RemoteCompanySetupDao dao = this.context.getBean(RemoteCompanySetupDao.class);
		EntityResult er = dao.query(EntityResultTools.keysvalues(RemoteVehicleNaming.EMP_FTPSYNC, "S"), Arrays
				.asList(//
						RemoteVehicleNaming.EMP_FTP_URL, //
						RemoteVehicleNaming.EMP_FTP_USR, //
						RemoteVehicleNaming.EMP_FTP_PASS, //
						RemoteVehicleNaming.EMP_FTP_PATH, //
						RemoteVehicleNaming.EMP_FTP_CONNECTION_TYPE, //
						OpentachFieldNames.CIF_FIELD //
						), null, "ftp");
		OpentachCheckingTools.checkValidEntityResult(er, RemoteVehicleException.class);
		int nrecords = er.calculateRecordNumber();
		for (int i = 0; i < nrecords; i++) {
			Map<?, ?> recordValues = er.getRecordValues(i);
			res.add(new FtpWatcherSettings( //
					(String) recordValues.get(RemoteVehicleNaming.EMP_FTP_URL), //
					(String) recordValues.get(RemoteVehicleNaming.EMP_FTP_PASS), //
					(String) recordValues.get(RemoteVehicleNaming.EMP_FTP_USR), //
					(String) recordValues.get(RemoteVehicleNaming.EMP_FTP_PATH), //
					(String) recordValues.get(OpentachFieldNames.CIF_FIELD), //
					FtpWatcherSettings.parseFtpConnectionType((String) recordValues.get(RemoteVehicleNaming.EMP_FTP_CONNECTION_TYPE))//
					));
		}
		return res;
	}

	private void waitNextExecution(long startTime, long endTime) {
		if ((endTime - startTime) < FtpRemoteDownloadWorker.SYNC_PERIOD) {
			FtpRemoteDownloadWorker.logger.debug("waiting for {} ms", FtpRemoteDownloadWorker.SYNC_PERIOD - (endTime - startTime));
			ThreadTools.sleep(FtpRemoteDownloadWorker.SYNC_PERIOD - (endTime - startTime));
		}
		FtpRemoteDownloadWorker.logger.info("no need to wait");
	}

}
