package com.opentach.client.comp.questionary;


public class QuestionChoiceEvent {

	private final QuestionModel	model;

	public QuestionChoiceEvent(QuestionModel model) {
		super();
		this.model = model;
	}

	/**
	 * @return the model
	 */
	public QuestionModel getModel() {
		return this.model;
	}

}
