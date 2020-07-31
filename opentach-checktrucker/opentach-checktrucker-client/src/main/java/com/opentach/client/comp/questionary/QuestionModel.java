package com.opentach.client.comp.questionary;

import java.util.ArrayList;
import java.util.List;

import com.opentach.client.comp.questionary.QuestionModelEvent.QuestionModelEventPart;
import com.opentach.client.comp.questionary.QuestionModelEvent.QuestionModelEventType;
import com.opentach.common.surveys.OptionTO;
import com.opentach.common.surveys.QuestionTO;

/**
 * The Class QuestionModel.
 */
public class QuestionModel {

	/** The modelTO. */
	private final QuestionTO							questionTO;

	/** The listeners. */
	private final List<IQuestionModelChangeListener>	listeners;

	/**
	 * Instantiates a question model.
	 *
	 * @param to
	 *            the QuestionTO
	 */
	public QuestionModel(QuestionTO to) {
		super();
		this.questionTO = to == null ? new QuestionTO() : to;
		this.listeners = new ArrayList<>();
	}

	/**
	 * Instantiates a question model.
	 *
	 * @param title
	 *            the title
	 * @param to
	 *            the QuestionTO
	 */
	public QuestionModel(QuestionTO to, String title) {
		this(to);
		this.questionTO.setTitle(title);
	}

	/**
	 * Get the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return this.questionTO.getTitle();
	}

	/**
	 * Set the title.
	 *
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.questionTO.setTitle(title);
		this.fireQuestionModelChange(new QuestionModelEvent(this, null, QuestionModelEventPart.TITLE, -1));
	}

	/**
	 * Get the QuestionTO.
	 *
	 * @return the QuestionTO
	 */
	public QuestionTO getQuestionTO() {
		return this.questionTO;
	}

	/**
	 * Get the id Question.
	 *
	 * @return the id Question
	 */
	public Object getId() {
		return this.questionTO.getId();
	}

	/**
	 * Set the id Question.
	 *
	 * @param id
	 *            the id Question to set
	 */
	public void setId(Number id) {
		this.questionTO.setId(id);
	}

	/**
	 * Get the id Question Classifier.
	 *
	 * @return the id Question Classifier
	 */
	public Number getIdType() {
		return this.questionTO.getIdType();
	}

	/**
	 * Set the id Question Classifier.
	 *
	 * @param id
	 *            the id Question Classifier to set
	 */
	public void setIdClass(Number id) {
		this.questionTO.setIdType(id);
	}

	/**
	 * Get the choice.
	 *
	 * @return the choice
	 */
	public Integer getChoice() {
		return this.questionTO.getChoice();
	}

	/**
	 * Set the choice.
	 *
	 * @param choice
	 *            the choice to set
	 */
	public void setChoice(Integer choice) {
		this.questionTO.setChoice(choice);
		this.fireQuestionModelChange(new QuestionModelEvent(this, QuestionModelEventType.SELECTION_CHANGE, QuestionModelEventPart.OPT, choice));
	}

	public void setCorrectOption(String correctOption) {
		this.questionTO.setCorrectOption(correctOption);
	}

	public String getCorrectOption() {
		return this.questionTO.getCorrectOption();
	}

	/**
	 * Get the option.
	 *
	 * @return the option
	 */
	public String getOption(int index) {
		return this.questionTO.getOption(index).getOptionText();
	}

	/**
	 * Get the options.
	 *
	 * @return the options
	 */
	public List<String> getOptions() {
		List<String> lOptions = new ArrayList<String>();
		for (OptionTO option : this.questionTO.getOptions()) {
			lOptions.add(option.getOptionText());
		}
		return lOptions;
	}

	@Override
	public int hashCode() {
		return this.questionTO.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.questionTO.equals(obj);
	}

	@Override
	public String toString() {
		return this.questionTO.toString();
	}

	/**
	 * Add the option.
	 *
	 * @param option
	 *            the option
	 */
	public void addOption(int index, String option) {
		this.questionTO.addOption(index, new OptionTO(null, option));
		this.fireQuestionModelChange(new QuestionModelEvent(this, QuestionModelEventType.NEW, QuestionModelEventPart.OPT, index));

	}

	/**
	 * Add the options.
	 *
	 * @param options
	 *            the options
	 */
	public void addOptions(List<String> options) {
		for (String option : options) {
			this.addOption(options.indexOf(option), option);
		}
	}

	/**
	 * Update the option.
	 *
	 * @param option
	 *            the option
	 */
	public void updateOption(int index, String option) {
		this.questionTO.updateOption(index, new OptionTO(null, option));
		this.fireQuestionModelChange(new QuestionModelEvent(this, QuestionModelEventType.UPDATE, QuestionModelEventPart.OPT, index));
	}

	/**
	 * Remove the option.
	 *
	 * @param option
	 *            the option
	 */
	public void removeOption(int index) {
		this.questionTO.removeOption(index);
		this.fireQuestionModelChange(new QuestionModelEvent(this, QuestionModelEventType.REMOVE, QuestionModelEventPart.OPT, index));
	}

	/**
	 * Remove the options.
	 *
	 * @param options
	 *            the option
	 */
	public void removeOptions(int[] index) {
		for (int i : index) {
			this.removeOption(i);
		}
	}

	/**
	 * Remove the options.
	 *
	 * @param options
	 *            the option
	 */
	public void removeOptions() {
		this.questionTO.getOptions().clear();
	}

	/**
	 * Add the question model listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void addQuestionModelListener(IQuestionModelChangeListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove the question model listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void removeQuestionModelListener(IQuestionModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Fire question model change.
	 *
	 * @param event
	 *            the event
	 */
	protected void fireQuestionModelChange(QuestionModelEvent event) {
		for (IQuestionModelChangeListener listener : this.listeners) {
			listener.modelQuestionChanged(event);
		}
	}

}
