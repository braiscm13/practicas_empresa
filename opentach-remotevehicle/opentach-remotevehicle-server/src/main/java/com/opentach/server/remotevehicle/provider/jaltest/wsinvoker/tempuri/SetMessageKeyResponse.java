
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResult;


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
 *         &lt;element name="setMessageKeyResult" type="{http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto}WebResult" minOccurs="0"/&gt;
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
    "setMessageKeyResult"
})
@XmlRootElement(name = "setMessageKeyResponse")
public class SetMessageKeyResponse {

    @XmlElementRef(name = "setMessageKeyResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<WebResult> setMessageKeyResult;

    /**
     * Obtiene el valor de la propiedad setMessageKeyResult.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WebResult }{@code >}
     *     
     */
    public JAXBElement<WebResult> getSetMessageKeyResult() {
        return setMessageKeyResult;
    }

    /**
     * Define el valor de la propiedad setMessageKeyResult.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WebResult }{@code >}
     *     
     */
    public void setSetMessageKeyResult(JAXBElement<WebResult> value) {
        this.setMessageKeyResult = value;
    }

}
