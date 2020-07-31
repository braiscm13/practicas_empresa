
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para WebResultOfTachoScheduleInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="WebResultOfTachoScheduleInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}WebResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Result" type="{http://tempuri.org/}TachoScheduleInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebResultOfTachoScheduleInfo", propOrder = {
    "result"
})
public class WebResultOfTachoScheduleInfo
    extends WebResult
{

    @XmlElement(name = "Result")
    protected TachoScheduleInfo result;

    /**
     * Obtiene el valor de la propiedad result.
     * 
     * @return
     *     possible object is
     *     {@link TachoScheduleInfo }
     *     
     */
    public TachoScheduleInfo getResult() {
        return result;
    }

    /**
     * Define el valor de la propiedad result.
     * 
     * @param value
     *     allowed object is
     *     {@link TachoScheduleInfo }
     *     
     */
    public void setResult(TachoScheduleInfo value) {
        this.result = value;
    }

}
