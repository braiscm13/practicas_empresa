package com.opentach.ws.common.rest.beans;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

	/**
	 * Unique identifier.
	 */
	protected String		id;

	/**
	 * Name.
	 */
	protected String		name;

	protected List<Option>	options;

	public Question() {}

	public Question(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public List<Option> getOptions() {
		return this.options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
}
