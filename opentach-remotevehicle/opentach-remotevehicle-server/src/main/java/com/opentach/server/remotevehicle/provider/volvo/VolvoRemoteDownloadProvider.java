package com.opentach.server.remotevehicle.provider.volvo;

import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService.RemoteDownloadPeriod;
import com.opentach.server.remotevehicle.provider.IRemoteVehicleProvider;
import com.opentach.server.remotevehicle.provider.volvo.client.api.VolvoApi;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.spring.ILocatorReferencer;

public class VolvoRemoteDownloadProvider extends AbstractDelegate implements IRemoteVehicleProvider {
	private static final String			SETTING_REMOTE_VEHICLE_VOLVO_API_URI	= "api_uri";
	private static final String			DEFAULT_REMOTE_VEHICLE_VOLVO_API_URI	= "https://api.volvotrucks.com/tacho";

	private static final Logger			logger									= LoggerFactory.getLogger(VolvoRemoteDownloadProvider.class);

	private VolvoApi					volvoApi;
	private Object						providerId;
	private VolvoRemoteDownloadWorker	downloadWorker;

	public VolvoRemoteDownloadProvider() {
		super(null);
	}

	@Override
	public void init(ApplicationContext context, Properties prop, Object providerId) {
		this.volvoApi = new VolvoApi(
				prop.getProperty(VolvoRemoteDownloadProvider.SETTING_REMOTE_VEHICLE_VOLVO_API_URI, VolvoRemoteDownloadProvider.DEFAULT_REMOTE_VEHICLE_VOLVO_API_URI));
		this.setLocator(context.getBean(ILocatorReferencer.class).getLocator());
		this.providerId = providerId;
		this.downloadWorker = new VolvoRemoteDownloadWorker(context, this.volvoApi, providerId);
	}

	public Object getProviderId() {
		return this.providerId;
	}

	@Override
	public void createCompany(String companyId) throws RemoteVehicleException {
		VolvoRemoteDownloadProvider.logger.trace("No need to setup volvo company");
	}

	@Override
	public void configureVehicle(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String vehicleId, String vehicleUnitSn, String vehicleType)
			throws RemoteVehicleException {
		VolvoRemoteDownloadProvider.logger.trace("No need to setup volvo vehicle");
	}

	@Override
	public void configureDriver(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String driverId) throws RemoteVehicleException {
		VolvoRemoteDownloadProvider.logger.trace("No need to setup volvo driver");
	}

	@Override
	public void requestForceDriverDownload(String companyId, String driverId) throws RemoteVehicleException {
		throw new RemoteVehicleException("E_VOLVO_INTEGRATION_NOT_SUPPORT_REQUEST");
	}

	@Override
	public void requestForceVehicleDownload(String companyId, String plateNumber, Date from, Date to) throws RemoteVehicleException {
		throw new RemoteVehicleException("E_VOLVO_INTEGRATION_NOT_SUPPORT_REQUEST");
	}

}
