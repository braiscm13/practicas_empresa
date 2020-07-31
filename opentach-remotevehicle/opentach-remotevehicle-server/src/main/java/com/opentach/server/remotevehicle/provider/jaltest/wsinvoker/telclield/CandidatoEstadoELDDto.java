
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CandidatoEstadoELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CandidatoEstadoELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DrivingState" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IdWs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LogIdCandidate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Origin" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SpecialMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UnidentifiedDrivingEvents" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfEventoELDDto" minOccurs="0"/&gt;
 *         &lt;element name="UnidentifiedDrivingId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CandidatoEstadoELDDto", propOrder = {
    "drivingState",
    "endDate",
    "idWs",
    "latitude",
    "location",
    "logIdCandidate",
    "longitude",
    "notes",
    "origin",
    "specialMode",
    "startDate",
    "unidentifiedDrivingEvents",
    "unidentifiedDrivingId"
})
public class CandidatoEstadoELDDto {

    @XmlElement(name = "DrivingState")
    protected Integer drivingState;
    @XmlElementRef(name = "EndDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endDate;
    @XmlElement(name = "IdWs")
    protected Integer idWs;
    @XmlElementRef(name = "Latitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> latitude;
    @XmlElementRef(name = "Location", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> location;
    @XmlElement(name = "LogIdCandidate")
    protected Integer logIdCandidate;
    @XmlElementRef(name = "Longitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> longitude;
    @XmlElementRef(name = "Notes", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> notes;
    @XmlElement(name = "Origin")
    protected Integer origin;
    @XmlElementRef(name = "SpecialMode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> specialMode;
    @XmlElementRef(name = "StartDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startDate;
    @XmlElementRef(name = "UnidentifiedDrivingEvents", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfEventoELDDto> unidentifiedDrivingEvents;
    @XmlElementRef(name = "UnidentifiedDrivingId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> unidentifiedDrivingId;

    /**
     * Obtiene el valor de la propiedad drivingState.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDrivingState() {
        return drivingState;
    }

    /**
     * Define el valor de la propiedad drivingState.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDrivingState(Integer value) {
        this.drivingState = value;
    }

    /**
     * Obtiene el valor de la propiedad endDate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndDate() {
        return endDate;
    }

    /**
     * Define el valor de la propiedad endDate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndDate(JAXBElement<String> value) {
        this.endDate = value;
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
     * Obtiene el valor de la propiedad latitude.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLatitude() {
        return latitude;
    }

    /**
     * Define el valor de la propiedad latitude.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLatitude(JAXBElement<String> value) {
        this.latitude = value;
    }

    /**
     * Obtiene el valor de la propiedad location.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocation() {
        return location;
    }

    /**
     * Define el valor de la propiedad location.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocation(JAXBElement<String> value) {
        this.location = value;
    }

    /**
     * Obtiene el valor de la propiedad logIdCandidate.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLogIdCandidate() {
        return logIdCandidate;
    }

    /**
     * Define el valor de la propiedad logIdCandidate.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLogIdCandidate(Integer value) {
        this.logIdCandidate = value;
    }

    /**
     * Obtiene el valor de la propiedad longitude.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLongitude() {
        return longitude;
    }

    /**
     * Define el valor de la propiedad longitude.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLongitude(JAXBElement<String> value) {
        this.longitude = value;
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
     * Obtiene el valor de la propiedad origin.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOrigin() {
        return origin;
    }

    /**
     * Define el valor de la propiedad origin.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOrigin(Integer value) {
        this.origin = value;
    }

    /**
     * Obtiene el valor de la propiedad specialMode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSpecialMode() {
        return specialMode;
    }

    /**
     * Define el valor de la propiedad specialMode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSpecialMode(JAXBElement<String> value) {
        this.specialMode = value;
    }

    /**
     * Obtiene el valor de la propiedad startDate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartDate() {
        return startDate;
    }

    /**
     * Define el valor de la propiedad startDate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartDate(JAXBElement<String> value) {
        this.startDate = value;
    }

    /**
     * Obtiene el valor de la propiedad unidentifiedDrivingEvents.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfEventoELDDto> getUnidentifiedDrivingEvents() {
        return unidentifiedDrivingEvents;
    }

    /**
     * Define el valor de la propiedad unidentifiedDrivingEvents.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}
     *     
     */
    public void setUnidentifiedDrivingEvents(JAXBElement<ArrayOfEventoELDDto> value) {
        this.unidentifiedDrivingEvents = value;
    }

    /**
     * Obtiene el valor de la propiedad unidentifiedDrivingId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getUnidentifiedDrivingId() {
        return unidentifiedDrivingId;
    }

    /**
     * Define el valor de la propiedad unidentifiedDrivingId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setUnidentifiedDrivingId(JAXBElement<Integer> value) {
        this.unidentifiedDrivingId = value;
    }

}
