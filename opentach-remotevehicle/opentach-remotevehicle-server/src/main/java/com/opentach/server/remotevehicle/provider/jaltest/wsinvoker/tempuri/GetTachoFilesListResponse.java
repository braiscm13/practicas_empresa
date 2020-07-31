
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
 *         &lt;element name="getTachoFilesListResult" type="{http://tempuri.org/}WebResultOfListOfTachoFileInfo" minOccurs="0"/&gt;
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
    "getTachoFilesListResult"
})
@XmlRootElement(name = "getTachoFilesListResponse")
public class GetTachoFilesListResponse {

    protected WebResultOfListOfTachoFileInfo getTachoFilesListResult;

    /**
     * Obtiene el valor de la propiedad getTachoFilesListResult.
     * 
     * @return
     *     possible object is
     *     {@link WebResultOfListOfTachoFileInfo }
     *     
     */
    public WebResultOfListOfTachoFileInfo getGetTachoFilesListResult() {
        return getTachoFilesListResult;
    }

    /**
     * Define el valor de la propiedad getTachoFilesListResult.
     * 
     * @param value
     *     allowed object is
     *     {@link WebResultOfListOfTachoFileInfo }
     *     
     */
    public void setGetTachoFilesListResult(WebResultOfListOfTachoFileInfo value) {
        this.getTachoFilesListResult = value;
    }

}
