package com.opentach.server.activities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.activity.Activity;
import com.imatia.tacho.activity.ActivityFlatter;
import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.AnalysisResult;
import com.imatia.tacho.infraction.Analyzer;
import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.imatia.tacho.infraction.InputActivity;
import com.imatia.tacho.infraction.Scale;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.InfractionAnalyzerInServerTask.ActivityIterator;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class AnalyzeTask.
 */
public class InfractionAnalyzerInServerFullInfoTask extends AbstractDelegate implements Callable<Pair<AnalysisResult, List<Activity>>>, Priorizable {

	private static final Logger			logger			= LoggerFactory.getLogger(InfractionAnalyzerInServerFullInfoTask.class);
	private static final AtomicLong		ID_GENERATOR	= new AtomicLong();

	/** The priority. */
	private final int					priority;

	/** The contract. */
	private final Object				contract;

	/** The driver. */
	private final Object				driver;

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
	public InfractionAnalyzerInServerFullInfoTask(Connection conn, Scale scale, AnalysisParameters parameters, Object contract, Object driver, Date beginDate, Date endDate,
			int priority, AnalyzerEngine engine, IOpentachServerLocator locator) {
		super(locator);
		this.con = conn;
		this.contract = contract;
		this.driver = driver;
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
	public Pair<AnalysisResult, List<Activity>> call() throws Exception {
		return this.analyze();
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
	private Pair<AnalysisResult, List<Activity>> analyze() throws Exception {
		String sql = new Template("sql/InfractionAnalyzerQueryActivities.sql").getTemplate();
		AnalysisResult analysisResult = new QueryJdbcTemplate<AnalysisResult>() {

			@Override
			protected AnalysisResult parseResponse(ResultSet rs) throws UException {
				try {
					return InfractionAnalyzerInServerFullInfoTask.this.analyze(new ActivityIterator(rs));
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, this.con, sql, this.contract, this.driver, this.beginDate, this.endDate);

		sql = new Template("sql/activities/ActivitiesAnalyzerQueryActivities.sql").fillTemplate("%NUMTRJ%", "");
		Object[] params = new Object[] { this.contract, this.driver, this.endDate, this.beginDate } ;
		List<Activity> activities = new QueryJdbcTemplate<List<Activity>>() {
			@Override
			protected List<Activity> parseResponse(ResultSet rs) throws UException {
				try {
					return new ActivityFlatter().convert(new ActivitiesAnalyzerInServerTask.ActivityIterator(rs, null));
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, this.con, sql, params);
		return new Pair<AnalysisResult, List<Activity>>(analysisResult, activities);
	}

	private AnalysisResult analyze(Iterator<InputActivity> activityIterator) throws Exception {
		Chronometer chrono = new Chronometer().start();
		AnalysisResult analysisResult = new Analyzer().analyzeExtraInfo(activityIterator, this.engine, this.scale, this.analysisParameters);
		return analysisResult;
	}
}