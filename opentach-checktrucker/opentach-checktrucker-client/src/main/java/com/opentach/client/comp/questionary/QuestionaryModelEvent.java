package com.opentach.client.comp.questionary;

public class QuestionaryModelEvent {
	public static enum QuestionaryModelEventType {
		NEW, REMOVE, NULL
	}

	private final QuestionaryModel			model;
	private final QuestionaryModelEventType	type;
	private final int						questIndex;

	public QuestionaryModelEvent(QuestionaryModel model, QuestionaryModelEventType type, int questIndex) {
		super();
		this.model = model;
		this.type = type;
		this.questIndex = questIndex;
	}

	/**
	 * @return the model
	 */
	public QuestionaryModel getModel() {
		return this.model;
	}

	/**
	 * @return the type
	 */
	public QuestionaryModelEventType getType() {
		return this.type;
	}

	/**
	 * @return the barIndex
	 */
	public int getOptIndex() {
		return this.questIndex;
	}

}
