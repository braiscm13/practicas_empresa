package com.opentach.common.companies;

import java.rmi.Remote;

/**
 * The Interface IContractService.
 */
public interface ICompanyService extends Remote {
	/** The Constant ID. */
	final static String	ID	= "CompanyService";

	/**
	 * Hace a una empresa demo fija.
	 *
	 * @param idCompany
	 *            the id company
	 * @throws Exception
	 *             the exception
	 */
	void makeDemoCompanyFix(Object idCompany, int sessionId) throws Exception;
}
