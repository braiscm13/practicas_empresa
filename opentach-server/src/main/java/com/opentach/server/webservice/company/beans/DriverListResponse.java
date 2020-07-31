package com.opentach.server.webservice.company.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "driverList", propOrder = { "driver" })
public class DriverListResponse {

	protected List<DriverRecord> driver;

	public DriverListResponse() {
		super();
		this.driver = new ArrayList<>();
	}

	public void add(DriverRecord driverRecord) {
		this.driver.add(driverRecord);

	}

	public List<DriverRecord> getDriver() {
		return this.driver;
	}

	public void setDriver(List<DriverRecord> driver) {
		this.driver = driver;
	}


}
