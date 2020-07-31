
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.gesttelematica.telmanagecomunwebuser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para MenuDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="MenuDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Descripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="IdPerfil" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ListaSubMenus" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb}ArrayOfMenuDto" minOccurs="0"/&gt;
 *         &lt;element name="MenuPadre" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NombreImagen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NombreMenuPadre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Orden" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Pagina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PaginaInicial" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Permiso" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TextoMI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TextoMIMenuPadre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="menuPadreFiltrado" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuDto", propOrder = {
    "descripcion",
    "id",
    "idPerfil",
    "listaSubMenus",
    "menuPadre",
    "nombre",
    "nombreImagen",
    "nombreMenuPadre",
    "orden",
    "pagina",
    "paginaInicial",
    "permiso",
    "textoMI",
    "textoMIMenuPadre",
    "menuPadreFiltrado"
})
public class MenuDto {

    @XmlElementRef(name = "Descripcion", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> descripcion;
    @XmlElement(name = "Id")
    protected Integer id;
    @XmlElement(name = "IdPerfil")
    protected Integer idPerfil;
    @XmlElementRef(name = "ListaSubMenus", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfMenuDto> listaSubMenus;
    @XmlElementRef(name = "MenuPadre", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> menuPadre;
    @XmlElementRef(name = "Nombre", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "NombreImagen", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nombreImagen;
    @XmlElementRef(name = "NombreMenuPadre", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nombreMenuPadre;
    @XmlElement(name = "Orden")
    protected Integer orden;
    @XmlElementRef(name = "Pagina", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pagina;
    @XmlElement(name = "PaginaInicial")
    protected Boolean paginaInicial;
    @XmlElement(name = "Permiso")
    protected Integer permiso;
    @XmlElementRef(name = "TextoMI", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> textoMI;
    @XmlElementRef(name = "TextoMIMenuPadre", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb", type = JAXBElement.class, required = false)
    protected JAXBElement<String> textoMIMenuPadre;
    protected Integer menuPadreFiltrado;

    /**
     * Obtiene el valor de la propiedad descripcion.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescripcion() {
        return descripcion;
    }

    /**
     * Define el valor de la propiedad descripcion.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescripcion(JAXBElement<String> value) {
        this.descripcion = value;
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
     * Obtiene el valor de la propiedad idPerfil.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdPerfil() {
        return idPerfil;
    }

    /**
     * Define el valor de la propiedad idPerfil.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdPerfil(Integer value) {
        this.idPerfil = value;
    }

    /**
     * Obtiene el valor de la propiedad listaSubMenus.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfMenuDto }{@code >}
     *     
     */
    public JAXBElement<ArrayOfMenuDto> getListaSubMenus() {
        return listaSubMenus;
    }

    /**
     * Define el valor de la propiedad listaSubMenus.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfMenuDto }{@code >}
     *     
     */
    public void setListaSubMenus(JAXBElement<ArrayOfMenuDto> value) {
        this.listaSubMenus = value;
    }

    /**
     * Obtiene el valor de la propiedad menuPadre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getMenuPadre() {
        return menuPadre;
    }

    /**
     * Define el valor de la propiedad menuPadre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setMenuPadre(JAXBElement<Integer> value) {
        this.menuPadre = value;
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
     * Obtiene el valor de la propiedad nombreImagen.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreImagen() {
        return nombreImagen;
    }

    /**
     * Define el valor de la propiedad nombreImagen.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreImagen(JAXBElement<String> value) {
        this.nombreImagen = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreMenuPadre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreMenuPadre() {
        return nombreMenuPadre;
    }

    /**
     * Define el valor de la propiedad nombreMenuPadre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreMenuPadre(JAXBElement<String> value) {
        this.nombreMenuPadre = value;
    }

    /**
     * Obtiene el valor de la propiedad orden.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOrden() {
        return orden;
    }

    /**
     * Define el valor de la propiedad orden.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOrden(Integer value) {
        this.orden = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPagina() {
        return pagina;
    }

    /**
     * Define el valor de la propiedad pagina.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPagina(JAXBElement<String> value) {
        this.pagina = value;
    }

    /**
     * Obtiene el valor de la propiedad paginaInicial.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaginaInicial() {
        return paginaInicial;
    }

    /**
     * Define el valor de la propiedad paginaInicial.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaginaInicial(Boolean value) {
        this.paginaInicial = value;
    }

    /**
     * Obtiene el valor de la propiedad permiso.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPermiso() {
        return permiso;
    }

    /**
     * Define el valor de la propiedad permiso.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPermiso(Integer value) {
        this.permiso = value;
    }

    /**
     * Obtiene el valor de la propiedad textoMI.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTextoMI() {
        return textoMI;
    }

    /**
     * Define el valor de la propiedad textoMI.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTextoMI(JAXBElement<String> value) {
        this.textoMI = value;
    }

    /**
     * Obtiene el valor de la propiedad textoMIMenuPadre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTextoMIMenuPadre() {
        return textoMIMenuPadre;
    }

    /**
     * Define el valor de la propiedad textoMIMenuPadre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTextoMIMenuPadre(JAXBElement<String> value) {
        this.textoMIMenuPadre = value;
    }

    /**
     * Obtiene el valor de la propiedad menuPadreFiltrado.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMenuPadreFiltrado() {
        return menuPadreFiltrado;
    }

    /**
     * Define el valor de la propiedad menuPadreFiltrado.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMenuPadreFiltrado(Integer value) {
        this.menuPadreFiltrado = value;
    }

}
