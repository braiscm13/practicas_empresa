package com.opentach.server.company.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.services.IAlertService;
import com.opentach.common.company.beans.LicensesBean;
import com.opentach.common.company.exception.LicenseException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.naming.CompanyUserNaming;
import com.opentach.common.company.naming.LicenseAlertNaming;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.company.service.ICompanyUserService;
import com.opentach.common.company.service.ILicenseService;
import com.opentach.common.company.tools.LicenseTools;
import com.opentach.common.naming.UserNaming;
import com.opentach.common.report.util.ReportSessionUtils;
import com.opentach.common.user.IUserData;
import com.opentach.server.company.dao.CompanyDao;
import com.opentach.server.company.task.TaskEndDemo;
import com.opentach.server.company.task.TaskEndLicense;
import com.opentach.server.task.scheduler.TaskException;
import com.opentach.server.task.scheduler.TaskScheduler;
import com.opentach.server.util.UserInfoComponent;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class LaborService.
 */
@Service("LicenseService")
public class LicenseServiceImpl extends AbstractSpringDelegate implements ILicenseService, ILicenseServiceServer {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(LicenseServiceImpl.class);

	@Autowired
	private CompanyDao					companyDao;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Autowired
	private ICompanyUserService			companyUserService;

	@Autowired
	private TaskScheduler				taskScheduler;

	@Autowired
	private IAlertService				alertService;

	@Autowired
	private UserInfoComponent			userInfoComponent;

	protected LicenseServiceImpl() {
		super();
	}

	// ******************* GENERAL **********************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Map<String, Object> getCompanyLicenseInfo(String cif) throws LicenseException {
		CheckingTools.failIf(cif == null, LicenseException.class, "E_GETTING_COMPANY_LICENSE_INFO__MANDATORY_CIF");
		EntityResult res = this.daoHelper.query(this.companyDao, //
				EntityResultTools.keysvalues(CompanyNaming.CIF, cif), //
				EntityResultTools.attributes(//
						LicenseNaming.LIC_OPENTACH, LicenseNaming.LIC_OPENTACH_FROM, LicenseNaming.LIC_OPENTACH_TO, //
						LicenseNaming.LIC_OPENTACH_DEMO, LicenseNaming.LIC_OPENTACH_DEMO_FROM, LicenseNaming.LIC_OPENTACH_DEMO_TO, //
						LicenseNaming.LIC_TACHOLAB, LicenseNaming.LIC_TACHOLAB_FROM, LicenseNaming.LIC_TACHOLAB_TO, //
						LicenseNaming.LIC_TACHOLAB_DEMO, LicenseNaming.LIC_TACHOLAB_DEMO_FROM, LicenseNaming.LIC_TACHOLAB_DEMO_TO, //
						LicenseNaming.LIC_TACHOLABPLUS, LicenseNaming.LIC_TACHOLABPLUS_FROM, LicenseNaming.LIC_TACHOLABPLUS_TO, //
						LicenseNaming.LIC_TACHOLABPLUS_DEMO, LicenseNaming.LIC_TACHOLABPLUS_DEMO_FROM, LicenseNaming.LIC_TACHOLABPLUS_DEMO_TO//
						), //
				"licenses");
		try {
			CheckingTools.checkValidEntityResult(res, "E_GETTING_COMPANY_LICENSE_INFO", true, true, new Object[] {});
		} catch (OntimizeJEEException err) {
			throw new LicenseException("E_GETTING_COMPANY_LICENSE_INFO", err);
		}
		return res.getRecordValues(0);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public LicensesBean getCompanyLicenseBeanInfo(String cif) throws LicenseException {
		return new LicensesBean(this.getCompanyLicenseInfo(cif));
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void requestLicenseInfo(String cif, String license) throws LicenseException {
		try {
			Map<String, Object> companyData = this.checkCIF(cif);
			Map<String, Object> alertData = new HashMap<String, Object>();
			MapTools.safePut(alertData, CompanyNaming.CIF, cif);
			MapTools.safePut(alertData, LicenseAlertNaming.EMPRESA, companyData.get(CompanyNaming.NOMB));
			MapTools.safePut(alertData, LicenseAlertNaming.FECHA, new SimpleDateFormat().format(new Date()));
			MapTools.safePut(alertData, LicenseAlertNaming.LICENCIA, license.substring(4));
			this.alertService.executeAlertByCodeAsynchWithData(LicenseAlertNaming.ALERT_CODE__LICENSE_REQUEST_INFO, alertData);
		} catch (AlertException err) {
			LicenseServiceImpl.logger.error(null, err);
			throw new LicenseException("E_REQUESTING_MORE_LICENSE_INFO");
		}
	}

	// ************************************** START CONTRACT *****************************************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void startContract(String cif, String license, Date startInput, Date endInput) throws LicenseException {
		LicenseServiceImpl.logger.info("startContract  CIF:{}  LICENSE:{}, FROM:{} TO:{}", cif, license, startInput, endInput);

		// Step 0: validate input data
		boolean opentach = LicenseNaming.LIC_OPENTACH.equals(license);
		boolean tacholab = LicenseNaming.LIC_TACHOLAB.equals(license);
		boolean tacholabPlus = LicenseNaming.LIC_TACHOLABPLUS.equals(license);
		if (!opentach && !tacholab && !tacholabPlus) {
			throw new LicenseException("E_INVALID_LICENSE", new Object[] { license });
		}
		Map<String, Object> companyInfo = this.checkCIF(cif);
		CheckingTools.failIf(startInput == null, LicenseException.class, "E_START_CONTRACT__MANDATORY_START_DATE");
		Date start = DateTools.truncate(startInput);
		Date end = endInput == null ? null : DateTools.lastMilisecond(endInput);
		CheckingTools.failIf((start != null) && (end != null) && start.after(end), LicenseException.class, "E_START_CONTRACT__INVALID_DATES");

		// Step 1: Check to deprecate demo (silent mode)
		LicensesBean currentLicenses = this.getCompanyLicenseBeanInfo(cif);
		if (opentach && LicenseTools.hasOpentachDemo(currentLicenses)) {
			this.endDemoWithContract(cif, LicenseNaming.LIC_OPENTACH_DEMO);
		} else if (tacholab && LicenseTools.hasTacholabDemo(currentLicenses)) {
			this.endDemoWithContract(cif, LicenseNaming.LIC_TACHOLAB_DEMO);
		} else if (tacholabPlus && LicenseTools.hasTacholabPlusDemo(currentLicenses)) {
			this.endDemoWithContract(cif, LicenseNaming.LIC_TACHOLABPLUS_DEMO);
		}

		// Step 2: Update + Audit
		String licenseFromField = opentach ? LicenseNaming.LIC_OPENTACH_FROM : (tacholab ? LicenseNaming.LIC_TACHOLAB_FROM : LicenseNaming.LIC_TACHOLABPLUS_FROM);
		String licenseToField = opentach ? LicenseNaming.LIC_OPENTACH_TO : (tacholab ? LicenseNaming.LIC_TACHOLAB_TO : LicenseNaming.LIC_TACHOLABPLUS_TO);
		this.startContractOrLicenseUpdate(cif, license, licenseFromField, start, licenseToField, end);

		// Step 3: specific actions
		if (opentach) {
			this.startContractOpentachSpecific(cif, start, end);
		} else if (tacholab) {
			this.startContractTacholabSpecific(cif, start, end);
		} else if (tacholabPlus) {
			this.startContractTacholabPlusSpecific(cif, start, end);
		}

		// Step 4: Ensure company user, else create it
		Map<String, Object> companyUserInfo = this.checkCompanyUser(cif);

		// Step 5: Email customer
		try {
			String alert = null;
			if (opentach && ReportSessionUtils.isOpentach()) {
				alert = LicenseAlertNaming.ALERT_CODE__LICENSE_START_OPENTACH;
			} else if (tacholab && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__LICENSE_START_TACHOLAB;
			} else if (tacholabPlus && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__LICENSE_START_TACHOLABPLUS;
			}
			if (alert != null) {
				Map<Object, Object> extraData = (Map) MapTools.union(companyInfo, companyUserInfo);
				LicensesBean newLicenses = this.getCompanyLicenseBeanInfo(cif);// Query again
				Date from = opentach ? newLicenses.getOpentachFrom() : (tacholab ? newLicenses.getTacholabFrom() : newLicenses.getTacholabPlusFrom());
				Date to = opentach ? newLicenses.getOpentachTo() : (tacholab ? newLicenses.getTacholabTo() : newLicenses.getTacholabPlusTo());
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				MapTools.safePut(extraData, LicenseAlertNaming.FROM, (from == null ? null : sdf.format(from)), false);
				MapTools.safePut(extraData, LicenseAlertNaming.TO, (to == null ? null : sdf.format(to)), false);
				MapTools.safePut(extraData, LicenseAlertNaming.USER, extraData.get(UserNaming.USUARIO), false);
				MapTools.safePut(extraData, LicenseAlertNaming.PASSWORD, extraData.get(UserNaming.PASSWORD), false);
				this.alertService.executeAlertByCodeAsynchWithData(alert, extraData);
			}
		} catch (AlertException err) {
			LicenseServiceImpl.logger.error(null, err);
		}
	}

	protected void startContractOpentachSpecific(String cif, Date start, Date end) throws LicenseException {
		if (ReportSessionUtils.isOpentach()) {
			LicenseServiceImpl.logger.info("startContractOpentachSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void startContractTacholabSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("startContractTacholabSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void startContractTacholabPlusSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("startContractTacholabPlusSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	// ************************************** END CONTRACT *****************************************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void endContract(String cif, String license, Date endInput) throws LicenseException {
		LicenseServiceImpl.logger.info("endContract  CIF:{}  LICENSE:{}, TO:{}", cif, license, endInput);

		// Step 0: validate input data
		boolean opentach = LicenseNaming.LIC_OPENTACH.equals(license);
		boolean tacholab = LicenseNaming.LIC_TACHOLAB.equals(license);
		boolean tacholabPlus = LicenseNaming.LIC_TACHOLABPLUS.equals(license);
		if (!opentach && !tacholab && !tacholabPlus) {
			throw new LicenseException("E_INVALID_LICENSE", new Object[] { license });
		}
		Map<String, Object> companyInfo = this.checkCIF(cif);
		CheckingTools.failIf(endInput == null, LicenseException.class, "E_END_CONTRACT__MANDATORY_END_DATE");
		Date end = DateTools.lastMilisecond(endInput);
		LicensesBean currentLicenses = this.getCompanyLicenseBeanInfo(cif);
		Date startDate = opentach ? currentLicenses.getOpentachFrom() : (tacholab ? currentLicenses.getTacholabFrom() : currentLicenses.getTacholabPlusFrom());
		CheckingTools.failIf((startDate != null) && startDate.after(end), LicenseException.class, "E_END_CONTRACT__INVALID_DATES");

		// Step 2: Update + Audit
		this.endLicenseInternalUpdate(cif, license, end);

		// Step 3: specific actions
		if (opentach) {
			this.endContractOpentachSpecific(cif, startDate, end);
		} else if (tacholab) {
			this.endContractTacholabSpecific(cif, startDate, end);
		} else if (tacholabPlus) {
			this.endContractTacholabPlusSpecific(cif, startDate, end);
		}

		// Step 4: launch end license task (if previous date, else will be launched automatically the night it finish)
		if (end.equals(DateTools.lastMilisecond(new Date())) || end.before(DateTools.lastMilisecond(new Date()))) {
			try {
				this.taskScheduler.processSingleTask(new TaskEndLicense(cif, license, end), false);
			} catch (TaskException err) {
				LicenseServiceImpl.logger.warn("W_DOWNDATING_TASK", err);
			}
		}

		// Step 5: Email customer
		try {
			String alert = null;
			if (opentach && ReportSessionUtils.isOpentach()) {
				alert = LicenseAlertNaming.ALERT_CODE__LICENSE_END_OPENTACH;
			} else if (tacholab && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__LICENSE_END_TACHOLAB;
			} else if (tacholabPlus && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__LICENSE_END_TACHOLABPLUS;
			}
			if (alert != null) {
				Map<Object, Object> extraData = new HashMap(companyInfo);
				LicensesBean newLicenses = this.getCompanyLicenseBeanInfo(cif);// Query again
				Date from = opentach ? newLicenses.getOpentachFrom() : (tacholab ? newLicenses.getTacholabFrom() : newLicenses.getTacholabPlusFrom());
				Date to = opentach ? newLicenses.getOpentachTo() : (tacholab ? newLicenses.getTacholabTo() : newLicenses.getTacholabPlusTo());
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				MapTools.safePut(extraData, LicenseAlertNaming.FROM, (from == null ? null : sdf.format(from)), false);
				MapTools.safePut(extraData, LicenseAlertNaming.TO, (to == null ? null : sdf.format(to)), false);
				this.alertService.executeAlertByCodeAsynchWithData(alert, extraData);
			}
		} catch (AlertException err) {
			LicenseServiceImpl.logger.error(null, err);
		}
	}

	protected void endContractOpentachSpecific(String cif, Date start, Date end) throws LicenseException {
		if (ReportSessionUtils.isOpentach()) {
			LicenseServiceImpl.logger.info("endContractOpentachSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void endContractTacholabSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("endContractTacholabSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void endContractTacholabPlusSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("endContractTacholabPlusSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	// ************************************** START DEMO *****************************************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void startDemo(String cif, String license, Date startInput, Date endInput) throws LicenseException {
		LicenseServiceImpl.logger.info("startDemo  CIF:{}  LICENSE:{}, FROM:{} TO:{}", cif, license, startInput, endInput);

		// Step 0: validate input data
		boolean opentach = LicenseNaming.LIC_OPENTACH.equals(license);
		boolean tacholab = LicenseNaming.LIC_TACHOLAB.equals(license);
		boolean tacholabPlus = LicenseNaming.LIC_TACHOLABPLUS.equals(license);
		if (!opentach && !tacholab && !tacholabPlus) {
			throw new LicenseException("E_INVALID_LICENSE", new Object[] { license });
		}
		Map<String, Object> companyInfo = this.checkCIF(cif);
		CheckingTools.failIf(startInput == null, LicenseException.class, "E_START_DEMO__MANDATORY_START_DATE");
		Date start = DateTools.truncate(startInput);
		Date end = endInput == null ? null : DateTools.lastMilisecond(endInput);
		CheckingTools.failIf((start != null) && (end != null) && start.after(end), LicenseException.class, "E_START_DEMO__INVALID_DATES");

		// Step 2: Update + Audit
		String licenseDemoField = opentach ? LicenseNaming.LIC_OPENTACH_DEMO : (tacholab ? LicenseNaming.LIC_TACHOLAB_DEMO : LicenseNaming.LIC_TACHOLABPLUS_DEMO);
		String licenseFromField = opentach ? LicenseNaming.LIC_OPENTACH_DEMO_FROM : (tacholab ? LicenseNaming.LIC_TACHOLAB_DEMO_FROM : LicenseNaming.LIC_TACHOLABPLUS_DEMO_FROM);
		String licenseToField = opentach ? LicenseNaming.LIC_OPENTACH_DEMO_TO : (tacholab ? LicenseNaming.LIC_TACHOLAB_DEMO_TO : LicenseNaming.LIC_TACHOLABPLUS_DEMO_TO);
		this.startContractOrLicenseUpdate(cif, licenseDemoField, licenseFromField, start, licenseToField, end);

		// Step 3: specific actions
		if (opentach) {
			this.startDemoOpentachSpecific(cif, start, end);
		} else if (tacholab) {
			this.startDemoTacholabSpecific(cif, start, end);
		} else if (tacholabPlus) {
			this.startDemoTacholabPlusSpecific(cif, start, end);
		}

		// Step 4: Ensure company user, else create it
		Map<String, Object> companyUserInfo = this.checkCompanyUser(cif);

		// Step 5: Email customer
		try {
			String alert = null;
			if (opentach && ReportSessionUtils.isOpentach()) {
				alert = LicenseAlertNaming.ALERT_CODE__DEMO_START_OPENTACH;
			} else if (tacholab && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__DEMO_START_TACHOLAB;
			} else if (tacholabPlus && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__DEMO_START_TACHOLABPLUS;
			}
			if (alert != null) {
				Map<Object, Object> extraData = (Map) MapTools.union(companyInfo, companyUserInfo);
				LicensesBean newLicenses = this.getCompanyLicenseBeanInfo(cif);// Query again
				Date from = opentach ? newLicenses.getOpentachDemoFrom() : (tacholab ? newLicenses.getTacholabDemoFrom() : newLicenses.getTacholabPlusDemoFrom());
				Date to = opentach ? newLicenses.getOpentachDemoTo() : (tacholab ? newLicenses.getTacholabDemoTo() : newLicenses.getTacholabPlusDemoTo());
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				MapTools.safePut(extraData, LicenseAlertNaming.FROM, (from == null ? null : sdf.format(from)), false);
				MapTools.safePut(extraData, LicenseAlertNaming.TO, (to == null ? null : sdf.format(to)), false);
				MapTools.safePut(extraData, LicenseAlertNaming.USER, extraData.get(UserNaming.USUARIO), false);
				MapTools.safePut(extraData, LicenseAlertNaming.PASSWORD, extraData.get(UserNaming.PASSWORD), false);
				this.alertService.executeAlertByCodeAsynchWithData(alert, extraData);
			}
		} catch (AlertException err) {
			LicenseServiceImpl.logger.error(null, err);
		}
	}

	protected void startDemoOpentachSpecific(String cif, Date start, Date end) throws LicenseException {
		if (ReportSessionUtils.isOpentach()) {
			LicenseServiceImpl.logger.info("startDemoOpentachSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void startDemoTacholabSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("startDemoTacholabSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void startDemoTacholabPlusSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("startDemoTacholabPlusSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	// ************************************** END DEMO *****************************************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void endDemo(String cif, String license, Date endInput) throws LicenseException {
		LicenseServiceImpl.logger.info("endDemo  CIF:{}  LICENSE:{}, TO:{}", cif, license, endInput);

		// Step 0: validate input data
		boolean opentach = LicenseNaming.LIC_OPENTACH.equals(license);
		boolean tacholab = LicenseNaming.LIC_TACHOLAB.equals(license);
		boolean tacholabPlus = LicenseNaming.LIC_TACHOLABPLUS.equals(license);
		if (!opentach && !tacholab && !tacholabPlus) {
			throw new LicenseException("E_INVALID_LICENSE", new Object[] { license });
		}
		Map<String, Object> companyInfo = this.checkCIF(cif);
		CheckingTools.failIf(endInput == null, LicenseException.class, "E_END_DEMO__MANDATORY_END_DATE");
		Date end = DateTools.lastMilisecond(endInput);
		LicensesBean currentLicenses = this.getCompanyLicenseBeanInfo(cif);
		Date start = opentach ? currentLicenses.getOpentachDemoFrom() : (tacholab ? currentLicenses.getTacholabDemoFrom() : currentLicenses.getTacholabPlusDemoFrom());
		CheckingTools.failIf((start != null) && start.after(end), LicenseException.class, "E_END_DEMO__INVALID_DATES");

		// Step 2: Update + Audit
		this.endDemoInternalUpdate(cif, license, end);

		// Step 3: specific actions
		if (opentach) {
			this.endDemoOpentachSpecific(cif, start, end);
		} else if (tacholab) {
			this.endDemoTacholabSpecific(cif, start, end);
		} else if (tacholabPlus) {
			this.endDemoTacholabPlusSpecific(cif, start, end);
		}

		if (end.equals(DateTools.lastMilisecond(new Date())) || end.before(DateTools.lastMilisecond(new Date()))) {
			try {
				this.taskScheduler.processSingleTask(new TaskEndDemo(cif, license, end), false);
			} catch (TaskException err) {
				LicenseServiceImpl.logger.warn("W_DOWNDATING_TASK", err);
			}
		}

		try {
			String alert = null;
			if (opentach && ReportSessionUtils.isOpentach()) {
				alert = LicenseAlertNaming.ALERT_CODE__DEMO_END_OPENTACH;
			} else if (tacholab && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__DEMO_END_TACHOLAB;
			} else if (tacholabPlus && ReportSessionUtils.isTacholab()) {
				alert = LicenseAlertNaming.ALERT_CODE__DEMO_END_TACHOLABPLUS;
			}
			if (alert != null) {
				Map<Object, Object> extraData = new HashMap(companyInfo);
				LicensesBean newLicenses = this.getCompanyLicenseBeanInfo(cif);// Query again
				Date from = opentach ? newLicenses.getOpentachDemoFrom() : (tacholab ? newLicenses.getTacholabDemoFrom() : newLicenses.getTacholabPlusDemoFrom());
				Date to = opentach ? newLicenses.getOpentachDemoTo() : (tacholab ? newLicenses.getTacholabDemoTo() : newLicenses.getTacholabPlusDemoTo());
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				MapTools.safePut(extraData, LicenseAlertNaming.FROM, (from == null ? null : sdf.format(from)), false);
				MapTools.safePut(extraData, LicenseAlertNaming.TO, (to == null ? null : sdf.format(to)), false);
				this.alertService.executeAlertByCodeAsynchWithData(alert, extraData);
			}
		} catch (AlertException err) {
			LicenseServiceImpl.logger.error(null, err);
		}

	}

	protected void endDemoOpentachSpecific(String cif, Date start, Date end) throws LicenseException {
		if (ReportSessionUtils.isOpentach()) {
			LicenseServiceImpl.logger.info("endDemoOpentachSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void endDemoTacholabSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("endDemoTacholabSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}

	private void endDemoTacholabPlusSpecific(String cif, Date start, Date end) {
		if (ReportSessionUtils.isTacholab()) {
			LicenseServiceImpl.logger.info("endDemoTacholabPlusSpecific  CIF:{}", cif);
			// Launch processes if its necessary
		}
	}
	/**
	 * When customer finally hire a license, we must "unckek" demo
	 *
	 * @param cif
	 * @param start
	 * @param licenseTo
	 * @param end
	 * @param demoField
	 * @throws LicenseException
	 */
	protected void endDemoWithContract(String cif, String demoField) throws LicenseException {
		try {
			this.daoHelper.update(this.companyDao, //
					EntityResultTools.keysvalues(demoField, "N"), //
					EntityResultTools.keysvalues(CompanyNaming.CIF, cif));
		} catch (Exception err) {
			throw new LicenseException("E_ENDING_DEMO", err);
		}
	}

	// *****************************************************************************************
	// ************************************** COMMON UTILITIES *****************************************
	// *****************************************************************************************

	protected void startContractOrLicenseUpdate(String cif, String license, String licenseFrom, Date start, String licenseTo, Date end) throws LicenseException {
		try {
			this.daoHelper.update(this.companyDao, //
					EntityResultTools.keysvalues(license, ((end == null) || end.after(new Date())) ? "S" : "N", licenseFrom, start, licenseTo, end), //
					EntityResultTools.keysvalues(CompanyNaming.CIF, cif));
		} catch (Exception err) {
			throw new LicenseException("E_SAVING_CONTRACT_DATA", err);
		}
	}

	@Override
	public void endDemoInternalUpdate(String cif, String license, Date end) throws LicenseException {
		boolean opentach = LicenseNaming.LIC_OPENTACH.equals(license);
		boolean tacholab = LicenseNaming.LIC_TACHOLAB.equals(license);
		boolean tacholabPlus = LicenseNaming.LIC_TACHOLABPLUS.equals(license);
		if (!opentach && !tacholab && !tacholabPlus) {
			throw new LicenseException("E_INVALID_LICENSE", new Object[] { license });
		}
		String licenseDemoField = opentach ? LicenseNaming.LIC_OPENTACH_DEMO : (tacholab ? LicenseNaming.LIC_TACHOLAB_DEMO : LicenseNaming.LIC_TACHOLABPLUS_DEMO);
		String licenseDemoToField = opentach ? LicenseNaming.LIC_OPENTACH_DEMO_TO : (tacholab ? LicenseNaming.LIC_TACHOLAB_DEMO_TO : LicenseNaming.LIC_TACHOLABPLUS_DEMO_TO);
		this.endDemoOrLicenseInternalUpdate(cif, licenseDemoField, licenseDemoToField, end);
	}

	@Override
	public void endLicenseInternalUpdate(String cif, String license, Date end) throws LicenseException {
		boolean opentach = LicenseNaming.LIC_OPENTACH.equals(license);
		boolean tacholab = LicenseNaming.LIC_TACHOLAB.equals(license);
		boolean tacholabPlus = LicenseNaming.LIC_TACHOLABPLUS.equals(license);
		if (!opentach && !tacholab && !tacholabPlus) {
			throw new LicenseException("E_INVALID_LICENSE", new Object[] { license });
		}
		String licenseToField = opentach ? LicenseNaming.LIC_OPENTACH_TO : (tacholab ? LicenseNaming.LIC_TACHOLAB_TO : LicenseNaming.LIC_TACHOLABPLUS_TO);
		this.endDemoOrLicenseInternalUpdate(cif, license, licenseToField, end);
	}

	private void endDemoOrLicenseInternalUpdate(String cif, String field, String toField, Date end) throws LicenseException {
		try {
			this.daoHelper.update(this.companyDao, //
					EntityResultTools.keysvalues(field, "N", toField, end), //
					EntityResultTools.keysvalues(CompanyNaming.CIF, cif));
		} catch (Exception err) {
			throw new LicenseException("E_SAVING_LICENSING_DATA", err);
		}
	}

	private Map<String, Object> checkCIF(String cif) throws LicenseException {
		// Raise error if this CIF is not found
		CheckingTools.failIf(cif == null, LicenseException.class, "E_GETTING_COMPANY_INFO__MANDATORY_CIF");
		EntityResult res = this.daoHelper.query(this.companyDao, //
				EntityResultTools.keysvalues(CompanyNaming.CIF, cif), //
				EntityResultTools.attributes(CompanyNaming.CIF, CompanyNaming.NOMB, CompanyNaming.EMAIL), //
				"licenses");
		try {
			CheckingTools.checkValidEntityResult(res, "E_GETTING_COMPANY_INFO", true, true, new Object[] {});
		} catch (OntimizeJEEException err) {
			throw new LicenseException("E_GETTING_COMPANY_INFO", err);
		}
		return res.getRecordValues(0);
	}

	/**
	 * Checks if the company already has a COMPANY USER enabled, else create one
	 *
	 * @param cif
	 * @return
	 */
	private Map<String, Object> checkCompanyUser(String cif) throws LicenseException {
		try {
			EntityResult companyUserQuery = this.companyUserService.companyUserQuery(//
					EntityResultTools.keysvalues(CompanyNaming.CIF, cif, UserNaming.NIVEL_CD, IUserData.NIVEL_EMPRESA), //
					EntityResultTools.attributes(CompanyUserNaming.USUARIO, UserNaming.PASSWORD));
			if ((companyUserQuery != null) && (companyUserQuery.calculateRecordNumber() > 0)) {
				return companyUserQuery.getRecordValues(0);
			}
		} catch (Exception err) {
			throw new LicenseException("E_GETTING_COMPANY_USER", err);
		}
		try {
			return this.companyUserService.companyUserCreateDefault(cif);
		} catch (Exception err) {
			throw new LicenseException("E_GENERATING_COMPANY_USER", err);
		}
	}

	// *****************************************************************************************
	// ************************************** OPENTACH *****************************************
	// *****************************************************************************************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasOpentachAccess(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasOpentachAccess(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasOpentachLicense(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasOpentachLicense(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasOpentachDemo(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasOpentachDemo(licensesInfo);
	}

	// *****************************************************************************************
	// ************************************** TACHOLAB *****************************************
	// *****************************************************************************************

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasTacholabAccess(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasTacholabLicense(licensesInfo) || LicenseTools.hasTacholabDemo(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasTacholabLicense(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasTacholabLicense(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasTacholabDemo(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasTacholabDemo(licensesInfo);
	}

	// *****************************************************************************************
	// ************************************** TACHOLAB PLUS *****************************************
	// *****************************************************************************************
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasTacholabPlusAccess(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasTacholabPlusLicense(licensesInfo) || LicenseTools.hasTacholabPlusDemo(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasTacholabPlusLicense(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasTacholabPlusLicense(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasTacholabPlusDemo(String cif) throws LicenseException {
		LicensesBean licensesInfo = this.getCompanyLicenseBeanInfo(cif);
		return LicenseTools.hasTacholabPlusDemo(licensesInfo);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public Boolean hasFichaAccess(String cif) throws LicenseException {
		return this.hasTacholabPlusAccess(cif);
	}

	// *****************************************************************************************
	// ************************************** TASKS *****************************************
	// *****************************************************************************************
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult expiredDemoQuery(Map<?, ?> keysValues, List<?> attributes) throws LicenseException {
		return this.daoHelper.query(this.companyDao, keysValues, attributes, "expiredDemos");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult expiredLicenseQuery(Map<?, ?> keysValues, List<?> attributes) throws LicenseException {
		return this.daoHelper.query(this.companyDao, keysValues, attributes, "expiredLicenses");
	}

	// *****************************************************************************************
	// ************************************** REPORTS *****************************************
	// *****************************************************************************************
	/** Query all companies (by the user) with all info about licenses/demos and expired date calculated cols. */
	@Override
	public EntityResult licensesReportQuery(Map<?, ?> keysValues, List<?> attributes) throws LicenseException {
		// TODO filter companies
		// userInfoComponent.checkCifFilter(keysValues, CompanyNaming.CIF);
		return this.daoHelper.query(this.companyDao, keysValues, attributes, "licensesReport");
	}
}