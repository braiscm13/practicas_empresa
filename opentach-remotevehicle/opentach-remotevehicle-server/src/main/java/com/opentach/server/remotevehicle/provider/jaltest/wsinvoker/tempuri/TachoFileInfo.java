
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para TachoFileInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TachoFileInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ExtensionData" type="{http://tempuri.org/}ExtensionDataObject" minOccurs="0"/&gt;
 *         &lt;element name="ActivityBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="DetailedSpeedBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="Driver1CardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Driver2CardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EventBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="FileContent" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *         &lt;element name="FileDownloadDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="FileEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FileStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="FileType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="NumberPlate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OverviewBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="TechnicalDataBlock" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TachoFileInfo", propOrder = {
    "extensionData",
    "activityBlock",
    "detailedSpeedBlock",
    "driver1CardNumber",
    "driver2CardNumber",
    "eventBlock",
    "fileContent",
    "fileDownloadDate",
    "fileEndDate",
    "fileName",
    "fileStartDate",
    "fileType",
    "id",
    "numberPlate",
    "overviewBlock",
    "technicalDataBlock"
})
public class TachoFileInfo {

    @XmlElement(name = "ExtensionData")
    protected ExtensionDataObject extensionData;
    @XmlElement(name = "ActivityBlock")
    protected boolean activityBlock;
    @XmlElement(name = "DetailedSpeedBlock")
    protected boolean detailedSpeedBlock;
    @XmlElement(name = "Driver1CardNumber")
    protected String driver1CardNumber;
    @XmlElement(name = "Driver2CardNumber")
    protected String driver2CardNumber;
    @XmlElement(name = "EventBlock")
    protected boolean eventBlock;
    @XmlElement(name = "FileContent")
    protected byte[] fileContent;
    @XmlElement(name = "FileDownloadDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fileDownloadDate;
    @XmlElement(name = "FileEndDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fileEndDate;
    @XmlElement(name = "FileName")
    protected String fileName;
    @XmlElement(name = "FileStartDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fileStartDate;
    @XmlElement(name = "FileType")
    protected String fileType;
    @XmlElement(name = "Id")
    protected int id;
    @XmlElement(name = "NumberPlate")
    protected String numberPlate;
    @XmlElement(name = "OverviewBlock")
    protected boolean overviewBlock;
    @XmlElement(name = "TechnicalDataBlock")
    protected boolean technicalDataBlock;

    /**
     * Obtiene el valor de la propiedad extensionData.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionDataObject }
     *     
     */
    public ExtensionDataObject getExtensionData() {
        return extensionData;
    }

    /**
     * Define el valor de la propiedad extensionData.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionDataObject }
     *     
     */
    public void setExtensionData(ExtensionDataObject value) {
        this.extensionData = value;
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
     * Obtiene el valor de la propiedad driver1CardNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriver1CardNumber() {
        return driver1CardNumber;
    }

    /**
     * Define el valor de la propiedad driver1CardNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriver1CardNumber(String value) {
        this.driver1CardNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad driver2CardNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriver2CardNumber() {
        return driver2CardNumber;
    }

    /**
     * Define el valor de la propiedad driver2CardNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriver2CardNumber(String value) {
        this.driver2CardNumber = value;
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
     * Obtiene el valor de la propiedad fileContent.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFileContent() {
        return fileContent;
    }

    /**
     * Define el valor de la propiedad fileContent.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFileContent(byte[] value) {
        this.fileContent = value;
    }

    /**
     * Obtiene el valor de la propiedad fileDownloadDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFileDownloadDate() {
        return fileDownloadDate;
    }

    /**
     * Define el valor de la propiedad fileDownloadDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFileDownloadDate(XMLGregorianCalendar value) {
        this.fileDownloadDate = value;
    }

    /**
     * Obtiene el valor de la propiedad fileEndDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFileEndDate() {
        return fileEndDate;
    }

    /**
     * Define el valor de la propiedad fileEndDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFileEndDate(XMLGregorianCalendar value) {
        this.fileEndDate = value;
    }

    /**
     * Obtiene el valor de la propiedad fileName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Define el valor de la propiedad fileName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Obtiene el valor de la propiedad fileStartDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFileStartDate() {
        return fileStartDate;
    }

    /**
     * Define el valor de la propiedad fileStartDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFileStartDate(XMLGregorianCalendar value) {
        this.fileStartDate = value;
    }

    /**
     * Obtiene el valor de la propiedad fileType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Define el valor de la propiedad fileType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileType(String value) {
        this.fileType = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     */
    public void setId(int value) {
        this.id = value;
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
     * Obtiene el valor de la propiedad overviewBlock.
     * 
     */
    public boolean isOverviewBlock() {
        return overviewBlock;
    }

    /**
     * Define el valor de la propiedad overviewBlock.
     * 
     */
    public void setOverviewBlock(boolean value) {
        this.overviewBlock = value;
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
