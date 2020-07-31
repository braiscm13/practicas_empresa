package com.opentach.common.remotevehicle.provider.opentach.messages;

/**
 * The Class OkRemoteDownloadMessage.
 */
public class RegisterMessage extends AbstractMessage {

	/** The company code. */
	private final String	companyCode;

	/** The atr. */
	private final byte[]	atr;

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
	public RegisterMessage(int sessionId, String companyCode, byte[] atr) {
		super(sessionId, AbstractMessage.FRAME_REGISTER);
		this.companyCode = companyCode;
		this.atr = atr;
	}

	/**
	 * Gets the atr.
	 *
	 * @return the atr
	 */
	public byte[] getAtr() {
		return this.atr;
	}

	/**
	 * Gets the company code.
	 *
	 * @return the company code
	 */
	public String getCompanyCode() {
		return this.companyCode;
	}

}
