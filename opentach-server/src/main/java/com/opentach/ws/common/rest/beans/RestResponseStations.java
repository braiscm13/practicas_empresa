package com.opentach.ws.common.rest.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RestResponseStations {

	/** Code: -1: ERROR / 0: OK */
	protected int						code;

	/** Code description: In case of error, message to return. */
	protected String					codeDesc;

	/** The list of stations. */
	protected StationList	stationList;

	public RestResponseStations() {
		super();
		this.code = 0;
	}

	public RestResponseStations(int code, String codeDesc) {
		this();
		this.setCode(code);
		this.setCodeDesc(codeDesc);
	}

	public RestResponseStations(StationList stationList) {
		this();
		this.setStationList(stationList);
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCodeDesc() {
		return this.codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	public StationList getStationList() {
		return this.stationList;
	}

	public void setStationList(StationList stationList) {
		this.stationList = stationList;
	}

}