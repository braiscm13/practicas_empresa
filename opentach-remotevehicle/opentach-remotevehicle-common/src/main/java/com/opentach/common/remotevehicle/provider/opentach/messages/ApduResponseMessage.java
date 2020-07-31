package com.opentach.common.remotevehicle.provider.opentach.messages;

/**
 * The Class ApduResponseRemoteDownloadMessage.
 */
public class ApduResponseMessage extends AbstractMessage {

	/** The atr. */
	private final byte[]	apdu;

	/**
	 * The Constructor.
	 *
	 * @param sessionId
	 *            the session id
	 * @param apdu
	 *            the apdu
	 */
	public ApduResponseMessage(int sessionId, byte[] apdu) {
		super(sessionId, AbstractMessage.FRAME_SEND_APDU_RESPONSE);
		this.apdu = apdu;
	}

	/**
	 * Gets the apdu.
	 *
	 * @return the apdu
	 */
	public byte[] getApdu() {
		return this.apdu;
	}

}
