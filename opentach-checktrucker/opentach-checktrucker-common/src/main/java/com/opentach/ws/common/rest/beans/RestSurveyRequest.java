package com.opentach.ws.common.rest.beans;

import java.io.Serializable;

public class RestSurveyRequest implements Serializable {

	private String idSurvey;

	public RestSurveyRequest() {
		super();
	}

	public String getIdSurvey() {
		return this.idSurvey;
	}

	public void setIdSurvey(String idSurvey) {
		this.idSurvey = idSurvey;
	}
}