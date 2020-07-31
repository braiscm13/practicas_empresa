
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfCandidatoLogELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCandidatoLogELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CandidatoLogELDDto" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD}CandidatoLogELDDto" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCandidatoLogELDDto", propOrder = {
    "candidatoLogELDDto"
})
public class ArrayOfCandidatoLogELDDto {

    @XmlElement(name = "CandidatoLogELDDto", nillable = true)
    protected List<CandidatoLogELDDto> candidatoLogELDDto;

    /**
     * Gets the value of the candidatoLogELDDto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the candidatoLogELDDto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCandidatoLogELDDto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CandidatoLogELDDto }
     * 
     * 
     */
    public List<CandidatoLogELDDto> getCandidatoLogELDDto() {
        if (candidatoLogELDDto == null) {
            candidatoLogELDDto = new ArrayList<CandidatoLogELDDto>();
        }
        return this.candidatoLogELDDto;
    }

}
