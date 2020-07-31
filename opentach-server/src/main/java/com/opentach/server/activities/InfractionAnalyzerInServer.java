package com.opentach.server.activities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.activity.Activity;
import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.AnalysisResult;
import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.imatia.tacho.infraction.Infraction;
import com.imatia.tacho.infraction.Infraction.InfractionType;
import com.imatia.tacho.infraction.Scale;
import com.imatia.tacho.infraction.ScaleEntry;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.ResourceManager;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class ServerInfractionAnalyzer.
 */
public class InfractionAnalyzerInServer extends AbstractDelegate {

	/** The Constant logger. */
	private static final Logger					logger			= LoggerFactory.getLogger(InfractionAnalyzerInServer.class);

	/** The Constant PRIORITY_LOW. */
	private static final int					PRIORITY_LOW	= 0;

	/** The Constant PRIORITY_HIGH. */
	private static final int					PRIORITY_HIGH	= 1;

	/** The instance. */
	private static InfractionAnalyzerInServer	instance;

	/**
	 * Gets the single instance of ServerInfractionAnalyzer.
	 *
	 * @param locator
	 *            the locator
	 * @return single instance of ServerInfractionAnalyzer
	 */
	public static InfractionAnalyzerInServer getInstance(IOpentachServerLocator locator) {
		if (InfractionAnalyzerInServer.instance == null) {
			InfractionAnalyzerInServer.instance = new InfractionAnalyzerInServer(locator);
		}
		return InfractionAnalyzerInServer.instance;
	}

	/** The scale. */
	private Scale								scale;

	/** The parameters. */
	private AnalysisParameters					parameters;

	/** The executor. */
	private final PriorityThreadPoolExecutor	executor;

	/**
	 * Instantiates a new server infraction analyzer.
	 *
	 * @param locator
	 *            the locator
	 */
	protected InfractionAnalyzerInServer(IOpentachServerLocator locator) {
		super(locator);
		this.executor = PoolExecutors.newPriorityPoolExecutor("InfractionsAnalyzer", 100, 10l, TimeUnit.SECONDS);
	}

	public AnalysisParameters getParameters() throws Exception {
		this.checkRequirements();
		return this.parameters;
	}

	/**
	 * Analyze.
	 *
	 * @param contract
	 *            the contract
	 * @param driverList
	 *            the driver list
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param av
	 *            the av
	 * @param sessionId
	 *            the session id
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	public EntityResult analyze(final Object contract, final Object cif, final List<Object> driverList, final Date beginDate, final Date endDate, final Vector<String> av,
			AnalyzerEngine engine, final int sessionId) throws Exception {
		Chronometer chrono = new Chronometer().start();
		List<InfractionSubmit<List<Infraction>>> submitList = this.analyzeFuture(contract, cif, driverList, beginDate, endDate, engine);

		Exception exception = null;
		EntityResult entityResult = new EntityResult();
		for (InfractionSubmit<List<Infraction>> submit : submitList) {
			if (exception != null) {
				this.cancelJob(submit);
			} else {
				try {
					List<Infraction> infractionList = submit.getFuture().get();
					InfractionAnalyzerInServer.logger.debug("Execution task time {}", chrono.elapsedMs());
					entityResult = EntityResultTools.doUnionAll(entityResult, this.toEntityResult(submit.getContract(), cif, submit.getDriver(), infractionList, av, sessionId));
					InfractionAnalyzerInServer.logger.debug("Union task time {}", chrono.elapsedMs());
				} catch (Exception ex) {
					exception = ex;
				}
			}
		}
		if (exception != null) {
			throw exception;
		}

		return entityResult;
	}

	public List<InfractionSubmit<List<Infraction>>> analyzeFuture(final Object contract, Object cif, final List<Object> driverList, final Date beginDate, final Date endDate,
			AnalyzerEngine engine) throws Exception {
		Chronometer chrono = new Chronometer().start();
		this.checkRequirements();
		InfractionAnalyzerInServer.logger.debug("Requirements time {}", chrono.elapsedMs());
		List<InfractionSubmit<List<Infraction>>> submitList = new ArrayList<>();
		List<Object> cleanDriverList = this.filterDrivers(driverList);
		for (Object driver : cleanDriverList) {
			InfractionAnalyzerInServer.logger.info("Request analysis for driver {} between {} and {}", driver, beginDate, endDate);
			Future<List<Infraction>> submit = this.executor.submit(new InfractionAnalyzerInServerTask(this.scale, this.parameters, contract, driver, beginDate, endDate,
					this.getLocator(), InfractionAnalyzerInServer.PRIORITY_LOW, engine));
			submitList.add(new InfractionSubmit<>(submit, contract, cif, driver));
		}
		return submitList;
	}

	public void analyzeForReport(Connection conn, Object contract, Object cif, List<Object> driverList, Date beginDate, Date endDate, AnalyzerEngine engine, int sessionId)
			throws Exception {
		Chronometer chrono = new Chronometer().start();
		this.checkRequirements();
		InfractionAnalyzerInServer.logger.debug("For report Requirements time {}", chrono.elapsedMs());
		List<InfractionSubmit<Void>> submitList = new ArrayList<>();
		List<Object> cleanDriverList = this.filterDrivers(driverList);
		for (Object driver : cleanDriverList) {
			InfractionAnalyzerInServer.logger.info("Request analysis for driver {} between {} and {}", driver, beginDate, endDate);
			Future<Void> submit = this.executor.submit(new InfractionAnalyzerInServerForReportTask(conn, this.scale, this.parameters, contract, cif, driver, beginDate, endDate,
					InfractionAnalyzerInServer.PRIORITY_LOW, engine, this.getLocator()));
			submitList.add(new InfractionSubmit<>(submit, contract, cif, driver));
		}
		Exception exception = null;
		// wait to finish
		for (InfractionSubmit<Void> submit : submitList) {
			if (exception != null) {
				this.cancelJob(submit);
			} else {
				try {
					submit.getFuture().get();
				} catch (Exception ex) {
					exception = ex;
				}
			}
			InfractionAnalyzerInServer.logger.debug("For report Execution task time {}", chrono.elapsedMs());
		}
		if (exception != null) {
			throw exception;
		}
	}

//	
//	public void analyzeForWS(Connection conn, Object contract, Object cif, List<Object> driverList, Date beginDate, Date endDate, AnalyzerEngine engine, int sessionId)
//			throws Exception {
//		Chronometer chrono = new Chronometer().start();
//		this.checkRequirements();
//		InfractionAnalyzerInServer.logger.debug("For report Requirements time {}", chrono.elapsedMs());
//		List<InfractionSubmit<Void>> submitList = new ArrayList<>();
//		List<Object> cleanDriverList = this.filterDrivers(driverList);
//		for (Object driver : cleanDriverList) {
//			InfractionAnalyzerInServer.logger.info("Request analysis for driver {} between {} and {}", driver, beginDate, endDate);
//			Future<Void> submit = this.executor.submit(new InfractionAnalyzerInServerForWSTask(conn, this.scale, this.parameters, contract, cif, driver, beginDate, endDate,
//					InfractionAnalyzerInServer.PRIORITY_LOW, engine, this.getLocator()));
//			submitList.add(new InfractionSubmit<>(submit, contract, cif, driver));
//		}
//		Exception exception = null;
//		// wait to finish
//		for (InfractionSubmit<Void> submit : submitList) {
//			if (exception != null) {
//				this.cancelJob(submit);
//			} else {
//				try {
//					submit.getFuture().get();
//				} catch (Exception ex) {
//					exception = ex;
//				}
//			}
//			InfractionAnalyzerInServer.logger.debug("For report Execution task time {}", chrono.elapsedMs());
//		}
//		if (exception != null) {
//			throw exception;
//		}
//	}
	public Pair<AnalysisResult, List<Activity>> analyzeFullInfo(Connection conn, Object contract, Object cif, Object driver, Date beginDate, Date endDate, AnalyzerEngine engine,
			int sessionId) throws Exception {
		Chronometer chrono = new Chronometer().start();
		this.checkRequirements();
		InfractionAnalyzerInServer.logger.debug("For report Requirements time {}", chrono.elapsedMs());

		Future<Pair<AnalysisResult, List<Activity>>> future = this.executor.submit(new InfractionAnalyzerInServerFullInfoTask(conn, this.scale, this.parameters, contract, driver,
				beginDate, endDate, InfractionAnalyzerInServer.PRIORITY_LOW, engine, this.getLocator()));
		return future.get();
	}

	private void cancelJob(InfractionSubmit<?> submit) {
		try {
			submit.getFuture().cancel(true);
		} catch (Exception ex) {
			// do nothing
		}
	}

	private List<Object> filterDrivers(List<Object> driverList) {
		HashSet<Object> set = new HashSet<Object>();
		set.addAll(driverList);
		return new ArrayList<Object>(set);
	}

	/**
	 * To entity result.
	 *
	 * @param contract
	 *            the contract
	 * @param driver
	 *            the driver
	 * @param infractionList
	 *            the infraction list
	 * @param av
	 *            the av
	 * @return the entity result
	 * @throws Exception
	 */
	private EntityResult toEntityResult(Object contract, Object cif, Object driver, List<Infraction> infractionList, Vector<String> av, int sessionID) throws Exception {
		if (!av.contains("IDCONDUCTOR")) {
			av.add("IDCONDUCTOR");
		}
		EntityResult er = new EntityResult();

		EntityResultTools.initEntityResult(er, av);

		Locale locale = null;
		IUserData du = this.getLocator().getUserData(sessionID);
		if (du != null) {
			locale = du.getLocale();
		}

		EntityResult erDrivers = ((TableEntity) this.getEntity("EConductorCont")).query(EntityResultTools.keysvalues("IDCONDUCTOR", driver, "CG_CONTRATO", contract),
				EntityResultTools.attributes("EXTERNAL_EMPLOYEE_ID", "DNI", "APELLIDOS", "NOMBRE", "IDDELEGACION"), sessionID);
		// CheckingTools.checkValidEntityResult(erDrivers, "DRIVER_NOT_FOUND", false, true, (Object[]) null);
		if (erDrivers.calculateRecordNumber() == 0) {
			InfractionAnalyzerInServer.logger.warn("Discarting results for non existing driver {}", driver);
			return er;
		}
		Map<String, Object> driverInfo = erDrivers.getRecordValues(0);

		EntityResult erEmpre = ((TableEntity) this.getEntity(CompanyNaming.ENTITY)).query(EntityResultTools.keysvalues("CIF", cif), EntityResultTools.attributes("NOMB", "CIF"), sessionID);
		CheckingTools.checkValidEntityResult(erEmpre, "COMPANY_NOT_FOUND", false, true, (Object[]) null);
		Map<String, Object> companyInfo = erEmpre.getRecordValues(0);

		int nregs = infractionList.size();
		for (String col : av) {
			Vector value = new Vector(nregs);
			value.setSize(nregs);
			er.put(col, value);
		}
		int i = 0;
		for (Infraction infraction : infractionList) {
			this.putRecordInfo(av, i, er, "CG_CONTRATO", contract);
			this.putRecordInfo(av, i, er, "NUMREQ", contract);
			this.putRecordInfo(av, i, er, "IDCONDUCTOR", driver);
			this.putRecordInfo(av, i, er, "DNI", driverInfo.get("DNI"));
			this.putRecordInfo(av, i, er, "EXTERNAL_EMPLOYEE_ID", driverInfo.get("EXTERNAL_EMPLOYEE_ID"));
			this.putRecordInfo(av, i, er, "NOMBRE", driverInfo.get("NOMBRE"));
			this.putRecordInfo(av, i, er, "APELLIDOS", driverInfo.get("APELLIDOS"));
			this.putRecordInfo(av, i, er, "IDDELEGACION", driverInfo.get("IDDELEGACION"));
			if (!driverInfo.isEmpty()) {
				this.putRecordInfo(av, i, er, "DSCR_COND",
						StringTools.concat((String) driverInfo.get("DNI"), " ", (String) driverInfo.get("APELLIDOS"), ", ", (String) driverInfo.get("NOMBRE")));

			}

			this.putRecordInfo(av, i, er, "CIF", companyInfo.get("CIF"));
			this.putRecordInfo(av, i, er, "NOMEMP", companyInfo.get("NOMB"));
			this.putRecordInfo(av, i, er, "FECHORAINI", infraction.getBeginDate());
			this.putRecordInfo(av, i, er, "FECHORAFIN", infraction.getEndDate());
			this.putRecordInfo(av, i, er, "NATURALEZA", infraction.getScaleEntry().getCgNatu());
			this.putRecordInfo(av, i, er, "IMPORTE", infraction.getScaleEntry().getAmount());
			this.putRecordInfo(av, i, er, "BARE1", infraction.getScaleEntry().getBare1());
			this.putRecordInfo(av, i, er, "BARE2", infraction.getScaleEntry().getBare2());
			this.putRecordInfo(av, i, er, "BARE34", infraction.getScaleEntry().getBare34());
			this.putRecordInfo(av, i, er, "BARE56", infraction.getScaleEntry().getBare56());
			this.putRecordInfo(av, i, er, "COD_BAREMO", infraction.getScaleEntry().getBare1() + "." + infraction.getScaleEntry().getBare2() + "." + infraction.getScaleEntry()
			.getBare34() + "." + ObjectTools.coalesce(infraction.getScaleEntry().getBare56(), ""));
			this.putRecordInfo(av, i, er, "LEVE", "L".equals(infraction.getScaleEntry().getCgNatu()) ? 1 : 0);
			this.putRecordInfo(av, i, er, "GRAVE", "G".equals(infraction.getScaleEntry().getCgNatu()) ? 1 : 0);
			this.putRecordInfo(av, i, er, "MUYGRAVE", "MG".equals(infraction.getScaleEntry().getCgNatu()) ? 1 : 0);

			switch (infraction.getType()) {
				case ECD1:
				case ECD2:
					this.putRecordInfo(av, i, er, "TIPO", "ECD");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "ECD" : ResourceManager.translate(locale, "ECD"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "ECD_INFRAC" : ResourceManager.translate(locale, "ECD_INFRAC"));
					break;
				case ECI:
					this.putRecordInfo(av, i, er, "TIPO", "ECI");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "ECI" : ResourceManager.translate(locale, "ECI"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "ECI_INFRAC" : ResourceManager.translate(locale, "ECI_INFRAC"));
					break;
				case ECS:
					this.putRecordInfo(av, i, er, "TIPO", "ECS");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "ECS" : ResourceManager.translate(locale, "ECS"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "ECS_INFRAC" : ResourceManager.translate(locale, "ECS_INFRAC"));
					break;
				case ECB:
					this.putRecordInfo(av, i, er, "TIPO", "ECB");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "ECB" : ResourceManager.translate(locale, "ECB"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "ECB_INFRAC" : ResourceManager.translate(locale, "ECB_INFRAC"));
					break;
				case FDD1:
				case FDD2:
				case FDD3:
				case FDD5:
					this.putRecordInfo(av, i, er, "TIPO", "FDD");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "FDD" : ResourceManager.translate(locale, "FDD"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "FDD_INFRAC" : ResourceManager.translate(locale, "FDD_INFRAC"));
					break;
				case FDS:
					this.putRecordInfo(av, i, er, "TIPO", "FDS");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "FDS" : ResourceManager.translate(locale, "FDS"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "FDS_INFRAC" : ResourceManager.translate(locale, "FDS_INFRAC"));
					break;
				case FDSR:
					this.putRecordInfo(av, i, er, "TIPO", "FDSR");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "FDSR" : ResourceManager.translate(locale, "FDSR"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "FDSR_INFRAC" : ResourceManager.translate(locale, "FDSR_INFRAC"));
					break;
				case FDS45:
					this.putRecordInfo(av, i, er, "TIPO", "FDS45");
					this.putRecordInfo(av, i, er, "TIPO_DSCR", locale == null ? "FDS45" : ResourceManager.translate(locale, "FDS45"));
					this.putRecordInfo(av, i, er, "DSCR_TIPO_INFRAC", locale == null ? "FDS45_INFRAC" : ResourceManager.translate(locale, "FDS45_INFRAC"));
					break;
			}

			switch (infraction.getType()) {
				case ECB:
				case ECD1:
				case ECD2:
				case ECI:
				case ECS:
					this.putRecordInfo(av, i, er, "TCP", infraction.getTime());
					this.putRecordInfo(av, i, er, "EXCON", infraction.getExcess());
					break;
				case FDD1:
				case FDD2:
				case FDD3:
				case FDD5:
				case FDS45:
				case FDS:
				case FDSR:
					this.putRecordInfo(av, i, er, "TDP", infraction.getTime());
					this.putRecordInfo(av, i, er, "FADES", infraction.getExcess());
					break;

				default:
					break;
			}
			i++;
		}
		return er;
	}

	private void putRecordInfo(Vector<String> av, int index, EntityResult er, String column, Object value) {
		if (av.contains(column) && (value != null)) {
			((Vector) er.get(column)).set(index, value);
		}
	}

	/**
	 * Check requirements.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void checkRequirements() throws Exception {
		synchronized (this) {
			if ((this.parameters == null) || (this.scale == null)) {
				new OntimizeConnectionTemplate<Void>() {
					@Override
					protected Void doTask(Connection con) throws UException {
						try {
							InfractionAnalyzerInServer.this.parameters = InfractionAnalyzerInServer.this.queryParameters(con);
							InfractionAnalyzerInServer.this.scale = InfractionAnalyzerInServer.this.queryScale(con);
							return null;
						} catch (Exception ex) {
							throw new UException(ex);
						}
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
				InfractionAnalyzerInServer.logger.warn("Not used parameter {}", cod);
			}
		}
		anaParam.setAllowDailyRestInterruptions(true);
		anaParam.setConvertIndeterminateToRest(true);
		return anaParam;
	}

	/**
	 * Query scale.
	 *
	 * @param con
	 *            the con
	 * @return the scale
	 * @throws Exception
	 *             the exception
	 */
	private Scale queryScale(Connection con) throws Exception {
		TransactionalEntity eScale = this.getEntity("EBaremo");
		EntityResult res = eScale.query(new Hashtable<String, Object>(),
				EntityResultTools.attributes("BARE1", "BARE2", "BARE34", "BARE56", "TMIN", "TMAX", "IMIN", "CG_NATU", "TP_INFRAC"), this.getEntityPrivilegedId(eScale), con);
		Map<InfractionType, List<ScaleEntry>> scaleMap = new HashMap<>();
		int nregs = res.calculateRecordNumber();
		for (int i = 0; i < nregs; i++) {
			Hashtable record = res.getRecordValues(i);
			String bare1 = (String) record.get("BARE1");
			String bare2 = (String) record.get("BARE2");
			String bare34 = (String) record.get("BARE34");
			String bare56 = (String) record.get("BARE56");
			int tmin = ((Number) record.get("TMIN")).intValue();
			int tmax = ((Number) record.get("TMAX")).intValue();
			int imin = ((Number) record.get("IMIN")).intValue();
			String natu = (String) record.get("CG_NATU");
			String type = (String) record.get("TP_INFRAC");

			try {
				InfractionType infractionType = InfractionType.fromString(type);
				ScaleEntry scaleEntry = new ScaleEntry(bare1, bare2, bare34, bare56, tmin, tmax, imin, natu, infractionType);
				List<ScaleEntry> list = scaleMap.get(infractionType);
				if (list == null) {
					list = new ArrayList<ScaleEntry>();
					scaleMap.put(infractionType, list);
				}
				list.add(scaleEntry);
			} catch (Exception ex) {
				InfractionAnalyzerInServer.logger.warn("Error loading baremo of infraction type {}", type);
			}
		}
		return new Scale(scaleMap);
	}

	public void shutdown() {
		this.executor.shutdownNow();
	}

}
