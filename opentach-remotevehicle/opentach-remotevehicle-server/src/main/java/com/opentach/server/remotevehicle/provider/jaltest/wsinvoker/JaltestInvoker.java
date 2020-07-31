package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.remotevehicle.provider.jaltest.JaltestException;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.ActivityBlockDownloadType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.ArrayOfInt;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.ContractInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.GPSPositionInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.IWsJaltestTelematicsAPI;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.TachoFileExtension;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.TachoFileInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.TachoScheduleInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.VehicleType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResult;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResultOfListOfGPSPositionInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResultOfListOfTachoFileInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResultOfString;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResultOfTachoDownloadInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResultOfTachoScheduleInfo;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WsJaltestTelematicsAPI;

public class JaltestInvoker {
	public static final String					FORMAT_DATE		= "yyyy-MM-dd";
	public static final String					FORMAT_DATETIME	= "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private static final Logger					logger			= LoggerFactory.getLogger(JaltestInvoker.class);

	private final String						serviceUri;
	private final String						secretKey;
	private final String						apiKey;

	private WSClient<IWsJaltestTelematicsAPI>	deviceClient;

	public JaltestInvoker(String devicemgtUri, String secretKey, String apiKey) {
		super();
		this.serviceUri = devicemgtUri;
		this.secretKey = secretKey;
		this.apiKey = apiKey;
	}

	private IWsJaltestTelematicsAPI getJaltestClient() {
		if (this.deviceClient == null) {
			WsJaltestTelematicsAPI deviceService = new WsJaltestTelematicsAPI((java.net.URL) null);
			deviceService.addPort(WsJaltestTelematicsAPI.SERVICE, javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING, this.serviceUri);
			IWsJaltestTelematicsAPI port = deviceService.getPort(IWsJaltestTelematicsAPI.class);
			BindingProvider provider = (BindingProvider) port;
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.serviceUri);

			// provider.getBinding().getBindingID()
			// getBindingID();
			// provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, this.user);
			// provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, this.pass);
			this.stablishServiceAuthorization(port);
			this.setupLogging(port);

			this.deviceClient = new WSClient<IWsJaltestTelematicsAPI>(deviceService, port);
		}
		return this.deviceClient.getPort();
	}

	private void setupLogging(IWsJaltestTelematicsAPI port) {
		org.apache.cxf.endpoint.Client client = ClientProxy.getClient(port);
		org.apache.cxf.endpoint.Endpoint cxfEndpoint = client.getEndpoint();
		cxfEndpoint.getInInterceptors().add(new LoggingInInterceptor());
		cxfEndpoint.getOutInterceptors().add(new LoggingOutInterceptor());

	}

	/**
	 * Stablish service authorization. La implementacion por defecto de java de jax-ws no soporta autenticacion digest. Para la implementacion de apache cxf hay que hacer esto.
	 * Aqui se explican otras configuraciones de autenticacion.
	 * http://cxf.apache.org/docs/client-http-transport-including-ssl-support.html#ClientHTTPTransport(includingSSLsupport)-Theconduitelement
	 *
	 * @param port
	 *            the port
	 */
	protected void stablishServiceAuthorization(Object port) {
		org.apache.cxf.endpoint.Client client = ClientProxy.getClient(port);
		org.apache.cxf.endpoint.Endpoint cxfEndpoint = client.getEndpoint();
		cxfEndpoint.getOutInterceptors().add(new JaltestSecurityInterceptor(this.secretKey, this.apiKey));
		// Client client = ClientProxy.getClient(port);
		// HTTPConduit http = (HTTPConduit) client.getConduit();
		// AuthorizationPolicy authorization = new AuthorizationPolicy();
		// authorization.setAuthorizationType("Digest");
		// authorization.setUserName(this.user);
		// authorization.setPassword(this.pass);
		// http.setAuthorization(authorization);
	}

	private void checkResult(WebResult result, String... warningCodes) throws JaltestException {
		if (this.isWarningCode(result, warningCodes)) {
			return;
		}
		if (!"00".equals(result.getErrorCode()) && !"0".equals(result.getErrorCode())) {
			JaltestInvoker.logger.error("Error invoking jaltest: {} {} - {}", result.getClass().getName(), result.getErrorCode(), result.getMessage());
			throw new JaltestException(result.getMessage());
		}
	}

	private boolean isWarningCode(WebResult result, String... warningCodes) {
		String errorCode = result.getErrorCode();
		if (ObjectTools.isIn(errorCode, (Object[]) warningCodes)) {
			JaltestInvoker.logger.warn(result.getMessage());
			return true;
		}
		return false;
	}

	public void createFleet(String name, String cif, String address, String phone, String email, String contractCode, Date contractCreationDate, Date contractExpirationDate,
			String contractHolder) throws JaltestException {
		SimpleDateFormat sdf = new SimpleDateFormat(JaltestInvoker.FORMAT_DATE);
		ContractInfo contractInfo = new ContractInfo();
		contractInfo.setContractCode(contractCode);
		contractInfo.setContractCreationDate(contractCreationDate == null ? null : sdf.format(contractCreationDate));
		contractInfo.setContractExpirationDate(contractExpirationDate == null ? null : sdf.format(contractExpirationDate));
		contractInfo.setContractHolder(contractHolder);
		WebResult createNewFleet = this.getJaltestClient().createNewFleet(name, cif, address, phone, email, 0, contractInfo);
		this.checkResult(createNewFleet);
	}

	public String generateCompanyKeys(String cif) throws JaltestException {
		WebResultOfString generateCompanyTachographCard = this.getJaltestClient().generateCompanyTachographCard(cif);
		this.checkResult(generateCompanyTachographCard);
		return generateCompanyTachographCard.getResult();
	}

	public void createVehicle(String cif, String vehicleType, String plateNumber) throws JaltestException {
		WebResult createVehicle = this.getJaltestClient().createVehicle(cif, "V".equals(vehicleType) ? VehicleType.BUS : VehicleType.TRUCK, plateNumber);
		this.checkResult(createVehicle, "21");
	}

	public void createDriver(String cif, String dni, String name, String surname, String tachoCardCountry, String tachoCardNumber, Date tachoCardExpirationDate)
			throws JaltestException {
		SimpleDateFormat sdf = new SimpleDateFormat(JaltestInvoker.FORMAT_DATE);
		WebResult createDriver = this.getJaltestClient().createDriver(cif, name, surname, dni, null, null, null, null, tachoCardCountry, tachoCardNumber,
				sdf.format(tachoCardExpirationDate));
		this.checkResult(createDriver);
	}

	public void setupVehicleTelematicUnit(String cif, String telematicUnitNumber, String plateNumber) throws JaltestException {
		WebResult setTelematicsUnitToVehicle = this.getJaltestClient().setTelematicsUnitToVehicle(cif, telematicUnitNumber, plateNumber);
		this.checkResult(setTelematicsUnitToVehicle, "25");
	}

	public TachoScheduleInfo stablishVehicleDownloadPeriod(String cif, String plateNumber, boolean firstDayOfMonth) throws JaltestException {
		WebResultOfTachoScheduleInfo setTachoScheduleForVehicle = this.getJaltestClient().setTachoScheduleForVehicle(cif, plateNumber, firstDayOfMonth);
		this.checkResult(setTachoScheduleForVehicle);
		return setTachoScheduleForVehicle.getResult();
	}

	public Integer startVehicleDownload(String cif, String plateNumber, Date from, Date to) throws JaltestException {
		SimpleDateFormat sdf = new SimpleDateFormat(JaltestInvoker.FORMAT_DATE);
		WebResultOfTachoDownloadInfo startTachoDownload = this.getJaltestClient().startTachoDownload(cif, plateNumber,
				((from == null) || (to == null)) ? ActivityBlockDownloadType.COMPLETE : ActivityBlockDownloadType.DATES_SELECTION, from == null ? null : sdf.format(from),
						from == null ? null : sdf.format(to));
		this.checkResult(startTachoDownload);
		return startTachoDownload.getResult().getTachoDownloadId();
	}

	public Integer startDriverDownload(String cif, String cardNumber) throws JaltestException {
		WebResultOfTachoDownloadInfo startCardDriverDownload = this.getJaltestClient().startCardDriverDownload(cif, cardNumber);
		this.checkResult(startCardDriverDownload);
		return startCardDriverDownload.getResult().getTachoDownloadId();
	}

	public List<TachoFileInfo> getTachoFileList(String cif, String numberPlate, String driverCardNumber, Date downloadStartDate, Date downloadEndDate) throws JaltestException {
		SimpleDateFormat sdf = new SimpleDateFormat(JaltestInvoker.FORMAT_DATE);
		WebResultOfListOfTachoFileInfo tachoFilesList = this.getJaltestClient().getTachoFilesList(cif, numberPlate, driverCardNumber, sdf.format(downloadStartDate),
				sdf.format(downloadEndDate), true, true, true, true);
		this.checkResult(tachoFilesList);
		return tachoFilesList.getResult().getTachoFileInfo();
	}

	public List<TachoFileInfo> downloadTachoFiles(String cif, List<Integer> ids) throws JaltestException {
		ArrayOfInt fileIds = new ArrayOfInt();
		fileIds.getInt().addAll(ids);
		WebResultOfListOfTachoFileInfo downloadTachoFiles = this.getJaltestClient().downloadTachoFiles(cif, fileIds, TachoFileExtension.TGD);
		this.checkResult(downloadTachoFiles);
		return downloadTachoFiles.getResult().getTachoFileInfo();
	}

	public List<GPSPositionInfo> getVehicleLocations(String cif, String plateNumber, Date startdate, Date endDate) throws JaltestException {
		SimpleDateFormat sdf = new SimpleDateFormat(JaltestInvoker.FORMAT_DATETIME);
		WebResultOfListOfGPSPositionInfo vehicleLocations = this.getJaltestClient().getVehicleLocations(cif, plateNumber, sdf.format(startdate), sdf.format(endDate));
		this.checkResult(vehicleLocations);
		return vehicleLocations.getResult().getGPSPositionInfo();
	}

}
