package com.opentach.common.callback;

public interface WebsocketMessageTypeOpentach {

	/** The Constant AUTOPASS_CALLBACK_TYPE. */
	public static final Integer OPENTACH_CALLBACK_TYPE = 100;

	/**
	 * The Enum WebsocketSubmessageType.
	 */
	public enum WebsocketSubmessageTypeOpentach {

		/** The save log. */
		SAVE_LOG("SAVE_LOG");

		/** The value. */
		String value;

		/**
		 * Instantiates a new websocket submessage type.
		 *
		 * @param value
		 *            the value
		 */
		WebsocketSubmessageTypeOpentach(String value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String value() {
			return this.value;
		}
	}
}