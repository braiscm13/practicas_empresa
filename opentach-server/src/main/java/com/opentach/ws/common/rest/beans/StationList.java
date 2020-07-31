package com.opentach.ws.common.rest.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StationList implements Serializable {

	protected List<Station> stations;

	public StationList() {
		this.stations = new ArrayList<Station>();
	}

	public List<Station> getStations() {
		return this.stations;
	}

	public void addStation(Station station) {
		this.stations.add(station);
	}
}