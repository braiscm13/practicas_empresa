
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.InfoELD;


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
 *         &lt;element name="saveUnidentifiedDrivingResult" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}InfoELD" minOccurs="0"/&gt;
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
    "saveUnidentifiedDrivingResult"
})
@XmlRootElement(name = "saveUnidentifiedDrivingResponse")
public class SaveUnidentifiedDrivingResponse {

    @XmlElementRef(name = "saveUnidentifiedDrivingResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<InfoELD> saveUnidentifiedDrivingResult;

    /**
     * Obtiene el valor de la propiedad saveUnidentifiedDrivingResult.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link InfoELD }{@code >}
     *     
     */
    public JAXBElement<InfoELD> getSaveUnidentifiedDrivingResult() {
        return saveUnidentifiedDrivingResult;
    }

    /**
     * Define el valor de la propiedad saveUnidentifiedDrivingResult.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link InfoELD }{@code >}
     *     
     */
    public void setSaveUnidentifiedDrivingResult(JAXBElement<InfoELD> value) {
        this.saveUnidentifiedDrivingResult = value;
    }

}
