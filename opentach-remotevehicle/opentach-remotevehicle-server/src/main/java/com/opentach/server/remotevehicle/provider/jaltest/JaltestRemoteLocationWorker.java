package com.opentach.server.remotevehicle.provider.jaltest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ThreadTools;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.remotevehicle.dao.RemoteDownloadVehicleConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteLocationDao;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.GPSPositionInfo;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.spring.ILocatorReferencer;
import com.utilmize.tools.exception.UException;

public class JaltestRemoteLocationWorker extends AbstractDelegate implements Runnable {

	private static final Logger					logger				= LoggerFactory.getLogger(JaltestRemoteLocationWorker.class);
	private final Date							DEFAULT_START_DATE	= DateTools.createDate(2018, 1, 1);
	private final long							SYNC_PERIOD			= 3l * 60l * 1000l;												// 3 minutos

	/** The exec thread. */
	private final Thread						execThread;
	private final JaltestRemoteDownloadProvider	provider;
	private final ApplicationContext			context;

	/**
	 * Instantiates a new ER synchronizer.
	 *
	 * @param locator
	 *            the locator
	 */
	public JaltestRemoteLocationWorker(JaltestRemoteDownloadProvider provider, ApplicationContext context) {
		super(context.getBean(ILocatorReferencer.class).getLocator());
		this.context = context;
		this.provider = provider;
		this.execThread = new Thread(this, "Jaltest location Synch");
		this.execThread.setDaemon(true);
		this.execThread.start();
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();
			try {
				if (this.hastToOperate()) {
					this.doSynch();
				}
				this.waitNextExecution(startTime, System.currentTimeMillis());
			} catch (Exception error) {
				JaltestRemoteLocationWorker.logger.error(null, error);
				this.waitNextExecution(startTime, System.currentTimeMillis());
			}
		}
	}

	private boolean hastToOperate() throws UException {
		EPreferenciasServidor prefServ = (EPreferenciasServidor) this.getEntity("EPreferenciasServidor");
		String value = prefServ.getValue("Jaltest.LocationSync.enabled", this.getEntityPrivilegedId(prefServ));
		return "S".equals(value);
	}

	private void doSynch() throws Exception {
		// consultamos vehiculos activos de jaltest
		List<VehicleLocationInfo> activePlates = this.queryPlatesToSync();
		JaltestRemoteLocationWorker.logger.info("Retrieved {} entries to query", activePlates.size());
		// TODO posiblemente haga falta un threadppoolexecutor para paralelizar esto
		for (VehicleLocationInfo info : activePlates) {
			try {
				JaltestRemoteLocationWorker.logger.info("Updating vehicle {} of company {}", info.vehId, info.comId);
				Pair<List<GPSPositionInfo>, Date> resLocation = this.queryLocations(info);
				List<GPSPositionInfo> queryLocations = resLocation.getFirst();
				Date endDate = resLocation.getSecond();
				this.saveLocations(info, queryLocations, endDate);
				JaltestRemoteLocationWorker.logger.info("locations saved");
			} catch (Exception err) {
				JaltestRemoteLocationWorker.logger.error(null, err);
			}
		}

	}

	private void saveLocations(VehicleLocationInfo vehicleInfo, List<GPSPositionInfo> queryLocations, Date endDate) {
		GPSPositionInfo mostRecent = null;
		RemoteLocationDao locationDao = this.context.getBean(RemoteLocationDao.class);
		for (GPSPositionInfo gpsInfo : queryLocations) {
			if ((mostRecent == null) || mostRecent.getDate().toGregorianCalendar().getTime().before(gpsInfo.getDate().toGregorianCalendar().getTime())) {
				mostRecent = gpsInfo;
			}
			if ((Math.abs(gpsInfo.getLatitude()) > 90) || (Math.abs(gpsInfo.getLongitude()) > 180)) {
				JaltestRemoteLocationWorker.logger.info("Skipping erroneus location {} - {}", gpsInfo.getLatitude(), gpsInfo.getLongitude());
			} else {
				// guardar en locations
				locationDao.insert(EntityResultTools.keysvalues( //
						RemoteVehicleNaming.COM_ID, vehicleInfo.comId, //
						RemoteVehicleNaming.VEH_ID, vehicleInfo.vehId, //
						RemoteVehicleNaming.RDP_ID, this.provider.getProviderId(), //
						RemoteVehicleNaming.LOC_ALTITUDE, gpsInfo.getAltitude(), //
						RemoteVehicleNaming.LOC_DATE, gpsInfo.getDate().toGregorianCalendar().getTime(), //
						RemoteVehicleNaming.LOC_ECUSN, gpsInfo.getECUSerialNumber(), //
						RemoteVehicleNaming.LOC_KL15STATE, gpsInfo.getKL15State(), //
						RemoteVehicleNaming.LOC_LATITUDE, gpsInfo.getLatitude(), //
						RemoteVehicleNaming.LOC_LONGITUDE, gpsInfo.getLongitude(), //
						RemoteVehicleNaming.LOC_TRPID, gpsInfo.getTripId()//
						));
			}
		}
		// actualizar rdw_veh_cfg
		RemoteDownloadVehicleConfigDao dao = this.context.getBean(RemoteDownloadVehicleConfigDao.class);
		Hashtable<Object, Object> attributes = EntityResultTools.keysvalues(//
				RemoteVehicleNaming.RDV_LAST_LATITUDE, (mostRecent == null) ? null : mostRecent.getLatitude(), //
						RemoteVehicleNaming.RDV_LAST_LONGITUDE, (mostRecent == null) ? null : mostRecent.getLongitude(), //
								RemoteVehicleNaming.RDV_LAST_LOCATION_DATE, (mostRecent == null) ? null : mostRecent.getDate().toGregorianCalendar()
										.getTime(), //
										RemoteVehicleNaming.RDV_LAST_LOCATION_SYNC, endDate //
				);
		dao.update(attributes, EntityResultTools.keysvalues(RemoteVehicleNaming.RDV_ID, vehicleInfo.rdvId));

	}

	private Pair<List<GPSPositionInfo>, Date> queryLocations(VehicleLocationInfo info) throws RemoteVehicleException {
		// maximo para pedir es 15 dias
		Date startDate = info.lastSynch == null ? this.DEFAULT_START_DATE : info.lastSynch;
		Date endDate = new Date();
		if (DateTools.countDaysBetween(startDate, endDate) > 5) {
			endDate = DateTools.add(startDate, Calendar.DAY_OF_MONTH, 5);
		}
		if (endDate.after(new Date())) {
			endDate = new Date();
		}
		List<GPSPositionInfo> vehicleLocations = this.provider.getInvoker().getVehicleLocations(info.comId, info.vehId /* + "_TEST" */, startDate, endDate);
		vehicleLocations = vehicleLocations == null ? new ArrayList<>() : vehicleLocations;
		JaltestRemoteLocationWorker.logger.info("found {} locations between {} and {}", vehicleLocations.size(), startDate, endDate);
		return new Pair<>(vehicleLocations, endDate);
	}

	private List<VehicleLocationInfo> queryPlatesToSync() throws Exception {
		List<VehicleLocationInfo> res = new ArrayList<>();
		RemoteDownloadVehicleConfigDao dao = this.context.getBean(RemoteDownloadVehicleConfigDao.class);
		EntityResult er = dao.query(EntityResultTools.keysvalues(RemoteVehicleNaming.RDP_ID, this.provider.getProviderId(), RemoteVehicleNaming.RDW_ACTIVE, "S"),
				EntityResultTools.attributes(RemoteVehicleNaming.RDV_ID, RemoteVehicleNaming.VEH_ID, RemoteVehicleNaming.COM_ID, RemoteVehicleNaming.RDV_LAST_LOCATION_SYNC), null,
				"default");
		CheckingTools.checkValidEntityResult(er);
		int nrecords = er.calculateRecordNumber();
		for (int i = 0; i < nrecords; i++) {
			res.add(new VehicleLocationInfo( //
					((List<?>) er.get(RemoteVehicleNaming.RDV_ID)).get(i), //
					(String) ((List<?>) er.get(RemoteVehicleNaming.COM_ID)).get(i), //
					(String) ((List<?>) er.get(RemoteVehicleNaming.VEH_ID)).get(i), //
					(Date) ((List<?>) er.get(RemoteVehicleNaming.RDV_LAST_LOCATION_SYNC)).get(i)));
		}
		return res;
	}

	private void waitNextExecution(long startTime, long endTime) {
		if ((endTime - startTime) < this.SYNC_PERIOD) {
			JaltestRemoteLocationWorker.logger.debug("waiting for {} ms", this.SYNC_PERIOD - (endTime - startTime));
			ThreadTools.sleep(this.SYNC_PERIOD - (endTime - startTime));
		}
		JaltestRemoteLocationWorker.logger.info("no need to wait");
	}

	private class VehicleLocationInfo {
		Object	rdvId;
		String	comId;
		String	vehId;
		Date	lastSynch;

		public VehicleLocationInfo(Object rdvId, String comId, String vehId, Date lastSynch) {
			super();
			this.rdvId = rdvId;
			this.comId = comId;
			this.vehId = vehId;
			this.lastSynch = lastSynch;
		}

	}

}
