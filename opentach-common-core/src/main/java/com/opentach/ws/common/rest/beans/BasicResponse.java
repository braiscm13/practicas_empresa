package com.opentach.ws.common.rest.beans;

import java.io.Serializable;

public class BasicResponse implements Serializable {

	private Integer	code;

	private String	description;

	public BasicResponse() {
		super();
	}

	public Integer getCode() {
		return this.code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
