
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfVehiculoELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfVehiculoELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="VehiculoELDDto" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}VehiculoELDDto" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVehiculoELDDto", propOrder = {
    "vehiculoELDDto"
})
public class ArrayOfVehiculoELDDto {

    @XmlElement(name = "VehiculoELDDto", nillable = true)
    protected List<VehiculoELDDto> vehiculoELDDto;

    /**
     * Gets the value of the vehiculoELDDto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehiculoELDDto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehiculoELDDto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VehiculoELDDto }
     * 
     * 
     */
    public List<VehiculoELDDto> getVehiculoELDDto() {
        if (vehiculoELDDto == null) {
            vehiculoELDDto = new ArrayList<VehiculoELDDto>();
        }
        return this.vehiculoELDDto;
    }

}
