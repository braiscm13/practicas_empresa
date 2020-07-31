
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
 *         &lt;element name="startTachoDownloadResult" type="{http://tempuri.org/}WebResultOfTachoDownloadInfo" minOccurs="0"/&gt;
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
    "startTachoDownloadResult"
})
@XmlRootElement(name = "startTachoDownloadResponse")
public class StartTachoDownloadResponse {

    protected WebResultOfTachoDownloadInfo startTachoDownloadResult;

    /**
     * Obtiene el valor de la propiedad startTachoDownloadResult.
     * 
     * @return
     *     possible object is
     *     {@link WebResultOfTachoDownloadInfo }
     *     
     */
    public WebResultOfTachoDownloadInfo getStartTachoDownloadResult() {
        return startTachoDownloadResult;
    }

    /**
     * Define el valor de la propiedad startTachoDownloadResult.
     * 
     * @param value
     *     allowed object is
     *     {@link WebResultOfTachoDownloadInfo }
     *     
     */
    public void setStartTachoDownloadResult(WebResultOfTachoDownloadInfo value) {
        this.startTachoDownloadResult = value;
    }

}
