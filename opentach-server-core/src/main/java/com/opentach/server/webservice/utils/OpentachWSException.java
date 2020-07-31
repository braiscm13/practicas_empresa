package com.opentach.server.webservice.utils;

import java.io.Serializable;

/**
 * The Class OpentachWSException.
 */
public class OpentachWSException extends Exception implements Serializable {

	private final Integer	code;

	private final String	description;

	public OpentachWSException(Integer code, String desc) {
		super("ERROR");
		this.code = code;
		this.description = desc;
	}

	public Integer getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}
}