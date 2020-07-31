package com.opentach.server.alert.alert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.ExceptionTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.server.spring.SpringTools;
import com.ontimize.locator.SecureReferenceLocator;
import com.opentach.common.alert.alert.IAlert;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.naming.AlertNaming;
import com.opentach.common.alert.result.IAlertResult;
import com.opentach.common.alert.result.IAlertResultCompany;
import com.opentach.common.alert.result.IAlertResultEmployee;
import com.opentach.common.alert.services.IAlertService;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.employee.naming.EmployeeNaming;
import com.opentach.common.util.TemplateTools;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.alert.utils.AlertMailTools;
import com.opentach.server.alert.utils.CronTools;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class AbstractAlert, with some useful features commonly used: parameters (id, name, properties), sql execution
 */
public abstract class AbstractAlert implements IAlert {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractAlert.class);

	public static final String	PROPERTY_CRON	= "CRON";

	/** The alert id. */
	private final Number		id;

	/** The alert name. */
	private final String		name;

	/** The alert properties. */
	private final Properties	properties;

	/** Properties from DB Alert settings */
	private final Map<?, ?>		settings;

	/** Extra data on execution time (on demand calls) */
	private final Map<?, ?>		extraData;

	public AbstractAlert(Number id, String name, Properties properties, Map<?, ?> settings, Map<?, ?> extraData) {
		super();
		this.id = id;
		this.name = name;
		this.properties = properties;
		this.settings = settings;
		this.extraData = extraData;
	}

	public AbstractAlert(Number id, String name, Properties properties, Map<?, ?> settings) {
		this(id, name, properties, settings, null);
	}

	public AbstractAlert(Number id, String name, Properties properties) {
		this(id, name, properties, null);
	}

	public AbstractAlert(String name, Properties properties) {
		this(null, name, properties, null);
	}

	public AbstractAlert(Properties properties) {
		this(null, null, properties, null);
	}

	/**
	 * Gets the indicator id.
	 *
	 * @return the indicator id
	 */
	@Override
	public Number getAlrId() {
		return this.id;
	}

	/**
	 * Gets the alert name.
	 *
	 * @return the alert name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the alert properties.
	 *
	 * @return the alert properties
	 */
	@Override
	public Properties getProperties() {
		return this.properties;
	}

	public Object getProperty(Object key) {
		return (this.properties == null) || (key == null) ? null : this.properties.get(key);
	}

	/**
	 * Gets the alertSettings
	 *
	 * @return
	 */
	@Override
	public Map<?, ?> getSettings() {
		return this.settings;
	}

	public Object getSetting(Object key) {
		return (this.settings == null) || (key == null) ? null : this.settings.get(key);
	}

	public String getSettingsEmailSubject() {
		return (String) this.getSetting(AlertNaming.ALR_SUBJECT);
	}

	public String getSettingsEmailBody() {
		return (String) this.getSetting(AlertNaming.ALR_BODY);
	}

	@Override
	public Map<?, ?> getExtraData() {
		return this.extraData;
	}

	@Override
	public String toString() {
		return "ALERT:  \tClass:" + this.getClass().getName() + "\tId:" + this.getAlrId() /* + "\tSettings:" + this.getSettings() */;
	}

	@Override
	public boolean isValidToExecute(Date dispatchDate) {
		String cron = (String) this.getProperty(AbstractAlert.PROPERTY_CRON);
		if ("NO".equals(cron)) {
			return false;
		} else if (cron != null) {
			return CronTools.checkCronHourly(cron, dispatchDate);
		}
		return true;
	}

	// Utilities --------------------------------------------
	// TODO consider to move to helpers

	protected EntityResult executeQuery(String sql) throws AlertException {
		AbstractAlert.logger.debug("sentence_to_execute:{}", sql);
		try {
			EntityResult res = new OntimizeConnectionTemplate<EntityResult>() {
				@Override
				protected EntityResult doTask(Connection con) throws UException, SQLException {
					return new QueryJdbcTemplate<EntityResult>() {

						@Override
						protected EntityResult parseResponse(ResultSet rset) throws UException, SQLException {
							EntityResult res = new EntityResult();
							FileTableEntity.resultSetToEntityResult(rset, res);
							return res;
						}
					}.execute(con, sql);
				}
			}.execute((SecureReferenceLocator) this.getLocator(), true);
			return res;
		} catch (Exception err) {
			throw new AlertException("E_EXECUTING_SQL", err);
		}
	}

	protected void saveAlertResult(Date executionDate, Number executionNumber, IAlert instance, IAlertResult alertResult) throws AlertException {
		if (alertResult == null) {
			AbstractAlert.logger.debug("NO_RESULT__executionNumber:{} alrdId:{}", executionNumber, instance.getAlrId());
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
		TemplateTools templateTools = this.getLocator().getBean(TemplateTools.class);
		String subject = templateTools.velocityWithStringTemplateExample(subjectBase, toFill);
		String body = templateTools.velocityWithStringTemplateExample(bodyBase, toFill);

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
					AbstractAlert.logger.warn("W_NO_TARGET_EMAILS");
					MapTools.safePut(targetValues, AlertNaming.ALE_ERROR, alertResult == null ? "NORESULT AND NO_TARGET_EMAILS" : "NO_TARGET_EMAILS");
				}
			} catch (Exception err) {
				AbstractAlert.logger.error("E_SENDING_EMAIL", err);
				MapTools.safePut(targetValues, AlertNaming.ALE_ERROR, ExceptionTools.getStackTrace(err));
			}
		}

		// Savable to DB?
		boolean saveDB = ParseUtilsExtended.getBoolean(settings.get(AlertNaming.ALR_SAVEDB), false);
		MapTools.safePut(targetValues, AlertNaming.ALE_SAVEDB, saveDB ? "S" : "N");
		if (saveDB) {
			try {
				MapTools.safePut(targetValues, AlertNaming.ALE_SAVEDDB, "S");
				this.saveAlertResult(targetValues);
			} catch (Exception err) {
				AbstractAlert.logger.error("E_SAVING_DB", err);
			}
		}
	}

	protected void saveAlertResult(Map<?, ?> attributes) throws AlertException {
		this.getAlertService().alertExecutionInsert(attributes);
	}

	protected void sendEmail(List<String> to, List<String> cc, List<String> bcc, String templateSubject, String templateBody) throws AlertException {
		this.getAlertService().sendEmail(to, cc, bcc, templateSubject, templateBody);
	}

	protected void sendEmailCompose(List<String> to, List<String> cc, List<String> bcc, String templateSubject, String templateBody, Map<?, ?> data) throws AlertException {
		this.getAlertService().sendEmailCompose(to, cc, bcc, templateSubject, templateBody, data);
	}

	///////// UTILITIES ///////////////////////

	protected IOpentachServerLocator getLocator() {
		return AbstractOpentachServerLocator.getLocator();
	}

	protected IAlertService getAlertService() {
		return this.getServiceSafe(IAlertService.class);
	}

	protected <T> T getServiceSafe(Class<T> serviceClass) {
		return SpringTools.getTargetObject(this.getLocator().getBean(serviceClass), serviceClass);
	}
}