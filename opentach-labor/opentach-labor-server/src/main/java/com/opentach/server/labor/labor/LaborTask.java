package com.opentach.server.labor.labor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.infraction.InputActivity;
import com.imatia.tacho.infraction.Stretch;
import com.imatia.tacho.infraction.StretchGenerator;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.labor.util.IntervalDate;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.AbstractActivityIterator;
import com.opentach.server.activities.ActivitiesAnalyzerInServerTask;
import com.opentach.server.labor.labor.DailyWorkRecord.DailyWorkRecordOrigin;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class LaborTask.
 */
public class LaborTask extends AbstractDelegate implements Callable<LaborResult>, Priorizable {

	/** The Constant logger. */
	private static final Logger				logger	= LoggerFactory.getLogger(ActivitiesAnalyzerInServerTask.class);

	/** The priority. */
	private final DriverSettings			driverSettings;

	/** The locator. */
	private final IOpentachServerLocator	locator;

	/** The priority. */
	private final int						priority;

	/** The query interval. */
	private final IntervalDate				queryInterval;

	/** The group days. */
	private final boolean					groupDays;

	/**
	 * Instantiates a new labor task.
	 *
	 * @param driverSettings
	 *            the driver settings
	 * @param queryInterval
	 *            the query interval
	 * @param groupDays
	 *            the group days
	 * @param locator
	 *            the locator
	 * @param priority
	 *            the priority
	 */
	public LaborTask(DriverSettings driverSettings, IntervalDate queryInterval, boolean groupDays, IOpentachServerLocator locator, int priority) {
		super(locator);
		this.driverSettings = driverSettings;
		this.queryInterval = queryInterval;
		this.priority = priority;
		this.locator = locator;
		this.groupDays = groupDays;
	}

	/**
	 * Gets the group days.
	 *
	 * @return the group days
	 */
	public boolean getGroupDays() {
		return this.groupDays;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.util.concurrent.PriorityExecutor.Priorizable#getPriority()
	 */
	@Override
	public int getPriority() {
		return this.priority;
	}

	/**
	 * Gets the query interval.
	 *
	 * @return the query interval
	 */
	public IntervalDate getQueryInterval() {
		return this.queryInterval;
	}

	/**
	 * Gets the driver settings.
	 *
	 * @return the driver settings
	 */
	public DriverSettings getDriverSettings() {
		return this.driverSettings;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public LaborResult call() throws Exception {
		return new OntimizeConnectionTemplate<LaborResult>() {

			@Override
			protected LaborResult doTask(Connection con) throws UException, SQLException {
				try {
					return LaborTask.this.analize(con);
				} catch (SQLException | UException err) {
					throw err;
				} catch (Exception err) {
					throw new UException(err.getMessage(), err);
				}
			}
		}.execute(this.locator.getConnectionManager(), true);
	}

	/**
	 * Analize.
	 *
	 * @param con
	 *            the con
	 * @return the labor result
	 * @throws Exception
	 *             the exception
	 */
	protected LaborResult analize(Connection con) throws Exception {
		List<DailyWorkRecord> dailyRecords = this.generateDailyWorkRecord(con);
		return new LaborResult(this.driverSettings, dailyRecords);
	}

	/**
	 * Generate daily work record.
	 *
	 * @param con
	 *            the con
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	protected List<DailyWorkRecord> generateDailyWorkRecord(Connection con) throws Exception {
		List<DailyWorkRecord> autoDailyWorkRecords = this.generateAutoDailyWorkRecords(con);
		List<DailyWorkRecord> manualDailyWorkRecords = this.generateManualDailyWorkRecords(con);
		return this.combineAutoManualWorkRecords(autoDailyWorkRecords, manualDailyWorkRecords);
	}

	/**
	 * Combine auto manual work records.
	 *
	 * @param autoDailyWorkRecords
	 *            the auto daily work records
	 * @param manualDailyWorkRecords
	 *            the manual daily work records
	 * @return the list
	 */
	protected List<DailyWorkRecord> combineAutoManualWorkRecords(List<DailyWorkRecord> autoDailyWorkRecords, List<DailyWorkRecord> manualDailyWorkRecords) {
		autoDailyWorkRecords.addAll(manualDailyWorkRecords);
		if (this.getGroupDays()) {
			Collections.sort(autoDailyWorkRecords);
			int ncount = autoDailyWorkRecords.size();
			for (int i = 0; i < (ncount - 1); i++) {
				DailyWorkRecord currentRecord = autoDailyWorkRecords.get(i);
				DailyWorkRecord nextRecord = autoDailyWorkRecords.get(i + 1);
				if (currentRecord.compareTo(nextRecord) == 0) {
					currentRecord.setFrom(currentRecord.getFrom().before(nextRecord.getFrom()) ? currentRecord.getFrom() : nextRecord.getFrom());
					currentRecord.setTo(currentRecord.getTo().after(nextRecord.getTo()) ? currentRecord.getTo() : nextRecord.getTo());
					currentRecord.setWorkingMinutes(currentRecord.getWorkingMinutes() + nextRecord.getWorkingMinutes());
					if ((currentRecord.getStretchs() == null) || (nextRecord.getStretchs() == null)) {
						currentRecord.setStretchs(null);
					} else {
						currentRecord.getStretchs().addAll(nextRecord.getStretchs());
					}
					if (!currentRecord.getOrigin().equals(nextRecord.getOrigin())) {
						currentRecord.setOrigin(DailyWorkRecordOrigin.MANUAL);
					}
					if ((currentRecord.getWorkingPeriods() == null) || (nextRecord.getWorkingPeriods() == null)) {
						currentRecord.setWorkingPeriods(null);
					} else {
						currentRecord.getWorkingPeriods().addAll(nextRecord.getWorkingPeriods());
					}
					autoDailyWorkRecords.remove(i + 1);
					i--;
					ncount--;
				}
			}
		}
		return autoDailyWorkRecords;
	}

	/**
	 * Clean extra records.
	 *
	 * @param autoDailyWorkRecords
	 *            the auto daily work records
	 * @return the list
	 */
	protected List<DailyWorkRecord> cleanExtraRecords(List<DailyWorkRecord> autoDailyWorkRecords) {
		Calendar fromCal = DateTools.createCalendar(this.queryInterval.getFrom());
		Calendar toCal = DateTools.createCalendar(this.queryInterval.getTo());
		String fromStr = String.format("%04d-%02d-%02d", fromCal.get(Calendar.YEAR), fromCal.get(Calendar.MONTH) + 1, fromCal.get(Calendar.DAY_OF_MONTH));
		String toStr = String.format("%04d-%02d-%02d", toCal.get(Calendar.YEAR), toCal.get(Calendar.MONTH) + 1, toCal.get(Calendar.DAY_OF_MONTH));

		ListIterator<DailyWorkRecord> iterator = autoDailyWorkRecords.listIterator();
		while (iterator.hasNext()) {
			DailyWorkRecord record = iterator.next();
			if (record.getDayString().compareTo(fromStr) < 0) {
				iterator.remove();
			} else if (record.getDayString().compareTo(toStr) > 0) {
				iterator.remove();
			}
		}
		return autoDailyWorkRecords;
	}

	/**
	 * Generate manual daily work records.
	 *
	 * @param con
	 *            the con
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	protected List<DailyWorkRecord> generateManualDailyWorkRecords(Connection con) throws Exception {
		// en el begin y en el end hay que meter mas días para asegurar y luego limpiar
		Date fromExt = DateTools.addDays(this.queryInterval.getFrom(), -5);
		Date toExt = DateTools.addDays(this.queryInterval.getTo(), 5);
		List<DailyWorkRecord> res = new ArrayList<>();
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues(//
				"IDCONDUCTOR", this.driverSettings.getDriverId(), //
				"CIF", this.driverSettings.getCompanyCif(), //
				"MAJ_BEGINDATE", new SearchValue(SearchValue.BETWEEN, new Vector<Object>(Arrays.asList(new Object[] { fromExt, toExt }))));
		Vector<String> av = EntityResultTools.attributes("CIF", "MAJ_DAY", "MAJ_BEGINDATE", "MAJ_ENDDATE", "MAJ_WORK_TIME");
		TransactionalEntity eManualJournal = this.getEntity("EManualJournal");
		EntityResult er = eManualJournal.query(kv, av, this.getEntityPrivilegedId(eManualJournal), con);
		int nrecord = er.calculateRecordNumber();
		for (int i = 0; i < nrecord; i++) {
			Hashtable record = er.getRecordValues(i);
			String dayStr = (String) record.get("MAJ_DAY");
			String[] splits = dayStr.split("-");
			int year = Integer.valueOf(splits[0]);
			int month = Integer.valueOf(splits[1]);
			int day = Integer.valueOf(splits[2]);
			DailyWorkRecord dailyRecord = new DailyWorkRecord(year, month, day, DailyWorkRecordOrigin.MANUAL);
			if (record.get("MAJ_WORK_TIME")!=null) {
				dailyRecord.setWorkingMinutes(((Number) record.get("MAJ_WORK_TIME")).intValue());
			}
			dailyRecord.setFrom((Date) record.get("MAJ_BEGINDATE"));
			dailyRecord.setTo((Date) record.get("MAJ_ENDDATE"));
			res.add(dailyRecord);
		}
		return this.cleanExtraRecords(res);
	}

	/**
	 * Generate auto daily work records.
	 *
	 * @param con
	 *            the con
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	protected List<DailyWorkRecord> generateAutoDailyWorkRecords(Connection con) throws Exception {
		// Obtenemos todos los tramos de actividades del conductor
		// en el begin y en el end hay que meter mas días para asegurar y luego limpiar
		Date fromExt = DateTools.addDays(this.queryInterval.getFrom(), -5);
		Date toExt = DateTools.addDays(this.queryInterval.getTo(), 5);
		// al meter mas dias nos podemos ir fuera de rango del contrato y peta
		LinkedList<Stretch> stretchs = this.computeStretchs(fromExt, toExt, con);
		LinkedList<WorkingPeriod> workingPeridos = this.computeWorkingPeriods(fromExt, toExt, con);

		// Separamos los stretchs en jornadas de trabajo
		List<DailyWorkRecord> dailyWorkRecords = new DaillyJournalGenerator().generate(stretchs, workingPeridos, this.getService(LaborService.class).getParameters());
		dailyWorkRecords = this.cleanExtraRecords(dailyWorkRecords);
		// Ahora hay que procesar cada jornada segun el convenio
		ListIterator<DailyWorkRecord> listIterator = dailyWorkRecords.listIterator();
		while (listIterator.hasNext()) {
			DailyWorkRecord record = listIterator.next();
			DriverContract laborContract = this.driverSettings.getLaborAgreeementForDay(record.getFrom());
			if (laborContract == null) {
				// throw new OntimizeJEEException("E_NO_CONTRACT_FOUND", record.getFrom());
				listIterator.remove();
			} else {
				int workingTime = laborContract.getLaborAgreement().computeDailyWorkingTime(record);
				record.setWorkingMinutes(workingTime);
			}
		}
		return dailyWorkRecords;
	}

	/**
	 * Compute stretchs.
	 *
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param con
	 *            the con
	 * @return the linked list
	 * @throws Exception
	 *             the exception
	 */
	protected LinkedList<Stretch> computeStretchs(Date beginDate, Date endDate, Connection con) throws Exception {
		String sql = new Template("sql/LaboralReportQueryActivities.sql").getTemplate();
		return new QueryJdbcTemplate<LinkedList<Stretch>>() {

			@Override
			protected LinkedList<Stretch> parseResponse(ResultSet rs) throws UException, SQLException {
				try {
					return new StretchGenerator().convert(new ActivityIterator(rs), LaborTask.this.getService(LaborService.class).getParameters());
				} catch (SQLException | UException err) {
					throw err;
				} catch (Exception err) {
					throw new UException(err.getMessage(), err);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, con, sql, this.driverSettings.getOpentachContract(), this.driverSettings.getDriverId(), beginDate,
				endDate);
	}

	/**
	 * Compute working periods.
	 *
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param con
	 *            the con
	 * @return the linked list
	 * @throws Exception
	 *             the exception
	 */
	protected LinkedList<WorkingPeriod> computeWorkingPeriods(Date beginDate, Date endDate, Connection con) throws Exception {
		String sql = new Template("sql/LaboralReportQueryWorkingPeriods.sql").getTemplate();
		return new QueryJdbcTemplate<LinkedList<WorkingPeriod>>() {
			@Override
			protected LinkedList<WorkingPeriod> parseResponse(ResultSet rs) throws UException, SQLException {
				LinkedList<WorkingPeriod> res = new LinkedList<>();
				while (rs.next()) {
					int tpPeriodo = rs.getInt(1);
					Date when = rs.getTimestamp(2);
					String numTrj = rs.getString(3);
					res.add(new WorkingPeriod(tpPeriodo, when, numTrj));
				}
				return res;
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, con, sql, this.driverSettings.getOpentachContract(), this.driverSettings.getDriverId(), beginDate,
				endDate);
	}

	/**
	 * The Class ActivityIterator.
	 */
	public static class ActivityIterator extends AbstractActivityIterator<InputActivity> {

		/**
		 * Instantiates a new activity iterator.
		 *
		 * @param rs
		 *            the rs
		 */
		public ActivityIterator(ResultSet rs) {
			super(rs);
		}

		/*
		 * (non-Javadoc)
		 * @see com.opentach.server.activities.AbstractActivityIterator#convert(java.sql.ResultSet)
		 */
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