package com.opentach.ws.common.rest.beans;

import java.io.Serializable;
import java.util.List;

public class RestOptionsSend implements Serializable {

	private String			idSurvey;
	private String			dni;
	private String			uuid;
	private List<Number>	options;

	public RestOptionsSend() {
		super();
	}

	public RestOptionsSend(String idSurvey, String dni, String uuid, List<Number> options) {
		super();
		this.idSurvey = idSurvey;
		this.dni = dni;
		this.uuid = uuid;
		this.options = options;
	}

	public String getIdSurvey() {
		return this.idSurvey;
	}

	public void setIdSurvey(String idSurvey) {
		this.idSurvey = idSurvey;
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String uuid) {
		this.dni = uuid;
	}

	public List<Number> getOptions() {
		return this.options;
	}

	public void setOptions(List<Number> options) {
		this.options = options;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}