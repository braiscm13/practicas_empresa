
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.system.ArrayOfTupleOfstringstring;


/**
 * <p>Clase Java para InfoELD complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="InfoELD"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ActivePrimaryCicle" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}CicloELDDto" minOccurs="0"/&gt;
 *         &lt;element name="ActiveSecondaryCicle" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}CicloELDDto" minOccurs="0"/&gt;
 *         &lt;element name="CardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Cicles" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfCicloELDDto" minOccurs="0"/&gt;
 *         &lt;element name="Client" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ClienteELDDto" minOccurs="0"/&gt;
 *         &lt;element name="Driver" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ConductorELDDto" minOccurs="0"/&gt;
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Records" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfRegistroELDDto" minOccurs="0"/&gt;
 *         &lt;element name="Signatures" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfFirmaELDDto" minOccurs="0"/&gt;
 *         &lt;element name="Vehicles" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfVehiculoELDDto" minOccurs="0"/&gt;
 *         &lt;element name="WebUserId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="WsErrorsFMCSA" type="{http://schemas.datacontract.org/2004/07/System}ArrayOfTupleOfstringstring" minOccurs="0"/&gt;
 *         &lt;element name="ErrorCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoELD", propOrder = {
    "activePrimaryCicle",
    "activeSecondaryCicle",
    "cardNumber",
    "cicles",
    "client",
    "driver",
    "message",
    "records",
    "signatures",
    "vehicles",
    "webUserId",
    "wsErrorsFMCSA",
    "errorCode"
})
public class InfoELD {

    @XmlElementRef(name = "ActivePrimaryCicle", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<CicloELDDto> activePrimaryCicle;
    @XmlElementRef(name = "ActiveSecondaryCicle", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<CicloELDDto> activeSecondaryCicle;
    @XmlElementRef(name = "CardNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cardNumber;
    @XmlElementRef(name = "Cicles", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfCicloELDDto> cicles;
    @XmlElementRef(name = "Client", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ClienteELDDto> client;
    @XmlElementRef(name = "Driver", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ConductorELDDto> driver;
    @XmlElementRef(name = "Message", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> message;
    @XmlElementRef(name = "Records", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRegistroELDDto> records;
    @XmlElementRef(name = "Signatures", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfFirmaELDDto> signatures;
    @XmlElementRef(name = "Vehicles", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfVehiculoELDDto> vehicles;
    @XmlElementRef(name = "WebUserId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> webUserId;
    @XmlElementRef(name = "WsErrorsFMCSA", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfTupleOfstringstring> wsErrorsFMCSA;
    @XmlElement(name = "ErrorCode", required = true, nillable = true)
    protected String errorCode;

    /**
     * Obtiene el valor de la propiedad activePrimaryCicle.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CicloELDDto }{@code >}
     *     
     */
    public JAXBElement<CicloELDDto> getActivePrimaryCicle() {
        return activePrimaryCicle;
    }

    /**
     * Define el valor de la propiedad activePrimaryCicle.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CicloELDDto }{@code >}
     *     
     */
    public void setActivePrimaryCicle(JAXBElement<CicloELDDto> value) {
        this.activePrimaryCicle = value;
    }

    /**
     * Obtiene el valor de la propiedad activeSecondaryCicle.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CicloELDDto }{@code >}
     *     
     */
    public JAXBElement<CicloELDDto> getActiveSecondaryCicle() {
        return activeSecondaryCicle;
    }

    /**
     * Define el valor de la propiedad activeSecondaryCicle.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CicloELDDto }{@code >}
     *     
     */
    public void setActiveSecondaryCicle(JAXBElement<CicloELDDto> value) {
        this.activeSecondaryCicle = value;
    }

    /**
     * Obtiene el valor de la propiedad cardNumber.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCardNumber() {
        return cardNumber;
    }

    /**
     * Define el valor de la propiedad cardNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCardNumber(JAXBElement<String> value) {
        this.cardNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad cicles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCicloELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfCicloELDDto> getCicles() {
        return cicles;
    }

    /**
     * Define el valor de la propiedad cicles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCicloELDDto }{@code >}
     *     
     */
    public void setCicles(JAXBElement<ArrayOfCicloELDDto> value) {
        this.cicles = value;
    }

    /**
     * Obtiene el valor de la propiedad client.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ClienteELDDto }{@code >}
     *     
     */
    public JAXBElement<ClienteELDDto> getClient() {
        return client;
    }

    /**
     * Define el valor de la propiedad client.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ClienteELDDto }{@code >}
     *     
     */
    public void setClient(JAXBElement<ClienteELDDto> value) {
        this.client = value;
    }

    /**
     * Obtiene el valor de la propiedad driver.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ConductorELDDto }{@code >}
     *     
     */
    public JAXBElement<ConductorELDDto> getDriver() {
        return driver;
    }

    /**
     * Define el valor de la propiedad driver.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ConductorELDDto }{@code >}
     *     
     */
    public void setDriver(JAXBElement<ConductorELDDto> value) {
        this.driver = value;
    }

    /**
     * Obtiene el valor de la propiedad message.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMessage() {
        return message;
    }

    /**
     * Define el valor de la propiedad message.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMessage(JAXBElement<String> value) {
        this.message = value;
    }

    /**
     * Obtiene el valor de la propiedad records.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistroELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRegistroELDDto> getRecords() {
        return records;
    }

    /**
     * Define el valor de la propiedad records.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistroELDDto }{@code >}
     *     
     */
    public void setRecords(JAXBElement<ArrayOfRegistroELDDto> value) {
        this.records = value;
    }

    /**
     * Obtiene el valor de la propiedad signatures.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFirmaELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfFirmaELDDto> getSignatures() {
        return signatures;
    }

    /**
     * Define el valor de la propiedad signatures.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFirmaELDDto }{@code >}
     *     
     */
    public void setSignatures(JAXBElement<ArrayOfFirmaELDDto> value) {
        this.signatures = value;
    }

    /**
     * Obtiene el valor de la propiedad vehicles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfVehiculoELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfVehiculoELDDto> getVehicles() {
        return vehicles;
    }

    /**
     * Define el valor de la propiedad vehicles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfVehiculoELDDto }{@code >}
     *     
     */
    public void setVehicles(JAXBElement<ArrayOfVehiculoELDDto> value) {
        this.vehicles = value;
    }

    /**
     * Obtiene el valor de la propiedad webUserId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getWebUserId() {
        return webUserId;
    }

    /**
     * Define el valor de la propiedad webUserId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setWebUserId(JAXBElement<Integer> value) {
        this.webUserId = value;
    }

    /**
     * Obtiene el valor de la propiedad wsErrorsFMCSA.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfTupleOfstringstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfTupleOfstringstring> getWsErrorsFMCSA() {
        return wsErrorsFMCSA;
    }

    /**
     * Define el valor de la propiedad wsErrorsFMCSA.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfTupleOfstringstring }{@code >}
     *     
     */
    public void setWsErrorsFMCSA(JAXBElement<ArrayOfTupleOfstringstring> value) {
        this.wsErrorsFMCSA = value;
    }

    /**
     * Obtiene el valor de la propiedad errorCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Define el valor de la propiedad errorCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

}
