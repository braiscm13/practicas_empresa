package com.opentach.common.remotevehicle.provider.opentach.messages;

/**
 * The Class OkRemoteDownloadMessage.
 */
public class OkMessage extends AbstractMessage {

	/**
	 * The Constructor.
	 *
	 * @param sessionId
	 *            the session id
	 */
	public OkMessage(int sessionId) {
		super(sessionId, AbstractMessage.FRAME_OK);
	}

}
