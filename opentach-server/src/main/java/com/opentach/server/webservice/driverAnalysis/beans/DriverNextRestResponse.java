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
		name = "driverNextRestResponse",
		propOrder = { "infractions", "nextRestDriver" })
public class DriverNextRestResponse implements Serializable {
	private NextRest	nextRestDriver;


	public NextRest getDailyWorkPeriods() {
		return this.nextRestDriver;
	}

	public void setNextRestDriver(NextRest nextRestDriver) {
		this.nextRestDriver = nextRestDriver;
	}

}