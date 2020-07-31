
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.gesttelematica.telmanagecomunwebuser.UsuarioWebDto;


/**
 * <p>Clase Java para WebResultOfUsuarioWebDto1YD7O3EO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="WebResultOfUsuarioWebDto1YD7O3EO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto}WebResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Result" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Dto.GestionTelematica.UsuarioWeb}UsuarioWebDto" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebResultOfUsuarioWebDto1YD7O3EO", propOrder = {
    "result"
})
public class WebResultOfUsuarioWebDto1YD7O3EO
    extends WebResult
{

    @XmlElementRef(name = "Result", namespace = "http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", type = JAXBElement.class, required = false)
    protected JAXBElement<UsuarioWebDto> result;

    /**
     * Obtiene el valor de la propiedad result.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UsuarioWebDto }{@code >}
     *     
     */
    public JAXBElement<UsuarioWebDto> getResult() {
        return result;
    }

    /**
     * Define el valor de la propiedad result.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UsuarioWebDto }{@code >}
     *     
     */
    public void setResult(JAXBElement<UsuarioWebDto> value) {
        this.result = value;
    }

}
