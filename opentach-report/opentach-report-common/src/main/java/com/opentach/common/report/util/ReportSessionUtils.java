package com.opentach.common.report.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.company.service.ICompanyService;
import com.opentach.common.company.service.ILicenseService;

public class ReportSessionUtils {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(ReportSessionUtils.class);

	public static UserInformation getUserInformation() {
		// In the SERVER side: using SecurityContext Holder
		if ((SecurityContextHolder.getContext() != null)//
				&& (SecurityContextHolder.getContext().getAuthentication() != null) //
				&& (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null)) {
			return (UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} else {
			// In the CLIENT side: ask to IUserInformationService
			try {
				IUserInformationService userService = (IUserInformationService) ReflectionTools.invoke(Class.forName("com.ontimize.jee.desktopclient.spring.BeansFactory"),
						"getBean", new Object[] { IUserInformationService.class });
				return userService.getUserInformation();
			} catch (Exception err) {
				ReportSessionUtils.logger.error(null, err);
			}
		}
		ReportSessionUtils.logger.warn("E_NULL_USERINFORMATION");
		return null;
	}

	public static boolean isLegalStaff() {
		UserInformation userInfo = ReportSessionUtils.getUserInformation();
		if (userInfo != null) {
			return ParseUtilsExtended.getBoolean(userInfo.getOtherData().get("IS_LEGAL_STAFF"), false);
		}
		ReportSessionUtils.logger.warn("E_NULL_USERINFORMATION");
		return false;
	}

	/**
	 * Return true if this code is executed from Opentach app, false in other cases.
	 *
	 * @return
	 */
	public static boolean isOpentach() {
		// First try: all clients and servers/locators must define this variable
		String property = System.getProperty(OpentachFieldNames.APP_CODE);
		if (property != null) {
			return OpentachFieldNames.APP_CODE_OPENTACH.equals(property);
		}
		// Second try: When user identification available, it has this info too (logindao)
		UserInformation userInfo = ReportSessionUtils.getUserInformation();
		if (userInfo != null) {
			return ParseUtilsExtended.getBoolean(userInfo.getOtherData().get("IS_OPENTACH"), false);
		}
		ReportSessionUtils.logger.warn("E_NULL_USERINFORMATION");
		return false;
	}

	/**
	 * Return true if this code is executed from Tacholab app, false in other cases.
	 *
	 * @return
	 */
	public static boolean isTacholab() {
		// First try: all clients and servers/locators must define this variable
		String property = System.getProperty(OpentachFieldNames.APP_CODE);
		if (property != null) {
			return OpentachFieldNames.APP_CODE_TACHOLAB.equals(property);
		}
		// Second try: When user identification available, it has this info too (logindao)
		UserInformation userInfo = ReportSessionUtils.getUserInformation();
		if (userInfo != null) {
			return ParseUtilsExtended.getBoolean(userInfo.getOtherData().get("IS_TACHOLAB"), false);
		}
		ReportSessionUtils.logger.warn("E_NULL_USERINFORMATION");
		return false;
	}

	/**
	 * Checks if this company is under license (purchased) or is Demo. Each report can be considered to be accessible for several licenses. If one of
	 * licensingToCheck is licensed (purchased) then its not demo. Else
	 *
	 * @param cif
	 * @param licensingToCheck
	 *            all licenses that can access to this report. See LicenseNaming constants
	 * @return
	 */
	public static boolean isDemo(String cif, String... licensingToCheck) {
		if (ReportSessionUtils.isOpentach()) {
			return ReportSessionUtils.isDemoLegacy(cif);
		} else {
			return ReportSessionUtils.isDemoNew(cif, licensingToCheck);
		}
	}

	private static boolean isDemoLegacy(String cif) {
		// Older license conditions (will disappear when using new licensing fields)
		try {
			ICompanyService companyService = (ICompanyService) ReportSessionUtils.getService(ICompanyService.class);
			return companyService.isCompanyDemo(cif);
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("E_CHECKING_COMPANY_CIF__FROM_SERVER", err);
			return false;
		}
	}

	private static boolean isDemoNew(String cif, String... licensingToCheck) {
		if ((licensingToCheck == null) || (licensingToCheck.length == 0)) {
			ReportSessionUtils.logger.error("isDemoNew: no licensing specified");
			return false;
		}
		try {
			ILicenseService licenseService = (ILicenseService) ReportSessionUtils.getService(ILicenseService.class);
			// Some licensed (purchased) in same product -> then consider NO DEMO
			boolean someLicensed = false;
			boolean someDemo = false;
			for (String license : licensingToCheck) {
				if (ReportSessionUtils.isOpentach() && LicenseNaming.LIC_OPENTACH.equals(license)) {
					someLicensed |= licenseService.hasOpentachLicense(cif);
					someDemo |= licenseService.hasOpentachDemo(cif);
				} else if (ReportSessionUtils.isTacholab() && LicenseNaming.LIC_TACHOLAB.equals(license)) {
					someLicensed |= licenseService.hasTacholabLicense(cif);
					someDemo |= licenseService.hasTacholabDemo(cif);
				} else if (ReportSessionUtils.isTacholab() && LicenseNaming.LIC_TACHOLABPLUS.equals(license)) {
					someLicensed |= licenseService.hasTacholabPlusLicense(cif);
					someDemo |= licenseService.hasTacholabPlusDemo(cif);
				}
				if (someLicensed) {
					return false;
				}
			}
			if (someDemo) {
				return true;
			}
			ReportSessionUtils.logger.error("E_THIS_REPORT_MUST_NOT_BE ACCESSIBLE");
			return false;
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("E_CHECKING_DEMO__CIF" + cif + "__LICENSES:" + licensingToCheck, err);
			return false;
		}
	}

	public static String iscompanyCif(String company) {
		try {
			ICompanyService companyService = (ICompanyService) ReportSessionUtils.getService(ICompanyService.class);
			return companyService.getCompanyCif(company);
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("E_CHECKING_COMPANY_CIF__FROM_SERVER", err);
		}
		return null;
	}

	// TODO make this method prettier
	private static Object getService(Class theClass) {
		// Check if we are in CLIENT side:
		try {
			if (ApplicationManager.getApplication() != null) {
				return ReflectionTools.invoke(Class.forName("com.ontimize.jee.desktopclient.spring.BeansFactory"), "getBean", new Object[] { theClass });
			}
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("W_GETTING_SERVICE__CLIENT_SIDE", err);
		}
		// Check if we are in CLIENT side: (legacy services)
		try {
			if (ApplicationManager.getApplication() != null) {
				Object clientLocator = ApplicationManager.getApplication().getReferenceLocator();// AbstractOpentachClientLocator
				return ReflectionTools.invoke(clientLocator, "getRemoteService", new Object[] { theClass });
			}
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("W_GETTING_SERVICE__CLIENT_SIDE", err);
		}

		// Check if we are in SERVER side:
		try {
			Object locator = ReflectionTools.invoke(Class.forName("com.opentach.server.AbstractOpentachServerLocator"), "getLocator", new Object[] {});
			return ReflectionTools.invoke(locator, "getBean", new Object[] { theClass });
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("W_GETTING_SERVICE__SERVER_SIDE", err);
		}

		// Check if we are in SERVER side (legacy services)
		try {
			Object locator = ReflectionTools.invoke(Class.forName("com.opentach.server.AbstractOpentachServerLocator"), "getLocator", new Object[] {});
			return ReflectionTools.invoke(locator, "getService", new Object[] { theClass });
		} catch (Exception err) {
			ReportSessionUtils.logger.trace("W_GETTING_SERVICE__SERVER_SIDE", err);
		}
		return null;
	}

}
