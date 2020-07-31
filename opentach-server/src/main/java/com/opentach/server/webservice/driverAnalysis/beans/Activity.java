package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "activity",
		propOrder = { "type", "endDate", "beginDate", "regimen", "origin", "outOfScope", "trainTrans", "plateNumber", "cardNumber" })
public class Activity {

	/** The stretch type. */
	private String	type;

	/** The end date. */
	private Date	endDate;

	/** The begin date. */
	private Date	beginDate;

	/** The regimen. */
	private String	regimen;

	/** The origin. */
	private String	origin;

	/** The out of scope. */
	private String	outOfScope;

	/** The train trans. */
	private String	trainTrans;

	/** The plate number. */
	private String	plateNumber;

	/** The card number. */
	private String	cardNumber;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public String getRegimen() {
		return this.regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getOrigin() {
		return this.origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getOutOfScope() {
		return this.outOfScope;
	}

	public void setOutOfScope(String outOfScope) {
		this.outOfScope = outOfScope;
	}

	public String getTrainTrans() {
		return this.trainTrans;
	}

	public void setTrainTrans(String trainTrans) {
		this.trainTrans = trainTrans;
	}

	public String getPlateNumber() {
		return this.plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[%.1s|%.1s %tH:%tM %td/%tm - %tH:%tM %td/%tm (%s,%s)]", String.valueOf(this.type), String.valueOf(this.regimen), this.beginDate, this.beginDate,
				this.beginDate, this.beginDate, this.endDate, this.endDate, this.endDate, this.endDate, this.outOfScope, this.trainTrans);
	}

}
