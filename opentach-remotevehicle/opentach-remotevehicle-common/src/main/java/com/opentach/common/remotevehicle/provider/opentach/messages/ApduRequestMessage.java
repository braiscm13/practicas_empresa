package com.opentach.common.remotevehicle.provider.opentach.messages;

/**
 * The Class ApduResponseRemoteDownloadMessage.
 */
public class ApduRequestMessage extends AbstractMessage {

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
	public ApduRequestMessage(int sessionId, byte[] apdu) {
		super(sessionId, AbstractMessage.FRAME_SEND_APDU);
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
