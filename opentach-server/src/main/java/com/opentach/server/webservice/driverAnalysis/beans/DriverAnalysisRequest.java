package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Input bean received in "DriverAnalysis.analize" WS method.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "user", "pass", "driver", "initDate", "endDate" })
public class DriverAnalysisRequest {

	/**
	 * User identifier.
	 */
	private String	user;

	/**
	 * User password
	 */
	private String	pass;

	/**
	 * User password
	 */
	private String	driver;

	/**
	 * User password
	 */
	private Date	initDate;

	/**
	 * User password
	 */
	private Date	endDate;

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Date getInitDate() {
		return this.initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}