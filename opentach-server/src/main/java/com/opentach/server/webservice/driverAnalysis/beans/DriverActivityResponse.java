package com.opentach.server.webservice.driverAnalysis.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Extended class with other values auto-calculated.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
		name = "driverActivityResponse",
		propOrder = { "activities" })
public class DriverActivityResponse implements Serializable {

	// 1.- Infracciones.
	@XmlElement(required = true, nillable = false, name = "activities")
	private ActivityList activities;

	public ActivityList getActivities() {
		return this.activities;
	}

	public void setActivities(ActivityList activities) {
		this.activities = activities;
	}

}
