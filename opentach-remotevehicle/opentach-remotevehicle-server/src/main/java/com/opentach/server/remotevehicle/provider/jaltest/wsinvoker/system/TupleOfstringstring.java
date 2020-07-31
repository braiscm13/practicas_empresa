
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.system;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TupleOfstringstring complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TupleOfstringstring"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="m_Item1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="m_Item2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TupleOfstringstring", propOrder = {
    "mItem1",
    "mItem2"
})
public class TupleOfstringstring {

    @XmlElement(name = "m_Item1", required = true, nillable = true)
    protected String mItem1;
    @XmlElement(name = "m_Item2", required = true, nillable = true)
    protected String mItem2;

    /**
     * Obtiene el valor de la propiedad mItem1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMItem1() {
        return mItem1;
    }

    /**
     * Define el valor de la propiedad mItem1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMItem1(String value) {
        this.mItem1 = value;
    }

    /**
     * Obtiene el valor de la propiedad mItem2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMItem2() {
        return mItem2;
    }

    /**
     * Define el valor de la propiedad mItem2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMItem2(String value) {
        this.mItem2 = value;
    }

}
