
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para LogELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LogELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ChangeTimeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CicleActive" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}CicloELDDto" minOccurs="0"/&gt;
 *         &lt;element name="CoDriver" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Distance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DriverId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="DriverName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DriverSurnames" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="From" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalZipCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IdWs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="MainOffice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MainOfficeAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MainOfficeCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MainOfficeState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MainOfficeZipCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ShiftStartTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Shipping" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SignatureId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="States" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfEstadoELDDto" minOccurs="0"/&gt;
 *         &lt;element name="TimeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="To" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Trailers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogELDDto", propOrder = {
    "changeTimeZone",
    "cicleActive",
    "coDriver",
    "date",
    "distance",
    "driverId",
    "driverName",
    "driverSurnames",
    "from",
    "homeTerminalAddress",
    "homeTerminalCity",
    "homeTerminalState",
    "homeTerminalZipCode",
    "idWs",
    "mainOffice",
    "mainOfficeAddress",
    "mainOfficeCity",
    "mainOfficeState",
    "mainOfficeZipCode",
    "notes",
    "shiftStartTime",
    "shipping",
    "signatureId",
    "states",
    "timeZone",
    "to",
    "trailers"
})
public class LogELDDto {

    @XmlElementRef(name = "ChangeTimeZone", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> changeTimeZone;
    @XmlElementRef(name = "CicleActive", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<CicloELDDto> cicleActive;
    @XmlElementRef(name = "CoDriver", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> coDriver;
    @XmlElementRef(name = "Date", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> date;
    @XmlElementRef(name = "Distance", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> distance;
    @XmlElement(name = "DriverId")
    protected Integer driverId;
    @XmlElementRef(name = "DriverName", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> driverName;
    @XmlElementRef(name = "DriverSurnames", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> driverSurnames;
    @XmlElementRef(name = "From", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> from;
    @XmlElementRef(name = "HomeTerminalAddress", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalAddress;
    @XmlElementRef(name = "HomeTerminalCity", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalCity;
    @XmlElementRef(name = "HomeTerminalState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalState;
    @XmlElementRef(name = "HomeTerminalZipCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalZipCode;
    @XmlElement(name = "IdWs")
    protected Integer idWs;
    @XmlElementRef(name = "MainOffice", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mainOffice;
    @XmlElementRef(name = "MainOfficeAddress", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mainOfficeAddress;
    @XmlElementRef(name = "MainOfficeCity", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mainOfficeCity;
    @XmlElementRef(name = "MainOfficeState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mainOfficeState;
    @XmlElementRef(name = "MainOfficeZipCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mainOfficeZipCode;
    @XmlElementRef(name = "Notes", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> notes;
    @XmlElementRef(name = "ShiftStartTime", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> shiftStartTime;
    @XmlElementRef(name = "Shipping", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> shipping;
    @XmlElementRef(name = "SignatureId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> signatureId;
    @XmlElementRef(name = "States", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfEstadoELDDto> states;
    @XmlElementRef(name = "TimeZone", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> timeZone;
    @XmlElementRef(name = "To", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> to;
    @XmlElementRef(name = "Trailers", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> trailers;

    /**
     * Obtiene el valor de la propiedad changeTimeZone.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getChangeTimeZone() {
        return changeTimeZone;
    }

    /**
     * Define el valor de la propiedad changeTimeZone.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setChangeTimeZone(JAXBElement<String> value) {
        this.changeTimeZone = value;
    }

    /**
     * Obtiene el valor de la propiedad cicleActive.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CicloELDDto }{@code >}
     *     
     */
    public JAXBElement<CicloELDDto> getCicleActive() {
        return cicleActive;
    }

    /**
     * Define el valor de la propiedad cicleActive.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CicloELDDto }{@code >}
     *     
     */
    public void setCicleActive(JAXBElement<CicloELDDto> value) {
        this.cicleActive = value;
    }

    /**
     * Obtiene el valor de la propiedad coDriver.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCoDriver() {
        return coDriver;
    }

    /**
     * Define el valor de la propiedad coDriver.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCoDriver(JAXBElement<String> value) {
        this.coDriver = value;
    }

    /**
     * Obtiene el valor de la propiedad date.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDate() {
        return date;
    }

    /**
     * Define el valor de la propiedad date.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDate(JAXBElement<String> value) {
        this.date = value;
    }

    /**
     * Obtiene el valor de la propiedad distance.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDistance() {
        return distance;
    }

    /**
     * Define el valor de la propiedad distance.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDistance(JAXBElement<String> value) {
        this.distance = value;
    }

    /**
     * Obtiene el valor de la propiedad driverId.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDriverId() {
        return driverId;
    }

    /**
     * Define el valor de la propiedad driverId.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDriverId(Integer value) {
        this.driverId = value;
    }

    /**
     * Obtiene el valor de la propiedad driverName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDriverName() {
        return driverName;
    }

    /**
     * Define el valor de la propiedad driverName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDriverName(JAXBElement<String> value) {
        this.driverName = value;
    }

    /**
     * Obtiene el valor de la propiedad driverSurnames.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDriverSurnames() {
        return driverSurnames;
    }

    /**
     * Define el valor de la propiedad driverSurnames.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDriverSurnames(JAXBElement<String> value) {
        this.driverSurnames = value;
    }

    /**
     * Obtiene el valor de la propiedad from.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFrom() {
        return from;
    }

    /**
     * Define el valor de la propiedad from.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFrom(JAXBElement<String> value) {
        this.from = value;
    }

    /**
     * Obtiene el valor de la propiedad homeTerminalAddress.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHomeTerminalAddress() {
        return homeTerminalAddress;
    }

    /**
     * Define el valor de la propiedad homeTerminalAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHomeTerminalAddress(JAXBElement<String> value) {
        this.homeTerminalAddress = value;
    }

    /**
     * Obtiene el valor de la propiedad homeTerminalCity.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHomeTerminalCity() {
        return homeTerminalCity;
    }

    /**
     * Define el valor de la propiedad homeTerminalCity.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHomeTerminalCity(JAXBElement<String> value) {
        this.homeTerminalCity = value;
    }

    /**
     * Obtiene el valor de la propiedad homeTerminalState.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHomeTerminalState() {
        return homeTerminalState;
    }

    /**
     * Define el valor de la propiedad homeTerminalState.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHomeTerminalState(JAXBElement<String> value) {
        this.homeTerminalState = value;
    }

    /**
     * Obtiene el valor de la propiedad homeTerminalZipCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHomeTerminalZipCode() {
        return homeTerminalZipCode;
    }

    /**
     * Define el valor de la propiedad homeTerminalZipCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHomeTerminalZipCode(JAXBElement<String> value) {
        this.homeTerminalZipCode = value;
    }

    /**
     * Obtiene el valor de la propiedad idWs.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdWs() {
        return idWs;
    }

    /**
     * Define el valor de la propiedad idWs.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdWs(Integer value) {
        this.idWs = value;
    }

    /**
     * Obtiene el valor de la propiedad mainOffice.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMainOffice() {
        return mainOffice;
    }

    /**
     * Define el valor de la propiedad mainOffice.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMainOffice(JAXBElement<String> value) {
        this.mainOffice = value;
    }

    /**
     * Obtiene el valor de la propiedad mainOfficeAddress.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMainOfficeAddress() {
        return mainOfficeAddress;
    }

    /**
     * Define el valor de la propiedad mainOfficeAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMainOfficeAddress(JAXBElement<String> value) {
        this.mainOfficeAddress = value;
    }

    /**
     * Obtiene el valor de la propiedad mainOfficeCity.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMainOfficeCity() {
        return mainOfficeCity;
    }

    /**
     * Define el valor de la propiedad mainOfficeCity.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMainOfficeCity(JAXBElement<String> value) {
        this.mainOfficeCity = value;
    }

    /**
     * Obtiene el valor de la propiedad mainOfficeState.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMainOfficeState() {
        return mainOfficeState;
    }

    /**
     * Define el valor de la propiedad mainOfficeState.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMainOfficeState(JAXBElement<String> value) {
        this.mainOfficeState = value;
    }

    /**
     * Obtiene el valor de la propiedad mainOfficeZipCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMainOfficeZipCode() {
        return mainOfficeZipCode;
    }

    /**
     * Define el valor de la propiedad mainOfficeZipCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMainOfficeZipCode(JAXBElement<String> value) {
        this.mainOfficeZipCode = value;
    }

    /**
     * Obtiene el valor de la propiedad notes.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNotes() {
        return notes;
    }

    /**
     * Define el valor de la propiedad notes.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNotes(JAXBElement<String> value) {
        this.notes = value;
    }

    /**
     * Obtiene el valor de la propiedad shiftStartTime.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getShiftStartTime() {
        return shiftStartTime;
    }

    /**
     * Define el valor de la propiedad shiftStartTime.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setShiftStartTime(JAXBElement<Integer> value) {
        this.shiftStartTime = value;
    }

    /**
     * Obtiene el valor de la propiedad shipping.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getShipping() {
        return shipping;
    }

    /**
     * Define el valor de la propiedad shipping.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setShipping(JAXBElement<String> value) {
        this.shipping = value;
    }

    /**
     * Obtiene el valor de la propiedad signatureId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getSignatureId() {
        return signatureId;
    }

    /**
     * Define el valor de la propiedad signatureId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setSignatureId(JAXBElement<Integer> value) {
        this.signatureId = value;
    }

    /**
     * Obtiene el valor de la propiedad states.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEstadoELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfEstadoELDDto> getStates() {
        return states;
    }

    /**
     * Define el valor de la propiedad states.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEstadoELDDto }{@code >}
     *     
     */
    public void setStates(JAXBElement<ArrayOfEstadoELDDto> value) {
        this.states = value;
    }

    /**
     * Obtiene el valor de la propiedad timeZone.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTimeZone() {
        return timeZone;
    }

    /**
     * Define el valor de la propiedad timeZone.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTimeZone(JAXBElement<String> value) {
        this.timeZone = value;
    }

    /**
     * Obtiene el valor de la propiedad to.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTo() {
        return to;
    }

    /**
     * Define el valor de la propiedad to.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTo(JAXBElement<String> value) {
        this.to = value;
    }

    /**
     * Obtiene el valor de la propiedad trailers.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTrailers() {
        return trailers;
    }

    /**
     * Define el valor de la propiedad trailers.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTrailers(JAXBElement<String> value) {
        this.trailers = value;
    }

}
