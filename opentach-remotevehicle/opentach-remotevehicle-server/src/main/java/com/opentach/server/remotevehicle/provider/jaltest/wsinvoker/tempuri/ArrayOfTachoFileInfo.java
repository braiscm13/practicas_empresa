
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfTachoFileInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTachoFileInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TachoFileInfo" type="{http://tempuri.org/}TachoFileInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTachoFileInfo", propOrder = {
    "tachoFileInfo"
})
public class ArrayOfTachoFileInfo {

    @XmlElement(name = "TachoFileInfo", nillable = true)
    protected List<TachoFileInfo> tachoFileInfo;

    /**
     * Gets the value of the tachoFileInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tachoFileInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTachoFileInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TachoFileInfo }
     * 
     * 
     */
    public List<TachoFileInfo> getTachoFileInfo() {
        if (tachoFileInfo == null) {
            tachoFileInfo = new ArrayList<TachoFileInfo>();
        }
        return this.tachoFileInfo;
    }

}
