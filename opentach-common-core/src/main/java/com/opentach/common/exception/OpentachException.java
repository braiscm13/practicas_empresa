package com.opentach.common.exception;

import com.ontimize.jee.common.tools.MessageType;
import com.utilmize.tools.exception.UException;

public class OpentachException extends UException {

	public OpentachException() {
		super();

	}

	public OpentachException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);

	}

	public OpentachException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);

	}

	public OpentachException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);

	}

	public OpentachException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);

	}

	public OpentachException(String message, Object[] msgParameters) {
		super(message, msgParameters);

	}

	public OpentachException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public OpentachException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, 
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public OpentachException(String message, Throwable cause) {
		super(message, cause);

	}

	public OpentachException(String message) {
		super(message);

	}

	public OpentachException(Throwable cause) {
		super(cause);

	}

}
