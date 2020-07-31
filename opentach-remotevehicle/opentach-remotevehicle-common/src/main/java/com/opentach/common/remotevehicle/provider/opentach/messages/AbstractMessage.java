package com.opentach.common.remotevehicle.provider.opentach.messages;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {

	// Common
	final static byte	FRAME_OK					= 0x04;
	final static byte	FRAME_KO					= 0x05;
	// client
	final static byte	FRAME_REGISTER				= 0x01;
	final static byte	FRAME_SEND_APDU_RESPONSE	= 0x06;
	// server
	final static byte	FRAME_CHECK					= 0x02;
	final static byte	FRAME_SEND_APDU				= 0x03;

	private final int	sessionId;
	private final int	code;

	public AbstractMessage(int sessionId, int code) {
		super();
		this.sessionId = sessionId;
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public int getSessionId() {
		return this.sessionId;
	}
}
