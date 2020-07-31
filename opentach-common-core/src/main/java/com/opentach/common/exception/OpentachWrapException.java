package com.opentach.common.exception;

import com.ontimize.jee.common.tools.MessageType;

public class OpentachWrapException extends OpentachException {

	public OpentachWrapException() {
		super();
	}

	public OpentachWrapException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);
	}

	public OpentachWrapException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);
	}

	public OpentachWrapException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);
	}

	public OpentachWrapException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);
	}

	public OpentachWrapException(String message, Object[] msgParameters) {
		super(message, msgParameters);
	}

	public OpentachWrapException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public OpentachWrapException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public OpentachWrapException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpentachWrapException(String message) {
		super(message);
	}

	public OpentachWrapException(Throwable cause) {
		super(cause);
	}

}
