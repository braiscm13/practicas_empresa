
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfRegistroELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRegistroELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroELDDto" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}RegistroELDDto" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRegistroELDDto", propOrder = {
    "registroELDDto"
})
public class ArrayOfRegistroELDDto {

    @XmlElement(name = "RegistroELDDto", nillable = true)
    protected List<RegistroELDDto> registroELDDto;

    /**
     * Gets the value of the registroELDDto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroELDDto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroELDDto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroELDDto }
     * 
     * 
     */
    public List<RegistroELDDto> getRegistroELDDto() {
        if (registroELDDto == null) {
            registroELDDto = new ArrayList<RegistroELDDto>();
        }
        return this.registroELDDto;
    }

}
