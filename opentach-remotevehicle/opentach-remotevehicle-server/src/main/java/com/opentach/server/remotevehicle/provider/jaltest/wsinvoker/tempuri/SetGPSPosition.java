
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="webUserId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="mobileKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="gpsPosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "webUserId",
    "mobileKey",
    "gpsPosition"
})
@XmlRootElement(name = "setGPSPosition")
public class SetGPSPosition {

    protected Integer webUserId;
    @XmlElementRef(name = "mobileKey", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mobileKey;
    @XmlElementRef(name = "gpsPosition", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> gpsPosition;

    /**
     * Obtiene el valor de la propiedad webUserId.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWebUserId() {
        return webUserId;
    }

    /**
     * Define el valor de la propiedad webUserId.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWebUserId(Integer value) {
        this.webUserId = value;
    }

    /**
     * Obtiene el valor de la propiedad mobileKey.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMobileKey() {
        return mobileKey;
    }

    /**
     * Define el valor de la propiedad mobileKey.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMobileKey(JAXBElement<String> value) {
        this.mobileKey = value;
    }

    /**
     * Obtiene el valor de la propiedad gpsPosition.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGpsPosition() {
        return gpsPosition;
    }

    /**
     * Define el valor de la propiedad gpsPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGpsPosition(JAXBElement<String> value) {
        this.gpsPosition = value;
    }

}
