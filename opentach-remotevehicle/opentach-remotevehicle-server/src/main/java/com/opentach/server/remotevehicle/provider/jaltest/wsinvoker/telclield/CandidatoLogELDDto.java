
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CandidatoLogELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CandidatoLogELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CandidatesState" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ArrayOfCandidatoEstadoELDDto" minOccurs="0"/&gt;
 *         &lt;element name="IdWs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="LogDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LogId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="RequestDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RequestState" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="RequestUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ResolutionDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CandidatoLogELDDto", propOrder = {
    "candidatesState",
    "idWs",
    "logDate",
    "logId",
    "requestDate",
    "requestState",
    "requestUserName",
    "resolutionDate"
})
public class CandidatoLogELDDto {

    @XmlElementRef(name = "CandidatesState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfCandidatoEstadoELDDto> candidatesState;
    @XmlElement(name = "IdWs")
    protected Integer idWs;
    @XmlElementRef(name = "LogDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> logDate;
    @XmlElement(name = "LogId")
    protected Integer logId;
    @XmlElementRef(name = "RequestDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> requestDate;
    @XmlElement(name = "RequestState")
    protected Integer requestState;
    @XmlElementRef(name = "RequestUserName", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> requestUserName;
    @XmlElementRef(name = "ResolutionDate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> resolutionDate;

    /**
     * Obtiene el valor de la propiedad candidatesState.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCandidatoEstadoELDDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfCandidatoEstadoELDDto> getCandidatesState() {
        return candidatesState;
    }

    /**
     * Define el valor de la propiedad candidatesState.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCandidatoEstadoELDDto }{@code >}
     *     
     */
    public void setCandidatesState(JAXBElement<ArrayOfCandidatoEstadoELDDto> value) {
        this.candidatesState = value;
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
     * Obtiene el valor de la propiedad logDate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLogDate() {
        return logDate;
    }

    /**
     * Define el valor de la propiedad logDate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLogDate(JAXBElement<String> value) {
        this.logDate = value;
    }

    /**
     * Obtiene el valor de la propiedad logId.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLogId() {
        return logId;
    }

    /**
     * Define el valor de la propiedad logId.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLogId(Integer value) {
        this.logId = value;
    }

    /**
     * Obtiene el valor de la propiedad requestDate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestDate() {
        return requestDate;
    }

    /**
     * Define el valor de la propiedad requestDate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestDate(JAXBElement<String> value) {
        this.requestDate = value;
    }

    /**
     * Obtiene el valor de la propiedad requestState.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRequestState() {
        return requestState;
    }

    /**
     * Define el valor de la propiedad requestState.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRequestState(Integer value) {
        this.requestState = value;
    }

    /**
     * Obtiene el valor de la propiedad requestUserName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestUserName() {
        return requestUserName;
    }

    /**
     * Define el valor de la propiedad requestUserName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestUserName(JAXBElement<String> value) {
        this.requestUserName = value;
    }

    /**
     * Obtiene el valor de la propiedad resolutionDate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResolutionDate() {
        return resolutionDate;
    }

    /**
     * Define el valor de la propiedad resolutionDate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResolutionDate(JAXBElement<String> value) {
        this.resolutionDate = value;
    }

}
