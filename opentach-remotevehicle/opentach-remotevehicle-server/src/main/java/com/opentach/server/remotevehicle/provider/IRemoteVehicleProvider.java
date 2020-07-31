package com.opentach.server.remotevehicle.provider;

import java.util.Date;
import java.util.Properties;

import org.springframework.context.ApplicationContext;

import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.remotevehicle.provider.jaltest.JaltestException;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService.RemoteDownloadPeriod;

/**
 * The Interface IRemoteVehicleProvider.
 */
public interface IRemoteVehicleProvider {

	/**
	 * Inits the.
	 *
	 * @param locator
	 *            the locator
	 * @param prop
	 *            the prop
	 * @param providerId
	 *            the provider id
	 */
	void init(ApplicationContext context, Properties prop, Object providerId);

	/**
	 * Configure vehicle.
	 *
	 * @param active
	 *            the active
	 * @param period
	 *            the period
	 * @param hour
	 *            the hour
	 * @param companyId
	 *            the company id
	 * @param vehicleIds
	 *            the vehicle ids
	 * @param vehicleUnitSn
	 *            the vehicle unit sn
	 * @param con
	 *            the con
	 * @throws JaltestException
	 *             the jaltest exception
	 */
	void configureVehicle(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String vehicleIds, String vehicleUnitSn, String vehicleType)
			throws RemoteVehicleException;

	/**
	 * Configure driver.
	 *
	 * @param active
	 *            the active
	 * @param period
	 *            the period
	 * @param hour
	 *            the hour
	 * @param companyId
	 *            the company id
	 * @param driverId
	 *            the driver id
	 * @param con
	 *            the con
	 * @throws Exception
	 *             the exception
	 */
	void configureDriver(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String driverId) throws RemoteVehicleException;

	/**
	 * Request force driver download.
	 *
	 * @param companyId
	 *            the company id
	 * @param driverId
	 *            the driver id
	 * @param con
	 *            the con
	 * @throws Exception
	 *             the exception
	 */
	void requestForceDriverDownload(String companyId, String driverId) throws RemoteVehicleException;

	/**
	 * Request force vehicle download.
	 *
	 * @param companyId
	 *            the company id
	 * @param plateNumber
	 *            the plate number
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param con
	 *            the con
	 * @throws Exception
	 *             the exception
	 */
	void requestForceVehicleDownload(String companyId, String plateNumber, Date from, Date to) throws RemoteVehicleException;

	/**
	 * Creates the company.
	 *
	 * @param parameters
	 *            the parameters
	 * @param companyId
	 *            the company id
	 * @param con
	 *            the con
	 * @throws Exception
	 *             the exception
	 */
	void createCompany(String companyId) throws RemoteVehicleException;

}
