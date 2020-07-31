package com.opentach.ws.common.rest.beans;

public class RestResponseQuestions implements IRestResponse {

	/** Code: -1: ERROR / 0: OK */
	protected int			code;

	/** Code description: In case of error, message to return. */
	protected String		codeDesc;

	/** The list of stations. */
	protected QuestionList	questionList;

	public RestResponseQuestions() {
		super();
		this.code = 0;
	}

	public RestResponseQuestions(int code, String codeDesc) {
		this();
		this.setCode(code);
		this.setCodeDesc(codeDesc);
	}

	public RestResponseQuestions(QuestionList questionList) {
		this();
		this.setQuestionList(questionList);
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

	public QuestionList getQuestionList() {
		return this.questionList;
	}

	public void setQuestionList(QuestionList questionList) {
		this.questionList = questionList;
	}

}