
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld.ELDDrivingState;


/**
 * <p>Clase Java para EstadoELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EstadoELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DriverState" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD}ELDDrivingState" minOccurs="0"/&gt;
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IdWs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="JtcuSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Origin" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SpecialMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EstadoELDDto", propOrder = {
    "driverState",
    "endDate",
    "idWs",
    "jtcuSerialNumber",
    "latitude",
    "location",
    "longitude",
    "notes",
    "origin",
    "specialMode",
    "startDate"
})
public class EstadoELDDto {

    @XmlElement(name = "DriverState")
    @XmlSchemaType(name = "string")
    protected ELDDrivingState driverState;
    @XmlElementRef(name = "EndDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endDate;
    @XmlElement(name = "IdWs")
    protected Integer idWs;
    @XmlElementRef(name = "JtcuSerialNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jtcuSerialNumber;
    @XmlElementRef(name = "Latitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> latitude;
    @XmlElementRef(name = "Location", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> location;
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

    /**
     * Obtiene el valor de la propiedad driverState.
     * 
     * @return
     *     possible object is
     *     {@link ELDDrivingState }
     *     
     */
    public ELDDrivingState getDriverState() {
        return driverState;
    }

    /**
     * Define el valor de la propiedad driverState.
     * 
     * @param value
     *     allowed object is
     *     {@link ELDDrivingState }
     *     
     */
    public void setDriverState(ELDDrivingState value) {
        this.driverState = value;
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
     * Obtiene el valor de la propiedad jtcuSerialNumber.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJtcuSerialNumber() {
        return jtcuSerialNumber;
    }

    /**
     * Define el valor de la propiedad jtcuSerialNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJtcuSerialNumber(JAXBElement<String> value) {
        this.jtcuSerialNumber = value;
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

}
