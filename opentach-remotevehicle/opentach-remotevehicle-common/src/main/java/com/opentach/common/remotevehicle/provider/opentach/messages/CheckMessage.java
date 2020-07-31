package com.opentach.common.remotevehicle.provider.opentach.messages;

/**
 * The Class OkRemoteDownloadMessage.
 */
public class CheckMessage extends AbstractMessage {

	/**
	 * The Constructor.
	 *
	 * @param sessionId
	 *            the session id
	 * @param companyCode
	 *            the company code
	 * @param atr
	 *            the atr
	 */
	public CheckMessage(int sessionId) {
		super(sessionId, AbstractMessage.FRAME_CHECK);
	}
}
