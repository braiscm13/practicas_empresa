package com.opentach.common.surveys;

import java.io.Serializable;

/**
 * The Class QuestionTO.
 */
public class OptionTO implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	/** The id. */
	private Number				idOption;
	private Number				idQuestion;
	private String				optionText;

	public OptionTO() {}

	public OptionTO(Number idOption, String optionText) {
		super();
		this.idOption = idOption;
		this.optionText = optionText;
	}

	public OptionTO(Number idOption, Number idQuestion, String optionText) {
		super();
		this.idOption = idOption;
		this.idQuestion = idQuestion;
		this.optionText = optionText;
	}

	public Number getIdOption() {
		return this.idOption;
	}

	public void setIdOption(Number idOption) {
		this.idOption = idOption;
	}

	public String getOptionText() {
		return this.optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public Number getIdQuestion() {
		return this.idQuestion;
	}

	public void setIdQuestion(Number idQuestion) {
		this.idQuestion = idQuestion;
	}

}
