package com.opentach.server.activities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.AnalysisResult;
import com.imatia.tacho.infraction.Analyzer;
import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.imatia.tacho.infraction.InputActivity;
import com.imatia.tacho.infraction.Period;
import com.imatia.tacho.infraction.Period.PeriodClass;
import com.imatia.tacho.infraction.Rest;
import com.imatia.tacho.infraction.RestClass;
import com.imatia.tacho.infraction.Scale;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.InfractionAnalyzerInServerTask.ActivityIterator;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.BatchUpdateJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class AnalyzeTask.
 */
public class InfractionAnalyzerInServerForWSTask extends AbstractDelegate implements Callable<Void>, Priorizable {

	private static final Logger			logger			= LoggerFactory.getLogger(InfractionAnalyzerInServerForWSTask.class);
	private static final AtomicLong		ID_GENERATOR	= new AtomicLong();

	/** The priority. */
	private final int					priority;

	/** The contract. */
	private final Object				contract;

	/** The driver. */
	private final Object				driver;
	private final Object				cif;

	/** The begin date. */
	private final Date					beginDate;

	/** The end date. */
	private final Date					endDate;

	private final Connection			con;

	/** The scale. */
	private final Scale					scale;

	/** The analysis parameters. */
	private final AnalysisParameters	analysisParameters;

	/** The engine. */
	private final AnalyzerEngine		engine;

	/**
	 * Instantiates a new analyze task.
	 *
	 * @param conn
	 *            the scale
	 * @param scale2
	 *            the parameters
	 * @param parameters
	 *            the contract
	 * @param driver
	 *            the driver
	 * @param driver2
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param endDate2
	 *            the av
	 * @param sessionId
	 *            the session id
	 * @param locator
	 *            the locator
	 * @param priority
	 *            the priority
	 */
	public InfractionAnalyzerInServerForWSTask(Connection conn, Scale scale, AnalysisParameters parameters, Object contract, Object cif, Object driver, Date beginDate,
			Date endDate, int priority, AnalyzerEngine engine, IOpentachServerLocator locator) {
		super(locator);
		this.con = conn;
		this.contract = contract;
		this.driver = driver;
		this.cif = cif;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.priority = priority;
		this.scale = scale;
		this.analysisParameters = parameters;
		this.engine = engine;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.util.concurrent.PriorityExecutor.Priorizable#getPriority()
	 */
	@Override
	public int getPriority() {
		return this.priority;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
		this.analyze();
		return null;
	}

	/**
	 * Analize.
	 *
	 * @param con
	 *            the con
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	private void analyze() throws Exception {
		String sql = new Template("sql/InfractionAnalyzerWSQueryActivities.sql").getTemplate();
		new QueryJdbcTemplate<Void>() {

			@Override
			protected Void parseResponse(ResultSet rs) throws UException {
				try {
					InfractionAnalyzerInServerForWSTask.this.analyze(new ActivityIterator(rs));
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, this.con, sql,
				this.contract, this.driver, this.beginDate, this.beginDate,
				this.beginDate,	this.beginDate,
				this.contract, this.driver, this.beginDate, this.beginDate,
				this.beginDate, this.beginDate);
	}

	private void analyze(Iterator<InputActivity> activityIterator) throws Exception {
		Chronometer chrono = new Chronometer().start();
		AnalysisResult analysisResult = new Analyzer().analyzeExtraInfo(activityIterator, this.engine, this.scale, this.analysisParameters);
		long generatorTime = chrono.elapsedMs();

		this.saveStretchs(analysisResult.getStretchs());
		this.savePeriods(analysisResult.getPeriods());
		this.saveRests(analysisResult.getRests());
		new InfractionDatabaseSaver().saveInfractions(analysisResult.getInfractions(), this.contract, this.cif, this.driver, "CDINFRACCIONES_TEMP", this.con);

		long saveToDb = chrono.elapsedMs();

		InfractionAnalyzerInServerForWSTask.logger.warn("Tiempo analisis {}, saveToDb {}, total {}", generatorTime, saveToDb, chrono.stopMs());
	}

	private void saveRests(Map<RestClass, List<Rest>> rests) throws Exception {
		List<Rest> restList = new ArrayList<Rest>();
		for (Entry<RestClass, List<Rest>> entry : rests.entrySet()) {
			restList.addAll(entry.getValue());
		}

		new BatchUpdateJdbcTemplate<Rest>(restList) {

			@Override
			protected Object[] beanToParametersArray(int idx, Rest rest) {
				Object[] array = new Object[7];
				array[0] = InfractionAnalyzerInServerForWSTask.this.driver;
				array[1] = rest.getBeginDate();
				array[2] = rest.getEndDate();
				array[3] = rest.getTime();
				array[4] = String.valueOf(rest.getType());
				array[5] = String.valueOf(rest.getPeriodType().getIntValue());
				array[6] = InfractionAnalyzerInServerForWSTask.ID_GENERATOR.getAndIncrement();

				return array;
			}
		}.execute(this.con, "insert into cddescansos_temp (IDCONDUCTOR,FECINI,FECFIN,MINUTOS,TIPO,TIPODD,IDINTERVALO) values (?,?,?,?,?,?,?)");

	}

	private void savePeriods(final Map<PeriodClass, List<Period>> periods) throws Exception {
		List<Period> periodList = new ArrayList<Period>();
		for (Entry<PeriodClass, List<Period>> entry : periods.entrySet()) {
			periodList.addAll(entry.getValue());
		}

		new BatchUpdateJdbcTemplate<Period>(periodList) {

			@Override
			protected Object[] beanToParametersArray(int idx, Period period) {
				Object[] array = new Object[10];
				array[0] = InfractionAnalyzerInServerForWSTask.ID_GENERATOR.getAndIncrement();
				array[1] = InfractionAnalyzerInServerForWSTask.this.driver;
				array[2] = period.getPeriodClass().toString();
				array[3] = period.getBeginPeriodDate();
				array[4] = period.getEndPeriodDate();
				array[5] = period.getPeriodClass().toString().startsWith("PC") ? period.getTime() : 0;
				array[6] = period.getPeriodClass().toString().startsWith("PC") ? 0 : period.getTime();
				array[7] = String.valueOf(period.getPeriodType().getIntValue());
				array[8] = period.getBeginRestDate();
				array[9] = period.getEndRestDate();

				return array;
			}
		}.execute(this.con, "insert into cdperiodos1_temp (IDPERIODO,IDCONDUCTOR,TIPO,FECINI,FECFIN,TCP,TDP,TIPODD,FECINI_DMAX,FECFIN_DMAX) values (?,?,?,?,?,?,?,?,?,?)");
	}

	private void saveStretchs(final LinkedList<Stretch> stretchs) throws Exception {
		new BatchUpdateJdbcTemplate<Stretch>(stretchs) {
			@Override
			protected Object[] beanToParametersArray(int idx, Stretch stretch) {
				Object[] array = new Object[8];
				array[0] = InfractionAnalyzerInServerForWSTask.ID_GENERATOR.getAndIncrement();
				array[1] = String.valueOf(stretch.getType().getIntValue());
				array[2] = stretch.getBeginDate();
				array[3] = stretch.getEndDate();
				array[4] = stretch.getDuration();
				array[5] = InfractionAnalyzerInServerForWSTask.this.driver;
				array[6] = stretch.getOrigin();
				array[7] = String.valueOf(stretch.getRegimen().getIntValue());
				return array;
			}
		}.execute(this.con, "insert into cdtramos_def_temp (IDTRAMO,TIPO,FECINI,FECFIN,MINUTOS_A,IDCONDUCTOR,PROCEDENCIA,REGIMEN) values (?,?,?,?,?,?,?,?)");
	}
}