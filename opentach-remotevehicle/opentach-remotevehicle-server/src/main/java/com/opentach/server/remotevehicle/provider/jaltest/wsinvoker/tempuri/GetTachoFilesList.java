
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CIF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numberPlate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="driverCardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="downloadStartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="downloadEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="activityBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="eventBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="detailedSpeedBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="technicalDataBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cif",
    "numberPlate",
    "driverCardNumber",
    "downloadStartDate",
    "downloadEndDate",
    "activityBlock",
    "eventBlock",
    "detailedSpeedBlock",
    "technicalDataBlock"
})
@XmlRootElement(name = "getTachoFilesList")
public class GetTachoFilesList {

    @XmlElement(name = "CIF")
    protected String cif;
    protected String numberPlate;
    protected String driverCardNumber;
    protected String downloadStartDate;
    protected String downloadEndDate;
    protected boolean activityBlock;
    protected boolean eventBlock;
    protected boolean detailedSpeedBlock;
    protected boolean technicalDataBlock;

    /**
     * Obtiene el valor de la propiedad cif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCIF() {
        return cif;
    }

    /**
     * Define el valor de la propiedad cif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCIF(String value) {
        this.cif = value;
    }

    /**
     * Obtiene el valor de la propiedad numberPlate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberPlate() {
        return numberPlate;
    }

    /**
     * Define el valor de la propiedad numberPlate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberPlate(String value) {
        this.numberPlate = value;
    }

    /**
     * Obtiene el valor de la propiedad driverCardNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverCardNumber() {
        return driverCardNumber;
    }

    /**
     * Define el valor de la propiedad driverCardNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverCardNumber(String value) {
        this.driverCardNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad downloadStartDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDownloadStartDate() {
        return downloadStartDate;
    }

    /**
     * Define el valor de la propiedad downloadStartDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDownloadStartDate(String value) {
        this.downloadStartDate = value;
    }

    /**
     * Obtiene el valor de la propiedad downloadEndDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDownloadEndDate() {
        return downloadEndDate;
    }

    /**
     * Define el valor de la propiedad downloadEndDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDownloadEndDate(String value) {
        this.downloadEndDate = value;
    }

    /**
     * Obtiene el valor de la propiedad activityBlock.
     * 
     */
    public boolean isActivityBlock() {
        return activityBlock;
    }

    /**
     * Define el valor de la propiedad activityBlock.
     * 
     */
    public void setActivityBlock(boolean value) {
        this.activityBlock = value;
    }

    /**
     * Obtiene el valor de la propiedad eventBlock.
     * 
     */
    public boolean isEventBlock() {
        return eventBlock;
    }

    /**
     * Define el valor de la propiedad eventBlock.
     * 
     */
    public void setEventBlock(boolean value) {
        this.eventBlock = value;
    }

    /**
     * Obtiene el valor de la propiedad detailedSpeedBlock.
     * 
     */
    public boolean isDetailedSpeedBlock() {
        return detailedSpeedBlock;
    }

    /**
     * Define el valor de la propiedad detailedSpeedBlock.
     * 
     */
    public void setDetailedSpeedBlock(boolean value) {
        this.detailedSpeedBlock = value;
    }

    /**
     * Obtiene el valor de la propiedad technicalDataBlock.
     * 
     */
    public boolean isTechnicalDataBlock() {
        return technicalDataBlock;
    }

    /**
     * Define el valor de la propiedad technicalDataBlock.
     * 
     */
    public void setTechnicalDataBlock(boolean value) {
        this.technicalDataBlock = value;
    }

}
