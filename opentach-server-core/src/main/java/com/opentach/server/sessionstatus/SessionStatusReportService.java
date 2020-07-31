package com.opentach.server.sessionstatus;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.tools.BasicExpressionTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.Trio;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.sessionstatus.ISessionStatusReportService;
import com.opentach.common.user.IUserData;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.SecurityTools;
import com.opentach.server.util.StatementBuilderHelper;
import com.utilmize.server.services.UAbstractService;

public class SessionStatusReportService extends UAbstractService implements ISessionStatusReportService {

	private static final Logger logger = LoggerFactory.getLogger(SessionStatusReportService.class);

	public SessionStatusReportService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	private void checkPermissions(BasicExpression queryFilter, int sessionId) throws Exception {
		if (SecurityTools.isAdmin((IOpentachServerLocator) this.getLocator(), sessionId)) {
			return;
		}
		BasicExpression findConditionForFields = BasicExpressionTools.findConditionForFields(queryFilter, "CIF");
		if (findConditionForFields == null) {
			throw new GeneralSecurityException("E_CHOOSE_COMPANIES");
		}
		Object rightOperand = findConditionForFields.getRightOperand();
		if (!ObjectTools.isIn(findConditionForFields.getOperator(), BasicOperator.IN_OP, BasicOperator.EQUAL_OP)) {
			throw new GeneralSecurityException("E_CHOOSE_COMPANIES");
		}
		if (rightOperand instanceof String) {
			SecurityTools.checkPermissions((IOpentachServerLocator) this.getLocator(), (String)rightOperand, sessionId);
		} else {
			SecurityTools.checkPermissions((IOpentachServerLocator)this.getLocator(), (List<String>)rightOperand, sessionId);
		}

	}

	// ********************************
	// ************SESSION*************
	// ********************************
	@Override
	public EntityResult getSessionMeanTime(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionMeanTime", queryFilter, grouping);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getSessionNumConnections(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionNumConnections", queryFilter, groupingTime);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getSessionConnectionsPerHour(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionConnectionsPerHour", queryFilter, null);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getSessionConnectionsPerDayOfWeek(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			final IUserData ud = ((IOpentachServerLocator) this.getLocator()).getUserData(sessionId);
			if (ud == null) {
				return null;
			}
			Locale locale = ud.getLocale();
			if (Locale.ITALY.equals(locale)) {
				return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionConnectionsPerDayOfWeek_Italian", queryFilter, null);
			}else {
				return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionConnectionsPerDayOfWeek", queryFilter, null);
			}
			
			
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getSessionUnconnectedUsers(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			List<Trio<String, String, BasicExpression>> preFilter = Arrays
					.asList(new Trio<String, String, BasicExpression>("#TIME_FILTER#", "", BasicExpressionTools.findConditionForFields(queryFilter, "F_INI")));
			BasicExpression copyFilter = BasicExpressionTools.copyWithoutFields(queryFilter, "F_INI");
			BasicExpressionTools.renameField(copyFilter, "CIF", "dfemp.CIF");
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionUnconnectedUsers", copyFilter, null, preFilter, Collections.EMPTY_LIST);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getSessionConnectedVsUnconnectedUsers(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			List<Trio<String, String, BasicExpression>> preFilter = Arrays.asList(//
					new Trio<String, String, BasicExpression>("#TIME_FILTER#", "", BasicExpressionTools.findConditionForFields(queryFilter, "F_INI")), //
					new Trio<String, String, BasicExpression>("#OTHER_FILTER#", " AND ", BasicExpressionTools.copyWithoutFields(queryFilter, "F_INI"))//
					);

			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/sessionConnectedVsUnconnectedUsers", queryFilter, null, preFilter, Collections.EMPTY_LIST);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	// ********************************
	// ************Files***************
	// ********************************
	@Override
	public EntityResult getFilesUploadsByCompany(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			this.updateFilesFilter(queryFilter);
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/filesUploadsByCompany", queryFilter, null);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getFilesUploadsByType(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			this.updateFilesFilter(queryFilter);
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/filesUploadsByType", queryFilter, null);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getFilesUploads(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			this.updateFilesFilter(queryFilter);
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/filesUploads", queryFilter, groupingTime);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getFilesUploadsRegistersBySource(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			this.updateFilesFilter(queryFilter);
			BasicExpressionTools.renameField(queryFilter, "DFEMP.CIF", "CDVEMPRE_REQ_REALES.CIF");
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/filesUploadsRegistersBySource", queryFilter, null);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	private void updateFilesFilter(BasicExpression queryFilter) {
		BasicExpressionTools.renameField(queryFilter, "F_INI", "CDFICHEROS.F_ALTA");
		BasicExpressionTools.renameField(queryFilter, "usu.USUARIO", "CDFICHEROS.USUARIO_ALTA");
		BasicExpressionTools.renameField(queryFilter, "CIF", "DFEMP.CIF");
	}

	// ********************************
	// ************USAGE***************
	// ********************************

	@Override
	public EntityResult getUsageToolsBySession(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			String queryPath = "sql/sessionstatus/usageToolsBySession";
			if (BasicExpressionTools.containsField(queryFilter, "CIF")) {
				queryPath = "sql/sessionstatus/usageToolsBySessionFilterCompany";
			}
			BasicExpressionTools.renameField(queryFilter, "F_INI", "CDLOGSESIONSTAT.F_INI");
			BasicExpressionTools.renameField(queryFilter, "usu.USUARIO", "CDLOGSESIONSTAT.USUARIO");
			return StatementBuilderHelper.doQuery(this.getLocator(), queryPath, queryFilter, null);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	// ********************************
	// ************TASKS***************
	// ********************************
	@Override
	public EntityResult getTaskEvolution(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			BasicExpressionTools.renameField(queryFilter, "F_INI", "TSK_CREATION_DATE");
			queryFilter = BasicExpressionTools.copyWithoutFields(queryFilter, "usu.USUARIO");

			List<Trio<String, String, BasicExpression>> previousReplacements = Arrays.asList(//
					new Trio<>("#OTHER_WHERE#", "", queryFilter));
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/tasksEvolution", queryFilter, groupingTime, previousReplacements, Collections.EMPTY_LIST);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

	@Override
	public EntityResult getTaskCreatedPerCompany(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			BasicExpressionTools.renameField(queryFilter, "F_INI", "TSK_CREATION_DATE");
			BasicExpressionTools.renameField(queryFilter, "CIF", "TSK_TASK.CIF");
			queryFilter = BasicExpressionTools.copyWithoutFields(queryFilter, "usu.USUARIO");
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/tasksCreatedPerCompany", queryFilter, null);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}
	
	
	@Override
	public EntityResult getDriversVehiclesWithoutDownload(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception {
		this.checkPermissions(queryFilter, sessionId);
		try {
			List<Trio<String, String, BasicExpression>> preFilter = Arrays
					.asList(new Trio<String, String, BasicExpression>("#TIME_FILTER#", "", BasicExpressionTools.findConditionForFields(queryFilter, "F_INI")));
			BasicExpression copyFilter = BasicExpressionTools.copyWithoutFields(queryFilter, "F_INI");
			BasicExpressionTools.renameField(copyFilter, "CIF", "dfemp.CIF");
			return StatementBuilderHelper.doQuery(this.getLocator(), "sql/sessionstatus/driversVehiclesWithoutDownload", copyFilter, null, preFilter, Collections.EMPTY_LIST);
		} catch (Exception error) {
			SessionStatusReportService.logger.error(null, error);
			throw new Exception("E_QUERYING_STATS");
		}
	}

}
