
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.gesttelematica.telmanagecomunwebuser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telmanagecomunconstants.UsuarioWebConstantesPerfil;


/**
 * <p>Clase Java para UsuarioWebDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="UsuarioWebDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Activado" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Alta" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Apellidos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ClaveMensajeria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CodigoIdioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CodigoTaller" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DNI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Desarrollador" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EmailRegistroEnviado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Entorno" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb}EntornoDto" minOccurs="0"/&gt;
 *         &lt;element name="ErrorPersistirGestion" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="FechaActivacionPassword" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="FechaCreacion" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="FechaEnvioEmail" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="FechaExpiracionRegistro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="FechaSolicitudNuevaPassword" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="FechaSolicitudPasswordCaducada" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="IdCliente" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="IdConductor" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="IdPais" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="IdPerfil" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Constantes}UsuarioWebConstantes.Perfil" minOccurs="0"/&gt;
 *         &lt;element name="IdTaller" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Idioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Login" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Menus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MobileKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NombrePais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Perfil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PerfilMostrar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PermisosMenus" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb}ArrayOfMenuDto" minOccurs="0"/&gt;
 *         &lt;element name="Rol" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Salt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TelefonoMovil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TipoUsuarioMostrar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TokenConfirmacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UrlAcceso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UsuarioWebDto", propOrder = {
    "activado",
    "alta",
    "apellidos",
    "claveMensajeria",
    "codigoIdioma",
    "codigoTaller",
    "dni",
    "desarrollador",
    "email",
    "emailRegistroEnviado",
    "entorno",
    "errorPersistirGestion",
    "fechaActivacionPassword",
    "fechaCreacion",
    "fechaEnvioEmail",
    "fechaExpiracionRegistro",
    "fechaSolicitudNuevaPassword",
    "fechaSolicitudPasswordCaducada",
    "id",
    "idCliente",
    "idConductor",
    "idPais",
    "idPerfil",
    "idTaller",
    "idioma",
    "login",
    "menus",
    "mobileKey",
    "nombre",
    "nombrePais",
    "password",
    "perfil",
    "perfilMostrar",
    "permisosMenus",
    "rol",
    "salt",
    "telefonoMovil",
    "tipoUsuarioMostrar",
    "tokenConfirmacion",
    "urlAcceso"
})
public class UsuarioWebDto {

    @XmlElement(name = "Activado")
    protected Integer activado;
    @XmlElement(name = "Alta")
    protected Boolean alta;
    @XmlElementRef(name = "Apellidos", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> apellidos;
    @XmlElementRef(name = "ClaveMensajeria", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> claveMensajeria;
    @XmlElementRef(name = "CodigoIdioma", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codigoIdioma;
    @XmlElementRef(name = "CodigoTaller", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codigoTaller;
    @XmlElementRef(name = "DNI", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> dni;
    @XmlElement(name = "Desarrollador")
    protected Integer desarrollador;
    @XmlElementRef(name = "Email", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> email;
    @XmlElement(name = "EmailRegistroEnviado")
    protected Boolean emailRegistroEnviado;
    @XmlElementRef(name = "Entorno", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<EntornoDto> entorno;
    @XmlElement(name = "ErrorPersistirGestion")
    protected Boolean errorPersistirGestion;
    @XmlElementRef(name = "FechaActivacionPassword", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaActivacionPassword;
    @XmlElementRef(name = "FechaCreacion", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaCreacion;
    @XmlElementRef(name = "FechaEnvioEmail", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaEnvioEmail;
    @XmlElementRef(name = "FechaExpiracionRegistro", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaExpiracionRegistro;
    @XmlElementRef(name = "FechaSolicitudNuevaPassword", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaSolicitudNuevaPassword;
    @XmlElement(name = "FechaSolicitudPasswordCaducada")
    protected Boolean fechaSolicitudPasswordCaducada;
    @XmlElement(name = "Id")
    protected Integer id;
    @XmlElementRef(name = "IdCliente", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> idCliente;
    @XmlElementRef(name = "IdConductor", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> idConductor;
    @XmlElement(name = "IdPais")
    protected Integer idPais;
    @XmlElementRef(name = "IdPerfil", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<UsuarioWebConstantesPerfil> idPerfil;
    @XmlElementRef(name = "IdTaller", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> idTaller;
    @XmlElementRef(name = "Idioma", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> idioma;
    @XmlElementRef(name = "Login", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> login;
    @XmlElementRef(name = "Menus", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> menus;
    @XmlElementRef(name = "MobileKey", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mobileKey;
    @XmlElementRef(name = "Nombre", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "NombrePais", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nombrePais;
    @XmlElementRef(name = "Password", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> password;
    @XmlElementRef(name = "Perfil", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> perfil;
    @XmlElementRef(name = "PerfilMostrar", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> perfilMostrar;
    @XmlElementRef(name = "PermisosMenus", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfMenuDto> permisosMenus;
    @XmlElement(name = "Rol")
    protected Integer rol;
    @XmlElementRef(name = "Salt", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> salt;
    @XmlElementRef(name = "TelefonoMovil", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> telefonoMovil;
    @XmlElementRef(name = "TipoUsuarioMostrar", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> tipoUsuarioMostrar;
    @XmlElementRef(name = "TokenConfirmacion", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> tokenConfirmacion;
    @XmlElementRef(name = "UrlAcceso", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> urlAcceso;

    /**
     * Obtiene el valor de la propiedad activado.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getActivado() {
        return activado;
    }

    /**
     * Define el valor de la propiedad activado.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setActivado(Integer value) {
        this.activado = value;
    }

    /**
     * Obtiene el valor de la propiedad alta.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlta() {
        return alta;
    }

    /**
     * Define el valor de la propiedad alta.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAlta(Boolean value) {
        this.alta = value;
    }

    /**
     * Obtiene el valor de la propiedad apellidos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getApellidos() {
        return apellidos;
    }

    /**
     * Define el valor de la propiedad apellidos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setApellidos(JAXBElement<String> value) {
        this.apellidos = value;
    }

    /**
     * Obtiene el valor de la propiedad claveMensajeria.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClaveMensajeria() {
        return claveMensajeria;
    }

    /**
     * Define el valor de la propiedad claveMensajeria.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClaveMensajeria(JAXBElement<String> value) {
        this.claveMensajeria = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoIdioma.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoIdioma() {
        return codigoIdioma;
    }

    /**
     * Define el valor de la propiedad codigoIdioma.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoIdioma(JAXBElement<String> value) {
        this.codigoIdioma = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoTaller.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoTaller() {
        return codigoTaller;
    }

    /**
     * Define el valor de la propiedad codigoTaller.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoTaller(JAXBElement<String> value) {
        this.codigoTaller = value;
    }

    /**
     * Obtiene el valor de la propiedad dni.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDNI() {
        return dni;
    }

    /**
     * Define el valor de la propiedad dni.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDNI(JAXBElement<String> value) {
        this.dni = value;
    }

    /**
     * Obtiene el valor de la propiedad desarrollador.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDesarrollador() {
        return desarrollador;
    }

    /**
     * Define el valor de la propiedad desarrollador.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDesarrollador(Integer value) {
        this.desarrollador = value;
    }

    /**
     * Obtiene el valor de la propiedad email.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmail() {
        return email;
    }

    /**
     * Define el valor de la propiedad email.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmail(JAXBElement<String> value) {
        this.email = value;
    }

    /**
     * Obtiene el valor de la propiedad emailRegistroEnviado.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmailRegistroEnviado() {
        return emailRegistroEnviado;
    }

    /**
     * Define el valor de la propiedad emailRegistroEnviado.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmailRegistroEnviado(Boolean value) {
        this.emailRegistroEnviado = value;
    }

    /**
     * Obtiene el valor de la propiedad entorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EntornoDto }{@code >}
     *     
     */
    public JAXBElement<EntornoDto> getEntorno() {
        return entorno;
    }

    /**
     * Define el valor de la propiedad entorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EntornoDto }{@code >}
     *     
     */
    public void setEntorno(JAXBElement<EntornoDto> value) {
        this.entorno = value;
    }

    /**
     * Obtiene el valor de la propiedad errorPersistirGestion.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isErrorPersistirGestion() {
        return errorPersistirGestion;
    }

    /**
     * Define el valor de la propiedad errorPersistirGestion.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setErrorPersistirGestion(Boolean value) {
        this.errorPersistirGestion = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaActivacionPassword.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaActivacionPassword() {
        return fechaActivacionPassword;
    }

    /**
     * Define el valor de la propiedad fechaActivacionPassword.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaActivacionPassword(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaActivacionPassword = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaCreacion.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Define el valor de la propiedad fechaCreacion.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaCreacion(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaCreacion = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaEnvioEmail.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaEnvioEmail() {
        return fechaEnvioEmail;
    }

    /**
     * Define el valor de la propiedad fechaEnvioEmail.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaEnvioEmail(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaEnvioEmail = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaExpiracionRegistro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaExpiracionRegistro() {
        return fechaExpiracionRegistro;
    }

    /**
     * Define el valor de la propiedad fechaExpiracionRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaExpiracionRegistro(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaExpiracionRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaSolicitudNuevaPassword.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaSolicitudNuevaPassword() {
        return fechaSolicitudNuevaPassword;
    }

    /**
     * Define el valor de la propiedad fechaSolicitudNuevaPassword.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaSolicitudNuevaPassword(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaSolicitudNuevaPassword = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaSolicitudPasswordCaducada.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFechaSolicitudPasswordCaducada() {
        return fechaSolicitudPasswordCaducada;
    }

    /**
     * Define el valor de la propiedad fechaSolicitudPasswordCaducada.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFechaSolicitudPasswordCaducada(Boolean value) {
        this.fechaSolicitudPasswordCaducada = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad idCliente.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getIdCliente() {
        return idCliente;
    }

    /**
     * Define el valor de la propiedad idCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setIdCliente(JAXBElement<Integer> value) {
        this.idCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad idConductor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getIdConductor() {
        return idConductor;
    }

    /**
     * Define el valor de la propiedad idConductor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setIdConductor(JAXBElement<Integer> value) {
        this.idConductor = value;
    }

    /**
     * Obtiene el valor de la propiedad idPais.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdPais() {
        return idPais;
    }

    /**
     * Define el valor de la propiedad idPais.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdPais(Integer value) {
        this.idPais = value;
    }

    /**
     * Obtiene el valor de la propiedad idPerfil.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UsuarioWebConstantesPerfil }{@code >}
     *     
     */
    public JAXBElement<UsuarioWebConstantesPerfil> getIdPerfil() {
        return idPerfil;
    }

    /**
     * Define el valor de la propiedad idPerfil.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UsuarioWebConstantesPerfil }{@code >}
     *     
     */
    public void setIdPerfil(JAXBElement<UsuarioWebConstantesPerfil> value) {
        this.idPerfil = value;
    }

    /**
     * Obtiene el valor de la propiedad idTaller.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getIdTaller() {
        return idTaller;
    }

    /**
     * Define el valor de la propiedad idTaller.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setIdTaller(JAXBElement<Integer> value) {
        this.idTaller = value;
    }

    /**
     * Obtiene el valor de la propiedad idioma.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIdioma() {
        return idioma;
    }

    /**
     * Define el valor de la propiedad idioma.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIdioma(JAXBElement<String> value) {
        this.idioma = value;
    }

    /**
     * Obtiene el valor de la propiedad login.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLogin() {
        return login;
    }

    /**
     * Define el valor de la propiedad login.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLogin(JAXBElement<String> value) {
        this.login = value;
    }

    /**
     * Obtiene el valor de la propiedad menus.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMenus() {
        return menus;
    }

    /**
     * Define el valor de la propiedad menus.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMenus(JAXBElement<String> value) {
        this.menus = value;
    }

    /**
     * Obtiene el valor de la propiedad mobileKey.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMobileKey() {
        return mobileKey;
    }

    /**
     * Define el valor de la propiedad mobileKey.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMobileKey(JAXBElement<String> value) {
        this.mobileKey = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombre(JAXBElement<String> value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad nombrePais.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombrePais() {
        return nombrePais;
    }

    /**
     * Define el valor de la propiedad nombrePais.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombrePais(JAXBElement<String> value) {
        this.nombrePais = value;
    }

    /**
     * Obtiene el valor de la propiedad password.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPassword() {
        return password;
    }

    /**
     * Define el valor de la propiedad password.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPassword(JAXBElement<String> value) {
        this.password = value;
    }

    /**
     * Obtiene el valor de la propiedad perfil.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPerfil() {
        return perfil;
    }

    /**
     * Define el valor de la propiedad perfil.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPerfil(JAXBElement<String> value) {
        this.perfil = value;
    }

    /**
     * Obtiene el valor de la propiedad perfilMostrar.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPerfilMostrar() {
        return perfilMostrar;
    }

    /**
     * Define el valor de la propiedad perfilMostrar.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPerfilMostrar(JAXBElement<String> value) {
        this.perfilMostrar = value;
    }

    /**
     * Obtiene el valor de la propiedad permisosMenus.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfMenuDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfMenuDto> getPermisosMenus() {
        return permisosMenus;
    }

    /**
     * Define el valor de la propiedad permisosMenus.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfMenuDto }{@code >}
     *     
     */
    public void setPermisosMenus(JAXBElement<ArrayOfMenuDto> value) {
        this.permisosMenus = value;
    }

    /**
     * Obtiene el valor de la propiedad rol.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRol() {
        return rol;
    }

    /**
     * Define el valor de la propiedad rol.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRol(Integer value) {
        this.rol = value;
    }

    /**
     * Obtiene el valor de la propiedad salt.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSalt() {
        return salt;
    }

    /**
     * Define el valor de la propiedad salt.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSalt(JAXBElement<String> value) {
        this.salt = value;
    }

    /**
     * Obtiene el valor de la propiedad telefonoMovil.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTelefonoMovil() {
        return telefonoMovil;
    }

    /**
     * Define el valor de la propiedad telefonoMovil.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTelefonoMovil(JAXBElement<String> value) {
        this.telefonoMovil = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoUsuarioMostrar.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTipoUsuarioMostrar() {
        return tipoUsuarioMostrar;
    }

    /**
     * Define el valor de la propiedad tipoUsuarioMostrar.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTipoUsuarioMostrar(JAXBElement<String> value) {
        this.tipoUsuarioMostrar = value;
    }

    /**
     * Obtiene el valor de la propiedad tokenConfirmacion.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTokenConfirmacion() {
        return tokenConfirmacion;
    }

    /**
     * Define el valor de la propiedad tokenConfirmacion.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTokenConfirmacion(JAXBElement<String> value) {
        this.tokenConfirmacion = value;
    }

    /**
     * Obtiene el valor de la propiedad urlAcceso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUrlAcceso() {
        return urlAcceso;
    }

    /**
     * Define el valor de la propiedad urlAcceso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUrlAcceso(JAXBElement<String> value) {
        this.urlAcceso = value;
    }

}
