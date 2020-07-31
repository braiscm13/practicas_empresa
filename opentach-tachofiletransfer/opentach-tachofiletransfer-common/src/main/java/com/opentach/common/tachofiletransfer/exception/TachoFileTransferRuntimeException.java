package com.opentach.common.tachofiletransfer.exception;

import com.ontimize.jee.common.tools.MessageType;
import com.opentach.common.exception.OpentachRuntimeException;

/**
 * The Class ApplicationRunnableException.
 */
public class TachoFileTransferRuntimeException extends OpentachRuntimeException {

	public TachoFileTransferRuntimeException() {
		super();
	}

	public TachoFileTransferRuntimeException(String message, Object[] msgParameters, MessageType msgType,
			boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);

	}

	public TachoFileTransferRuntimeException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);

	}

	public TachoFileTransferRuntimeException(String message, Object[] msgParameters, Throwable cause, MessageType msgType,
			boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);

	}

	public TachoFileTransferRuntimeException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);

	}

	public TachoFileTransferRuntimeException(String message, Object[] msgParameters) {
		super(message, msgParameters);

	}

	public TachoFileTransferRuntimeException(String message, Throwable cause, MessageType msgType, boolean msgBlocking,
			boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public TachoFileTransferRuntimeException(String message, Throwable cause, Object[] msgParameters, MessageType msgType,
			boolean msgBlocking, boolean silent, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public TachoFileTransferRuntimeException(String message, Throwable cause) {
		super(message, cause);

	}

	public TachoFileTransferRuntimeException(String message) {
		super(message);

	}

	public TachoFileTransferRuntimeException(Throwable cause) {
		super(cause);
	}

}
