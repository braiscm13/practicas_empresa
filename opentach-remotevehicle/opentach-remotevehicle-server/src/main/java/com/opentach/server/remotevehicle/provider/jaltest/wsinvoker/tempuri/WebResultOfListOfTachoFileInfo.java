
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para WebResultOfListOfTachoFileInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="WebResultOfListOfTachoFileInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}WebResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Result" type="{http://tempuri.org/}ArrayOfTachoFileInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebResultOfListOfTachoFileInfo", propOrder = {
    "result"
})
public class WebResultOfListOfTachoFileInfo
    extends WebResult
{

    @XmlElement(name = "Result")
    protected ArrayOfTachoFileInfo result;

    /**
     * Obtiene el valor de la propiedad result.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTachoFileInfo }
     *     
     */
    public ArrayOfTachoFileInfo getResult() {
        return result;
    }

    /**
     * Define el valor de la propiedad result.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTachoFileInfo }
     *     
     */
    public void setResult(ArrayOfTachoFileInfo value) {
        this.result = value;
    }

}
