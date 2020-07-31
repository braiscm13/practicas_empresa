
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld.UnidentifiedDrivingState;


/**
 * <p>Clase Java para ConduccionNoIdentificadaELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConduccionNoIdentificadaELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CandidateDriverId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CandidateDriverName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CandidateDriverSurnames" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndAccumulatedMiles" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndComment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndDiagnosticCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndDistanceLastValidCo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndElapsedEngineHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="EndLatitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndLocationDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndLongitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EndMalfunctionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Events" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfEventoELDDto" minOccurs="0"/&gt;
 *         &lt;element name="IdWs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="JtcuSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LastModificationDriverId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="StartAccumulatedMiles" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartComment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartDiagnosticCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartDistanceLastValidCo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartElapsedEngineHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="StartLatitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartLocationDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartLongitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StartMalfunctionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD}UnidentifiedDrivingState" minOccurs="0"/&gt;
 *         &lt;element name="VehicleCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="VehicleNumberPlate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConduccionNoIdentificadaELDDto", propOrder = {
    "candidateDriverId",
    "candidateDriverName",
    "candidateDriverSurnames",
    "endAccumulatedMiles",
    "endComment",
    "endDate",
    "endDiagnosticCode",
    "endDistanceLastValidCo",
    "endElapsedEngineHours",
    "endInterval",
    "endLatitude",
    "endLocationDescription",
    "endLongitude",
    "endMalfunctionCode",
    "events",
    "idWs",
    "jtcuSerialNumber",
    "lastModificationDriverId",
    "startAccumulatedMiles",
    "startComment",
    "startDate",
    "startDiagnosticCode",
    "startDistanceLastValidCo",
    "startElapsedEngineHours",
    "startInterval",
    "startLatitude",
    "startLocationDescription",
    "startLongitude",
    "startMalfunctionCode",
    "state",
    "vehicleCode",
    "vehicleNumberPlate"
})
public class ConduccionNoIdentificadaELDDto {

    @XmlElementRef(name = "CandidateDriverId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> candidateDriverId;
    @XmlElementRef(name = "CandidateDriverName", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> candidateDriverName;
    @XmlElementRef(name = "CandidateDriverSurnames", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> candidateDriverSurnames;
    @XmlElementRef(name = "EndAccumulatedMiles", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endAccumulatedMiles;
    @XmlElementRef(name = "EndComment", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endComment;
    @XmlElementRef(name = "EndDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endDate;
    @XmlElementRef(name = "EndDiagnosticCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endDiagnosticCode;
    @XmlElementRef(name = "EndDistanceLastValidCo", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endDistanceLastValidCo;
    @XmlElementRef(name = "EndElapsedEngineHours", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endElapsedEngineHours;
    @XmlElement(name = "EndInterval")
    protected Integer endInterval;
    @XmlElementRef(name = "EndLatitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endLatitude;
    @XmlElementRef(name = "EndLocationDescription", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endLocationDescription;
    @XmlElementRef(name = "EndLongitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endLongitude;
    @XmlElementRef(name = "EndMalfunctionCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> endMalfunctionCode;
    @XmlElementRef(name = "Events", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfEventoELDDto> events;
    @XmlElement(name = "IdWs")
    protected Integer idWs;
    @XmlElementRef(name = "JtcuSerialNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jtcuSerialNumber;
    @XmlElementRef(name = "LastModificationDriverId", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> lastModificationDriverId;
    @XmlElementRef(name = "StartAccumulatedMiles", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startAccumulatedMiles;
    @XmlElementRef(name = "StartComment", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startComment;
    @XmlElementRef(name = "StartDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startDate;
    @XmlElementRef(name = "StartDiagnosticCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startDiagnosticCode;
    @XmlElementRef(name = "StartDistanceLastValidCo", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startDistanceLastValidCo;
    @XmlElementRef(name = "StartElapsedEngineHours", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startElapsedEngineHours;
    @XmlElement(name = "StartInterval")
    protected Integer startInterval;
    @XmlElementRef(name = "StartLatitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startLatitude;
    @XmlElementRef(name = "StartLocationDescription", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startLocationDescription;
    @XmlElementRef(name = "StartLongitude", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startLongitude;
    @XmlElementRef(name = "StartMalfunctionCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> startMalfunctionCode;
    @XmlElement(name = "State")
    @XmlSchemaType(name = "string")
    protected UnidentifiedDrivingState state;
    @XmlElementRef(name = "VehicleCode", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> vehicleCode;
    @XmlElementRef(name = "VehicleNumberPlate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> vehicleNumberPlate;

    /**
     * Obtiene el valor de la propiedad candidateDriverId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getCandidateDriverId() {
        return candidateDriverId;
    }

    /**
     * Define el valor de la propiedad candidateDriverId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setCandidateDriverId(JAXBElement<Integer> value) {
        this.candidateDriverId = value;
    }

    /**
     * Obtiene el valor de la propiedad candidateDriverName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCandidateDriverName() {
        return candidateDriverName;
    }

    /**
     * Define el valor de la propiedad candidateDriverName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCandidateDriverName(JAXBElement<String> value) {
        this.candidateDriverName = value;
    }

    /**
     * Obtiene el valor de la propiedad candidateDriverSurnames.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCandidateDriverSurnames() {
        return candidateDriverSurnames;
    }

    /**
     * Define el valor de la propiedad candidateDriverSurnames.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCandidateDriverSurnames(JAXBElement<String> value) {
        this.candidateDriverSurnames = value;
    }

    /**
     * Obtiene el valor de la propiedad endAccumulatedMiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndAccumulatedMiles() {
        return endAccumulatedMiles;
    }

    /**
     * Define el valor de la propiedad endAccumulatedMiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndAccumulatedMiles(JAXBElement<String> value) {
        this.endAccumulatedMiles = value;
    }

    /**
     * Obtiene el valor de la propiedad endComment.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndComment() {
        return endComment;
    }

    /**
     * Define el valor de la propiedad endComment.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndComment(JAXBElement<String> value) {
        this.endComment = value;
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
     * Obtiene el valor de la propiedad endDiagnosticCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndDiagnosticCode() {
        return endDiagnosticCode;
    }

    /**
     * Define el valor de la propiedad endDiagnosticCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndDiagnosticCode(JAXBElement<String> value) {
        this.endDiagnosticCode = value;
    }

    /**
     * Obtiene el valor de la propiedad endDistanceLastValidCo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndDistanceLastValidCo() {
        return endDistanceLastValidCo;
    }

    /**
     * Define el valor de la propiedad endDistanceLastValidCo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndDistanceLastValidCo(JAXBElement<String> value) {
        this.endDistanceLastValidCo = value;
    }

    /**
     * Obtiene el valor de la propiedad endElapsedEngineHours.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndElapsedEngineHours() {
        return endElapsedEngineHours;
    }

    /**
     * Define el valor de la propiedad endElapsedEngineHours.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndElapsedEngineHours(JAXBElement<String> value) {
        this.endElapsedEngineHours = value;
    }

    /**
     * Obtiene el valor de la propiedad endInterval.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEndInterval() {
        return endInterval;
    }

    /**
     * Define el valor de la propiedad endInterval.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEndInterval(Integer value) {
        this.endInterval = value;
    }

    /**
     * Obtiene el valor de la propiedad endLatitude.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndLatitude() {
        return endLatitude;
    }

    /**
     * Define el valor de la propiedad endLatitude.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndLatitude(JAXBElement<String> value) {
        this.endLatitude = value;
    }

    /**
     * Obtiene el valor de la propiedad endLocationDescription.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndLocationDescription() {
        return endLocationDescription;
    }

    /**
     * Define el valor de la propiedad endLocationDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndLocationDescription(JAXBElement<String> value) {
        this.endLocationDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad endLongitude.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndLongitude() {
        return endLongitude;
    }

    /**
     * Define el valor de la propiedad endLongitude.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndLongitude(JAXBElement<String> value) {
        this.endLongitude = value;
    }

    /**
     * Obtiene el valor de la propiedad endMalfunctionCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEndMalfunctionCode() {
        return endMalfunctionCode;
    }

    /**
     * Define el valor de la propiedad endMalfunctionCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEndMalfunctionCode(JAXBElement<String> value) {
        this.endMalfunctionCode = value;
    }

    /**
     * Obtiene el valor de la propiedad events.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfEventoELDDto> getEvents() {
        return events;
    }

    /**
     * Define el valor de la propiedad events.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEventoELDDto }{@code >}
     *     
     */
    public void setEvents(JAXBElement<ArrayOfEventoELDDto> value) {
        this.events = value;
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
     * Obtiene el valor de la propiedad lastModificationDriverId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getLastModificationDriverId() {
        return lastModificationDriverId;
    }

    /**
     * Define el valor de la propiedad lastModificationDriverId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setLastModificationDriverId(JAXBElement<Integer> value) {
        this.lastModificationDriverId = value;
    }

    /**
     * Obtiene el valor de la propiedad startAccumulatedMiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartAccumulatedMiles() {
        return startAccumulatedMiles;
    }

    /**
     * Define el valor de la propiedad startAccumulatedMiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartAccumulatedMiles(JAXBElement<String> value) {
        this.startAccumulatedMiles = value;
    }

    /**
     * Obtiene el valor de la propiedad startComment.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartComment() {
        return startComment;
    }

    /**
     * Define el valor de la propiedad startComment.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartComment(JAXBElement<String> value) {
        this.startComment = value;
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
     * Obtiene el valor de la propiedad startDiagnosticCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartDiagnosticCode() {
        return startDiagnosticCode;
    }

    /**
     * Define el valor de la propiedad startDiagnosticCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartDiagnosticCode(JAXBElement<String> value) {
        this.startDiagnosticCode = value;
    }

    /**
     * Obtiene el valor de la propiedad startDistanceLastValidCo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartDistanceLastValidCo() {
        return startDistanceLastValidCo;
    }

    /**
     * Define el valor de la propiedad startDistanceLastValidCo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartDistanceLastValidCo(JAXBElement<String> value) {
        this.startDistanceLastValidCo = value;
    }

    /**
     * Obtiene el valor de la propiedad startElapsedEngineHours.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartElapsedEngineHours() {
        return startElapsedEngineHours;
    }

    /**
     * Define el valor de la propiedad startElapsedEngineHours.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartElapsedEngineHours(JAXBElement<String> value) {
        this.startElapsedEngineHours = value;
    }

    /**
     * Obtiene el valor de la propiedad startInterval.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStartInterval() {
        return startInterval;
    }

    /**
     * Define el valor de la propiedad startInterval.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStartInterval(Integer value) {
        this.startInterval = value;
    }

    /**
     * Obtiene el valor de la propiedad startLatitude.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartLatitude() {
        return startLatitude;
    }

    /**
     * Define el valor de la propiedad startLatitude.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartLatitude(JAXBElement<String> value) {
        this.startLatitude = value;
    }

    /**
     * Obtiene el valor de la propiedad startLocationDescription.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartLocationDescription() {
        return startLocationDescription;
    }

    /**
     * Define el valor de la propiedad startLocationDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartLocationDescription(JAXBElement<String> value) {
        this.startLocationDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad startLongitude.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartLongitude() {
        return startLongitude;
    }

    /**
     * Define el valor de la propiedad startLongitude.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartLongitude(JAXBElement<String> value) {
        this.startLongitude = value;
    }

    /**
     * Obtiene el valor de la propiedad startMalfunctionCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStartMalfunctionCode() {
        return startMalfunctionCode;
    }

    /**
     * Define el valor de la propiedad startMalfunctionCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStartMalfunctionCode(JAXBElement<String> value) {
        this.startMalfunctionCode = value;
    }

    /**
     * Obtiene el valor de la propiedad state.
     * 
     * @return
     *     possible object is
     *     {@link UnidentifiedDrivingState }
     *     
     */
    public UnidentifiedDrivingState getState() {
        return state;
    }

    /**
     * Define el valor de la propiedad state.
     * 
     * @param value
     *     allowed object is
     *     {@link UnidentifiedDrivingState }
     *     
     */
    public void setState(UnidentifiedDrivingState value) {
        this.state = value;
    }

    /**
     * Obtiene el valor de la propiedad vehicleCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getVehicleCode() {
        return vehicleCode;
    }

    /**
     * Define el valor de la propiedad vehicleCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setVehicleCode(JAXBElement<Integer> value) {
        this.vehicleCode = value;
    }

    /**
     * Obtiene el valor de la propiedad vehicleNumberPlate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVehicleNumberPlate() {
        return vehicleNumberPlate;
    }

    /**
     * Define el valor de la propiedad vehicleNumberPlate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVehicleNumberPlate(JAXBElement<String> value) {
        this.vehicleNumberPlate = value;
    }

}
