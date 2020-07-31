
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfCicloELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCicloELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CicloELDDto" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}CicloELDDto" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCicloELDDto", propOrder = {
    "cicloELDDto"
})
public class ArrayOfCicloELDDto {

    @XmlElement(name = "CicloELDDto", nillable = true)
    protected List<CicloELDDto> cicloELDDto;

    /**
     * Gets the value of the cicloELDDto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cicloELDDto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCicloELDDto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CicloELDDto }
     * 
     * 
     */
    public List<CicloELDDto> getCicloELDDto() {
        if (cicloELDDto == null) {
            cicloELDDto = new ArrayList<CicloELDDto>();
        }
        return this.cicloELDDto;
    }

}
