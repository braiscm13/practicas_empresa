package com.opentach.common;

/**
 * The Class MessageCodeException.
 */
public class MessageCodeException extends Exception {

	/** The code. */
	private final int code;


	/**
	 * Instantiates a new message code exception.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 */
	public MessageCodeException(String message, int code) {
		this(message, code, null);
	}

	/**
	 * Instantiates a new message code exception.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 * @param cause
	 *            the cause
	 */
	public MessageCodeException(String message, int code, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

}
