package com.opentach.common.remotevehicle.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;

public interface IRemoteVehicleService {
	enum RemoteDownloadPeriod {
		DAILY, WEEKLY, BIWEEKLY, MONTHLY
	}

	static final long MAX_DAYS = 4;

	EntityResult createCompany(Map<String, Object> parameters, String companyId) throws RemoteVehicleException;

	EntityResult updateCompany(Map<String, Object> parameters, Object registryId) throws RemoteVehicleException;

	EntityResult configureVehicle(boolean active, Object providerId, RemoteDownloadPeriod period, Long hour, String companyId, String vehicleId, String unitId)
			throws RemoteVehicleException;

	EntityResult configureDriver(boolean active, Object providerId, RemoteDownloadPeriod period, Long hour, String companyId, String driverId) throws RemoteVehicleException;

	void requestForceDriverDownload(String companyId, List<String> driverIds) throws RemoteVehicleException;

	void requestForceVehicleDownload(String companyId, List<String> vehiclesIds, Date from, Date to) throws RemoteVehicleException;

	boolean isRemoteDownloadActive() throws RemoteVehicleException;

	EntityResult queryVehicleLocations(String cif, String plate, Date from, Date to) throws RemoteVehicleException;
}
