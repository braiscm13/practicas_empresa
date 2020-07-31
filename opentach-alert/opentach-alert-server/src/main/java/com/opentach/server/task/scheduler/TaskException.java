package com.opentach.server.task.scheduler;

import com.ontimize.jee.common.tools.MessageType;
import com.opentach.common.exception.OpentachException;

public class TaskException extends OpentachException {

	public TaskException() {
		super();
	}

	public TaskException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);
	}

	public TaskException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);
	}

	public TaskException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);
	}

	public TaskException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);
	}

	public TaskException(String message, Object[] msgParameters) {
		super(message, msgParameters);
	}

	public TaskException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public TaskException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskException(String message) {
		super(message);
	}

	public TaskException(Throwable cause) {
		super(cause);
	}

}
