package com.opentach.ws.common.rest.beans;


public class Option {
	private String idOption;
	private String name;

	public Option() {
		super();
	}
	public Option(String idOption, String name) {
		this.idOption = idOption;
		this.name = name;
	}

	public String getIdOption() {
		return this.idOption;
	}

	public void setIdOption(String idOption) {
		this.idOption = idOption;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
