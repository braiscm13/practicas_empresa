
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
 *         &lt;element name="downloadTachoFilesResult" type="{http://tempuri.org/}WebResultOfListOfTachoFileInfo" minOccurs="0"/&gt;
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
    "downloadTachoFilesResult"
})
@XmlRootElement(name = "downloadTachoFilesResponse")
public class DownloadTachoFilesResponse {

    protected WebResultOfListOfTachoFileInfo downloadTachoFilesResult;

    /**
     * Obtiene el valor de la propiedad downloadTachoFilesResult.
     * 
     * @return
     *     possible object is
     *     {@link WebResultOfListOfTachoFileInfo }
     *     
     */
    public WebResultOfListOfTachoFileInfo getDownloadTachoFilesResult() {
        return downloadTachoFilesResult;
    }

    /**
     * Define el valor de la propiedad downloadTachoFilesResult.
     * 
     * @param value
     *     allowed object is
     *     {@link WebResultOfListOfTachoFileInfo }
     *     
     */
    public void setDownloadTachoFilesResult(WebResultOfListOfTachoFileInfo value) {
        this.downloadTachoFilesResult = value;
    }

}
