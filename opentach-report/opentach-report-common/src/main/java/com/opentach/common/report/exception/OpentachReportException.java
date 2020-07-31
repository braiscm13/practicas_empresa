package com.opentach.common.report.exception;

import com.ontimize.jee.common.tools.MessageType;
import com.opentach.common.exception.OpentachException;

public class OpentachReportException extends OpentachException {

	private static final long serialVersionUID = 1L;

	public OpentachReportException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);
	}

	public OpentachReportException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);
	}

	public OpentachReportException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);
	}

	public OpentachReportException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);
	}

	public OpentachReportException(String message, Object[] msgParameters) {
		super(message, msgParameters);
	}

	public OpentachReportException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public OpentachReportException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public OpentachReportException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpentachReportException(String message) {
		super(message);
	}

	public OpentachReportException(Throwable cause) {
		super(cause);
	}

}
