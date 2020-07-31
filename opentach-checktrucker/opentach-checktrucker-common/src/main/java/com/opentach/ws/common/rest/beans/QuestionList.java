package com.opentach.ws.common.rest.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionList implements Serializable {

	protected List<Question> questions;

	public QuestionList() {
		this.questions = new ArrayList<Question>();
	}

	public List<Question> getQuestions() {
		return this.questions;
	}

	public void addQuestion(Question question) {
		this.questions.add(question);
	}
}