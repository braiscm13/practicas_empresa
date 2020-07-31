
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld.ELDEventOrigin;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld.ELDEventStatus;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld.ELDEventType;


/**
 * <p>Clase Java para EventoELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EventoELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AccumulatedMiles" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CmvPowerUnitNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CmvVin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DiagnosticCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DistanceLastValidCo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="DriverId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ElapsedEngineHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EventCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="EventDateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EventOrigin" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD}ELDEventOrigin" minOccurs="0"/&gt;
 *         &lt;element name="EventSequenceIdApp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EventSequenceJTCU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EventStatus" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD}ELDEventStatus" minOccurs="0"/&gt;
 *         &lt;element name="EventType" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD}ELDEventType" minOccurs="0"/&gt;
 *         &lt;element name="IdWs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="JtcuSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="KeyApp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LocationDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MalfunctionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MalfunctionDiagnosticCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SpecialMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TimeZoneOffset" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalEngineHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalMiles" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventoELDDto", propOrder = {
    "accumulatedMiles",
    "cmvPowerUnitNumber",
    "cmvVin",
    "comment",
    "diagnosticCode",
    "distanceLastValidCo",
    "driverId",
    "elapsedEngineHours",
    "eventCode",
    "eventDateTime",
    "eventOrigin",
    "eventSequenceIdApp",
    "eventSequenceJTCU",
    "eventStatus",
    "eventType",
    "idWs",
    "jtcuSerialNumber",
    "keyApp",
    "latitude",
    "locationDescription",
    "longitude",
    "malfunctionCode",
    "malfunctionDiagnosticCode",
    "specialMode",
    "timeZoneOffset",
    "totalEngineHours",
    "totalMiles"
})
public class EventoELDDto {

    @XmlElementRef(name = "AccumulatedMiles", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> accumulatedMiles;
    @XmlElementRef(name = "CmvPowerUnitNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cmvPowerUnitNumber;
    @XmlElementRef(name = "CmvVin", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cmvVin;
    @XmlElementRef(name = "Comment", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> comment;
    @XmlElementRef(name = "DiagnosticCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> diagnosticCode;
    @XmlElementRef(name = "DistanceLastValidCo", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> distanceLastValidCo;
    @XmlElementRef(name = "DriverId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> driverId;
    @XmlElementRef(name = "ElapsedEngineHours", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> elapsedEngineHours;
    @XmlElement(name = "EventCode")
    protected Integer eventCode;
    @XmlElementRef(name = "EventDateTime", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> eventDateTime;
    @XmlElement(name = "EventOrigin")
    @XmlSchemaType(name = "string")
    protected ELDEventOrigin eventOrigin;
    @XmlElementRef(name = "EventSequenceIdApp", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> eventSequenceIdApp;
    @XmlElementRef(name = "EventSequenceJTCU", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> eventSequenceJTCU;
    @XmlElement(name = "EventStatus")
    @XmlSchemaType(name = "string")
    protected ELDEventStatus eventStatus;
    @XmlElement(name = "EventType")
    @XmlSchemaType(name = "string")
    protected ELDEventType eventType;
    @XmlElement(name = "IdWs")
    protected Integer idWs;
    @XmlElementRef(name = "JtcuSerialNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jtcuSerialNumber;
    @XmlElementRef(name = "KeyApp", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> keyApp;
    @XmlElementRef(name = "Latitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> latitude;
    @XmlElementRef(name = "LocationDescription", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> locationDescription;
    @XmlElementRef(name = "Longitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> longitude;
    @XmlElementRef(name = "MalfunctionCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> malfunctionCode;
    @XmlElementRef(name = "MalfunctionDiagnosticCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> malfunctionDiagnosticCode;
    @XmlElementRef(name = "SpecialMode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> specialMode;
    @XmlElementRef(name = "TimeZoneOffset", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> timeZoneOffset;
    @XmlElementRef(name = "TotalEngineHours", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> totalEngineHours;
    @XmlElementRef(name = "TotalMiles", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> totalMiles;

    /**
     * Obtiene el valor de la propiedad accumulatedMiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAccumulatedMiles() {
        return accumulatedMiles;
    }

    /**
     * Define el valor de la propiedad accumulatedMiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAccumulatedMiles(JAXBElement<String> value) {
        this.accumulatedMiles = value;
    }

    /**
     * Obtiene el valor de la propiedad cmvPowerUnitNumber.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCmvPowerUnitNumber() {
        return cmvPowerUnitNumber;
    }

    /**
     * Define el valor de la propiedad cmvPowerUnitNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCmvPowerUnitNumber(JAXBElement<String> value) {
        this.cmvPowerUnitNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad cmvVin.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCmvVin() {
        return cmvVin;
    }

    /**
     * Define el valor de la propiedad cmvVin.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCmvVin(JAXBElement<String> value) {
        this.cmvVin = value;
    }

    /**
     * Obtiene el valor de la propiedad comment.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getComment() {
        return comment;
    }

    /**
     * Define el valor de la propiedad comment.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setComment(JAXBElement<String> value) {
        this.comment = value;
    }

    /**
     * Obtiene el valor de la propiedad diagnosticCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDiagnosticCode() {
        return diagnosticCode;
    }

    /**
     * Define el valor de la propiedad diagnosticCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDiagnosticCode(JAXBElement<String> value) {
        this.diagnosticCode = value;
    }

    /**
     * Obtiene el valor de la propiedad distanceLastValidCo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getDistanceLastValidCo() {
        return distanceLastValidCo;
    }

    /**
     * Define el valor de la propiedad distanceLastValidCo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setDistanceLastValidCo(JAXBElement<Integer> value) {
        this.distanceLastValidCo = value;
    }

    /**
     * Obtiene el valor de la propiedad driverId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getDriverId() {
        return driverId;
    }

    /**
     * Define el valor de la propiedad driverId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setDriverId(JAXBElement<Integer> value) {
        this.driverId = value;
    }

    /**
     * Obtiene el valor de la propiedad elapsedEngineHours.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getElapsedEngineHours() {
        return elapsedEngineHours;
    }

    /**
     * Define el valor de la propiedad elapsedEngineHours.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setElapsedEngineHours(JAXBElement<String> value) {
        this.elapsedEngineHours = value;
    }

    /**
     * Obtiene el valor de la propiedad eventCode.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEventCode() {
        return eventCode;
    }

    /**
     * Define el valor de la propiedad eventCode.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEventCode(Integer value) {
        this.eventCode = value;
    }

    /**
     * Obtiene el valor de la propiedad eventDateTime.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Define el valor de la propiedad eventDateTime.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEventDateTime(JAXBElement<String> value) {
        this.eventDateTime = value;
    }

    /**
     * Obtiene el valor de la propiedad eventOrigin.
     * 
     * @return
     *     possible object is
     *     {@link ELDEventOrigin }
     *     
     */
    public ELDEventOrigin getEventOrigin() {
        return eventOrigin;
    }

    /**
     * Define el valor de la propiedad eventOrigin.
     * 
     * @param value
     *     allowed object is
     *     {@link ELDEventOrigin }
     *     
     */
    public void setEventOrigin(ELDEventOrigin value) {
        this.eventOrigin = value;
    }

    /**
     * Obtiene el valor de la propiedad eventSequenceIdApp.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEventSequenceIdApp() {
        return eventSequenceIdApp;
    }

    /**
     * Define el valor de la propiedad eventSequenceIdApp.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEventSequenceIdApp(JAXBElement<String> value) {
        this.eventSequenceIdApp = value;
    }

    /**
     * Obtiene el valor de la propiedad eventSequenceJTCU.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEventSequenceJTCU() {
        return eventSequenceJTCU;
    }

    /**
     * Define el valor de la propiedad eventSequenceJTCU.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEventSequenceJTCU(JAXBElement<String> value) {
        this.eventSequenceJTCU = value;
    }

    /**
     * Obtiene el valor de la propiedad eventStatus.
     * 
     * @return
     *     possible object is
     *     {@link ELDEventStatus }
     *     
     */
    public ELDEventStatus getEventStatus() {
        return eventStatus;
    }

    /**
     * Define el valor de la propiedad eventStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link ELDEventStatus }
     *     
     */
    public void setEventStatus(ELDEventStatus value) {
        this.eventStatus = value;
    }

    /**
     * Obtiene el valor de la propiedad eventType.
     * 
     * @return
     *     possible object is
     *     {@link ELDEventType }
     *     
     */
    public ELDEventType getEventType() {
        return eventType;
    }

    /**
     * Define el valor de la propiedad eventType.
     * 
     * @param value
     *     allowed object is
     *     {@link ELDEventType }
     *     
     */
    public void setEventType(ELDEventType value) {
        this.eventType = value;
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
     * Obtiene el valor de la propiedad keyApp.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getKeyApp() {
        return keyApp;
    }

    /**
     * Define el valor de la propiedad keyApp.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setKeyApp(JAXBElement<String> value) {
        this.keyApp = value;
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
     * Obtiene el valor de la propiedad locationDescription.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocationDescription() {
        return locationDescription;
    }

    /**
     * Define el valor de la propiedad locationDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocationDescription(JAXBElement<String> value) {
        this.locationDescription = value;
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
     * Obtiene el valor de la propiedad malfunctionCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMalfunctionCode() {
        return malfunctionCode;
    }

    /**
     * Define el valor de la propiedad malfunctionCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMalfunctionCode(JAXBElement<String> value) {
        this.malfunctionCode = value;
    }

    /**
     * Obtiene el valor de la propiedad malfunctionDiagnosticCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMalfunctionDiagnosticCode() {
        return malfunctionDiagnosticCode;
    }

    /**
     * Define el valor de la propiedad malfunctionDiagnosticCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMalfunctionDiagnosticCode(JAXBElement<String> value) {
        this.malfunctionDiagnosticCode = value;
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
     * Obtiene el valor de la propiedad timeZoneOffset.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTimeZoneOffset() {
        return timeZoneOffset;
    }

    /**
     * Define el valor de la propiedad timeZoneOffset.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTimeZoneOffset(JAXBElement<String> value) {
        this.timeZoneOffset = value;
    }

    /**
     * Obtiene el valor de la propiedad totalEngineHours.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTotalEngineHours() {
        return totalEngineHours;
    }

    /**
     * Define el valor de la propiedad totalEngineHours.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTotalEngineHours(JAXBElement<String> value) {
        this.totalEngineHours = value;
    }

    /**
     * Obtiene el valor de la propiedad totalMiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTotalMiles() {
        return totalMiles;
    }

    /**
     * Define el valor de la propiedad totalMiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTotalMiles(JAXBElement<String> value) {
        this.totalMiles = value;
    }

}
