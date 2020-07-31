package com.opentach.common.company.service;

import com.opentach.common.company.exception.CompanyException;

/**
 * The Interface ICompanyService contains methods to manage company and related.
 */
public interface ICompanyService {
	/**
	 * Fast method to get CIF of a company searching by name. Legacy support, its a bad idea use this.
	 *
	 * @param companyName
	 * @return
	 * @throws CompanyException
	 * @deprecated This field must be always present and unique, but name can be repeated
	 */
	@Deprecated
	String getCompanyCif(String companyName) throws CompanyException;

	/**
	 * Fast method to rescue LEGACY demo info of this company (in OPENTACH)
	 *
	 * @param cif
	 * @return
	 * @throws CompanyException
	 * @deprecated See {@link ILicenseService}
	 */
	@Deprecated
	boolean isCompanyDemo(String cif) throws CompanyException;

	/**
	 * Returns the current contract of this company (must exists one o none)
	 * 
	 * @param cif
	 * @return
	 * @throws CompanyException
	 */
	String getCompanyContract(String cif) throws CompanyException;
}
