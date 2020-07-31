package com.opentach.ws.common.rest.beans;

import java.io.Serializable;

public class RestSurveysRequest implements Serializable {

	private String dni;

	public RestSurveysRequest() {
		super();
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}
}