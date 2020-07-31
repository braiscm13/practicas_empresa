
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para TachoScheduleInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TachoScheduleInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ExtensionData" type="{http://tempuri.org/}ExtensionDataObject" minOccurs="0"/&gt;
 *         &lt;element name="DriverCardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="NextExecutionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="NumberPlate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TachoScheduleInfo", propOrder = {
    "extensionData",
    "driverCardNumber",
    "id",
    "nextExecutionDate",
    "numberPlate"
})
public class TachoScheduleInfo {

    @XmlElement(name = "ExtensionData")
    protected ExtensionDataObject extensionData;
    @XmlElement(name = "DriverCardNumber")
    protected String driverCardNumber;
    @XmlElement(name = "Id")
    protected int id;
    @XmlElement(name = "NextExecutionDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar nextExecutionDate;
    @XmlElement(name = "NumberPlate")
    protected String numberPlate;

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
     * Obtiene el valor de la propiedad nextExecutionDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNextExecutionDate() {
        return nextExecutionDate;
    }

    /**
     * Define el valor de la propiedad nextExecutionDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNextExecutionDate(XMLGregorianCalendar value) {
        this.nextExecutionDate = value;
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

}
