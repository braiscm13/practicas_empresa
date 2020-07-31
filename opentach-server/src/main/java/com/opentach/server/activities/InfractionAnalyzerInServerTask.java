package com.opentach.server.activities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.Analyzer;
import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.imatia.tacho.infraction.Infraction;
import com.imatia.tacho.infraction.InputActivity;
import com.imatia.tacho.infraction.Scale;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class AnalyzeTask.
 */
public class InfractionAnalyzerInServerTask implements Callable<List<Infraction>>, Priorizable {

	/** The priority. */
	private final int						priority;

	/** The contract. */
	private final Object					contract;

	/** The driver. */
	private final Object					driver;

	/** The begin date. */
	private final Date						beginDate;

	/** The end date. */
	private final Date						endDate;

	/** The locator. */
	private final IOpentachServerLocator	locator;

	/** The scale. */
	private final Scale						scale;

	/** The analysis parameters. */
	private final AnalysisParameters		analysisParameters;

	/** The engine. */
	private final AnalyzerEngine			engine;

	/**
	 * Instantiates a new analyze task.
	 *
	 * @param scale
	 *            the scale
	 * @param parameters
	 *            the parameters
	 * @param contract
	 *            the contract
	 * @param driver
	 *            the driver
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param av
	 *            the av
	 * @param sessionId
	 *            the session id
	 * @param locator
	 *            the locator
	 * @param priority
	 *            the priority
	 */
	public InfractionAnalyzerInServerTask(Scale scale, AnalysisParameters parameters, Object contract, Object driver, Date beginDate, Date endDate, IOpentachServerLocator locator,
			int priority, AnalyzerEngine engine) {
		super();
		this.contract = contract;
		this.driver = driver;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.priority = priority;
		this.locator = locator;
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
	public List<Infraction> call() throws Exception {
		return new OntimizeConnectionTemplate<List<Infraction>>() {

			@Override
			protected List<Infraction> doTask(Connection con) throws UException {
				try {
					return InfractionAnalyzerInServerTask.this.analyze(con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.locator.getConnectionManager(), true);
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
	protected List<Infraction> analyze(Connection con) throws Exception {
		String sql = new Template("sql/InfractionAnalyzerQueryActivities.sql").getTemplate();
		return new QueryJdbcTemplate<List<Infraction>>() {

			@Override
			protected List<Infraction> parseResponse(ResultSet rs) throws UException {
				try {
					return new Analyzer().analyze(new ActivityIterator(rs), InfractionAnalyzerInServerTask.this.engine, InfractionAnalyzerInServerTask.this.scale,
							InfractionAnalyzerInServerTask.this.analysisParameters);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, con, sql, this.contract, this.driver, this.beginDate, this.endDate);
	}

	public static class ActivityIterator extends AbstractActivityIterator<InputActivity> {
		public ActivityIterator(ResultSet rs) {
			super(rs);
		}

		@Override
		public InputActivity convert(ResultSet rs) throws SQLException {
			String regimen = rs.getString(6);
			Object origin = rs.getObject(5);
			Object type = rs.getObject(1);
			String transTren = rs.getString(7);
			return new InputActivity(origin, regimen == null ? null : Integer.valueOf(regimen), rs.getTimestamp(2), rs.getTimestamp(4),
					type == null ? null : ((Number) type).intValue(), "S".equals(transTren));
		}
	}

}