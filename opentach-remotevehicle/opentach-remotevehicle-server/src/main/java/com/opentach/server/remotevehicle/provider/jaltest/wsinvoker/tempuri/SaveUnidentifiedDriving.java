
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield.ConduccionNoIdentificadaELDDto;


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
 *         &lt;element name="unidentifiedDrivingToSave" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}ConduccionNoIdentificadaELDDto" minOccurs="0"/&gt;
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
    "unidentifiedDrivingToSave"
})
@XmlRootElement(name = "saveUnidentifiedDriving")
public class SaveUnidentifiedDriving {

    @XmlElementRef(name = "unidentifiedDrivingToSave", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<ConduccionNoIdentificadaELDDto> unidentifiedDrivingToSave;

    /**
     * Obtiene el valor de la propiedad unidentifiedDrivingToSave.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ConduccionNoIdentificadaELDDto }{@code >}
     *     
     */
    public JAXBElement<ConduccionNoIdentificadaELDDto> getUnidentifiedDrivingToSave() {
        return unidentifiedDrivingToSave;
    }

    /**
     * Define el valor de la propiedad unidentifiedDrivingToSave.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ConduccionNoIdentificadaELDDto }{@code >}
     *     
     */
    public void setUnidentifiedDrivingToSave(JAXBElement<ConduccionNoIdentificadaELDDto> value) {
        this.unidentifiedDrivingToSave = value;
    }

}
