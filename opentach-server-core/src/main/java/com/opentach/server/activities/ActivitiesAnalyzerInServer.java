package com.opentach.server.activities;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.activity.Activity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.i18n.TranslationService;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class ServerInfractionAnalyzer.
 */
public class ActivitiesAnalyzerInServer extends AbstractDelegate {

	/** The Constant logger. */
	private static final Logger					logger			= LoggerFactory.getLogger(ActivitiesAnalyzerInServer.class);

	/** The Constant PRIORITY_LOW. */
	private static final int					PRIORITY_LOW	= 0;

	/** The Constant PRIORITY_HIGH. */
	private static final int					PRIORITY_HIGH	= 1;

	/** The instance. */
	private static ActivitiesAnalyzerInServer	instance;

	/**
	 * Gets the single instance of ServerInfractionAnalyzer.
	 *
	 * @param locator
	 *            the locator
	 * @return single instance of ServerInfractionAnalyzer
	 */
	public static ActivitiesAnalyzerInServer getInstance(IOpentachServerLocator locator) {
		if (ActivitiesAnalyzerInServer.instance == null) {
			ActivitiesAnalyzerInServer.instance = new ActivitiesAnalyzerInServer(locator);
		}
		return ActivitiesAnalyzerInServer.instance;
	}

	/** The executor. */
	private final PriorityThreadPoolExecutor executor;

	/**
	 * Instantiates a new server infraction analyzer.
	 *
	 * @param locator
	 *            the locator
	 */
	protected ActivitiesAnalyzerInServer(IOpentachServerLocator locator) {
		super(locator);
		this.executor = new PriorityThreadPoolExecutor("ActivityAnalyzer", 40, 10, TimeUnit.SECONDS) {
			@Override
			protected void beforeExecute(Thread t, Runnable command) {
				// ActivitiesAnalyzerInServerTask task = this.extractActivitiesAnalyzerInServerTask(command);
				super.beforeExecute(t, command);
			}

			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
			}

			private ActivitiesAnalyzerInServerTask extractActivitiesAnalyzerInServerTask(Runnable command) {
				ActivitiesAnalyzerInServerTask task = null;
				if (command instanceof ActivitiesAnalyzerInServerTask) {
					task = (ActivitiesAnalyzerInServerTask) command;
				} else {
					task = (ActivitiesAnalyzerInServerTask) ((PriorityTask<?>) command).getWrappedObject();
				}
				return task;
			}
		};
	}

	/**
	 * Analyze.
	 *
	 * @param contract
	 *            the contract
	 * @param cardNumber
	 * @param driverList
	 *            the driver list
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param queryPeriods
	 * @param av
	 *            the av
	 * @param sesionId
	 *            the session id
	 * @param timeZone
	 *            the time zone
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	public EntityResult analyze(final Object contract, Object comId, final String cardNumber, final List<Object> driverList, final Date beginDate, final Date endDate,
			boolean queryPeriods,
			final Vector<String> av, final int sesionId) throws Exception {
		final Chronometer chrono = new Chronometer().start();
		final List<Submit> submitList = new ArrayList<>();

		final List<Object> cleanDriverList = this.filterDrivers(driverList);
		for (final Object driver : cleanDriverList) {
			final Future<List<Activity>> submit = this.executor.submit(new ActivitiesAnalyzerInServerTask(contract, comId, cardNumber, driver, beginDate, endDate,
					this.getLocator(),
					ActivitiesAnalyzerInServer.PRIORITY_LOW, av.contains("REGION") || av.contains("PAIS")));
			submitList.add(new Submit(submit, contract, driver));
		}
		ActivitiesAnalyzerInServer.logger.debug("Prepare time {}", chrono.elapsedMs());
		EntityResult resPeriods = null;
		if (queryPeriods) {
			resPeriods = new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws UException {
					try {
						return ActivitiesAnalyzerInServer.this.queryPeriods(contract, cardNumber, cleanDriverList, beginDate, endDate, sesionId, con);
					} catch (final Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), true);
		}
		ActivitiesAnalyzerInServer.logger.debug("QueryPeriods time {}", chrono.elapsedMs());

		EntityResult res = new EntityResult();
		for (final Submit submit : submitList) {
			final List<Activity> activityList = submit.getFuture().get();
			ActivitiesAnalyzerInServer.logger.debug("Get tasks time {}", chrono.elapsedMs());
			final EntityResult partialRes = this.toEntityResult(submit.getContract(), submit.getDriver(), activityList, av, sesionId);
			ActivitiesAnalyzerInServer.logger.debug("To entityResult time {}", chrono.elapsedMs());
			activityList.clear();// empty memory
			res = EntityResultTools.doUnionAll(res, partialRes);
			ActivitiesAnalyzerInServer.logger.debug("Union time {}", chrono.elapsedMs());
		}

		if (queryPeriods) {
			final EntityResult resmayor = new EntityResult();
			final Hashtable<String, Object> avmayor = new Hashtable<String, Object>();
			avmayor.put("ACTIVIDADES", res);
			avmayor.put("PERIODOS", resPeriods);
			resmayor.addRecord(avmayor);
			return resmayor;
		}
		ActivitiesAnalyzerInServer.logger.debug("Total time {}", chrono.stopMs());

		return res;
	}

	public List<Activity> queryActivityList(final Object contract, final Object comId, final String cardNumber, final List<Object> driverList, final Date beginDate,
			final Date endDate,
			final int sesionId) throws Exception {
		final List<Activity> response = new ArrayList<>();

		final Chronometer chrono = new Chronometer().start();
		final List<Submit> submitList = new ArrayList<>();

		final List<Object> cleanDriverList = this.filterDrivers(driverList);
		for (final Object driver : cleanDriverList) {
			final Future<List<Activity>> submit = this.executor.submit(
					new ActivitiesAnalyzerInServerTask(contract, comId, cardNumber, driver, beginDate, endDate, this.getLocator(), ActivitiesAnalyzerInServer.PRIORITY_LOW, false));
			submitList.add(new Submit(submit, contract, driver));
		}
		ActivitiesAnalyzerInServer.logger.debug("Prepare time {}", chrono.elapsedMs());
		ActivitiesAnalyzerInServer.logger.debug("QueryPeriods time {}", chrono.elapsedMs());

		for (final Submit submit : submitList) {
			response.addAll(submit.getFuture().get());
		}

		ActivitiesAnalyzerInServer.logger.debug("Total time {}", chrono.stopMs());
		return response;
	}

	private List<Object> filterDrivers(List<Object> driverList) {
		final HashSet<Object> set = new HashSet<Object>();
		set.addAll(driverList);
		return new ArrayList<Object>(set);
	}

	private EntityResult queryPeriods(Object numReq, String cardNumber, List<Object> vDrivers, Date fIni, Date fFin, int sesionId, Connection con) throws Exception {
		final TransactionalEntity ent2 = this.getEntity("EPeriodosTrabajo");
		final Hashtable<String, Object> cvPeriodos = new Hashtable<String, Object>();
		cvPeriodos.put(OpentachFieldNames.NUMREQ_FIELD, numReq);
		cvPeriodos.put(OpentachFieldNames.IDCONDUCTOR_FIELD, new SearchValue(SearchValue.IN, new Vector<Object>(vDrivers)));
		final BasicExpression be1 = new BasicExpression(new SQLStatementBuilder.BasicField(OpentachFieldNames.FECINI_FIELD), BasicOperator.MORE_EQUAL_OP, fIni);
		final BasicExpression be2 = new BasicExpression(new SQLStatementBuilder.BasicField(OpentachFieldNames.FECINI_FIELD), BasicOperator.LESS_EQUAL_OP, fFin);
		final BasicExpression be = new BasicExpression(be1, BasicOperator.AND_OP, be2);
		cvPeriodos.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be);
		if (cardNumber != null) {
			cvPeriodos.put("NUM_TARJETA", cardNumber);
		}

		return ent2.query(cvPeriodos, new Vector(0), sesionId, con);
	}

	/**
	 * To entity result.
	 *
	 * @param contract
	 *            the contract
	 * @param driver
	 *            the driver
	 * @param activityList
	 *            the infraction list
	 * @param av
	 *            the av
	 * @return the entity result
	 * @throws Exception
	 */
	private EntityResult toEntityResult(Object contract, Object driver, List<Activity> activityList, Vector<String> av, int sessionID)
			throws Exception {

		// TODO: Activity.getDuration() is invoked several times below.
		// Activity just subtracts "end" - "start" to get duration; since "start" and "end" can now sometimes be in local time (not in GMT) this
		// returns a wrong value whenever "start" and "end" comprise a change in daylight savings.
		// Activity class should be adapted to know when its contained dates are not expressed in GMT and adapt the method getDuration() accordingly.

		final Chronometer chrono = new Chronometer().start();

		final EntityResult er = new EntityResult();
		EntityResultTools.initEntityResult(er, av);
		Map<String, Object> driverInfo = new Hashtable<>();
		Map<String, Object> companyInfo = new Hashtable<>();
		Object companyCif = null;
		Object companyName = null;
		if (this.isQueryForAny(av, "FECINID", "FECFIND", "IDINSPECCION", "APELLIDOS", "NOMBRE", "DNI", "CIF", "NOMB")) {
			try {
				driverInfo = this.querySingleRow("EConductorCont", new Object[] { "IDCONDUCTOR", driver, "CG_CONTRATO", contract },
						new String[] { "EXTERNAL_EMPLOYEE_ID", "DNI", "APELLIDOS", "NOMBRE", "CIF" }, sessionID, "DRIVER_NOT_FOUND");
			} catch (final Exception ex) {
				driverInfo = new Hashtable<String, Object>();
			}
			companyCif = driverInfo.get("CIF");
			ActivitiesAnalyzerInServer.logger.debug("Driver info time {}", chrono.elapsedMs());
			if (companyCif != null) {
				companyInfo = this.querySingleRow("ECifEmpreReq", new Object[] { "NUMREQ", contract, "CIF", companyCif }, new String[] { "FECINID", "FECFIND", "IDINSPECCION" },
						sessionID, "COMPANY_NOT_FOUND");
				ActivitiesAnalyzerInServer.logger.debug("Company info time {}", chrono.elapsedMs());

				final Map<String, Object> extraCompanyInfo = this.querySingleRow(CompanyNaming.ENTITY, new Object[] { "CIF", companyCif }, new String[] { "NOMB" }, sessionID,
						"COMPANY_NOT_FOUND");
				companyName = extraCompanyInfo.get("NOMB");
				ActivitiesAnalyzerInServer.logger.debug("Extra company info time {}", chrono.elapsedMs());
			}
		}

		final boolean queryForTime = this.isQueryForAny(av, "HORAS_COND", "SEGUNDOS", "ANHO", "MES2", "DIA", "HORA", "DIA_SEM", "SEMANA", "MES");
		final boolean queryForWeek = this.isQueryForAny(av, "INI_SEMANA", "FIN_SEMANA");
		final boolean queryForRegion = this.isQueryForAny(av, "REGION", "PAIS");

		final int nregs = activityList.size();
		for (final String col : av) {
			final Vector value = new Vector(nregs);
			value.setSize(nregs);
			er.put(col, value);
		}

		int i = 0;
		final TranslationService translationService = this.getService(TranslationService.class);
		for (final Activity activity : activityList) {
			this.putRecordInfo(av, i, er, "NUMREQ", contract);
			this.putRecordInfo(av, i, er, "CG_CONTRATO", contract);
			this.putRecordInfo(av, i, er, "INSP_FECINI", companyInfo.get("FECINID"));
			this.putRecordInfo(av, i, er, "INSP_FECFIN", companyInfo.get("FECFIND"));
			this.putRecordInfo(av, i, er, "CIF", companyCif);
			this.putRecordInfo(av, i, er, "IDINSPECCION", companyInfo.get("IDINSPECCION"));
			this.putRecordInfo(av, i, er, "NOMB", companyName);

			this.putRecordInfo(av, i, er, "IDCONDUCTOR", driver);
			this.putRecordInfo(av, i, er, "EXTERNAL_EMPLOYEE_ID", driverInfo.get("EXTERNAL_EMPLOYEE_ID"));
			this.putRecordInfo(av, i, er, "DNI", driverInfo.get("DNI"));
			this.putRecordInfo(av, i, er, "APELLIDOS", driverInfo.get("APELLIDOS"));
			this.putRecordInfo(av, i, er, "NOMBRE", driverInfo.get("NOMBRE"));

			this.putRecordInfo(av, i, er, "TPACTIVIDAD", activity.getType().getIntValue());
			this.putRecordInfo(av, i, er, "DSCR_ACT", translationService.getActivityDesctiption(activity.getType().getIntValue()));
			this.putRecordInfo(av, i, er, "FEC_COMIENZO", activity.getBeginDate());
			this.putRecordInfo(av, i, er, "FECINI", activity.getBeginDate());
			this.putRecordInfo(av, i, er, "MINUTOS", activity.getDuration());
			this.putRecordInfo(av, i, er, "FEC_FIN", activity.getEndDate());
			this.putRecordInfo(av, i, er, "FECFIN", activity.getEndDate());
			this.putRecordInfo(av, i, er, "RANURA", activity.getSlot());
			this.putRecordInfo(av, i, er, "ESTADO_TRJ_RANURA", activity.getSlotCardStatus());
			this.putRecordInfo(av, i, er, "FUERA_AMBITO", activity.getOutOfScope());
			this.putRecordInfo(av, i, er, "TRANS_TREN", activity.getTrainTrans());
			this.putRecordInfo(av, i, er, "PROCEDENCIA", activity.getProcedencia());
			this.putRecordInfo(av, i, er, "ORIGEN", activity.getOrigin());
			this.putRecordInfo(av, i, er, "REGIMEN", activity.getRegimen() == null ? null : String.valueOf(activity.getRegimen().getIntValue()));
			this.putRecordInfo(av, i, er, "MATRICULA", activity.getPlateNumber());
			this.putRecordInfo(av, i, er, "NUM_TARJ", activity.getCardNumber());
			this.putRecordInfo(av, i, er, "ORIGEN_DSCR", translationService.getOriginDescription(activity.getOrigin()));
			this.putRecordInfo(av, i, er, "REGIMEN_DSCR", translationService.getRegimenDesctiption(activity.getRegimen()));
			this.putRecordInfo(av, i, er, "RANURA_DSCR", translationService.getSlotDesctiption(activity.getSlot()));
			this.putRecordInfo(av, i, er, "ESTADO_TRJ_RANURA_DSCR", translationService.getSlotCardStatusDescription(activity.getSlotCardStatus()));

			if (queryForRegion) {
				this.putRecordInfo(av, i, er, "REGION", activity instanceof ActivityExtended ? ((ActivityExtended) activity).getRegion() : null);
				this.putRecordInfo(av, i, er, "PAIS", activity instanceof ActivityExtended ? ((ActivityExtended) activity).getPais() : null);
			}

			if (queryForTime) {
				final float duration = activity.getDuration();
				final float horas_cond = duration / 60;
				// Number d3 = activity.getDuration() % 60;
				this.putRecordInfo(av, i, er, "HORAS_COND", horas_cond);
				this.putRecordInfo(av, i, er, "SEGUNDOS", activity.getDuration() * 60);
				this.putRecordInfo(av, i, er, "ANHO", new SimpleDateFormat("yyyy").format(activity.getBeginDate()));
				this.putRecordInfo(av, i, er, "MES2", new SimpleDateFormat("MMMM").format(activity.getBeginDate()));
				this.putRecordInfo(av, i, er, "DIA", new SimpleDateFormat("dd").format(activity.getBeginDate()));
				this.putRecordInfo(av, i, er, "HORA", new SimpleDateFormat("HH").format(activity.getBeginDate()));
				this.putRecordInfo(av, i, er, "DIA_SEM", new SimpleDateFormat("EEEE").format(activity.getBeginDate()));
				this.putRecordInfo(av, i, er, "SEMANA", new SimpleDateFormat("ww").format(activity.getBeginDate()));
				this.putRecordInfo(av, i, er, "MES", StringTools.concat("MES", new SimpleDateFormat("MM.MMMM").format(activity.getBeginDate())));
			}

			if (queryForWeek) {
				final Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				this.putRecordInfo(av, i, er, "INI_SEMANA", new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				this.putRecordInfo(av, i, er, "FIN_SEMANA", new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
			}

			i++;
		}
		ActivitiesAnalyzerInServer.logger.debug("Iteration time {}", chrono.elapsedMs());
		return er;
	}

	private boolean isQueryForAny(Vector<String> av, String... asks) {
		for (final String check : asks) {
			if (av.contains(check)) {
				return true;
			}
		}
		return false;
	}

	private Map<String, Object> querySingleRow(String entityName, Object[] kv, String[] av, int sessionID, String errorMsg) throws Exception {
		final EntityResult entity = ((TableEntity) this.getEntity(entityName)).query(EntityResultTools.keysvalues(kv), EntityResultTools.attributes(av), sessionID);
		CheckingTools.checkValidEntityResult(entity, errorMsg, false, true, (Object[]) null);
		final Map<String, Object> singleRecord = entity.getRecordValues(0);
		return singleRecord;
	}

	private void putRecordInfo(Vector<String> av, int index, EntityResult er, String column, Object value) {
		if (av.contains(column) && (value != null)) {
			((Vector) er.get(column)).set(index, value);
		}
	}

	/**
	 * The Class Submit.
	 */
	private class Submit {

		/** The future. */
		private final Future<List<Activity>>	future;

		/** The contract. */
		private final Object					contract;

		/** The driver. */
		private final Object					driver;

		/**
		 * Instantiates a new submit.
		 *
		 * @param future
		 *            the future
		 * @param contract
		 *            the contract
		 * @param driver
		 *            the driver
		 */
		public Submit(Future<List<Activity>> future, Object contract, Object driver) {
			super();
			this.future = future;
			this.contract = contract;
			this.driver = driver;
		}

		/**
		 * Gets the future.
		 *
		 * @return the future
		 */
		public Future<List<Activity>> getFuture() {
			return this.future;
		}

		/**
		 * Gets the contract.
		 *
		 * @return the contract
		 */
		public Object getContract() {
			return this.contract;
		}

		/**
		 * Gets the driver.
		 *
		 * @return the driver
		 */
		public Object getDriver() {
			return this.driver;
		}

	}

	static class ActivityExtended extends Activity {

		private final String	pais;
		private final String	region;

		public ActivityExtended(Activity orig, String pais, String region) {
			super(orig.getType(), orig.getEndDate(), orig.getBeginDate(), orig.getRegimen(), orig.getOrigin(), orig.getSlot(), orig.getSlotCardStatus(), orig.getOutOfScope(),
					orig.getTrainTrans(), orig.getProcedencia(), orig.getPlateNumber(), orig.getCardNumber());
			this.pais = pais;
			this.region = region;
		}

		public String getPais() {
			return this.pais;
		}

		public String getRegion() {
			return this.region;
		}

	}
}
