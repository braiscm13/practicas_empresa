package com.opentach.ws.common.rest.exceptions;

import java.io.Serializable;

/**
 * The Class RestRequestException.
 */
public class RestRequestException extends Exception implements Serializable {

	private final Integer	code;

	private final String	description;

	public RestRequestException(Integer code, String desc) {
		super(desc);
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