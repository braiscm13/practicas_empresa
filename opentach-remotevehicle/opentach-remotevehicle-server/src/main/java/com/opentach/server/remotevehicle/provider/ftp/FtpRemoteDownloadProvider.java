package com.opentach.server.remotevehicle.provider.ftp;

import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService.RemoteDownloadPeriod;
import com.opentach.server.remotevehicle.provider.IRemoteVehicleProvider;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.spring.ILocatorReferencer;

public class FtpRemoteDownloadProvider extends AbstractDelegate implements IRemoteVehicleProvider {

	private static final Logger		logger	= LoggerFactory.getLogger(FtpRemoteDownloadProvider.class);

	private Object					providerId;
	private FtpRemoteDownloadWorker	downloadWorker;

	public FtpRemoteDownloadProvider() {
		super(null);
	}

	@Override
	public void init(ApplicationContext context, Properties prop, Object providerId) {
		this.setLocator(context.getBean(ILocatorReferencer.class).getLocator());
		this.providerId = providerId;
		this.downloadWorker = new FtpRemoteDownloadWorker(context, providerId);
	}

	public Object getProviderId() {
		return this.providerId;
	}

	@Override
	public void createCompany(String companyId) throws RemoteVehicleException {
		FtpRemoteDownloadProvider.logger.trace("No need to setup ftp company");
	}

	@Override
	public void configureVehicle(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String vehicleId, String vehicleUnitSn, String vehicleType)
			throws RemoteVehicleException {
		FtpRemoteDownloadProvider.logger.trace("No need to setup ftp vehicle");
	}

	@Override
	public void configureDriver(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String driverId) throws RemoteVehicleException {
		FtpRemoteDownloadProvider.logger.trace("No need to setup ftp driver");
	}

	@Override
	public void requestForceDriverDownload(String companyId, String driverId) throws RemoteVehicleException {
		throw new RemoteVehicleException("E_FTP_INTEGRATION_NOT_SUPPORT_REQUEST");
	}

	@Override
	public void requestForceVehicleDownload(String companyId, String plateNumber, Date from, Date to) throws RemoteVehicleException {
		throw new RemoteVehicleException("E_FTP_INTEGRATION_NOT_SUPPORT_REQUEST");
	}

}
