package com.opentach.common.surveys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class QuestionTO.
 */
public class QuestionTO implements Serializable {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;

	/** The id. */
	private Number					idQuest;
	private Number					idType;

	/** The title. */
	private String					title;

	/** The options. */
	private final List<OptionTO>	options				= new ArrayList<OptionTO>();

	/** The choice */
	private Integer					choice				= null;

	/** The choice */
	private String					correctOption		= null;

	public QuestionTO() {
		System.out.println();
	}

	public Number getIdType() {
		return this.idType;
	}

	public void setIdType(Number idType) {
		this.idType = idType;
	}

	/**
	 * Get the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Set the title.
	 *
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the id.
	 *
	 * @return the id
	 */
	public Number getId() {
		return this.idQuest;
	}

	/**
	 * Set the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(Number id) {
		this.idQuest = id;
	}

	/**
	 * Get the choice.
	 *
	 * @return the choice
	 */
	public Integer getChoice() {
		return this.choice;
	}

	/**
	 * Set the choice.
	 *
	 * @param choice
	 *            the choice to set
	 */
	public void setChoice(Integer choice) {
		this.choice = choice;
	}

	public String getCorrectOption() {
		return this.correctOption;
	}

	public void setCorrectOption(String correctOption) {
		this.correctOption = correctOption;
	}

	/**
	 * Get the option.
	 *
	 * @param index
	 *            the position
	 * @return the options
	 */
	public OptionTO getOption(int index) {
		return this.options.get(index);
	}

	/**
	 * Get the options.
	 *
	 * @return the options
	 */
	public List<OptionTO> getOptions() {
		return this.options;
	}

	/**
	 * Add the option.
	 *
	 * @param index
	 *            the position
	 * @param option
	 *            the option
	 */
	public void addOption(int index, OptionTO option) {
		this.options.add(index, option);
	}

	/**
	 * Update the option.
	 *
	 * @param index
	 *            the position
	 * @param option
	 *            the option
	 */
	public void updateOption(int index, OptionTO option) {
		this.options.set(index, option);
	}

	/**
	 * Remove the option.
	 *
	 * @param option
	 *            the option
	 */
	public void removeOption(int index) {
		this.options.remove(index);
	}

}
