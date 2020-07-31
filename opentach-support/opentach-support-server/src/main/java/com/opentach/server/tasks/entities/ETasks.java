package com.opentach.server.tasks.entities;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.db.NullValue;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.BasicExpressionTools;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.interfaces.IOpentachLocator;
import com.opentach.common.tasks.ITasksReport;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.ResourceManager;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class ETasks extends FileTableEntity implements ITasksReport {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(ETasks.class);

	public ETasks(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public ETasks(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		IUserData userData = ((IOpentachLocator) this.locator).getUserData(sesionId);
		if ((userData != null) && IUserData.NIVEL_AGENTE.equals(userData.getLevel())) {
			List<String> companiesList = userData.getAllCompaniesList();
			if ((companiesList == null) || companiesList.isEmpty()) {
				throw new Exception("E_USER_CONFIG");
			}
			BasicExpression basicExpression = BasicExpressionTools.convertSearchValue(new BasicField("CIF"), new SearchValue(SearchValue.IN, new Vector<String>(companiesList)));
			avOrig.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
					BasicExpressionTools.combineExpression(basicExpression, (BasicExpression) avOrig.get(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)));
		}
		return super.query(avOrig, av, sesionId, con);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {

		try {
			IUserData userData = ((IOpentachLocator) this.locator).getUserData(sesionId);
			if (userData.getLevel().equals(IUserData.NIVEL_AGENTE)) {
				av.put("USUARIO_CREATOR", userData.getLogin());
			}
		} catch (Exception err) {
			ETasks.logger.error(null, err);
		}
		this.checkClosedDate(av, null, sesionId, con);
		return super.insert(av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId, Connection con) throws Exception {
		this.checkClosedDate(av, cv, sesionId, con);
		return super.update(av, cv, sesionId, con);
	}

	private void checkClosedDate(Hashtable av, Hashtable cv, int sesionId, Connection con) {
		// Consider to set close time
		Object status = ObjectTools.coalesce(av.get("TKS_ID"), cv == null ? null : cv.get("TKS_ID"));
		if (status != null) {
			Object closedDateToSet = null;
			if (this.isFinalStatus(status, sesionId, con)) {
				// Mark current endDate
				closedDateToSet = new Date();// Consider to set GMT hour
			} else {
				// Clean endDate (re-openned task)
				closedDateToSet = new NullValue();
			}
			av.put("TSK_CLOSED_DATE", closedDateToSet);
		}
	}

	private boolean isFinalStatus(Object status, int sesionId, Connection con) {
		if ((status == null) || (status instanceof NullValue)) {
			return false;
		}

		try {
			Entity eTaskStatus = this.getEntityReference("ETaskStatus");
			EntityResult res = eTaskStatus.query(EntityResultTools.keysvalues("TKS_IS_FINAL", "S", "TKS_ID", status), EntityResultTools.attributes("TKS_ID"),
					TableEntity.getEntityPrivilegedId(eTaskStatus));
			CheckingTools.checkValidEntityResult(res, "E_GETTING_CLOSED_STATUS");
			return res.calculateRecordNumber() > 0;
		} catch (Exception ex) {
			ETasks.logger.error("E_GETTING_CLOSED_STATUS", ex);
			return false;
		}
	}

	@Override
	public JasperPrint generateReport(Hashtable<Object, Object> filters, int sessionId) throws Exception {
		try {
			String timezoneOffsetMs = (String) filters.remove("OPENTACH_SYSTEM_DEFAULT_TIMEZONE");

			MapTools.safePut(filters, "CIF", new SearchValue(SearchValue.NOT_NULL, null));
			EntityResult res = this.query(filters, EntityResultTools.attributes("USUARIO_ASIGNEE", "CIF", "COMPANY_NAME", "CONTACT_NAME", "ECN_TLF1", "ECN_TLF2", "ECN_MAIL",
					"TSK_TITLE", "TSK_CREATION_DATE", "TSK_CLOSED_DATE", "TPR_NAME", "TKS_NAME", "TKC_NAME", "TSU_NAME"), sessionId);

			// Customize report data with user timezone
			String[] dateColumns = new String[] { "TSK_CREATION_DATE", "TSK_CLOSED_DATE" };
			if ((timezoneOffsetMs != null) && (res.calculateRecordNumber() > 0)) {
				int offset = Integer.valueOf(timezoneOffsetMs);
				for (String dateColumn : dateColumns) {
					Vector<Date> values = (Vector<Date>) res.get(dateColumn);
					for (int i = 0; i < values.size(); i++) {
						values.set(i, values.get(i) == null ? null : new Timestamp(values.get(i).getTime() + offset));
					}
				}
			}

			String report = "com/opentach/reports/incidenciasPorUsuarioYEmpresa.jasper";
			HashMap<String, Object> params = new HashMap<String, Object>();
			
			Locale locale = null;
			IUserData du = this.getLocator().getUserData(sessionId);
			if (du != null) {
				locale = du.getLocale();
			}
			
			params.put(JRParameter.REPORT_LOCALE, locale == null ? new Locale("es", "ES") : locale);
			ResourceBundle rb = ResourceManager.getBundle((Locale) params.get(JRParameter.REPORT_LOCALE));
			if (rb != null) {
				params.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
			}
			JasperReport jr = JRReportUtil.getJasperReport(report);
			JasperPrint jp = JasperFillManager.fillReport(jr, params, new JRTableModelDataSource(EntityResultUtils.createTableModel(res)));
			return jp;
		} catch (Exception ex) {
			ETasks.logger.error("E_GENERATING_REPORT", ex);
			throw new UException("E_GENERATING_REPORT", ex);
		}
	}
}
