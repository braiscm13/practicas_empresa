package com.opentach.common.company.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.company.beans.LicensesBean;
import com.opentach.common.company.exception.LicenseException;
import com.opentach.common.company.naming.LicenseNaming;

/**
 * The Interface ILicenseService contains methods to manage company licenses.
 */
public interface ILicenseService {

	// *****************************************************************************************
	// ************************************** GENERAL *****************************************
	// *****************************************************************************************
	/**
	 * Returns all fields related with licenses, relatives to input company.
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Map<String, Object> getCompanyLicenseInfo(String cif) throws LicenseException;

	/**
	 * Returns a bean with all fields related with licenses, relatives to input company.
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	LicensesBean getCompanyLicenseBeanInfo(String cif) throws LicenseException;


	/**
	 * Send email to Openservices indicating this company wants more info about his product
	 *
	 * @param cif
	 * @param license
	 * @throws LicenseException
	 */
	void requestLicenseInfo(String cif, String license) throws LicenseException;

	/**
	 * Allow to start contract,and launch all events required to do that.
	 *
	 * @param license:
	 *            See {@link LicenseNaming.LIC_OPENTACH}, {@link LicenseNaming.LIC_TACHOLAB or {@link LicenseNaming.LIC_TACHOLABPLUS}}
	 */
	void startContract(String cif, String license, Date start, Date end) throws LicenseException;

	/**
	 * Allow to end contract,and launch all events required to do that.
	 *
	 * @param license:
	 *            See {@link LicenseNaming.LIC_OPENTACH}, {@link LicenseNaming.LIC_TACHOLAB or {@link LicenseNaming.LIC_TACHOLABPLUS}}
	 */
	void endContract(String cif, String license, Date end) throws LicenseException;

	/**
	 * Allow to start demo,and launch all events required to do that.
	 *
	 * @param license:
	 *            See {@link LicenseNaming.LIC_OPENTACH}, {@link LicenseNaming.LIC_TACHOLAB or {@link LicenseNaming.LIC_TACHOLABPLUS}}
	 */
	void startDemo(String cif, String license, Date start, Date end) throws LicenseException;

	/**
	 * Allow to end demo,and launch all events required to do that.
	 *
	 * @param license:
	 *            See {@link LicenseNaming.LIC_OPENTACH}, {@link LicenseNaming.LIC_TACHOLAB or {@link LicenseNaming.LIC_TACHOLABPLUS}}
	 */
	void endDemo(String cif, String license, Date end) throws LicenseException;

	// *****************************************************************************************
	// ************************************** OPENTACH *****************************************
	// *****************************************************************************************

	/**
	 * // * Returns if a company has currently access to OPENTACH app: that is has been purchased or active demo
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasOpentachAccess(String cif) throws LicenseException;

	/**
	 * Returns if a company has OPENTACH license: that is purchased
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasOpentachLicense(String cif) throws LicenseException;

	/**
	 * Returns if a company has OPENTACH active demo.
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasOpentachDemo(String cif) throws LicenseException;

	// *****************************************************************************************
	// ************************************** TACHOLAB *****************************************
	// *****************************************************************************************

	/**
	 * Returns if a company has currently access to TACHOLAB app: that is has been purchased or active demo
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasTacholabAccess(String cif) throws LicenseException;

	/**
	 * Returns if a company has TACHOLAB license: that is purchased
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasTacholabLicense(String cif) throws LicenseException;

	/**
	 * Returns if a company has TACHOLAB active demo.
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasTacholabDemo(String cif) throws LicenseException;

	// *****************************************************************************************
	// ************************************** TACHOLABPLUS *****************************************
	// *****************************************************************************************

	/**
	 * Returns if a company has currently access to TACHOLABPLUS app: that is has been purchased or active demo
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasTacholabPlusAccess(String cif) throws LicenseException;

	/**
	 * Returns if a company has TACHOLABPLUS license: that is purchased
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasTacholabPlusLicense(String cif) throws LicenseException;

	/**
	 * Returns if a company has TACHOLABPLUS active demo.
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasTacholabPlusDemo(String cif) throws LicenseException;

	/**
	 * Returns if a company has currently access to FICH@ app: that is has been purchased or active demo
	 *
	 * @param cif
	 * @return
	 * @throws LicenseException
	 */
	Boolean hasFichaAccess(String cif) throws LicenseException;

	// *****************************************************************************************
	// ************************************** TASKS *****************************************
	// *****************************************************************************************

	/**
	 * Query all expired demos (to fire Tasks to force clean invalid data)
	 *
	 * @param keysvalues
	 * @param attributes
	 * @return
	 * @throws LicenseException
	 */
	EntityResult expiredDemoQuery(Map<?, ?> keysValues, List<?> attributes) throws LicenseException;

	/**
	 * Query all expired licenses (to fire Tasks to force close access)
	 *
	 * @param keysvalues
	 * @param attributes
	 * @return
	 * @throws LicenseException
	 */
	EntityResult expiredLicenseQuery(Map<?, ?> keysValues, List<?> attributes) throws LicenseException;

	// *****************************************************************************************
	// ************************************** REPORTS *****************************************
	// *****************************************************************************************
	/** Query all companies (by the user) with all info about licenses/demos and expired date calculated cols. */
	EntityResult licensesReportQuery(Map<?, ?> keysValues, List<?> attributes) throws LicenseException;

}
