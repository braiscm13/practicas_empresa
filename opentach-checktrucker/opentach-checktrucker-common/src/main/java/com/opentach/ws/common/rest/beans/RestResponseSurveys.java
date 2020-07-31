package com.opentach.ws.common.rest.beans;

public class RestResponseSurveys implements IRestResponse {

	/** Code: -1: ERROR / 0: OK */
	protected int			code;

	/** Code description: In case of error, message to return. */
	protected String		codeDesc;

	/** The list of stations. */
	protected SurveyList	surveyList;

	public RestResponseSurveys() {
		super();
		this.code = 0;
	}

	public RestResponseSurveys(int code, String codeDesc) {
		this();
		this.setCode(code);
		this.setCodeDesc(codeDesc);
	}

	public RestResponseSurveys(SurveyList surveyList) {
		this();
		this.setSurveyList(surveyList);
	}

	@Override
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String getCodeDesc() {
		return this.codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	public SurveyList getSurveyList() {
		return this.surveyList;
	}

	public void setSurveyList(SurveyList surveyList) {
		this.surveyList = surveyList;
	}

}