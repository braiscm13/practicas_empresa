package com.opentach.server.checktrucker.webservice.utils;

import java.io.Serializable;

/**
 * The Class OpentachWSException.
 */
public class OpentachNoDriverException extends Exception implements Serializable {

	public OpentachNoDriverException() {
		super();
	}

	public OpentachNoDriverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OpentachNoDriverException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpentachNoDriverException(String message) {
		super(message);
	}

	public OpentachNoDriverException(Throwable cause) {
		super(cause);
	}

}