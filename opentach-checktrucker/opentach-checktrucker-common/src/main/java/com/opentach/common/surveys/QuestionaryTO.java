package com.opentach.common.surveys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class QuestionaryTO.
 */
public class QuestionaryTO implements Serializable {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;

	/** The id Survey. */
	private Number					idSurvey;

	/** The questions. */
	private final List<QuestionTO>	questions			= new ArrayList<QuestionTO>();

	/**
	 * Get the id Survey.
	 *
	 * @return the id Survey
	 */
	public Number getId() {
		return this.idSurvey;
	}

	/**
	 * Set the idSurvey.
	 *
	 * @param id
	 *            the new id Survey
	 */
	public void setId(Number id) {
		this.idSurvey = id;
	}

	/**
	 * Get the question.
	 *
	 * @param index
	 *            the position
	 * @return the question
	 */
	public QuestionTO getQuestion(int index) {
		return this.questions.get(index);
	}

	/**
	 * Get the questions.
	 *
	 * @return the questions
	 */
	public List<QuestionTO> getQuestions() {
		return this.questions;
	}

	/**
	 * Add the question.
	 *
	 * @param index
	 *            the position
	 * @param question
	 *            the question
	 */
	public void addQuestion(int index, QuestionTO question) {
		this.questions.add(index, question);
	}

	/**
	 * Add the questions.
	 *
	 * @param questions
	 *            the questions
	 */
	public void addQuestions(List<QuestionTO> questions) {
		this.questions.addAll(questions);
	}

	/**
	 * Remove the question.
	 *
	 * @param index
	 *            the position
	 */
	public void removeQuestion(int index) {
		this.questions.remove(index);
	}

	/**
	 * Remove the questions.
	 *
	 */
	public void removeQuestions() {
		this.questions.clear();
	}

}
