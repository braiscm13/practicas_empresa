package com.opentach.common.exception;

import com.ontimize.jee.common.tools.MessageType;

public class OpentachSecurityException extends OpentachRuntimeException {

	public OpentachSecurityException() {
		super();

	}

	public OpentachSecurityException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);

	}

	public OpentachSecurityException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);

	}

	public OpentachSecurityException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);

	}

	public OpentachSecurityException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);

	}

	public OpentachSecurityException(String message, Object[] msgParameters) {
		super(message, msgParameters);

	}

	public OpentachSecurityException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public OpentachSecurityException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public OpentachSecurityException(String message, Throwable cause) {
		super(message, cause);

	}

	public OpentachSecurityException(String message) {
		super(message);

	}

	public OpentachSecurityException(Throwable cause) {
		super(cause);

	}

}
