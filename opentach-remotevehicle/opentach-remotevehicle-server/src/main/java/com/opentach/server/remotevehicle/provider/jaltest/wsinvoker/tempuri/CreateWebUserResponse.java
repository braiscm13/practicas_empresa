
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
 *         &lt;element name="createWebUserResult" type="{http://tempuri.org/}WebResult" minOccurs="0"/&gt;
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
    "createWebUserResult"
})
@XmlRootElement(name = "createWebUserResponse")
public class CreateWebUserResponse {

    protected WebResult createWebUserResult;

    /**
     * Obtiene el valor de la propiedad createWebUserResult.
     * 
     * @return
     *     possible object is
     *     {@link WebResult }
     *     
     */
    public WebResult getCreateWebUserResult() {
        return createWebUserResult;
    }

    /**
     * Define el valor de la propiedad createWebUserResult.
     * 
     * @param value
     *     allowed object is
     *     {@link WebResult }
     *     
     */
    public void setCreateWebUserResult(WebResult value) {
        this.createWebUserResult = value;
    }

}
