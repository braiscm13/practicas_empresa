package com.opentach.common.remotevehicle.exceptions;

import com.ontimize.jee.common.tools.MessageType;
import com.opentach.common.exception.OpentachException;

public class RemoteVehicleException extends OpentachException {

	public RemoteVehicleException() {
		super();
	}

	public RemoteVehicleException(String message) {
		super(message);
	}

	public RemoteVehicleException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public RemoteVehicleException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoteVehicleException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);
	}

	public RemoteVehicleException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);
	}

	public RemoteVehicleException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);
	}

	public RemoteVehicleException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);
	}

	public RemoteVehicleException(String message, Object[] msgParameters) {
		super(message, msgParameters);
	}

	public RemoteVehicleException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}

	public RemoteVehicleException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);
	}


}
