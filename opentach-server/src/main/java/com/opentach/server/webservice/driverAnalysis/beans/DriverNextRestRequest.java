package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Input bean received in "DriverAnalysis.analize" WS method.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "user", "pass", "driver", "initDate" })
public class DriverNextRestRequest {

	/**
	 * User identifier.
	 */
	private String	user;

	/**
	 * User password
	 */
	private String	pass;

	/**
	 * driver
	 */
	private String	driver;

	/**
	 * initDate
	 */
	private Date	initDate;


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

	public Date getDate() {
		return this.initDate;
	}

	public void setDate(Date initDate) {
		this.initDate = initDate;
	}
}