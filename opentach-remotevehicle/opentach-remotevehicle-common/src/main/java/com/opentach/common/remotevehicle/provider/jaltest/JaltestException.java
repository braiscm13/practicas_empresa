package com.opentach.common.remotevehicle.provider.jaltest;

import com.ontimize.jee.common.tools.MessageType;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;

public class JaltestException extends RemoteVehicleException {

	public JaltestException() {
		super();
	}

	public JaltestException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);
	}

	public JaltestException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);
	}

	public JaltestException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);
	}

	public JaltestException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);
	}

	public JaltestException(String message, Object[] msgParameters) {
		super(message, msgParameters);
	}

	public JaltestException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public JaltestException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public JaltestException(String message, Throwable cause) {
		super(message, cause);
	}

	public JaltestException(String message) {
		super(message);
	}

	public JaltestException(Throwable cause) {
		super(cause);
	}


}
