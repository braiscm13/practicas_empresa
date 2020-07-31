
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
 *         &lt;element name="driverCardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="weekDay" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="hour" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
    "driverCardNumber",
    "weekDay",
    "hour"
})
@XmlRootElement(name = "setTachoScheduleForDriver")
public class SetTachoScheduleForDriver {

    @XmlElement(name = "CIF")
    protected String cif;
    protected String driverCardNumber;
    protected int weekDay;
    protected int hour;

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
     * Obtiene el valor de la propiedad weekDay.
     * 
     */
    public int getWeekDay() {
        return weekDay;
    }

    /**
     * Define el valor de la propiedad weekDay.
     * 
     */
    public void setWeekDay(int value) {
        this.weekDay = value;
    }

    /**
     * Obtiene el valor de la propiedad hour.
     * 
     */
    public int getHour() {
        return hour;
    }

    /**
     * Define el valor de la propiedad hour.
     * 
     */
    public void setHour(int value) {
        this.hour = value;
    }

}
