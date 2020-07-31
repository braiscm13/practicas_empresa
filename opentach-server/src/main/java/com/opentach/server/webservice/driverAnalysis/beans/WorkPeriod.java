package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
		name = "workPeriodSingle",
		propOrder = { "init", "end", "workDuration", "restDuration", "availableDuration", "drivingDuration", "indeterminateDuration", "periodRestDuration" })
public class WorkPeriod {

	private Date	init;

	private Date	end;

	private Integer	workDuration;
	private Integer	restDuration;
	private Integer	availableDuration;
	private Integer	drivingDuration;
	private Integer	indeterminateDuration;
	private Integer	periodRestDuration;

	public Integer getPeriodRestDuration() {
		return this.periodRestDuration;
	}

	public void setPeriodRestDuration(Integer periodRestDuration) {
		this.periodRestDuration = periodRestDuration;
	}

	public Date getInit() {
		return this.init;
	}

	public void setInit(Date init) {
		this.init = init;
	}

	public Date getEnd() {
		return this.end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * @return the workDuration
	 */
	public Integer getWorkDuration() {
		return this.workDuration;
	}

	/**
	 * @param workDuration
	 *            the workDuration to set
	 */
	public void setWorkDuration(Integer workDuration) {
		this.workDuration = workDuration;
	}

	/**
	 * @return the restDuration
	 */
	public Integer getRestDuration() {
		return this.restDuration;
	}

	/**
	 * @param restDuration
	 *            the restDuration to set
	 */
	public void setRestDuration(Integer restDuration) {
		this.restDuration = restDuration;
	}

	/**
	 * @return the availableDuration
	 */
	public Integer getAvailableDuration() {
		return this.availableDuration;
	}

	/**
	 * @param availableDuration
	 *            the availableDuration to set
	 */
	public void setAvailableDuration(Integer availableDuration) {
		this.availableDuration = availableDuration;
	}

	/**
	 * @return the drivingDuration
	 */
	public Integer getDrivingDuration() {
		return this.drivingDuration;
	}

	/**
	 * @param drivingDuration
	 *            the drivingDuration to set
	 */
	public void setDrivingDuration(Integer drivingDuration) {
		this.drivingDuration = drivingDuration;
	}

	/**
	 * @return the indeterminateDuration
	 */
	public Integer getIndeterminateDuration() {
		return this.indeterminateDuration;
	}

	/**
	 * @param indeterminateDuration
	 *            the indeterminateDuration to set
	 */
	public void setIndeterminateDuration(Integer indeterminateDuration) {
		this.indeterminateDuration = indeterminateDuration;
	}

	public boolean isEmpty() {
		return (this.getInit() == null) || (this.getEnd() == null) //
				|| (
						(this.getWorkDuration() == null) //
						&& (this.getRestDuration() == null) //
						&& (this.getAvailableDuration() == null) //
						&& (this.getDrivingDuration() == null) //
						&& (this.getIndeterminateDuration() == null));
	}


}
