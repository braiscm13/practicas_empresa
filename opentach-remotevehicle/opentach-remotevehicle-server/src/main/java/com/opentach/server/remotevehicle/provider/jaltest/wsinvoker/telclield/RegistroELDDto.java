
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RegistroELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DriverEvents" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfEventoELDDto" minOccurs="0"/&gt;
 *         &lt;element name="Log" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}LogELDDto" minOccurs="0"/&gt;
 *         &lt;element name="LogModificationRequests" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfCandidatoLogELDDto" minOccurs="0"/&gt;
 *         &lt;element name="UnidentifiedDrivingRequests" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfConduccionNoIdentificadaELDDto" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroELDDto", propOrder = {
    "date",
    "driverEvents",
    "log",
    "logModificationRequests",
    "unidentifiedDrivingRequests"
})
public class RegistroELDDto {

    @XmlElementRef(name = "Date", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> date;
    @XmlElementRef(name = "DriverEvents", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfEventoELDDto> driverEvents;
    @XmlElementRef(name = "Log", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<LogELDDto> log;
    @XmlElementRef(name = "LogModificationRequests", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfCandidatoLogELDDto> logModificationRequests;
    @XmlElementRef(name = "UnidentifiedDrivingRequests", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfConduccionNoIdentificadaELDDto> unidentifiedDrivingRequests;

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
     * Obtiene el valor de la propiedad driverEvents.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfEventoELDDto> getDriverEvents() {
        return driverEvents;
    }

    /**
     * Define el valor de la propiedad driverEvents.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}
     *     
     */
    public void setDriverEvents(JAXBElement<ArrayOfEventoELDDto> value) {
        this.driverEvents = value;
    }

    /**
     * Obtiene el valor de la propiedad log.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LogELDDto }{@code >}
     *     
     */
    public JAXBElement<LogELDDto> getLog() {
        return log;
    }

    /**
     * Define el valor de la propiedad log.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LogELDDto }{@code >}
     *     
     */
    public void setLog(JAXBElement<LogELDDto> value) {
        this.log = value;
    }

    /**
     * Obtiene el valor de la propiedad logModificationRequests.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCandidatoLogELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfCandidatoLogELDDto> getLogModificationRequests() {
        return logModificationRequests;
    }

    /**
     * Define el valor de la propiedad logModificationRequests.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCandidatoLogELDDto }{@code >}
     *     
     */
    public void setLogModificationRequests(JAXBElement<ArrayOfCandidatoLogELDDto> value) {
        this.logModificationRequests = value;
    }

    /**
     * Obtiene el valor de la propiedad unidentifiedDrivingRequests.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfConduccionNoIdentificadaELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfConduccionNoIdentificadaELDDto> getUnidentifiedDrivingRequests() {
        return unidentifiedDrivingRequests;
    }

    /**
     * Define el valor de la propiedad unidentifiedDrivingRequests.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfConduccionNoIdentificadaELDDto }{@code >}
     *     
     */
    public void setUnidentifiedDrivingRequests(JAXBElement<ArrayOfConduccionNoIdentificadaELDDto> value) {
        this.unidentifiedDrivingRequests = value;
    }

}
