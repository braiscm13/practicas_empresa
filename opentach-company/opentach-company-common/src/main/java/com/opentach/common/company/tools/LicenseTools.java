package com.opentach.common.company.tools;

import java.util.Date;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.common.company.beans.LicensesBean;
import com.opentach.common.company.exception.LicenseException;

//@formatter:off
/**
 * This tools allow to check company license permissions, based on its LicenseBean (see IlicenseService.getCompanyLicenseBeanInfo method).
 * Can be considered for each license:
 *  · "access" : that is has been purchased/license  or  active demo
 *  · "purchased/licensed": formal facturable client
 *  · "demo": only temporary demo
 */
// @formatter:on
public class LicenseTools {

	private LicenseTools() {
		// Nothing
	}

	// *****************************************************************************************
	// ************************************** OPENTACH *****************************************
	// *****************************************************************************************

	public static Boolean hasOpentachAccess(LicensesBean licensesInfo) throws LicenseException {
		return ParseUtilsExtended.getBoolean(LicenseTools.hasOpentachLicense(licensesInfo), false) //
				|| ParseUtilsExtended.getBoolean(LicenseTools.hasOpentachDemo(licensesInfo), false);
	}

	public static Boolean hasOpentachLicense(LicensesBean licensesInfo) throws LicenseException {
		if (licensesInfo == null) {
			return null;
		}
		return LicenseTools.checkLicense(licensesInfo.hasOpentach(), licensesInfo.getOpentachFrom(), licensesInfo.getOpentachTo());
	}

	public static Boolean hasOpentachDemo(LicensesBean licensesInfo) throws LicenseException {
		if (licensesInfo == null) {
			return null;
		}
		return LicenseTools.checkLicense(licensesInfo.hasOpentachDemo(), licensesInfo.getOpentachDemoFrom(), licensesInfo.getOpentachDemoTo());
	}

	// *****************************************************************************************
	// ************************************** TACHOLAB *****************************************
	// *****************************************************************************************

	public static Boolean hasTacholabAccess(LicensesBean licensesInfo) throws LicenseException {
		return ParseUtilsExtended.getBoolean(LicenseTools.hasTacholabLicense(licensesInfo), false) //
				|| ParseUtilsExtended.getBoolean(LicenseTools.hasTacholabDemo(licensesInfo), false);
	}

	public static Boolean hasTacholabLicense(LicensesBean licensesInfo) throws LicenseException {
		// if (1 == 1) {// TODO TEMP
		// return false;
		// }
		if (licensesInfo == null) {
			return null;
		}
		return LicenseTools.checkLicense(licensesInfo.hasTacholab(), licensesInfo.getTacholabFrom(), licensesInfo.getTacholabTo());
	}

	public static Boolean hasTacholabDemo(LicensesBean licensesInfo) throws LicenseException {
		// if (1 == 1) {// TODO TEMP
		// return false;
		// }
		if (licensesInfo == null) {
			return null;
		}
		return LicenseTools.checkLicense(licensesInfo.hasTacholabDemo(), licensesInfo.getTacholabDemoFrom(), licensesInfo.getTacholabDemoTo());
	}

	// *****************************************************************************************
	// ************************************** TACHOLAB PLUS *****************************************
	// *****************************************************************************************
	public static Boolean hasTacholabPlusAccess(LicensesBean licensesInfo) throws LicenseException {
		return ParseUtilsExtended.getBoolean(LicenseTools.hasTacholabPlusLicense(licensesInfo), false) //
				|| ParseUtilsExtended.getBoolean(LicenseTools.hasTacholabPlusDemo(licensesInfo), false);
	}

	public static Boolean hasTacholabPlusLicense(LicensesBean licensesInfo) throws LicenseException {
		// if (1 == 1) {// TODO TEMP
		// return false;
		// }
		if (licensesInfo == null) {
			return null;
		}
		return LicenseTools.checkLicense(licensesInfo.hasTacholabPlus(), licensesInfo.getTacholabPlusFrom(), licensesInfo.getTacholabPlusTo());
	}

	public static Boolean hasTacholabPlusDemo(LicensesBean licensesInfo) throws LicenseException {
		// if (1 == 1) {// TODO TEMP
		// return true;
		// }
		if (licensesInfo == null) {
			return null;
		}
		return LicenseTools.checkLicense(licensesInfo.hasTacholabPlusDemo(), licensesInfo.getTacholabPlusDemoFrom(), licensesInfo.getTacholabPlusDemoTo());
	}

	public static Boolean hasFichaAccess(LicensesBean licensesInfo) throws LicenseException {
		return LicenseTools.hasTacholabPlusAccess(licensesInfo);
	}

	public static Boolean checkLicense(boolean hasLicense, Date from, Date to) {
		return hasLicense //
				&& ((from == null) || ((from != null) && from.before(new Date())))//
				&& ((to == null) || ((to == null) || to.after(new Date())));
	}

}
