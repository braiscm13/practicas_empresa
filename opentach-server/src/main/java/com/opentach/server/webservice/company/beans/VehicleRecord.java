package com.opentach.server.webservice.company.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vehicle", propOrder = { "plate", "creationDate" })
public class VehicleRecord {
	private String	plate;
	private String	creationDate;

	public VehicleRecord() {
		this(null, null);

	}

	public VehicleRecord(String plate, String date) {
		this.plate = plate;
		this.creationDate = date;
	}

	public String getPlate() {
		return this.plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

}
