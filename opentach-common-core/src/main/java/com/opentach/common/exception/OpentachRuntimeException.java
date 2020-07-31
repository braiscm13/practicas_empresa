package com.opentach.common.exception;

import com.ontimize.jee.common.tools.MessageType;
import com.utilmize.tools.exception.URuntimeException;

public class OpentachRuntimeException extends URuntimeException {

	public OpentachRuntimeException() {
		super();

	}

	public OpentachRuntimeException(String message, Object... msgParameters) {
		super(message, msgParameters);

	}

	public OpentachRuntimeException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);

	}

	public OpentachRuntimeException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);

	}

	public OpentachRuntimeException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);

	}

	public OpentachRuntimeException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);

	}

	public OpentachRuntimeException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public OpentachRuntimeException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public OpentachRuntimeException(String message, Throwable cause) {
		super(message, cause);

	}

	public OpentachRuntimeException(String message) {
		super(message);

	}

	public OpentachRuntimeException(Throwable cause) {
		super(cause);

	}

}
