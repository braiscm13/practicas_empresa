package com.opentach.client.comp.questionary;

import java.util.ArrayList;
import java.util.List;

import com.opentach.client.comp.questionary.QuestionaryModelEvent.QuestionaryModelEventType;
import com.opentach.common.surveys.QuestionTO;
import com.opentach.common.surveys.QuestionaryTO;

/**
 * The Class QuestionaryModel.
 */
public class QuestionaryModel {

	/** The modelTO. */
	private final QuestionaryTO							questionaryTO;

	/** The listeners. */
	private final List<IQuestionaryModelChangeListener>	listeners;

	/**
	 * Instantiates a questionary model.
	 */
	public QuestionaryModel(QuestionaryTO to) {
		super();
		this.questionaryTO = to == null ? new QuestionaryTO() : to;
		this.listeners = new ArrayList<>();
	}

	/**
	 * Get the QuestionaryTO,
	 *
	 * @return the QuestionaryTO
	 */
	public QuestionaryTO getQuestionaryTO() {
		return this.questionaryTO;
	}

	/**
	 * Set the id Work Unit.
	 *
	 * @param id
	 *            the id Work Unit
	 */
	public void setId(Number id) {
		this.questionaryTO.setId(id);
	}

	/**
	 * Get the id Work Unit.
	 *
	 * @return the id Work Unit
	 */
	public Number getId() {
		return this.questionaryTO.getId();
	}

	/**
	 * Get the question.
	 *
	 * @return the questionTO
	 */
	public QuestionTO getQuestion(int index) {
		return this.questionaryTO.getQuestion(index);
	}

	/**
	 * Get the questions.
	 *
	 * @return the questionsTO
	 */
	public List<QuestionTO> getQuestions() {
		return this.questionaryTO.getQuestions();
	}

	/**
	 * Add the question.
	 *
	 * @param index
	 *            the position
	 * @param question
	 *            the questionTO
	 */
	public void addQuestion(int index, QuestionTO question) {
		this.questionaryTO.addQuestion(index, question);
		this.fireQuestionaryModelChange(new QuestionaryModelEvent(this, QuestionaryModelEventType.NEW, index));
	}

	/**
	 * Adds the questions.
	 *
	 * @param questions
	 *            the questionsTO
	 */
	public void addQuestions(List<QuestionTO> questions) {
		for (QuestionTO questionTO : questions) {
			this.addQuestion(this.questionaryTO.getQuestions().size(), questionTO);
		}
	}

	/**
	 * Remove the question.
	 *
	 * @param index
	 *            the position
	 */
	public void removeQuestion(int index) {
		this.questionaryTO.removeQuestion(index);
		this.fireQuestionaryModelChange(new QuestionaryModelEvent(this, QuestionaryModelEventType.REMOVE, index));
	}

	/**
	 * Remove the questions.
	 *
	 */
	public void removeQuestions() {
		while (!this.questionaryTO.getQuestions().isEmpty()) {
			this.removeQuestion(0);
		}
	}

	/**
	 * Fire question model change.
	 *
	 * @param event
	 *            the event
	 */
	protected void fireQuestionaryModelChange(QuestionaryModelEvent event) {
		for (IQuestionaryModelChangeListener listener : this.listeners) {
			listener.modelQuestionaryChanged(event);
		}
	}

	/**
	 * Adds the question model listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void addQuestionaryModelListener(IQuestionaryModelChangeListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes the question model listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void removeQuestionaryModelListener(IQuestionaryModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public int hashCode() {
		return this.questionaryTO.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.questionaryTO.equals(obj);
	}

	@Override
	public String toString() {
		return this.questionaryTO.toString();
	}
}
