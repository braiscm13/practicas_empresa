
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfCandidatoEstadoELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCandidatoEstadoELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CandidatoEstadoELDDto" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}CandidatoEstadoELDDto" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCandidatoEstadoELDDto", propOrder = {
    "candidatoEstadoELDDto"
})
public class ArrayOfCandidatoEstadoELDDto {

    @XmlElement(name = "CandidatoEstadoELDDto", nillable = true)
    protected List<CandidatoEstadoELDDto> candidatoEstadoELDDto;

    /**
     * Gets the value of the candidatoEstadoELDDto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the candidatoEstadoELDDto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCandidatoEstadoELDDto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CandidatoEstadoELDDto }
     * 
     * 
     */
    public List<CandidatoEstadoELDDto> getCandidatoEstadoELDDto() {
        if (candidatoEstadoELDDto == null) {
            candidatoEstadoELDDto = new ArrayList<CandidatoEstadoELDDto>();
        }
        return this.candidatoEstadoELDDto;
    }

}
