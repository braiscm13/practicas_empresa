package com.opentach.client.comp.questionary;

public class QuestionModelEvent {
	public static enum QuestionModelEventType {
		UPDATE, NEW, REMOVE, SELECTION_CHANGE
	}

	public static enum QuestionModelEventPart {
		TITLE, OPT
	}

	private final QuestionModel				model;
	private final QuestionModelEventType	type;
	private final QuestionModelEventPart	part;
	private final int						optIndex;

	public QuestionModelEvent(QuestionModel model, QuestionModelEventType type, QuestionModelEventPart part, int optIndex) {
		super();
		this.model = model;
		this.type = type;
		this.part = part;
		this.optIndex = optIndex;
	}

	/**
	 * @return the model
	 */
	public QuestionModel getModel() {
		return this.model;
	}

	/**
	 * @return the type
	 */
	public QuestionModelEventType getType() {
		return this.type;
	}

	/**
	 * @return the part
	 */
	public QuestionModelEventPart getPart() {
		return this.part;
	}

	/**
	 * @return the optIndex
	 */
	public int getOptIndex() {
		return this.optIndex;
	}

}
