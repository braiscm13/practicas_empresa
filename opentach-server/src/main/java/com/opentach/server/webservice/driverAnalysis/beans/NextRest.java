package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nextRest", propOrder = { "nextDate" })
public class NextRest {

	private Date	nextDate;

	public Date getInit() {
		return this.nextDate;
	}

	public void setInit(Date nD) {
		this.nextDate = nD;
	}

}
