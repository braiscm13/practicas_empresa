package com.opentach.server.labor.labor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.AnalysisParameters;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.security.GeneralSecurityException;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.exception.OpentachWrapException;
import com.opentach.common.labor.laboral.ILaborService;
import com.opentach.common.labor.util.IntervalDate;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.labor.labor.DailyWorkRecord.DailyWorkRecordOrigin;
import com.opentach.server.labor.util.ExceptionUtils;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.PermissionsUtil;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.thread.Paralelizer;
import com.utilmize.tools.thread.Paralelizer.IParalelizable;

import net.sf.jasperreports.engine.JasperPrint;
/**
 * The Class LaborService.
 */
public class LaborService extends UAbstractService implements ILaborService {

	/** The Constant logger. */
	private static final Logger				logger			= LoggerFactory.getLogger(LaborService.class);

	/** The Constant LABOR_SERVICE. */
	private static final String				LABOR_SERVICE	= "LaborService";
	/** The parameters. */
	private AnalysisParameters				parameters;

	/** The executor. */
	private final LaborTaskQueueExecutor	executor;

	/** The labor report helper. */
	private final LaborServiceReportHelper	laborReportHelper;

	/**
	 * Instantiates a new labor service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public LaborService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.executor = new LaborTaskQueueExecutor((IOpentachServerLocator) erl);
		this.laborReportHelper = new LaborServiceReportHelper((IOpentachServerLocator) erl);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.common.labor.laboral.ILaborService#createLaborReport(java.util.List, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public BytesBlock createLaborReport(List<Object> driverIds, String companyCif, Date from, Date to, int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, LaborService.LABOR_SERVICE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		try {
			return new OntimizeConnectionTemplate<BytesBlock>() {

				@Override
				protected BytesBlock doTask(Connection con) throws UException, SQLException {
					try {
						return LaborService.this.createlaborReport(driverIds, companyCif, from, to, con, sessionId);
					} catch (SQLException | UException err) {
						throw err;
					} catch (Exception err) {
						throw new OpentachWrapException(err.getMessage(), err);
					}
				}

			}.execute(this.getConnectionManager(), true);
		} catch (Exception error) {
			LaborService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.common.labor.laboral.ILaborService#createDriverJournalReport(java.util.List, java.lang.String, java.util.Date, java.util.Date, java.lang.String, boolean,
	 * int)
	 */
	@Override
	public BytesBlock createDriverJournalReport(List<Object> driverIds, String companyCif, Date from, Date to, String reportType, boolean avoidExtraTime, int sessionId)
			throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, LaborService.LABOR_SERVICE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		try {
			return new OntimizeConnectionTemplate<BytesBlock>() {

				@Override
				protected BytesBlock doTask(Connection con) throws UException, SQLException {
					try {
						return LaborService.this.createDriverJournalReport(driverIds, companyCif, new IntervalDate(from, to), reportType, avoidExtraTime, con, sessionId);
					} catch (SQLException | UException err) {
						throw err;
					} catch (Exception err) {
						throw new OpentachWrapException(err.getMessage(), err);
					}
				}

			}.execute(this.getConnectionManager(), true);
		} catch (Exception error) {
			LaborService.logger.error(null, error);
			throw ExceptionUtils.translateException(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.common.labor.laboral.ILaborService#createDriverStatusTimeReport(java.util.List, java.lang.String, java.util.Date, java.util.Date, java.lang.String,
	 * java.util.List, int)
	 */
	@Override
	public BytesBlock createDriverStatusTimeReport(List<Object> driverIds, String companyCif, Date from, Date to, String reportType, List<String> activityTypes, int sessionId)
			throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, LaborService.LABOR_SERVICE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		try {
			return new OntimizeConnectionTemplate<BytesBlock>() {

				@Override
				protected BytesBlock doTask(Connection con) throws UException, SQLException {
					List<StretchType> stretchTypes = new ArrayList<>();
					for (String name : activityTypes) {
						stretchTypes.add(StretchType.valueOf(name));
					}
					try {
						return LaborService.this.createDriverStatusTimeReport(driverIds, companyCif, new IntervalDate(from, to), reportType, stretchTypes, con, sessionId);
					} catch (SQLException | UException err) {
						throw err;
					} catch (Exception err) {
						throw new OpentachWrapException(err.getMessage(), err);
					}
				}

			}.execute(this.getConnectionManager(), true);
		} catch (Exception error) {
			LaborService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.common.labor.laboral.ILaborService#createDailyRecordReport(java.util.List, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public BytesBlock createDailyRecordReport(List<Object> driverIds, String companyCif, Date from, Date to, int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, LaborService.LABOR_SERVICE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		try {
			return new OntimizeConnectionTemplate<BytesBlock>() {

				@Override
				protected BytesBlock doTask(Connection con) throws UException, SQLException {
					try {
						return LaborService.this.createDailyRecordReport(driverIds, companyCif, new IntervalDate(from, to), con, sessionId);
					} catch (SQLException | UException err) {
						throw err;
					} catch (Exception err) {
						throw new OpentachWrapException(err.getMessage(), err);
					}
				}

			}.execute(this.getConnectionManager(), true);
		} catch (Exception error) {
			LaborService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.common.labor.laboral.ILaborService#queryLaborReport(java.util.List, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public EntityResult queryLaborReport(List<Object> driverIds, String companyCif, Date from, Date to, int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, LaborService.LABOR_SERVICE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws UException, SQLException {
					try {
						return LaborService.this.querylaborReport(driverIds, companyCif, new IntervalDate(from, to), con, sessionId);
					} catch (SQLException | UException err) {
						throw err;
					} catch (Exception err) {
						throw new OpentachWrapException(err.getMessage(), err);
					}
				}

			}.execute(this.getConnectionManager(), true);
		} catch (Exception error) {
			LaborService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/**
	 * Creates the daily record report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param queryInterval
	 *            the query interval
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	protected BytesBlock createDailyRecordReport(List<Object> driverIds, String companyCif, IntervalDate queryInterval, Connection con, int sessionId) throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		return this.laborReportHelper.createPdfReport("reports/dailyjournalrecord.jasper", driverIds, companyCif, queryInterval, parameters, con, sessionId);
	}

	/**
	 * Createlabor report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	protected BytesBlock createlaborReport(List<Object> driverIds, String companyCif, Date from, Date to, Connection con, int sessionId) throws Exception {
		return this.createLaborReport(driverIds, companyCif, new IntervalDate(from, to), con, sessionId);
	}

	/**
	 * Create Driver Journal Report report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param queryInterval
	 *            the query interval
	 * @param reportType
	 *            the report type
	 * @param avoidExtraTime
	 *            the avoid extra time
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	protected BytesBlock createDriverJournalReport(List<Object> driverIds, String companyCif, IntervalDate queryInterval, String reportType, boolean avoidExtraTime, Connection con,
			int sessionId) throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("ReportType", reportType);
		parameters.put("AvoidExtraTime", avoidExtraTime);
		parameters.put("Firma", "Opentach es una marca propiedad de OpenServices S.L.");
		return this.laborReportHelper.createPdfReport("reports/driverjournalrecord.jasper", driverIds, companyCif, queryInterval, parameters, con, sessionId);
	}

	/**
	 * Creates the driver status time report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param intervalDate
	 *            the interval date
	 * @param reportType
	 *            the report type
	 * @param activityTypes
	 *            the activity types
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	protected BytesBlock createDriverStatusTimeReport(List<Object> driverIds, String companyCif, IntervalDate intervalDate, String reportType, List<StretchType> activityTypes,
			Connection con, int sessionId) throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("ReportType", reportType);
		parameters.put("AvoidExtraTime", true);
		parameters.put("Firma", "Opentach es una marca propiedad de OpenServices S.L.");

		// obtenemos el tipo de convenio y las caracteristicas laborales del trabajador
		List<DriverSettings> analyzeDriverSettings = this.executor.analyzeDriverSettings(driverIds, companyCif, sessionId);
		List<LaborResult> laborResults = this.executor.analyzeStatusTime(analyzeDriverSettings, intervalDate, activityTypes, true, sessionId);
		List<JasperPrint> concat = new ArrayList<>(laborResults.size());
		for (LaborResult laborResult : laborResults) {
			JasperPrint jp = this.laborReportHelper.buildReport("reports/driverstatustime.jasper", laborResult, parameters);
			concat.add(jp);
		}
		return this.laborReportHelper.toPdfBytesBlock(concat);

	}

	/**
	 * Creates the report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param queryInterval
	 *            the query interval
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	protected BytesBlock createLaborReport(List<Object> driverIds, String companyCif, IntervalDate queryInterval, Connection con, int sessionId) throws Exception {
		// obtenemos el tipo de convenio y las caracteristicas laborales del trabajador
		List<DriverSettings> driverSettings = this.executor.analyzeDriverSettings(driverIds, companyCif, sessionId);
		List<JasperPrint> concat = new ArrayList<>(driverSettings.size());
		try {
			Paralelizer.paralelizeAndWaitResult(driverSettings, new IParalelizable<DriverSettings, Void>() {

				@Override
				public Void execute(DriverSettings driverSetting) throws UException {
					try {
						Pair<IntervalDate, String> reportSetting = LaborService.this.laborReportHelper.combineContracts(driverSetting, queryInterval);
						LaborResult laborResult = LaborService.this.executor
								.analyze(Arrays.asList(new DriverSettings[] { driverSetting }), reportSetting.getFirst(), true, sessionId).get(0);

						Map<String, Object> parameters = new HashMap<>();
						parameters.put("ReportType", reportSetting.getSecond());
						JasperPrint jp = LaborService.this.laborReportHelper.buildReport("reports/extrajournalrecord.jasper", laborResult, parameters);
						// TODO ver si luego se pueden concatenar pdf en vez de volver a generar los jasperprint
						LaborService.this.laborReportHelper.saveLaborReportIntoWarehouse(jp, laborResult.getDriverSettings().getDriverId(),
								laborResult.getDriverSettings().getCompanyCif(), reportSetting.getFirst(), sessionId, con);
						synchronized (concat) {
							concat.add(jp);
						}
						return null;
					} catch (UException err) {
						throw err;
					} catch (Exception err) {
						throw new OpentachWrapException(err.getMessage(), err);
					}
				}

			});
		} catch (Exception error) {
			throw (Exception) error.getCause();
		}
		return this.laborReportHelper.toPdfBytesBlock(concat);
	}

	/**
	 * Analyze.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param queryInterval
	 *            the query interval
	 * @param groupDays
	 *            the group days
	 * @param sessionId
	 *            the session id
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	public List<LaborResult> analyze(List<Object> driverIds, String companyCif, IntervalDate queryInterval, boolean groupDays, int sessionId) throws Exception {
		List<DriverSettings> analyzeDriverSettings = this.executor.analyzeDriverSettings(driverIds, companyCif, sessionId);
		return this.executor.analyze(analyzeDriverSettings, queryInterval, groupDays, sessionId);
	}

	/**
	 * Querylabor report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param queryInterval
	 *            the query interval
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	protected EntityResult querylaborReport(List<Object> driverIds, String companyCif, IntervalDate queryInterval, Connection con, int sessionId) throws Exception {
		// obtenemos el tipo de convenio y las caracteristicas laborales del trabajador
		List<LaborResult> laborResults = this.analyze(driverIds, companyCif, queryInterval, false, sessionId);
		EntityResult res = new EntityResult();
		List<String> cols = Arrays.asList("IDCONDUCTOR", "DNI", "NOMBRE", "APELLIDOS", "DIA", "FECINI", "FECFIN", "MINUTOS", "CIF", "NOMB", "ORIGEN");
		EntityResultTools.initEntityResult(res, cols);
		for (LaborResult laborResult : laborResults) {
			DriverSettings driverSettings = laborResult.getDriverSettings();
			List<DailyWorkRecord> dailyWorkRecord = laborResult.getDailyRecords();
			this.toEntityResult(res, cols, driverSettings, dailyWorkRecord);
		}
		// ordenamos por conductor y fecha
		res = EntityResultTools.doSort(res, "IDCONDUCTOR", "DIA");
		return res;
	}

	/**
	 * To entity result.
	 *
	 * @param res
	 *            the res
	 * @param cols
	 *            the cols
	 * @param driverSettings
	 *            the driver settings
	 * @param dailyWorkRecord
	 *            the daily work record
	 * @return the entity result
	 */
	private void toEntityResult(EntityResult res, List<String> cols, DriverSettings driverSettings, List<DailyWorkRecord> dailyWorkRecord) {
		int i = res.calculateRecordNumber();

		for (String col : cols) {
			((Vector<Object>) res.get(col)).ensureCapacity(i + dailyWorkRecord.size());

			Object[] ob = new Object[dailyWorkRecord.size()];
			switch (col) {
				case "IDCONDUCTOR":
					Arrays.fill(ob, driverSettings.getDriverId());
					break;
				case "DNI":
					Arrays.fill(ob, driverSettings.getDriverDni());
					break;
				case "NOMBRE":
					Arrays.fill(ob, driverSettings.getDriverName());
					break;
				case "APELLIDOS":
					Arrays.fill(ob, driverSettings.getDriverSurname());
					break;
				case "CIF":
					Arrays.fill(ob, driverSettings.getCompanyCif());
					break;
				case "NOMB":
					Arrays.fill(ob, driverSettings.getCompanyName());
					break;
				default:
					Arrays.fill(ob, null);
					break;
			}
			((List<Object>) res.get(col)).addAll(Arrays.asList(ob));
		}

		for (DailyWorkRecord record : dailyWorkRecord) {
//			System.out.println(record);
			((List<Object>) res.get("DIA")).set(i, record.getDayString());
			((List<Object>) res.get("FECINI")).set(i, record.getFrom());
			((List<Object>) res.get("FECFIN")).set(i, record.getTo());
			((List<Object>) res.get("MINUTOS")).set(i, record.getWorkingMinutes());
			((List<Object>) res.get("ORIGEN")).set(i, DailyWorkRecordOrigin.AUTO.equals(record.getOrigin()) ? "AUTO" : "MANUAL");
			i++;
		}
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 * @throws Exception
	 *             the exception
	 */
	public AnalysisParameters getParameters() throws Exception {
		this.checkRequirements();
		return this.parameters.copy();
	}

	/**
	 * Check requirements.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void checkRequirements() throws Exception {
		synchronized (this) {
			if (this.parameters == null) {
				new OntimizeConnectionTemplate<Void>() {
					@Override
					protected Void doTask(Connection con) throws UException, SQLException {
						try {
							LaborService.this.parameters = LaborService.this.queryParameters(con);
						} catch (SQLException | UException err) {
							throw err;
						} catch (Exception err) {
							throw new OpentachWrapException(err.getMessage(), err);
						}
						return null;
					}
				}.execute(this.getConnectionManager(), true);
			}
		}
	}

	/**
	 * Query parameters.
	 *
	 * @param con
	 *            the con
	 * @return the analysis parameters
	 * @throws Exception
	 *             the exception
	 */
	private AnalysisParameters queryParameters(Connection con) throws Exception {
		AnalysisParameters anaParam = new AnalysisParameters();
		TransactionalEntity eParameters = this.getEntity("EParametros");
		EntityResult res = eParameters.query(new Hashtable<String, Object>(), EntityResultTools.attributes("COD", "VALOR"), this.getEntityPrivilegedId(eParameters), con);
		int nregs = res.calculateRecordNumber();
		for (int i = 0; i < nregs; i++) {
			Hashtable record = res.getRecordValues(i);
			String cod = ((String) record.get("COD"));
			int value = ((Number) record.get("VALOR")).intValue();
			try {
				ReflectionTools.invoke(anaParam, "set" + cod, value);
			} catch (Exception ex) {
				LaborService.logger.warn("Not used parameter {}", cod);
			}
		}
		anaParam.setAllowDailyRestInterruptions(true);
		anaParam.setConvertIndeterminateToRest(true);
		return anaParam;
	}

}
