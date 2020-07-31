package com.opentach.server.company.task;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.server.spring.SpringTools;
import com.opentach.common.company.exception.LicenseException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.company.service.ILicenseService;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.company.service.ILicenseServiceServer;
import com.opentach.server.task.interfaces.AbstractCronBasedTask;

/**
 * This task checks for companies with expired license, and automatically close it.
 * Can be executed automatically at nights (TaskScheduler) or manually (LicenseService)
 */
public class TaskEndLicense extends AbstractCronBasedTask {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(TaskEndLicense.class);

	protected String			cif;
	protected String			license;
	protected Date				targetDate;

	public TaskEndLicense() {
		// Nothing
	}

	public TaskEndLicense(String cif, String license, Date targetDate) {
		this.cif = cif;
		this.license = license;
		this.targetDate = targetDate;
	}

	@Override
	public String getCronExpression() {
		return "0 0 3 * * *";
	}

	@Override
	public String toString() {
		return "TaskEndLicense";
	}

	@Override
	public void execute() {
		// Step 1: Localize companies and licenses expired ------------
		List<String> cifsToResumeDemo = null;
		List<String> licensesToResumeDemo = null;
		List<Date> targetDatesToResumneDemo = null;
		if ((this.cif == null) || (this.license == null)) {
			// Query all companies that can be expired
			try {
				EntityResult resExpiredLicenses = this.getLicenseService().expiredLicenseQuery(EntityResultTools.keysvalues(CompanyNaming.CIF, this.cif),
						EntityResultTools.attributes(CompanyNaming.CIF, LicenseNaming.LICENSE, LicenseNaming.TARGETDATE));
				if (resExpiredLicenses.calculateRecordNumber() == 0) {
					TaskEndLicense.logger.info("NO_EXPIRED_LICENSES");
					return;
				}
				cifsToResumeDemo = (List<String>) resExpiredLicenses.get(CompanyNaming.CIF);
				licensesToResumeDemo = (List<String>) resExpiredLicenses.get(LicenseNaming.LICENSE);
				targetDatesToResumneDemo = (List<Date>) resExpiredLicenses.get(LicenseNaming.TARGETDATE);
			} catch (Exception err) {
				TaskEndLicense.logger.error("E_SEARCHING_BY_EXPIRED_DEMOS", err);
			}
		} else {
			// Use parametrized data : only check this one
			cifsToResumeDemo = Arrays.asList(new String[] { this.cif });
			licensesToResumeDemo = Arrays.asList(new String[] { this.license });
			targetDatesToResumneDemo = Arrays.asList(new Date[] { this.targetDate });
		}
		if ((cifsToResumeDemo == null) || (licensesToResumeDemo == null) || (cifsToResumeDemo.size() <= 0) || (cifsToResumeDemo.size() != licensesToResumeDemo.size())) {
			TaskEndLicense.logger.warn("ABORTED_TASK__INVALID_INPUT_DATA");
			return;
		}

		// Step 2: manage each detected
		int num = cifsToResumeDemo.size();
		for (int i = 0; i < num; i++) {
			this.endLicense(cifsToResumeDemo.get(i), licensesToResumeDemo.get(i), targetDatesToResumneDemo.get(i));
		}

		return;
	}

	private void endLicense(String cif, String license, Date targetDate) {
		// Step 1: Downdate users ? NO, granted no access by company license parameters
		// Step 2: Downdate emplopyees? NO, granted no access by company license parameters
		// Step 3: Clean data? NO, legally we must preserve data X years
		// Step 4: unckeck license tag
		try {
			this.getLicenseServiceServer().endLicenseInternalUpdate(cif, license, targetDate);
		} catch (LicenseException err) {
			TaskEndLicense.logger.error("E_FINISHING_TASK", err);
		}
	}

	private ILicenseService getLicenseService() {
		ILicenseService licenseService = AbstractOpentachServerLocator.getLocator().getBean(ILicenseService.class);
		return SpringTools.getTargetObject(licenseService, ILicenseService.class);
	}

	private ILicenseServiceServer getLicenseServiceServer() {
		ILicenseServiceServer licenseService = AbstractOpentachServerLocator.getLocator().getBean(ILicenseServiceServer.class);
		return SpringTools.getTargetObject(licenseService, ILicenseServiceServer.class);
	}

}
