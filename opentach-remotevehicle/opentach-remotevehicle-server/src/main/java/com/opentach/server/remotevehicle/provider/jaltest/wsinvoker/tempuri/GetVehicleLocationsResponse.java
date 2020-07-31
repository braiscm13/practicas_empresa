
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
 *         &lt;element name="getVehicleLocationsResult" type="{http://tempuri.org/}WebResultOfListOfGPSPositionInfo" minOccurs="0"/&gt;
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
    "getVehicleLocationsResult"
})
@XmlRootElement(name = "getVehicleLocationsResponse")
public class GetVehicleLocationsResponse {

    protected WebResultOfListOfGPSPositionInfo getVehicleLocationsResult;

    /**
     * Obtiene el valor de la propiedad getVehicleLocationsResult.
     * 
     * @return
     *     possible object is
     *     {@link WebResultOfListOfGPSPositionInfo }
     *     
     */
    public WebResultOfListOfGPSPositionInfo getGetVehicleLocationsResult() {
        return getVehicleLocationsResult;
    }

    /**
     * Define el valor de la propiedad getVehicleLocationsResult.
     * 
     * @param value
     *     allowed object is
     *     {@link WebResultOfListOfGPSPositionInfo }
     *     
     */
    public void setGetVehicleLocationsResult(WebResultOfListOfGPSPositionInfo value) {
        this.getVehicleLocationsResult = value;
    }

}
