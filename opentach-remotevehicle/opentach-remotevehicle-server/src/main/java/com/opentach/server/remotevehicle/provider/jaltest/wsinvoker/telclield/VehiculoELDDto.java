
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para VehiculoELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="VehiculoELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="JtcuMAC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="JtcuSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NumberPlate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehiculoELDDto", propOrder = {
    "code",
    "jtcuMAC",
    "jtcuSerialNumber",
    "numberPlate"
})
public class VehiculoELDDto {

    @XmlElement(name = "Code")
    protected Integer code;
    @XmlElementRef(name = "JtcuMAC", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jtcuMAC;
    @XmlElementRef(name = "JtcuSerialNumber", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jtcuSerialNumber;
    @XmlElementRef(name = "NumberPlate", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> numberPlate;

    /**
     * Obtiene el valor de la propiedad code.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Define el valor de la propiedad code.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCode(Integer value) {
        this.code = value;
    }

    /**
     * Obtiene el valor de la propiedad jtcuMAC.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJtcuMAC() {
        return jtcuMAC;
    }

    /**
     * Define el valor de la propiedad jtcuMAC.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJtcuMAC(JAXBElement<String> value) {
        this.jtcuMAC = value;
    }

    /**
     * Obtiene el valor de la propiedad jtcuSerialNumber.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJtcuSerialNumber() {
        return jtcuSerialNumber;
    }

    /**
     * Define el valor de la propiedad jtcuSerialNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJtcuSerialNumber(JAXBElement<String> value) {
        this.jtcuSerialNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad numberPlate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNumberPlate() {
        return numberPlate;
    }

    /**
     * Define el valor de la propiedad numberPlate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNumberPlate(JAXBElement<String> value) {
        this.numberPlate = value;
    }

}
