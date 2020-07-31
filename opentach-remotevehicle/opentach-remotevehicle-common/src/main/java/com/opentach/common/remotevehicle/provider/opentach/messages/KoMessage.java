package com.opentach.common.remotevehicle.provider.opentach.messages;

/**
 * The Class OkRemoteDownloadMessage.
 */
public class KoMessage extends AbstractMessage {

	/**
	 * The Constructor.
	 *
	 * @param sessionId
	 *            the session id
	 */
	public KoMessage(int sessionId) {
		super(sessionId, AbstractMessage.FRAME_KO);
	}

}
