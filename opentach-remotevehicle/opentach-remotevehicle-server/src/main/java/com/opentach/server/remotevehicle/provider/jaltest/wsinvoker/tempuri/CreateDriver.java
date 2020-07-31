
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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="surnames" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DNI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="notificationsPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="homePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tachographCardCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tachographCardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tachographCardExpirationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "name",
    "surnames",
    "dni",
    "notificationsPhone",
    "homePhone",
    "address",
    "email",
    "tachographCardCountry",
    "tachographCardNumber",
    "tachographCardExpirationDate"
})
@XmlRootElement(name = "createDriver")
public class CreateDriver {

    @XmlElement(name = "CIF")
    protected String cif;
    protected String name;
    protected String surnames;
    @XmlElement(name = "DNI")
    protected String dni;
    protected String notificationsPhone;
    protected String homePhone;
    protected String address;
    protected String email;
    protected String tachographCardCountry;
    protected String tachographCardNumber;
    protected String tachographCardExpirationDate;

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
     * Obtiene el valor de la propiedad name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Define el valor de la propiedad name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtiene el valor de la propiedad surnames.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurnames() {
        return surnames;
    }

    /**
     * Define el valor de la propiedad surnames.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurnames(String value) {
        this.surnames = value;
    }

    /**
     * Obtiene el valor de la propiedad dni.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNI() {
        return dni;
    }

    /**
     * Define el valor de la propiedad dni.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNI(String value) {
        this.dni = value;
    }

    /**
     * Obtiene el valor de la propiedad notificationsPhone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationsPhone() {
        return notificationsPhone;
    }

    /**
     * Define el valor de la propiedad notificationsPhone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationsPhone(String value) {
        this.notificationsPhone = value;
    }

    /**
     * Obtiene el valor de la propiedad homePhone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * Define el valor de la propiedad homePhone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePhone(String value) {
        this.homePhone = value;
    }

    /**
     * Obtiene el valor de la propiedad address.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Define el valor de la propiedad address.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Obtiene el valor de la propiedad email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define el valor de la propiedad email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Obtiene el valor de la propiedad tachographCardCountry.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTachographCardCountry() {
        return tachographCardCountry;
    }

    /**
     * Define el valor de la propiedad tachographCardCountry.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTachographCardCountry(String value) {
        this.tachographCardCountry = value;
    }

    /**
     * Obtiene el valor de la propiedad tachographCardNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTachographCardNumber() {
        return tachographCardNumber;
    }

    /**
     * Define el valor de la propiedad tachographCardNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTachographCardNumber(String value) {
        this.tachographCardNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad tachographCardExpirationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTachographCardExpirationDate() {
        return tachographCardExpirationDate;
    }

    /**
     * Define el valor de la propiedad tachographCardExpirationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTachographCardExpirationDate(String value) {
        this.tachographCardExpirationDate = value;
    }

}
