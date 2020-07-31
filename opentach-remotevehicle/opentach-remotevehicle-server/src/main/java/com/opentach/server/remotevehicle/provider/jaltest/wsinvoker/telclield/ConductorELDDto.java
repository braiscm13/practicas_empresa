
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ConductorELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConductorELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AllowPC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="AllowYM" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="CarrierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DriverFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DriverLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EldId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HomeTerminalZipCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LicenseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LicenseState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LogIncrement" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OfficeAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OfficeCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OfficeState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OfficeZip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ShiftStartTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TimeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idDriver" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConductorELDDto", propOrder = {
    "allowPC",
    "allowYM",
    "carrierName",
    "driverFirstName",
    "driverLastName",
    "eldId",
    "homeTerminalAddress",
    "homeTerminalCity",
    "homeTerminalState",
    "homeTerminalZipCode",
    "licenseNumber",
    "licenseState",
    "logIncrement",
    "officeAddress",
    "officeCity",
    "officeState",
    "officeZip",
    "shiftStartTime",
    "timeZone",
    "idDriver"
})
public class ConductorELDDto {

    @XmlElement(name = "AllowPC")
    protected Boolean allowPC;
    @XmlElement(name = "AllowYM")
    protected Boolean allowYM;
    @XmlElementRef(name = "CarrierName", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> carrierName;
    @XmlElementRef(name = "DriverFirstName", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> driverFirstName;
    @XmlElementRef(name = "DriverLastName", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> driverLastName;
    @XmlElementRef(name = "EldId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> eldId;
    @XmlElementRef(name = "HomeTerminalAddress", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalAddress;
    @XmlElementRef(name = "HomeTerminalCity", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalCity;
    @XmlElementRef(name = "HomeTerminalState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalState;
    @XmlElementRef(name = "HomeTerminalZipCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> homeTerminalZipCode;
    @XmlElementRef(name = "LicenseNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> licenseNumber;
    @XmlElementRef(name = "LicenseState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> licenseState;
    @XmlElementRef(name = "LogIncrement", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> logIncrement;
    @XmlElementRef(name = "OfficeAddress", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> officeAddress;
    @XmlElementRef(name = "OfficeCity", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> officeCity;
    @XmlElementRef(name = "OfficeState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> officeState;
    @XmlElementRef(name = "OfficeZip", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> officeZip;
    @XmlElementRef(name = "ShiftStartTime", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> shiftStartTime;
    @XmlElementRef(name = "TimeZone", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> timeZone;
    protected Integer idDriver;

    /**
     * Obtiene el valor de la propiedad allowPC.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowPC() {
        return allowPC;
    }

    /**
     * Define el valor de la propiedad allowPC.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowPC(Boolean value) {
        this.allowPC = value;
    }

    /**
     * Obtiene el valor de la propiedad allowYM.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowYM() {
        return allowYM;
    }

    /**
     * Define el valor de la propiedad allowYM.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowYM(Boolean value) {
        this.allowYM = value;
    }

    /**
     * Obtiene el valor de la propiedad carrierName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCarrierName() {
        return carrierName;
    }

    /**
     * Define el valor de la propiedad carrierName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCarrierName(JAXBElement<String> value) {
        this.carrierName = value;
    }

    /**
     * Obtiene el valor de la propiedad driverFirstName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDriverFirstName() {
        return driverFirstName;
    }

    /**
     * Define el valor de la propiedad driverFirstName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDriverFirstName(JAXBElement<String> value) {
        this.driverFirstName = value;
    }

    /**
     * Obtiene el valor de la propiedad driverLastName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDriverLastName() {
        return driverLastName;
    }

    /**
     * Define el valor de la propiedad driverLastName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDriverLastName(JAXBElement<String> value) {
        this.driverLastName = value;
    }

    /**
     * Obtiene el valor de la propiedad eldId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEldId() {
        return eldId;
    }

    /**
     * Define el valor de la propiedad eldId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEldId(JAXBElement<String> value) {
        this.eldId = value;
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
     * Obtiene el valor de la propiedad licenseNumber.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Define el valor de la propiedad licenseNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLicenseNumber(JAXBElement<String> value) {
        this.licenseNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad licenseState.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLicenseState() {
        return licenseState;
    }

    /**
     * Define el valor de la propiedad licenseState.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLicenseState(JAXBElement<String> value) {
        this.licenseState = value;
    }

    /**
     * Obtiene el valor de la propiedad logIncrement.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getLogIncrement() {
        return logIncrement;
    }

    /**
     * Define el valor de la propiedad logIncrement.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setLogIncrement(JAXBElement<Integer> value) {
        this.logIncrement = value;
    }

    /**
     * Obtiene el valor de la propiedad officeAddress.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOfficeAddress() {
        return officeAddress;
    }

    /**
     * Define el valor de la propiedad officeAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOfficeAddress(JAXBElement<String> value) {
        this.officeAddress = value;
    }

    /**
     * Obtiene el valor de la propiedad officeCity.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOfficeCity() {
        return officeCity;
    }

    /**
     * Define el valor de la propiedad officeCity.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOfficeCity(JAXBElement<String> value) {
        this.officeCity = value;
    }

    /**
     * Obtiene el valor de la propiedad officeState.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOfficeState() {
        return officeState;
    }

    /**
     * Define el valor de la propiedad officeState.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOfficeState(JAXBElement<String> value) {
        this.officeState = value;
    }

    /**
     * Obtiene el valor de la propiedad officeZip.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOfficeZip() {
        return officeZip;
    }

    /**
     * Define el valor de la propiedad officeZip.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOfficeZip(JAXBElement<String> value) {
        this.officeZip = value;
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
     * Obtiene el valor de la propiedad idDriver.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdDriver() {
        return idDriver;
    }

    /**
     * Define el valor de la propiedad idDriver.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdDriver(Integer value) {
        this.idDriver = value;
    }

}
