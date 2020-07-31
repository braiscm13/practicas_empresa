package com.opentach.ws.common.rest.beans;

import java.io.Serializable;

public class SendOptionsResponse implements Serializable {

	private Integer	correct;
	private Integer	wrong;

	public SendOptionsResponse() {}

	public SendOptionsResponse(Integer correct, Integer wrong) {
		super();
		this.correct = correct;
		this.wrong = wrong;
	}

	public Integer getCorrect() {
		return this.correct;
	}

	public void setCorrect(Integer correct) {
		this.correct = correct;
	}

	public Integer getWrong() {
		return this.wrong;
	}

	public void setWrong(Integer wrong) {
		this.wrong = wrong;
	}
}