package com.opentach.common.surveys;

import java.io.Serializable;

/**
 * The Class QuestionTO.
 */
public class ResultsSurvey implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private Integer				correct;
	private Integer				wrong;

	public ResultsSurvey() {}

	public ResultsSurvey(Integer correct, Integer wrong) {
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

	public Number getTotal() {
		Integer total = 0;
		if (this.correct != null) {
			total += this.correct;
		}
		if (this.wrong != null) {
			total += this.wrong;
		}
		return total;
	}
}
