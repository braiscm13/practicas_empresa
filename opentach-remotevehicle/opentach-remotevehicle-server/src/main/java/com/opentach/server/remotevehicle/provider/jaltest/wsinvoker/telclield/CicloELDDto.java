
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telclield;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld.SbTypes;


/**
 * <p>Clase Java para CicloELDDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CicloELDDto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CanadaSur" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="CargoType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DaysOfCicle" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LimitCycleWorkingHours" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="LimitDrivingWindow" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="LimitWorkingWindow" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="MaxContinuedWorking" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="MinimumBreak" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ResetTimeCycle" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SbEight" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SbMin" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SbMinCombo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SbTotal" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SbTwo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SbType" type="{http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD}SbTypes" minOccurs="0"/&gt;
 *         &lt;element name="ShortHaulException" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ShortHaulWindowAmpliation" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalOff" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="WindowResetTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="WindowSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CicloELDDto", propOrder = {
    "canadaSur",
    "cargoType",
    "daysOfCicle",
    "description",
    "limitCycleWorkingHours",
    "limitDrivingWindow",
    "limitWorkingWindow",
    "maxContinuedWorking",
    "minimumBreak",
    "resetTimeCycle",
    "sbEight",
    "sbMin",
    "sbMinCombo",
    "sbTotal",
    "sbTwo",
    "sbType",
    "shortHaulException",
    "shortHaulWindowAmpliation",
    "totalOff",
    "windowResetTime",
    "windowSize"
})
public class CicloELDDto {

    @XmlElement(name = "CanadaSur")
    protected Boolean canadaSur;
    @XmlElementRef(name = "CargoType", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cargoType;
    @XmlElementRef(name = "DaysOfCicle", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> daysOfCicle;
    @XmlElementRef(name = "Description", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> description;
    @XmlElementRef(name = "LimitCycleWorkingHours", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> limitCycleWorkingHours;
    @XmlElementRef(name = "LimitDrivingWindow", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> limitDrivingWindow;
    @XmlElementRef(name = "LimitWorkingWindow", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> limitWorkingWindow;
    @XmlElementRef(name = "MaxContinuedWorking", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> maxContinuedWorking;
    @XmlElementRef(name = "MinimumBreak", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> minimumBreak;
    @XmlElementRef(name = "ResetTimeCycle", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> resetTimeCycle;
    @XmlElementRef(name = "SbEight", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> sbEight;
    @XmlElementRef(name = "SbMin", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> sbMin;
    @XmlElementRef(name = "SbMinCombo", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> sbMinCombo;
    @XmlElementRef(name = "SbTotal", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> sbTotal;
    @XmlElementRef(name = "SbTwo", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> sbTwo;
    @XmlElementRef(name = "SbType", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<SbTypes> sbType;
    @XmlElement(name = "ShortHaulException")
    protected Boolean shortHaulException;
    @XmlElementRef(name = "ShortHaulWindowAmpliation", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> shortHaulWindowAmpliation;
    @XmlElementRef(name = "TotalOff", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> totalOff;
    @XmlElementRef(name = "WindowResetTime", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> windowResetTime;
    @XmlElementRef(name = "WindowSize", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Dto.ELD", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> windowSize;

    /**
     * Obtiene el valor de la propiedad canadaSur.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanadaSur() {
        return canadaSur;
    }

    /**
     * Define el valor de la propiedad canadaSur.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanadaSur(Boolean value) {
        this.canadaSur = value;
    }

    /**
     * Obtiene el valor de la propiedad cargoType.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCargoType() {
        return cargoType;
    }

    /**
     * Define el valor de la propiedad cargoType.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCargoType(JAXBElement<String> value) {
        this.cargoType = value;
    }

    /**
     * Obtiene el valor de la propiedad daysOfCicle.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getDaysOfCicle() {
        return daysOfCicle;
    }

    /**
     * Define el valor de la propiedad daysOfCicle.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setDaysOfCicle(JAXBElement<Integer> value) {
        this.daysOfCicle = value;
    }

    /**
     * Obtiene el valor de la propiedad description.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Define el valor de la propiedad description.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = value;
    }

    /**
     * Obtiene el valor de la propiedad limitCycleWorkingHours.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getLimitCycleWorkingHours() {
        return limitCycleWorkingHours;
    }

    /**
     * Define el valor de la propiedad limitCycleWorkingHours.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setLimitCycleWorkingHours(JAXBElement<Integer> value) {
        this.limitCycleWorkingHours = value;
    }

    /**
     * Obtiene el valor de la propiedad limitDrivingWindow.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getLimitDrivingWindow() {
        return limitDrivingWindow;
    }

    /**
     * Define el valor de la propiedad limitDrivingWindow.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setLimitDrivingWindow(JAXBElement<Integer> value) {
        this.limitDrivingWindow = value;
    }

    /**
     * Obtiene el valor de la propiedad limitWorkingWindow.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getLimitWorkingWindow() {
        return limitWorkingWindow;
    }

    /**
     * Define el valor de la propiedad limitWorkingWindow.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setLimitWorkingWindow(JAXBElement<Integer> value) {
        this.limitWorkingWindow = value;
    }

    /**
     * Obtiene el valor de la propiedad maxContinuedWorking.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getMaxContinuedWorking() {
        return maxContinuedWorking;
    }

    /**
     * Define el valor de la propiedad maxContinuedWorking.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setMaxContinuedWorking(JAXBElement<Integer> value) {
        this.maxContinuedWorking = value;
    }

    /**
     * Obtiene el valor de la propiedad minimumBreak.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getMinimumBreak() {
        return minimumBreak;
    }

    /**
     * Define el valor de la propiedad minimumBreak.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setMinimumBreak(JAXBElement<Integer> value) {
        this.minimumBreak = value;
    }

    /**
     * Obtiene el valor de la propiedad resetTimeCycle.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getResetTimeCycle() {
        return resetTimeCycle;
    }

    /**
     * Define el valor de la propiedad resetTimeCycle.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setResetTimeCycle(JAXBElement<Integer> value) {
        this.resetTimeCycle = value;
    }

    /**
     * Obtiene el valor de la propiedad sbEight.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getSbEight() {
        return sbEight;
    }

    /**
     * Define el valor de la propiedad sbEight.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setSbEight(JAXBElement<Integer> value) {
        this.sbEight = value;
    }

    /**
     * Obtiene el valor de la propiedad sbMin.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getSbMin() {
        return sbMin;
    }

    /**
     * Define el valor de la propiedad sbMin.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setSbMin(JAXBElement<Integer> value) {
        this.sbMin = value;
    }

    /**
     * Obtiene el valor de la propiedad sbMinCombo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getSbMinCombo() {
        return sbMinCombo;
    }

    /**
     * Define el valor de la propiedad sbMinCombo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setSbMinCombo(JAXBElement<Integer> value) {
        this.sbMinCombo = value;
    }

    /**
     * Obtiene el valor de la propiedad sbTotal.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getSbTotal() {
        return sbTotal;
    }

    /**
     * Define el valor de la propiedad sbTotal.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setSbTotal(JAXBElement<Integer> value) {
        this.sbTotal = value;
    }

    /**
     * Obtiene el valor de la propiedad sbTwo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getSbTwo() {
        return sbTwo;
    }

    /**
     * Define el valor de la propiedad sbTwo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setSbTwo(JAXBElement<Integer> value) {
        this.sbTwo = value;
    }

    /**
     * Obtiene el valor de la propiedad sbType.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SbTypes }{@code >}
     *     
     */
    public JAXBElement<SbTypes> getSbType() {
        return sbType;
    }

    /**
     * Define el valor de la propiedad sbType.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SbTypes }{@code >}
     *     
     */
    public void setSbType(JAXBElement<SbTypes> value) {
        this.sbType = value;
    }

    /**
     * Obtiene el valor de la propiedad shortHaulException.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShortHaulException() {
        return shortHaulException;
    }

    /**
     * Define el valor de la propiedad shortHaulException.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShortHaulException(Boolean value) {
        this.shortHaulException = value;
    }

    /**
     * Obtiene el valor de la propiedad shortHaulWindowAmpliation.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getShortHaulWindowAmpliation() {
        return shortHaulWindowAmpliation;
    }

    /**
     * Define el valor de la propiedad shortHaulWindowAmpliation.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setShortHaulWindowAmpliation(JAXBElement<Integer> value) {
        this.shortHaulWindowAmpliation = value;
    }

    /**
     * Obtiene el valor de la propiedad totalOff.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getTotalOff() {
        return totalOff;
    }

    /**
     * Define el valor de la propiedad totalOff.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setTotalOff(JAXBElement<Integer> value) {
        this.totalOff = value;
    }

    /**
     * Obtiene el valor de la propiedad windowResetTime.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getWindowResetTime() {
        return windowResetTime;
    }

    /**
     * Define el valor de la propiedad windowResetTime.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setWindowResetTime(JAXBElement<Integer> value) {
        this.windowResetTime = value;
    }

    /**
     * Obtiene el valor de la propiedad windowSize.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getWindowSize() {
        return windowSize;
    }

    /**
     * Define el valor de la propiedad windowSize.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setWindowSize(JAXBElement<Integer> value) {
        this.windowSize = value;
    }

}
