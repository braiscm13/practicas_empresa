
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfGPSPositionInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGPSPositionInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GPSPositionInfo" type="{http://tempuri.org/}GPSPositionInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGPSPositionInfo", propOrder = {
    "gpsPositionInfo"
})
public class ArrayOfGPSPositionInfo {

    @XmlElement(name = "GPSPositionInfo", nillable = true)
    protected List<GPSPositionInfo> gpsPositionInfo;

    /**
     * Gets the value of the gpsPositionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gpsPositionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGPSPositionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GPSPositionInfo }
     * 
     * 
     */
    public List<GPSPositionInfo> getGPSPositionInfo() {
        if (gpsPositionInfo == null) {
            gpsPositionInfo = new ArrayList<GPSPositionInfo>();
        }
        return this.gpsPositionInfo;
    }

}
