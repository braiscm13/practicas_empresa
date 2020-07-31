package com.opentach.server.webservice.company.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vehicleList", propOrder = { "plate" })
public class VehicleListResponse {

	private List<VehicleRecord> plate;

	public VehicleListResponse() {
		super();
		this.plate = new ArrayList<>();
	}

	public void add(VehicleRecord vehicleRecord) {
		this.plate.add(vehicleRecord);
	}

	public List<VehicleRecord> getPlate() {
		return this.plate;
	}

	public void setPlate(List<VehicleRecord> plate) {
		this.plate = plate;
	}

}
