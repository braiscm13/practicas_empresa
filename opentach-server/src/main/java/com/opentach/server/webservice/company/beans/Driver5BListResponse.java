package com.opentach.server.webservice.company.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "driver5BList", propOrder = { "driver" })
public class Driver5BListResponse {

	protected List<String> driver;

	public Driver5BListResponse() {
		this(null);
	}

	public Driver5BListResponse(List<String> list) {
		this.driver = list;
	}

	public List<String> getDriver() {
		return this.driver;
	}

	public void setDriver(List<String> drivers) {
		this.driver = drivers;
	}

}
