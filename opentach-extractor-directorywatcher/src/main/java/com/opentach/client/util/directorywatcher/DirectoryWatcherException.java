package com.opentach.client.util.directorywatcher;

import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.tools.MessageType;

public class DirectoryWatcherException extends OntimizeJEEException {

	public DirectoryWatcherException() {
		super();

	}

	public DirectoryWatcherException(String message, Object... msgParameters) {
		super(message, msgParameters);

	}

	public DirectoryWatcherException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
		super(message, msgParameters, msgType, msgBlocking);

	}

	public DirectoryWatcherException(String message, Object[] msgParameters, MessageType messageType) {
		super(message, msgParameters, messageType);

	}

	public DirectoryWatcherException(String message, Object[] msgParameters, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent) {
		super(message, msgParameters, cause, msgType, msgBlocking, silent);

	}

	public DirectoryWatcherException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);

	}

	public DirectoryWatcherException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public DirectoryWatcherException(String message, Throwable cause, Object[] msgParameters, MessageType msgType, boolean msgBlocking, boolean silent, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

	}

	public DirectoryWatcherException(String message, Throwable cause) {
		super(message, cause);

	}

	public DirectoryWatcherException(String message) {
		super(message);

	}

	public DirectoryWatcherException(Throwable cause) {
		super(cause);

	}

}
