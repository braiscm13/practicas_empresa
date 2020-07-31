
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.gesttelematica.telmanagecomunwebuser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EntornoDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EntornoDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IPEntorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IdEntorno" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NombreEntorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PasswordEntorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UsuarioEntorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntornoDto", propOrder = {
    "ipEntorno",
    "idEntorno",
    "nombreEntorno",
    "passwordEntorno",
    "usuarioEntorno"
})
public class EntornoDto {

    @XmlElementRef(name = "IPEntorno", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ipEntorno;
    @XmlElement(name = "IdEntorno")
    protected Integer idEntorno;
    @XmlElementRef(name = "NombreEntorno", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nombreEntorno;
    @XmlElementRef(name = "PasswordEntorno", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> passwordEntorno;
    @XmlElementRef(name = "UsuarioEntorno", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> usuarioEntorno;

    /**
     * Obtiene el valor de la propiedad ipEntorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIPEntorno() {
        return ipEntorno;
    }

    /**
     * Define el valor de la propiedad ipEntorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIPEntorno(JAXBElement<String> value) {
        this.ipEntorno = value;
    }

    /**
     * Obtiene el valor de la propiedad idEntorno.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdEntorno() {
        return idEntorno;
    }

    /**
     * Define el valor de la propiedad idEntorno.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdEntorno(Integer value) {
        this.idEntorno = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreEntorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreEntorno() {
        return nombreEntorno;
    }

    /**
     * Define el valor de la propiedad nombreEntorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreEntorno(JAXBElement<String> value) {
        this.nombreEntorno = value;
    }

    /**
     * Obtiene el valor de la propiedad passwordEntorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPasswordEntorno() {
        return passwordEntorno;
    }

    /**
     * Define el valor de la propiedad passwordEntorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPasswordEntorno(JAXBElement<String> value) {
        this.passwordEntorno = value;
    }

    /**
     * Obtiene el valor de la propiedad usuarioEntorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioEntorno() {
        return usuarioEntorno;
    }

    /**
     * Define el valor de la propiedad usuarioEntorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioEntorno(JAXBElement<String> value) {
        this.usuarioEntorno = value;
    }

}
