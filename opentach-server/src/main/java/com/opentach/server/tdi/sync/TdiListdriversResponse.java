package com.opentach.server.tdi.sync;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TdiListdriversResponse {
	@JsonProperty(value = "DRIVERS")
	private List<TdiDriverQueryBean>	drivers;

	public TdiListdriversResponse() {
		super();
		this.drivers = new ArrayList<TdiDriverQueryBean>();
	}

	public List<TdiDriverQueryBean> getDrivers() {
		return this.drivers;
	}

	public void setDrivers(List<TdiDriverQueryBean> drivers) {
		this.drivers = drivers;
	}

}
