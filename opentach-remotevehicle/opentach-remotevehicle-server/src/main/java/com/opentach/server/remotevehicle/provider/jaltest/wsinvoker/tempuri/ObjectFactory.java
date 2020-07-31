
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResultOfUsuarioWebDto1YD7O3EO;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.ArrayOfEventoELDDto;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.ConduccionNoIdentificadaELDDto;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.ConductorELDDto;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.FirmaELDDto;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.InfoELD;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.RegistroELDDto;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetAccessLogin_QNAME = new QName("http://tempuri.org/", "login");
    private final static QName _GetAccessPassword_QNAME = new QName("http://tempuri.org/", "password");
    private final static QName _GetAccessMobileKey_QNAME = new QName("http://tempuri.org/", "mobileKey");
    private final static QName _GetAccessMobileUuid_QNAME = new QName("http://tempuri.org/", "mobile_uuid");
    private final static QName _GetAccessResponseGetAccessResult_QNAME = new QName("http://tempuri.org/", "getAccessResult");
    private final static QName _SetMessageKeyKey_QNAME = new QName("http://tempuri.org/", "key");
    private final static QName _SetMessageKeyResponseSetMessageKeyResult_QNAME = new QName("http://tempuri.org/", "setMessageKeyResult");
    private final static QName _SetGPSPositionGpsPosition_QNAME = new QName("http://tempuri.org/", "gpsPosition");
    private final static QName _SetGPSPositionResponseSetGPSPositionResult_QNAME = new QName("http://tempuri.org/", "setGPSPositionResult");
    private final static QName _GetGeneralInfoResponseGetGeneralInfoResult_QNAME = new QName("http://tempuri.org/", "getGeneralInfoResult");
    private final static QName _GetVehiclesResponseGetVehiclesResult_QNAME = new QName("http://tempuri.org/", "getVehiclesResult");
    private final static QName _GetCiclesResponseGetCiclesResult_QNAME = new QName("http://tempuri.org/", "getCiclesResult");
    private final static QName _GetInfoDayDateInfo_QNAME = new QName("http://tempuri.org/", "dateInfo");
    private final static QName _GetInfoDayResponseGetInfoDayResult_QNAME = new QName("http://tempuri.org/", "getInfoDayResult");
    private final static QName _SaveInfoDayRecordToSave_QNAME = new QName("http://tempuri.org/", "recordToSave");
    private final static QName _SaveInfoDayResponseSaveInfoDayResult_QNAME = new QName("http://tempuri.org/", "saveInfoDayResult");
    private final static QName _ConfirmCandidateResponseConfirmCandidateResult_QNAME = new QName("http://tempuri.org/", "confirmCandidateResult");
    private final static QName _GetCandidatesResponseGetCandidatesResult_QNAME = new QName("http://tempuri.org/", "getCandidatesResult");
    private final static QName _SaveEventsEventsToSave_QNAME = new QName("http://tempuri.org/", "eventsToSave");
    private final static QName _SaveEventsResponseSaveEventsResult_QNAME = new QName("http://tempuri.org/", "saveEventsResult");
    private final static QName _SaveUnidentifiedDrivingUnidentifiedDrivingToSave_QNAME = new QName("http://tempuri.org/", "unidentifiedDrivingToSave");
    private final static QName _SaveUnidentifiedDrivingResponseSaveUnidentifiedDrivingResult_QNAME = new QName("http://tempuri.org/", "saveUnidentifiedDrivingResult");
    private final static QName _GetSignatureId_QNAME = new QName("http://tempuri.org/", "id");
    private final static QName _GetSignatureResponseGetSignatureResult_QNAME = new QName("http://tempuri.org/", "getSignatureResult");
    private final static QName _SaveSignatureSignatureToSave_QNAME = new QName("http://tempuri.org/", "signatureToSave");
    private final static QName _SaveSignatureResponseSaveSignatureResult_QNAME = new QName("http://tempuri.org/", "saveSignatureResult");
    private final static QName _UpdateSettingsNewSettings_QNAME = new QName("http://tempuri.org/", "newSettings");
    private final static QName _UpdateSettingsResponseUpdateSettingsResult_QNAME = new QName("http://tempuri.org/", "updateSettingsResult");
    private final static QName _GetAccessELDResponseGetAccessELDResult_QNAME = new QName("http://tempuri.org/", "getAccessELDResult");
    private final static QName _GetDriverResponseGetDriverResult_QNAME = new QName("http://tempuri.org/", "getDriverResult");
    private final static QName _SendMailDOTRecipientEmail_QNAME = new QName("http://tempuri.org/", "recipientEmail");
    private final static QName _SendMailDOTResponseSendMailDOTResult_QNAME = new QName("http://tempuri.org/", "sendMailDOTResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAccess }
     * 
     */
    public GetAccess createGetAccess() {
        return new GetAccess();
    }

    /**
     * Create an instance of {@link GetAccessResponse }
     * 
     */
    public GetAccessResponse createGetAccessResponse() {
        return new GetAccessResponse();
    }

    /**
     * Create an instance of {@link SetMessageKey }
     * 
     */
    public SetMessageKey createSetMessageKey() {
        return new SetMessageKey();
    }

    /**
     * Create an instance of {@link SetMessageKeyResponse }
     * 
     */
    public SetMessageKeyResponse createSetMessageKeyResponse() {
        return new SetMessageKeyResponse();
    }

    /**
     * Create an instance of {@link SetGPSPosition }
     * 
     */
    public SetGPSPosition createSetGPSPosition() {
        return new SetGPSPosition();
    }

    /**
     * Create an instance of {@link SetGPSPositionResponse }
     * 
     */
    public SetGPSPositionResponse createSetGPSPositionResponse() {
        return new SetGPSPositionResponse();
    }

    /**
     * Create an instance of {@link GetGeneralInfo }
     * 
     */
    public GetGeneralInfo createGetGeneralInfo() {
        return new GetGeneralInfo();
    }

    /**
     * Create an instance of {@link GetGeneralInfoResponse }
     * 
     */
    public GetGeneralInfoResponse createGetGeneralInfoResponse() {
        return new GetGeneralInfoResponse();
    }

    /**
     * Create an instance of {@link GetVehicles }
     * 
     */
    public GetVehicles createGetVehicles() {
        return new GetVehicles();
    }

    /**
     * Create an instance of {@link GetVehiclesResponse }
     * 
     */
    public GetVehiclesResponse createGetVehiclesResponse() {
        return new GetVehiclesResponse();
    }

    /**
     * Create an instance of {@link GetCicles }
     * 
     */
    public GetCicles createGetCicles() {
        return new GetCicles();
    }

    /**
     * Create an instance of {@link GetCiclesResponse }
     * 
     */
    public GetCiclesResponse createGetCiclesResponse() {
        return new GetCiclesResponse();
    }

    /**
     * Create an instance of {@link GetInfoDay }
     * 
     */
    public GetInfoDay createGetInfoDay() {
        return new GetInfoDay();
    }

    /**
     * Create an instance of {@link GetInfoDayResponse }
     * 
     */
    public GetInfoDayResponse createGetInfoDayResponse() {
        return new GetInfoDayResponse();
    }

    /**
     * Create an instance of {@link SaveInfoDay }
     * 
     */
    public SaveInfoDay createSaveInfoDay() {
        return new SaveInfoDay();
    }

    /**
     * Create an instance of {@link SaveInfoDayResponse }
     * 
     */
    public SaveInfoDayResponse createSaveInfoDayResponse() {
        return new SaveInfoDayResponse();
    }

    /**
     * Create an instance of {@link ConfirmCandidate }
     * 
     */
    public ConfirmCandidate createConfirmCandidate() {
        return new ConfirmCandidate();
    }

    /**
     * Create an instance of {@link ConfirmCandidateResponse }
     * 
     */
    public ConfirmCandidateResponse createConfirmCandidateResponse() {
        return new ConfirmCandidateResponse();
    }

    /**
     * Create an instance of {@link GetCandidates }
     * 
     */
    public GetCandidates createGetCandidates() {
        return new GetCandidates();
    }

    /**
     * Create an instance of {@link GetCandidatesResponse }
     * 
     */
    public GetCandidatesResponse createGetCandidatesResponse() {
        return new GetCandidatesResponse();
    }

    /**
     * Create an instance of {@link SaveEvents }
     * 
     */
    public SaveEvents createSaveEvents() {
        return new SaveEvents();
    }

    /**
     * Create an instance of {@link SaveEventsResponse }
     * 
     */
    public SaveEventsResponse createSaveEventsResponse() {
        return new SaveEventsResponse();
    }

    /**
     * Create an instance of {@link SaveUnidentifiedDriving }
     * 
     */
    public SaveUnidentifiedDriving createSaveUnidentifiedDriving() {
        return new SaveUnidentifiedDriving();
    }

    /**
     * Create an instance of {@link SaveUnidentifiedDrivingResponse }
     * 
     */
    public SaveUnidentifiedDrivingResponse createSaveUnidentifiedDrivingResponse() {
        return new SaveUnidentifiedDrivingResponse();
    }

    /**
     * Create an instance of {@link GetSignature }
     * 
     */
    public GetSignature createGetSignature() {
        return new GetSignature();
    }

    /**
     * Create an instance of {@link GetSignatureResponse }
     * 
     */
    public GetSignatureResponse createGetSignatureResponse() {
        return new GetSignatureResponse();
    }

    /**
     * Create an instance of {@link SaveSignature }
     * 
     */
    public SaveSignature createSaveSignature() {
        return new SaveSignature();
    }

    /**
     * Create an instance of {@link SaveSignatureResponse }
     * 
     */
    public SaveSignatureResponse createSaveSignatureResponse() {
        return new SaveSignatureResponse();
    }

    /**
     * Create an instance of {@link UpdateSettings }
     * 
     */
    public UpdateSettings createUpdateSettings() {
        return new UpdateSettings();
    }

    /**
     * Create an instance of {@link UpdateSettingsResponse }
     * 
     */
    public UpdateSettingsResponse createUpdateSettingsResponse() {
        return new UpdateSettingsResponse();
    }

    /**
     * Create an instance of {@link GetAccessELD }
     * 
     */
    public GetAccessELD createGetAccessELD() {
        return new GetAccessELD();
    }

    /**
     * Create an instance of {@link GetAccessELDResponse }
     * 
     */
    public GetAccessELDResponse createGetAccessELDResponse() {
        return new GetAccessELDResponse();
    }

    /**
     * Create an instance of {@link GetDriver }
     * 
     */
    public GetDriver createGetDriver() {
        return new GetDriver();
    }

    /**
     * Create an instance of {@link GetDriverResponse }
     * 
     */
    public GetDriverResponse createGetDriverResponse() {
        return new GetDriverResponse();
    }

    /**
     * Create an instance of {@link SendMailDOT }
     * 
     */
    public SendMailDOT createSendMailDOT() {
        return new SendMailDOT();
    }

    /**
     * Create an instance of {@link SendMailDOTResponse }
     * 
     */
    public SendMailDOTResponse createSendMailDOTResponse() {
        return new SendMailDOTResponse();
    }

    /**
     * Create an instance of {@link CreateNewFleet }
     * 
     */
    public CreateNewFleet createCreateNewFleet() {
        return new CreateNewFleet();
    }

    /**
     * Create an instance of {@link ContractInfo }
     * 
     */
    public ContractInfo createContractInfo() {
        return new ContractInfo();
    }

    /**
     * Create an instance of {@link CreateNewFleetResponse }
     * 
     */
    public CreateNewFleetResponse createCreateNewFleetResponse() {
        return new CreateNewFleetResponse();
    }

    /**
     * Create an instance of {@link com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResult }
     * 
     */
    public com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResult createWebResult() {
        return new com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri.WebResult();
    }

    /**
     * Create an instance of {@link CreateWebUser }
     * 
     */
    public CreateWebUser createCreateWebUser() {
        return new CreateWebUser();
    }

    /**
     * Create an instance of {@link CreateWebUserResponse }
     * 
     */
    public CreateWebUserResponse createCreateWebUserResponse() {
        return new CreateWebUserResponse();
    }

    /**
     * Create an instance of {@link GenerateCompanyTachographCard }
     * 
     */
    public GenerateCompanyTachographCard createGenerateCompanyTachographCard() {
        return new GenerateCompanyTachographCard();
    }

    /**
     * Create an instance of {@link GenerateCompanyTachographCardResponse }
     * 
     */
    public GenerateCompanyTachographCardResponse createGenerateCompanyTachographCardResponse() {
        return new GenerateCompanyTachographCardResponse();
    }

    /**
     * Create an instance of {@link WebResultOfString }
     * 
     */
    public WebResultOfString createWebResultOfString() {
        return new WebResultOfString();
    }

    /**
     * Create an instance of {@link CreateVehicle }
     * 
     */
    public CreateVehicle createCreateVehicle() {
        return new CreateVehicle();
    }

    /**
     * Create an instance of {@link CreateVehicleResponse }
     * 
     */
    public CreateVehicleResponse createCreateVehicleResponse() {
        return new CreateVehicleResponse();
    }

    /**
     * Create an instance of {@link SetTelematicsUnitToVehicle }
     * 
     */
    public SetTelematicsUnitToVehicle createSetTelematicsUnitToVehicle() {
        return new SetTelematicsUnitToVehicle();
    }

    /**
     * Create an instance of {@link SetTelematicsUnitToVehicleResponse }
     * 
     */
    public SetTelematicsUnitToVehicleResponse createSetTelematicsUnitToVehicleResponse() {
        return new SetTelematicsUnitToVehicleResponse();
    }

    /**
     * Create an instance of {@link CreateDriver }
     * 
     */
    public CreateDriver createCreateDriver() {
        return new CreateDriver();
    }

    /**
     * Create an instance of {@link CreateDriverResponse }
     * 
     */
    public CreateDriverResponse createCreateDriverResponse() {
        return new CreateDriverResponse();
    }

    /**
     * Create an instance of {@link GetTachoFilesList }
     * 
     */
    public GetTachoFilesList createGetTachoFilesList() {
        return new GetTachoFilesList();
    }

    /**
     * Create an instance of {@link GetTachoFilesListResponse }
     * 
     */
    public GetTachoFilesListResponse createGetTachoFilesListResponse() {
        return new GetTachoFilesListResponse();
    }

    /**
     * Create an instance of {@link WebResultOfListOfTachoFileInfo }
     * 
     */
    public WebResultOfListOfTachoFileInfo createWebResultOfListOfTachoFileInfo() {
        return new WebResultOfListOfTachoFileInfo();
    }

    /**
     * Create an instance of {@link DownloadTachoFiles }
     * 
     */
    public DownloadTachoFiles createDownloadTachoFiles() {
        return new DownloadTachoFiles();
    }

    /**
     * Create an instance of {@link ArrayOfInt }
     * 
     */
    public ArrayOfInt createArrayOfInt() {
        return new ArrayOfInt();
    }

    /**
     * Create an instance of {@link DownloadTachoFilesResponse }
     * 
     */
    public DownloadTachoFilesResponse createDownloadTachoFilesResponse() {
        return new DownloadTachoFilesResponse();
    }

    /**
     * Create an instance of {@link SetTachoScheduleForVehicle }
     * 
     */
    public SetTachoScheduleForVehicle createSetTachoScheduleForVehicle() {
        return new SetTachoScheduleForVehicle();
    }

    /**
     * Create an instance of {@link SetTachoScheduleForVehicleResponse }
     * 
     */
    public SetTachoScheduleForVehicleResponse createSetTachoScheduleForVehicleResponse() {
        return new SetTachoScheduleForVehicleResponse();
    }

    /**
     * Create an instance of {@link WebResultOfTachoScheduleInfo }
     * 
     */
    public WebResultOfTachoScheduleInfo createWebResultOfTachoScheduleInfo() {
        return new WebResultOfTachoScheduleInfo();
    }

    /**
     * Create an instance of {@link SetTachoScheduleForDriver }
     * 
     */
    public SetTachoScheduleForDriver createSetTachoScheduleForDriver() {
        return new SetTachoScheduleForDriver();
    }

    /**
     * Create an instance of {@link SetTachoScheduleForDriverResponse }
     * 
     */
    public SetTachoScheduleForDriverResponse createSetTachoScheduleForDriverResponse() {
        return new SetTachoScheduleForDriverResponse();
    }

    /**
     * Create an instance of {@link StartTachoDownload }
     * 
     */
    public StartTachoDownload createStartTachoDownload() {
        return new StartTachoDownload();
    }

    /**
     * Create an instance of {@link StartTachoDownloadResponse }
     * 
     */
    public StartTachoDownloadResponse createStartTachoDownloadResponse() {
        return new StartTachoDownloadResponse();
    }

    /**
     * Create an instance of {@link WebResultOfTachoDownloadInfo }
     * 
     */
    public WebResultOfTachoDownloadInfo createWebResultOfTachoDownloadInfo() {
        return new WebResultOfTachoDownloadInfo();
    }

    /**
     * Create an instance of {@link StartCardDriverDownload }
     * 
     */
    public StartCardDriverDownload createStartCardDriverDownload() {
        return new StartCardDriverDownload();
    }

    /**
     * Create an instance of {@link StartCardDriverDownloadResponse }
     * 
     */
    public StartCardDriverDownloadResponse createStartCardDriverDownloadResponse() {
        return new StartCardDriverDownloadResponse();
    }

    /**
     * Create an instance of {@link GetVehicleLocations }
     * 
     */
    public GetVehicleLocations createGetVehicleLocations() {
        return new GetVehicleLocations();
    }

    /**
     * Create an instance of {@link GetVehicleLocationsResponse }
     * 
     */
    public GetVehicleLocationsResponse createGetVehicleLocationsResponse() {
        return new GetVehicleLocationsResponse();
    }

    /**
     * Create an instance of {@link WebResultOfListOfGPSPositionInfo }
     * 
     */
    public WebResultOfListOfGPSPositionInfo createWebResultOfListOfGPSPositionInfo() {
        return new WebResultOfListOfGPSPositionInfo();
    }

    /**
     * Create an instance of {@link ArrayOfTachoFileInfo }
     * 
     */
    public ArrayOfTachoFileInfo createArrayOfTachoFileInfo() {
        return new ArrayOfTachoFileInfo();
    }

    /**
     * Create an instance of {@link TachoFileInfo }
     * 
     */
    public TachoFileInfo createTachoFileInfo() {
        return new TachoFileInfo();
    }

    /**
     * Create an instance of {@link ExtensionDataObject }
     * 
     */
    public ExtensionDataObject createExtensionDataObject() {
        return new ExtensionDataObject();
    }

    /**
     * Create an instance of {@link TachoScheduleInfo }
     * 
     */
    public TachoScheduleInfo createTachoScheduleInfo() {
        return new TachoScheduleInfo();
    }

    /**
     * Create an instance of {@link TachoDownloadInfo }
     * 
     */
    public TachoDownloadInfo createTachoDownloadInfo() {
        return new TachoDownloadInfo();
    }

    /**
     * Create an instance of {@link ArrayOfGPSPositionInfo }
     * 
     */
    public ArrayOfGPSPositionInfo createArrayOfGPSPositionInfo() {
        return new ArrayOfGPSPositionInfo();
    }

    /**
     * Create an instance of {@link GPSPositionInfo }
     * 
     */
    public GPSPositionInfo createGPSPositionInfo() {
        return new GPSPositionInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "login", scope = GetAccess.class)
    public JAXBElement<String> createGetAccessLogin(String value) {
        return new JAXBElement<String>(_GetAccessLogin_QNAME, String.class, GetAccess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "password", scope = GetAccess.class)
    public JAXBElement<String> createGetAccessPassword(String value) {
        return new JAXBElement<String>(_GetAccessPassword_QNAME, String.class, GetAccess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "mobileKey", scope = GetAccess.class)
    public JAXBElement<String> createGetAccessMobileKey(String value) {
        return new JAXBElement<String>(_GetAccessMobileKey_QNAME, String.class, GetAccess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "mobile_uuid", scope = GetAccess.class)
    public JAXBElement<String> createGetAccessMobileUuid(String value) {
        return new JAXBElement<String>(_GetAccessMobileUuid_QNAME, String.class, GetAccess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WebResultOfUsuarioWebDto1YD7O3EO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getAccessResult", scope = GetAccessResponse.class)
    public JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO> createGetAccessResponseGetAccessResult(WebResultOfUsuarioWebDto1YD7O3EO value) {
        return new JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO>(_GetAccessResponseGetAccessResult_QNAME, WebResultOfUsuarioWebDto1YD7O3EO.class, GetAccessResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "login", scope = SetMessageKey.class)
    public JAXBElement<String> createSetMessageKeyLogin(String value) {
        return new JAXBElement<String>(_GetAccessLogin_QNAME, String.class, SetMessageKey.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "password", scope = SetMessageKey.class)
    public JAXBElement<String> createSetMessageKeyPassword(String value) {
        return new JAXBElement<String>(_GetAccessPassword_QNAME, String.class, SetMessageKey.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "key", scope = SetMessageKey.class)
    public JAXBElement<String> createSetMessageKeyKey(String value) {
        return new JAXBElement<String>(_SetMessageKeyKey_QNAME, String.class, SetMessageKey.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "setMessageKeyResult", scope = SetMessageKeyResponse.class)
    public JAXBElement<com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult> createSetMessageKeyResponseSetMessageKeyResult(com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult value) {
        return new JAXBElement<com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult>(_SetMessageKeyResponseSetMessageKeyResult_QNAME, com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult.class, SetMessageKeyResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "mobileKey", scope = SetGPSPosition.class)
    public JAXBElement<String> createSetGPSPositionMobileKey(String value) {
        return new JAXBElement<String>(_GetAccessMobileKey_QNAME, String.class, SetGPSPosition.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "gpsPosition", scope = SetGPSPosition.class)
    public JAXBElement<String> createSetGPSPositionGpsPosition(String value) {
        return new JAXBElement<String>(_SetGPSPositionGpsPosition_QNAME, String.class, SetGPSPosition.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "setGPSPositionResult", scope = SetGPSPositionResponse.class)
    public JAXBElement<com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult> createSetGPSPositionResponseSetGPSPositionResult(com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult value) {
        return new JAXBElement<com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult>(_SetGPSPositionResponseSetGPSPositionResult_QNAME, com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult.class, SetGPSPositionResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getGeneralInfoResult", scope = GetGeneralInfoResponse.class)
    public JAXBElement<InfoELD> createGetGeneralInfoResponseGetGeneralInfoResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetGeneralInfoResponseGetGeneralInfoResult_QNAME, InfoELD.class, GetGeneralInfoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getVehiclesResult", scope = GetVehiclesResponse.class)
    public JAXBElement<InfoELD> createGetVehiclesResponseGetVehiclesResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetVehiclesResponseGetVehiclesResult_QNAME, InfoELD.class, GetVehiclesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getCiclesResult", scope = GetCiclesResponse.class)
    public JAXBElement<InfoELD> createGetCiclesResponseGetCiclesResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetCiclesResponseGetCiclesResult_QNAME, InfoELD.class, GetCiclesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "dateInfo", scope = GetInfoDay.class)
    public JAXBElement<String> createGetInfoDayDateInfo(String value) {
        return new JAXBElement<String>(_GetInfoDayDateInfo_QNAME, String.class, GetInfoDay.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getInfoDayResult", scope = GetInfoDayResponse.class)
    public JAXBElement<InfoELD> createGetInfoDayResponseGetInfoDayResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetInfoDayResponseGetInfoDayResult_QNAME, InfoELD.class, GetInfoDayResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistroELDDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "recordToSave", scope = SaveInfoDay.class)
    public JAXBElement<RegistroELDDto> createSaveInfoDayRecordToSave(RegistroELDDto value) {
        return new JAXBElement<RegistroELDDto>(_SaveInfoDayRecordToSave_QNAME, RegistroELDDto.class, SaveInfoDay.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "saveInfoDayResult", scope = SaveInfoDayResponse.class)
    public JAXBElement<InfoELD> createSaveInfoDayResponseSaveInfoDayResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_SaveInfoDayResponseSaveInfoDayResult_QNAME, InfoELD.class, SaveInfoDayResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "confirmCandidateResult", scope = ConfirmCandidateResponse.class)
    public JAXBElement<InfoELD> createConfirmCandidateResponseConfirmCandidateResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_ConfirmCandidateResponseConfirmCandidateResult_QNAME, InfoELD.class, ConfirmCandidateResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getCandidatesResult", scope = GetCandidatesResponse.class)
    public JAXBElement<InfoELD> createGetCandidatesResponseGetCandidatesResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetCandidatesResponseGetCandidatesResult_QNAME, InfoELD.class, GetCandidatesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "eventsToSave", scope = SaveEvents.class)
    public JAXBElement<ArrayOfEventoELDDto> createSaveEventsEventsToSave(ArrayOfEventoELDDto value) {
        return new JAXBElement<ArrayOfEventoELDDto>(_SaveEventsEventsToSave_QNAME, ArrayOfEventoELDDto.class, SaveEvents.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "saveEventsResult", scope = SaveEventsResponse.class)
    public JAXBElement<InfoELD> createSaveEventsResponseSaveEventsResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_SaveEventsResponseSaveEventsResult_QNAME, InfoELD.class, SaveEventsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConduccionNoIdentificadaELDDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "unidentifiedDrivingToSave", scope = SaveUnidentifiedDriving.class)
    public JAXBElement<ConduccionNoIdentificadaELDDto> createSaveUnidentifiedDrivingUnidentifiedDrivingToSave(ConduccionNoIdentificadaELDDto value) {
        return new JAXBElement<ConduccionNoIdentificadaELDDto>(_SaveUnidentifiedDrivingUnidentifiedDrivingToSave_QNAME, ConduccionNoIdentificadaELDDto.class, SaveUnidentifiedDriving.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "saveUnidentifiedDrivingResult", scope = SaveUnidentifiedDrivingResponse.class)
    public JAXBElement<InfoELD> createSaveUnidentifiedDrivingResponseSaveUnidentifiedDrivingResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_SaveUnidentifiedDrivingResponseSaveUnidentifiedDrivingResult_QNAME, InfoELD.class, SaveUnidentifiedDrivingResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "id", scope = GetSignature.class)
    public JAXBElement<String> createGetSignatureId(String value) {
        return new JAXBElement<String>(_GetSignatureId_QNAME, String.class, GetSignature.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getSignatureResult", scope = GetSignatureResponse.class)
    public JAXBElement<InfoELD> createGetSignatureResponseGetSignatureResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetSignatureResponseGetSignatureResult_QNAME, InfoELD.class, GetSignatureResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmaELDDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "signatureToSave", scope = SaveSignature.class)
    public JAXBElement<FirmaELDDto> createSaveSignatureSignatureToSave(FirmaELDDto value) {
        return new JAXBElement<FirmaELDDto>(_SaveSignatureSignatureToSave_QNAME, FirmaELDDto.class, SaveSignature.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "saveSignatureResult", scope = SaveSignatureResponse.class)
    public JAXBElement<InfoELD> createSaveSignatureResponseSaveSignatureResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_SaveSignatureResponseSaveSignatureResult_QNAME, InfoELD.class, SaveSignatureResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConductorELDDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "newSettings", scope = UpdateSettings.class)
    public JAXBElement<ConductorELDDto> createUpdateSettingsNewSettings(ConductorELDDto value) {
        return new JAXBElement<ConductorELDDto>(_UpdateSettingsNewSettings_QNAME, ConductorELDDto.class, UpdateSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "updateSettingsResult", scope = UpdateSettingsResponse.class)
    public JAXBElement<InfoELD> createUpdateSettingsResponseUpdateSettingsResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_UpdateSettingsResponseUpdateSettingsResult_QNAME, InfoELD.class, UpdateSettingsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "login", scope = GetAccessELD.class)
    public JAXBElement<String> createGetAccessELDLogin(String value) {
        return new JAXBElement<String>(_GetAccessLogin_QNAME, String.class, GetAccessELD.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "password", scope = GetAccessELD.class)
    public JAXBElement<String> createGetAccessELDPassword(String value) {
        return new JAXBElement<String>(_GetAccessPassword_QNAME, String.class, GetAccessELD.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getAccessELDResult", scope = GetAccessELDResponse.class)
    public JAXBElement<InfoELD> createGetAccessELDResponseGetAccessELDResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetAccessELDResponseGetAccessELDResult_QNAME, InfoELD.class, GetAccessELDResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "getDriverResult", scope = GetDriverResponse.class)
    public JAXBElement<InfoELD> createGetDriverResponseGetDriverResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_GetDriverResponseGetDriverResult_QNAME, InfoELD.class, GetDriverResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "recipientEmail", scope = SendMailDOT.class)
    public JAXBElement<String> createSendMailDOTRecipientEmail(String value) {
        return new JAXBElement<String>(_SendMailDOTRecipientEmail_QNAME, String.class, SendMailDOT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoELD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sendMailDOTResult", scope = SendMailDOTResponse.class)
    public JAXBElement<InfoELD> createSendMailDOTResponseSendMailDOTResult(InfoELD value) {
        return new JAXBElement<InfoELD>(_SendMailDOTResponseSendMailDOTResult_QNAME, InfoELD.class, SendMailDOTResponse.class, value);
    }

}
