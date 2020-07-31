package com.opentach.common.smartphonevipcodes;

import java.rmi.Remote;

/**
 * The Interface ISmartphoneVipCodeService.
 */
public interface ISmartphoneVipCodeService extends Remote {

	/**
	 * Generate codes.
	 *
	 * @param numCodes
	 *            the num codes
	 * @throws Exception
	 *             the exception
	 */
	void generateCodes(int numCodes) throws Exception;

}
