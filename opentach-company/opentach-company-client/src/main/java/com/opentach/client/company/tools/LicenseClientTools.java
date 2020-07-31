package com.opentach.client.company.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.company.exception.LicenseException;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.company.service.ILicenseService;
import com.opentach.common.report.util.ReportSessionUtils;

public class LicenseClientTools {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(LicenseClientTools.class);

	private LicenseClientTools() {}

	public static boolean isTacholabPlusModuleEnabled() {
		return LicenseNaming.TACHOLABPLUS_ENABLED && ReportSessionUtils.isTacholab();
	}

	public static boolean hasTacholabPlus(String cif) {
		if (StringTools.isEmpty(cif)) {
			LicenseClientTools.logger.warn("W_NULL_CIF_TO_CHECK_TACHOLABPLUS");
			return false;
		}

		try {
			return LicenseClientTools.isTacholabPlusModuleEnabled() && BeansFactory.getBean(ILicenseService.class).hasTacholabPlusAccess(cif);
		} catch (LicenseException err) {
			LicenseClientTools.logger.warn("W_CHECK_TACHOLABPLUS", err);
			return false;
		}
	}

}
