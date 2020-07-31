
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ContractInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ContractInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ContractCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ContractHolder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ContractCreationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ContractExpirationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ContractPermanence" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ContractNumberOfVehicles" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContractInfo", propOrder = {
    "contractCode",
    "contractHolder",
    "contractCreationDate",
    "contractExpirationDate",
    "contractPermanence",
    "contractNumberOfVehicles"
})
public class ContractInfo {

    @XmlElement(name = "ContractCode")
    protected String contractCode;
    @XmlElement(name = "ContractHolder")
    protected String contractHolder;
    @XmlElement(name = "ContractCreationDate")
    protected String contractCreationDate;
    @XmlElement(name = "ContractExpirationDate")
    protected String contractExpirationDate;
    @XmlElement(name = "ContractPermanence")
    protected int contractPermanence;
    @XmlElement(name = "ContractNumberOfVehicles")
    protected int contractNumberOfVehicles;

    /**
     * Obtiene el valor de la propiedad contractCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * Define el valor de la propiedad contractCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractCode(String value) {
        this.contractCode = value;
    }

    /**
     * Obtiene el valor de la propiedad contractHolder.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractHolder() {
        return contractHolder;
    }

    /**
     * Define el valor de la propiedad contractHolder.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractHolder(String value) {
        this.contractHolder = value;
    }

    /**
     * Obtiene el valor de la propiedad contractCreationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractCreationDate() {
        return contractCreationDate;
    }

    /**
     * Define el valor de la propiedad contractCreationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractCreationDate(String value) {
        this.contractCreationDate = value;
    }

    /**
     * Obtiene el valor de la propiedad contractExpirationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractExpirationDate() {
        return contractExpirationDate;
    }

    /**
     * Define el valor de la propiedad contractExpirationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractExpirationDate(String value) {
        this.contractExpirationDate = value;
    }

    /**
     * Obtiene el valor de la propiedad contractPermanence.
     * 
     */
    public int getContractPermanence() {
        return contractPermanence;
    }

    /**
     * Define el valor de la propiedad contractPermanence.
     * 
     */
    public void setContractPermanence(int value) {
        this.contractPermanence = value;
    }

    /**
     * Obtiene el valor de la propiedad contractNumberOfVehicles.
     * 
     */
    public int getContractNumberOfVehicles() {
        return contractNumberOfVehicles;
    }

    /**
     * Define el valor de la propiedad contractNumberOfVehicles.
     * 
     */
    public void setContractNumberOfVehicles(int value) {
        this.contractNumberOfVehicles = value;
    }

}
