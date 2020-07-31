package com.opentach.server.webservice.company.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vehiclePlateList", propOrder = { "plate" })
public class VehiclePlateListResponse {
	private List<String> plate;

	public VehiclePlateListResponse() {
		this(null);
	}

	public VehiclePlateListResponse(List<String> list) {
		this.plate = list;
	}

	public List<String> getPlate() {
		return this.plate;
	}

	public void setPlate(List<String> plate) {
		this.plate = plate;
	}

}
