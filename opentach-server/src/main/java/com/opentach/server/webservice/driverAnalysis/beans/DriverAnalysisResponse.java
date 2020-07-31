package com.opentach.server.webservice.driverAnalysis.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Extended class with other values auto-calculated.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
		name = "driverAnalysisResponse",
		propOrder = { "infractions", "dailyWorkPeriods", "weeklyWorkPeriods", "biweeklyWorkPeriods", "mondayToSundayPeriods" })
public class DriverAnalysisResponse implements Serializable {

	// 1.- Infracciones.
	private InfractionList	infractions;
	// 2. - daily WorkPeriods
	private WorkPeriodList	dailyWorkPeriods;
	// 3. - weekly WorkPeriods
	private WorkPeriodList	weeklyWorkPeriods;
	// 4. - biweekly WorkPeriods
	private WorkPeriodList	biweeklyWorkPeriods;
	// 5. monday to sunday periods
	private WorkPeriodList	mondayToSundayPeriods;

	public WorkPeriodList getMondayToSundayPeriods() {
		return this.mondayToSundayPeriods;
	}

	public void setMondayToSundayPeriods(WorkPeriodList mondayToSundayPeriods) {
		this.mondayToSundayPeriods = mondayToSundayPeriods;
	}

	public InfractionList getInfractions() {
		return this.infractions;
	}

	public void setInfractions(InfractionList infractions) {
		this.infractions = infractions;
	}

	public WorkPeriodList getDailyWorkPeriods() {
		return this.dailyWorkPeriods;
	}

	public void setDailyWorkPeriods(WorkPeriodList dailyWorkPeriods) {
		this.dailyWorkPeriods = dailyWorkPeriods;
	}

	public WorkPeriodList getWeeklyWorkPeriods() {
		return this.weeklyWorkPeriods;
	}

	public void setWeeklyWorkPeriods(WorkPeriodList weeklyWorkPeriods) {
		this.weeklyWorkPeriods = weeklyWorkPeriods;
	}

	public WorkPeriodList getBiweeklyWorkPeriods() {
		return this.biweeklyWorkPeriods;
	}

	public void setBiweeklyWorkPeriods(WorkPeriodList biweeklyWorkPeriods) {
		this.biweeklyWorkPeriods = biweeklyWorkPeriods;
	}


}