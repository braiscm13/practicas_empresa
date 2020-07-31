package com.opentach.server.alert.service;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.services.mail.IMailService;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ExceptionTools;
import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.spring.SpringTools;
import com.opentach.common.alert.alert.IAlert;
import com.opentach.common.alert.alert.IAlertResultDispatcher;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.naming.AlertNaming;
import com.opentach.common.alert.result.IAlertResult;
import com.opentach.common.alert.result.IAlertResultCompany;
import com.opentach.common.alert.result.IAlertResultEmployee;
import com.opentach.common.alert.services.IAlertService;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.employee.naming.EmployeeNaming;
import com.opentach.common.util.TemplateTools;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor;
import com.opentach.server.alert.dao.AlertDao;
import com.opentach.server.alert.dao.AlertExecutionDao;
import com.opentach.server.alert.utils.AlertMailTools;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The implementation of IAlertService.
 */
@Service("AlertService")
public class AlertServiceImpl extends AbstractSpringDelegate implements IAlertService, IAlertServiceServer {

	/** The CONSTANT logger */
	private static final Logger					logger	= LoggerFactory.getLogger(AlertServiceImpl.class);

	@Autowired
	private AlertDao							alertDao;

	@Autowired
	private AlertExecutionDao					alertExecutionDao;

	@Autowired(required = false)
	private IMailService						mailService;

	@Autowired
	private DefaultOntimizeDaoHelper			daoHelper;

	@Autowired
	private TemplateTools						templateTools;

	/** The executor. */
	private final PriorityThreadPoolExecutor	executor;

	public AlertServiceImpl() {
		this.executor = PoolExecutors.newPriorityPoolExecutor("AlertSchedulerExecutor", 20, 10, TimeUnit.SECONDS);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// ALERT METHODS //////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult alertQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException {
		return this.daoHelper.query(this.alertDao, keysValues, attributes, null, "default");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult alertSchedulerQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException {
		return this.daoHelper.query(this.alertDao, keysValues, attributes, null, "scheduler");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult alertInsert(Map<?, ?> attributes) throws AlertException {
		return this.daoHelper.insert(this.alertDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult alertUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws AlertException {
		return this.daoHelper.update(this.alertDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult alertDelete(Map<?, ?> keysValues) throws AlertException {
		return this.daoHelper.delete(this.alertDao, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult alertImplementationQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException {
		// Complete this one using getAlertImplementations and return ER to use in combo.
		List<String> alertClasses = this.getAlertImplementations();
		EntityResult res = new EntityResult(Arrays.asList(new String[] { AlertNaming.ALR_ACTION }));
		alertClasses.stream().forEach((className) -> EntityResultTools.fastAddRecord(res, EntityResultTools.keysvalues(AlertNaming.ALR_ACTION, className)));
		return EntityResultTools.dofilter(res, new Hashtable<>(keysValues));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// ALERT EXECUTION METHODS
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult alertExecutionQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException {
		return this.daoHelper.query(this.alertExecutionDao, keysValues, attributes, null, "query");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult alertExecutionInsert(Map<?, ?> attributes) throws AlertException {
		return this.daoHelper.insert(this.alertExecutionDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult alertExecutionUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws AlertException {
		return this.daoHelper.update(this.alertExecutionDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult alertExecutionDelete(Map<?, ?> keysValues) throws AlertException {
		return this.daoHelper.delete(this.alertExecutionDao, keysValues);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// EMAIL NOTIFICATIONS ////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void sendEmail(List<String> to, List<String> cc, List<String> bcc, String subject, String body) throws AlertException {
		try {
			this.getMailService().sendMail(null, to, cc, bcc, subject, body, null, null);
		} catch (Exception e) {
			AlertServiceImpl.logger.error(null, e);
			throw new AlertException(e);
		}
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void sendEmailCompose(List<String> to, List<String> cc, List<String> bcc, String templateSubject, String templateBody, Map<?, ?> data) throws AlertException {
		String subject = this.templateTools.velocityWithStringTemplateExample(templateSubject, data);
		String body = this.templateTools.velocityWithStringTemplateExample(templateBody, data);
		this.sendEmail(to, cc, bcc, subject, body);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// ALERT EXECUTIONS ////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public List<IAlertResult> executeAlertByCode(String alrCode) throws AlertException {
		return this.executeAlertByCodeWithData(alrCode, null);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public List<IAlertResult> executeAlertByCodeWithData(String alrCode, Map<?, ?> extraData) throws AlertException {
		Object alrId = this.getAlertIdByCode(alrCode);
		return this.executeAlertWithData(alrId, extraData);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void executeAlertByCodeAsynch(String alrCode) throws AlertException {
		this.executeAlertByCodeAsynchWithData(alrCode, null);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void executeAlertByCodeAsynchWithData(String alrCode, Map<?, ?> extraData) throws AlertException {
		Object alrId = this.getAlertIdByCode(alrCode);
		this.executeAlertAsynchWithData(alrId, extraData);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public List<IAlertResult> executeAlert(Object alrId) throws AlertException {
		return this.executeAlertWithData(alrId, null);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public List<IAlertResult> executeAlertWithData(Object alrId, Map<?, ?> extraData) throws AlertException {
		AlertServiceImpl.logger.info("executeAlert: {}", alrId);
		return this.executeAlertInternal(alrId, true, new Date(), extraData);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void executeAlertAsynch(Object alrId) throws AlertException {
		this.executeAlertAsynchWithData(alrId, null);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public void executeAlertAsynchWithData(Object alrId, Map<?, ?> extraData) throws AlertException {
		this.executeAlertAsynchOnDate(alrId, true, new Date(), extraData);
	}

	@Override
	public void executeAlertAsynchOnDate(final Object alrId, final boolean forceExecution, final Date dispatchDate, final Map<?, ?> extraData) {
		AlertServiceImpl.logger.info("executeAlertAsynch: {} SUBMIT", alrId);
		this.executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					AlertServiceImpl.logger.debug("\tprocessSingleAlert: {} START", alrId);
					AlertServiceImpl.this.executeAlertInternal(alrId, forceExecution, dispatchDate, extraData);
					AlertServiceImpl.logger.debug("\t\tprocessSingleAlert: {}  DONE", alrId);
				} catch (IgnoreAlertException err) {
					AlertServiceImpl.logger.debug("\t\tprocessSingleAlert: {}  IGNORED", alrId);
				} catch (Exception err) {
					AlertServiceImpl.logger.error("\tprocessSingleAlert: {}  ERROR", alrId, err);
				}
			}
		});
	}

	protected List<IAlertResult> executeAlertInternal(Object alrId, boolean forceExecution, Date dispatchDate, Map<?, ?> extraData) throws AlertException {
		CheckingTools.failIf(alrId == null, AlertException.class, "E_REQUIRED_ALR_ID");
		Date executionDate = new Date();
		Number executionNumber = null;
		IAlert instance = null;
		try {
			// Get atomic execution number
			executionNumber = this.getNextExecutionNumber();
			AlertServiceImpl.logger.trace("executionNumber:{}", executionNumber);

			// Build alert instance and configure it
			AlertServiceImpl.logger.trace("BUILDING_ALERT__ALR_ID:{}  instance:{}", alrId);
			instance = this.buildAlert(alrId, extraData);

			// Check if runnable
			if (!(forceExecution || instance.isValidToExecute(dispatchDate))) {
				throw new IgnoreAlertException();
			}

			// Launch indicator ------------------------
			AlertServiceImpl.logger.trace("LAUNCHING_ALERT__ALR_ID:{}  instance:{}", alrId, instance);
			List<IAlertResult> results = null;
			try {
				results = instance.execute();
			} catch (Exception err) {
				throw new AlertException("E_EXECUTING_ALERT", err);
			}

			// Process results
			if (IAlertResultDispatcher.class.isAssignableFrom(instance.getClass())) {
				// Delegate to task the fact of process results
				((IAlertResultDispatcher) instance).processResults(executionDate, executionNumber, results);
			} else {
				this.processAlertResults(executionDate, executionNumber, instance, results);
			}
			return results;
		} catch (IgnoreAlertException ignore) {
			throw ignore;
		} catch (Exception err) {
			AlertServiceImpl.logger.error(null, err);
			// Process error
			if ((instance != null) && IAlertResultDispatcher.class.isAssignableFrom(instance.getClass())) {
				// Delegate to task the fact of process error
				((IAlertResultDispatcher) instance).onError(executionDate, executionNumber, err);
			} else {
				this.processAlertError(executionDate, executionNumber, alrId, err);
			}

			throw err;
		}
	}

	// Another utilities ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected List<String> getAlertImplementations() throws AlertException {
		final List<String> alerts = new ArrayList<String>();
		try {
			Enumeration<URL> taskFiles = Thread.currentThread().getContextClassLoader().getResources("META-INF/alerts.properties");
			while (taskFiles.hasMoreElements()) {
				alerts.addAll(this.parseAlertFile(taskFiles.nextElement()));
			}
		} catch (Exception err) {
			throw new AlertException("E_GETTING_ALERT_IMPLEMENTATIONS", err);
		}

		return alerts;
	}

	private Collection<String> parseAlertFile(URL taskFile) {
		final List<String> alerts = new ArrayList<String>();
		try (InputStream stream = taskFile.openStream()) {
			Properties prop = new Properties();
			prop.load(stream);
			List<String> collected = prop.keySet().stream().map(s -> {
				return (String) prop.get(s);
			}).collect(Collectors.toList());
			collected = ListTools.removeDuplicates(collected);
			collected.remove(null);
			alerts.addAll(collected);
		} catch (Exception err) {
			AlertServiceImpl.logger.error("E_GETTING_ALERT_IMPLEMENTATIONS", err);
		}
		return alerts;
	}

	protected Number getNextExecutionNumber() throws AlertException {
		EntityResult res = this.daoHelper.query(this.alertExecutionDao, new HashMap<>(), EntityResultTools.attributes(AlertNaming.ALE_NUMBER), "nextExecutionNumber");
		CheckingTools.failIf((res == null) || (res.calculateRecordNumber() != 1), AlertException.class, "E_GETTING_NEXT_EXECUTION_NUMBER");
		return (Number) ((List<?>) res.get(AlertNaming.ALE_NUMBER)).get(0);
	}

	protected IAlert buildAlert(Object alrId, Map<?, ?> extraData) throws AlertException {
		// Query Indicator settings ------------------------
		AlertServiceImpl.logger.trace("QUERYING_ALERT_SETTINGS_ALR_ID:{}", alrId);
		Map<?, ?> settings = this.getAlertSettings(alrId);

		// Instantiate Indicator ------------------------------
		AlertServiceImpl.logger.trace("INSTANTIATING_ALERT_ALR_ID:{}  values:{}", alrId, settings);
		IAlert instance = this.buildAlert(alrId, settings, extraData);
		return instance;
	}

	protected Map<?, ?> getAlertSettings(Object alrId) throws AlertException {
		try {
			EntityResult indSettingsRes = this.alertQuery(//
					EntityResultTools.keysvalues(AlertNaming.ALR_ID, alrId), //
					EntityResultTools.attributes(AlertNaming.ALR_NAME, AlertNaming.ALR_ACTION, AlertNaming.ALR_PROPERTIES, AlertNaming.ALR_SUBJECT, AlertNaming.ALR_BODY,
							AlertNaming.ALR_SAVEDB, AlertNaming.ALR_SENDMAIL));
			CheckingTools.failIf((indSettingsRes == null) || (indSettingsRes.calculateRecordNumber() != 1), AlertException.class, "E_GETTING_ALERT_SETTINGS");
			return indSettingsRes.getRecordValues(0);
		} catch (Exception err) {
			throw new AlertException("E_GETTING_ALERT_SETTINGS", err);
		}
	}

	protected IAlert buildAlert(Object alrId, Map<?, ?> indSettings, Map<?, ?> extraData) throws AlertException {
		try {
			String setName = (String) indSettings.get(AlertNaming.ALR_NAME);
			String className = (String) indSettings.get(AlertNaming.ALR_ACTION);
			String propString = (String) indSettings.get(AlertNaming.ALR_PROPERTIES);
			Properties prop = new Properties();
			if (!StringTools.isEmpty(propString)) {
				prop.load(new StringReader(propString));
			}

			// Support to multiple constructors
			IAlert instance = null;
			try {
				instance = (IAlert) ReflectionTools.newInstance(className, alrId, setName, prop, indSettings, extraData);
			} catch (Exception err) {
				try {
					instance = (IAlert) ReflectionTools.newInstance(className, alrId, setName, prop, indSettings);
				} catch (Exception err1) {
					try {
						instance = (IAlert) ReflectionTools.newInstance(className, alrId, setName, prop);
					} catch (Exception err2) {
						try {
							instance = (IAlert) ReflectionTools.newInstance(className, setName, prop);
						} catch (Exception err3) {
							instance = (IAlert) ReflectionTools.newInstance(className, prop);
						}
					}
				}
			}
			return instance;
		} catch (Exception err) {
			throw new AlertException("E_INSTANTIATING_ALERT", err);
		}
	}

	protected void processAlertResults(Date executionDate, Number executionNumber, IAlert instance, List<IAlertResult> results) throws AlertException {
		if ((results == null) || results.isEmpty()) {
			this.processAlertResult(executionDate, executionNumber, instance, null);
		} else {
			for (IAlertResult alertResult : results) {
				this.processAlertResult(executionDate, executionNumber, instance, alertResult);
			}
		}
	}

	public void processAlertResult(Date executionDate, Number executionNumber, IAlert instance, IAlertResult alertResult) {
		if (alertResult == null) {
			AlertServiceImpl.logger.debug("NO_RESULT__executionNumber:{} alrdId:{}", executionNumber, instance.getAlrId());
		}
		// Prepare common data
		Map<?, ?> settings = instance.getSettings() == null ? new HashMap() : instance.getSettings();
		Map<?, ?> data = (alertResult == null) || (alertResult.getData() == null) ? new HashMap() : alertResult.getData();
		Map<?, ?> extraData = instance.getExtraData() == null ? new HashMap() : (Map) instance.getExtraData();
		Map<?, ?> toFill = MapTools.union(MapTools.union(MapTools.union(settings, new HashMap(instance.getProperties())), data), (Map) extraData);

		Object cif = (alertResult instanceof IAlertResultCompany ? ((IAlertResultCompany) alertResult).getCompany() //
				: ObjectTools.coalesce(toFill.get(CompanyNaming.CIF), toFill.get(AlertNaming.COM_ID)));
		Object drvId = (alertResult instanceof IAlertResultEmployee ? ((IAlertResultEmployee) alertResult).getEmployee() //
				: ObjectTools.coalesce(toFill.get(EmployeeNaming.IDCONDUCTOR), toFill.get(EmployeeNaming.DRV_ID)));
		String subjectBase = (String) settings.get(AlertNaming.ALR_SUBJECT);
		String bodyBase = (String) settings.get(AlertNaming.ALR_BODY);
		String subject = this.templateTools.velocityWithStringTemplateExample(subjectBase, toFill);
		String body = this.templateTools.velocityWithStringTemplateExample(bodyBase, toFill);

		// Consider to ad to this map all targetvalues to send to DB (if apply)
		Map<String, Object> targetValues = new HashMap<String, Object>();
		MapTools.safePut(targetValues, AlertNaming.ALR_ID, instance.getAlrId());
		MapTools.safePut(targetValues, AlertNaming.ALE_NUMBER, executionNumber);
		MapTools.safePut(targetValues, AlertNaming.ALE_EXEDATE, executionDate);
		MapTools.safePut(targetValues, AlertNaming.ALE_ERROR, alertResult == null ? "NO_RESULT" : null);
		MapTools.safePut(targetValues, AlertNaming.ALE_SUBJECT, subject);
		MapTools.safePut(targetValues, AlertNaming.ALE_BODY, body);
		MapTools.safePut(targetValues, AlertNaming.COM_ID, cif);
		MapTools.safePut(targetValues, AlertNaming.DRV_ID, drvId);

		// Notify Email?
		boolean sendEmail = ParseUtilsExtended.getBoolean(settings.get(AlertNaming.ALR_SENDMAIL), false);
		MapTools.safePut(targetValues, AlertNaming.ALE_SENDMAIL, sendEmail ? "S" : "N");
		if (sendEmail) {
			try {
				List<String> emailsTo = AlertMailTools.parseEmailsTo(instance.getProperties(), toFill);
				MapTools.safePut(targetValues, AlertNaming.ALE_SENTMAIL_TO, emailsTo == null ? null : String.join(",", emailsTo));
				List<String> emailsCC = AlertMailTools.parseEmailsCC(instance.getProperties(), toFill);
				MapTools.safePut(targetValues, AlertNaming.ALE_SENTMAIL_CC, emailsCC == null ? null : String.join(",", emailsCC));
				List<String> emailsBCC = AlertMailTools.parseEmailsBCC(instance.getProperties(), toFill);
				MapTools.safePut(targetValues, AlertNaming.ALE_SENTMAIL_BCC, emailsBCC == null ? null : String.join(",", emailsBCC));
				boolean someEmail = (emailsTo.size() > 0) || (emailsCC.size() > 0) || (emailsBCC.size() > 0);
				if (someEmail) {
					this.sendEmail(emailsTo, emailsCC, emailsBCC, subject, body);
					MapTools.safePut(targetValues, AlertNaming.ALE_SENTMAIL, "S");
				} else {
					AlertServiceImpl.logger.warn("W_NO_TARGET_EMAILS");
					MapTools.safePut(targetValues, AlertNaming.ALE_ERROR, alertResult == null ? "NORESULT AND NO_TARGET_EMAILS" : "NO_TARGET_EMAILS");
				}
			}catch (Exception err) {
				AlertServiceImpl.logger.error("E_SENDING_EMAIL", err);
				MapTools.safePut(targetValues, AlertNaming.ALE_ERROR, ExceptionTools.getStackTrace(err));
			}
		}

		// Savable to DB?
		boolean saveDB = ParseUtilsExtended.getBoolean(settings.get(AlertNaming.ALR_SAVEDB), false);
		MapTools.safePut(targetValues, AlertNaming.ALE_SAVEDB, saveDB ? "S" : "N");
		if (saveDB) {
			try {
				MapTools.safePut(targetValues, AlertNaming.ALE_SAVEDDB, "S");
				this.alertExecutionInsert(targetValues);
			} catch (Exception err) {
				AlertServiceImpl.logger.error("E_SAVING_DB", err);
			}
		}

	}

	protected void processAlertError(Date executionDate, Number executionNumber, Object alrId, Exception err) throws AlertException {
		// Audit fail
		this.alertExecutionInsert(EntityResultTools.keysvalues(//
				AlertNaming.ALR_ID, alrId, //
				AlertNaming.ALE_NUMBER, executionNumber, //
				AlertNaming.ALE_EXEDATE, executionDate, //
				AlertNaming.ALE_ERROR, ExceptionTools.getStackTrace(err) //
				));
	}

	protected Object getAlertIdByCode(Object alrCode) throws AlertException {
		try {
			CheckingTools.failIfNull(alrCode, "E_MANDATORY_ALERT_CODE");
			EntityResult res = this.alertQuery(EntityResultTools.keysvalues(AlertNaming.ALR_CODE, alrCode), EntityResultTools.attributes(AlertNaming.ALR_ID));
			CheckingTools.checkValidEntityResult(res, "E_LOOKING_FOR_ALERT_BY_CODE__" + alrCode, true, true, null);
			return ((List<Object>) res.get(AlertNaming.ALR_ID)).get(0);
		} catch (AlertException err) {
			AlertServiceImpl.logger.error("E_LOOKING_FOR_ALERT_BY_CODE__" + alrCode, err);
			throw err;
		} catch (Exception err) {
			throw new AlertException("E_LOOKING_FOR_ALERT_BY_CODE__", new Object[] { alrCode }, err);
		}
	}

	protected IMailService getMailService() {
		return SpringTools.getTargetObject(this.mailService, IMailService.class);
	}
}