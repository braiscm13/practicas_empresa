
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="setTachoScheduleForDriverResult" type="{http://tempuri.org/}WebResultOfTachoScheduleInfo" minOccurs="0"/&gt;
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
    "setTachoScheduleForDriverResult"
})
@XmlRootElement(name = "setTachoScheduleForDriverResponse")
public class SetTachoScheduleForDriverResponse {

    protected WebResultOfTachoScheduleInfo setTachoScheduleForDriverResult;

    /**
     * Obtiene el valor de la propiedad setTachoScheduleForDriverResult.
     * 
     * @return
     *     possible object is
     *     {@link WebResultOfTachoScheduleInfo }
     *     
     */
    public WebResultOfTachoScheduleInfo getSetTachoScheduleForDriverResult() {
        return setTachoScheduleForDriverResult;
    }

    /**
     * Define el valor de la propiedad setTachoScheduleForDriverResult.
     * 
     * @param value
     *     allowed object is
     *     {@link WebResultOfTachoScheduleInfo }
     *     
     */
    public void setSetTachoScheduleForDriverResult(WebResultOfTachoScheduleInfo value) {
        this.setTachoScheduleForDriverResult = value;
    }

}
