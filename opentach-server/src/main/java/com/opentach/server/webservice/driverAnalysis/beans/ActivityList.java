package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Listado de actividades.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "activities", propOrder = { "activities" })
public class ActivityList {

	@XmlElement(required = true, nillable = false, name = "activity")
	private List<Activity> activities;

	public List<Activity> getActivities() {
		return this.activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
}
