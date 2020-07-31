package com.opentach.ws.common.rest.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SurveyList implements Serializable {

	protected List<Survey> survey;

	public SurveyList() {
		this.survey = new ArrayList<Survey>();
	}

	public List<Survey> getSurveys() {
		return this.survey;
	}

	public void addSurvey(Survey survey) {
		this.survey.add(survey);
	}
}