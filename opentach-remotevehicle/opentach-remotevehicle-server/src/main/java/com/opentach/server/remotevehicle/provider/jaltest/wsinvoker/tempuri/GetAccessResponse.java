
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto.WebResultOfUsuarioWebDto1YD7O3EO;


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
 *         &lt;element name="getAccessResult" type="{http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto}WebResultOfUsuarioWebDto1YD7O3EO" minOccurs="0"/&gt;
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
    "getAccessResult"
})
@XmlRootElement(name = "getAccessResponse")
public class GetAccessResponse {

    @XmlElementRef(name = "getAccessResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO> getAccessResult;

    /**
     * Obtiene el valor de la propiedad getAccessResult.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WebResultOfUsuarioWebDto1YD7O3EO }{@code >}
     *     
     */
    public JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO> getGetAccessResult() {
        return getAccessResult;
    }

    /**
     * Define el valor de la propiedad getAccessResult.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WebResultOfUsuarioWebDto1YD7O3EO }{@code >}
     *     
     */
    public void setGetAccessResult(JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO> value) {
        this.getAccessResult = value;
    }

}
