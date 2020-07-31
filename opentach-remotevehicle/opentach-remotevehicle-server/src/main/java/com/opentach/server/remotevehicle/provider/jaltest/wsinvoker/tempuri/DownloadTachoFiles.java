
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="fileIds" type="{http://tempuri.org/}ArrayOfInt" minOccurs="0"/&gt;
 *         &lt;element name="extensionFile" type="{http://tempuri.org/}TachoFileExtension"/&gt;
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
    "fileIds",
    "extensionFile"
})
@XmlRootElement(name = "downloadTachoFiles")
public class DownloadTachoFiles {

    @XmlElement(name = "CIF")
    protected String cif;
    protected ArrayOfInt fileIds;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TachoFileExtension extensionFile;

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
     * Obtiene el valor de la propiedad fileIds.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getFileIds() {
        return fileIds;
    }

    /**
     * Define el valor de la propiedad fileIds.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setFileIds(ArrayOfInt value) {
        this.fileIds = value;
    }

    /**
     * Obtiene el valor de la propiedad extensionFile.
     * 
     * @return
     *     possible object is
     *     {@link TachoFileExtension }
     *     
     */
    public TachoFileExtension getExtensionFile() {
        return extensionFile;
    }

    /**
     * Define el valor de la propiedad extensionFile.
     * 
     * @param value
     *     allowed object is
     *     {@link TachoFileExtension }
     *     
     */
    public void setExtensionFile(TachoFileExtension value) {
        this.extensionFile = value;
    }

}
