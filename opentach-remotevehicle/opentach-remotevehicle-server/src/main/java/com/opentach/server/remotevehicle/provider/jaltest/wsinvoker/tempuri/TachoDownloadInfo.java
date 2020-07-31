
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TachoDownloadInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TachoDownloadInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ExtensionData" type="{http://tempuri.org/}ExtensionDataObject" minOccurs="0"/&gt;
 *         &lt;element name="TachoDownloadId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TachoDownloadInfo", propOrder = {
    "extensionData",
    "tachoDownloadId"
})
public class TachoDownloadInfo {

    @XmlElement(name = "ExtensionData")
    protected ExtensionDataObject extensionData;
    @XmlElement(name = "TachoDownloadId")
    protected int tachoDownloadId;

    /**
     * Obtiene el valor de la propiedad extensionData.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionDataObject }
     *     
     */
    public ExtensionDataObject getExtensionData() {
        return extensionData;
    }

    /**
     * Define el valor de la propiedad extensionData.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionDataObject }
     *     
     */
    public void setExtensionData(ExtensionDataObject value) {
        this.extensionData = value;
    }

    /**
     * Obtiene el valor de la propiedad tachoDownloadId.
     * 
     */
    public int getTachoDownloadId() {
        return tachoDownloadId;
    }

    /**
     * Define el valor de la propiedad tachoDownloadId.
     * 
     */
    public void setTachoDownloadId(int value) {
        this.tachoDownloadId = value;
    }

}
