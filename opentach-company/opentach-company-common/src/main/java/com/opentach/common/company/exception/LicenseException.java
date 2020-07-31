package com.opentach.common.company.exception;

import com.ontimize.jee.common.tools.MessageType;
import com.opentach.common.exception.OpentachException;

public class LicenseException extends OpentachException {

	private static final long serialVersionUID = 1L;

	public LicenseException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);

	}

	public LicenseException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);

	}

	public LicenseException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);

	}

	public LicenseException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);

	}

	public LicenseException(String message, Object[] msgParameters) {
		super(message, msgParameters);

	}

	public LicenseException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public LicenseException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public LicenseException(String message, Throwable cause) {
		super(message, cause);

	}

	public LicenseException(String message) {
		super(message);

	}

	public LicenseException(Throwable cause) {
		super(cause);

	}
}