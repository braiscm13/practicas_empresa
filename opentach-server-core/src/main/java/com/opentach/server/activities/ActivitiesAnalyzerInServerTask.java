package com.opentach.server.activities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.activity.Activity;
import com.imatia.tacho.activity.ActivityFlatter;
import com.imatia.tacho.infraction.RegimenType;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.ActivitiesAnalyzerInServer.ActivityExtended;
import com.opentach.server.entities.EPreferenciasServidor;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class AnalyzeTask.
 */
public class ActivitiesAnalyzerInServerTask implements Callable<List<Activity>>, Priorizable {

	private static final Logger				logger	= LoggerFactory.getLogger(ActivitiesAnalyzerInServerTask.class);

	/** The priority. */
	private final int						priority;
	private final Object					contract;
	private final Object					comId;
	private final Object					driver;
	private final Date						beginDate;
	private final Date						endDate;
	private final IOpentachServerLocator	locator;
	private final String					cardNumber;
	private final boolean					queryRegions;
	private final TimeZone					timeZone;

	public ActivitiesAnalyzerInServerTask( //
			Object contract, //
			Object comId, //
			String cardNumber, //
			Object driver, //
			Date beginDate, //
			Date endDate, //
			IOpentachServerLocator locator, //
			int priority, //
			boolean queryRegions) { //
		super();
		this.contract = contract;
		this.driver = driver;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.priority = priority;
		this.locator = locator;
		this.cardNumber = cardNumber;
		this.queryRegions = queryRegions;
		this.comId = comId;
		this.timeZone = EPreferenciasServidor.getClientTimeZone(this.locator);
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
	public List<Activity> call() throws Exception {
		return new OntimizeConnectionTemplate<List<Activity>>() {

			@Override
			protected List<Activity> doTask(Connection con) throws UException {
				try {
					return ActivitiesAnalyzerInServerTask.this.analize(con);
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(this.locator.getConnectionManager(), true);
	}

	protected List<Activity> analize(Connection con) throws Exception {
		final String sql = new Template("sql/activities/ActivitiesAnalyzerQueryActivities.sql").fillTemplate("%NUMTRJ%", this.cardNumber == null ? "" : "AND NUM_TARJ=?");
		final Object[] params = this.cardNumber == null ? new Object[] { this.contract, this.driver, this.endDate, this.beginDate } : new Object[] { this.contract, this.driver, this.endDate, this.beginDate, this.cardNumber };
				List<Activity> activities = new QueryJdbcTemplate<List<Activity>>() {
					@Override
					protected List<Activity> parseResponse(ResultSet rs) throws UException {
						try {
							return new ActivityFlatter().convert(new ActivityIterator(rs, ActivitiesAnalyzerInServerTask.this.timeZone));
						} catch (final Exception err) {
							throw new UException(err);
						}
					}
				}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, con, sql, params);

				if (this.queryRegions) {
					activities = this.appendRegions(activities, con);
				}
				return activities;
	}

	private List<Activity> appendRegions(List<Activity> activities, Connection con) throws Exception {
		try {
			final TableEntity ePeriodos = (TableEntity) this.locator.getEntityReferenceFromServer("EPeriodosTrabajo");
			final Hashtable<Object, Object> keysvalues = EntityResultTools.keysvalues("IDCONDUCTOR", this.driver, "CG_CONTRATO", this.contract, "FECINI",
					new SearchValue(SearchValue.BETWEEN, new Vector<Object>(Arrays.asList(new Date[] { this.beginDate, this.endDate }))));
			if (this.cardNumber != null) {
				keysvalues.put("NUM_TARJETA", this.cardNumber);
			}
			final EntityResult periods = ePeriodos.query(keysvalues, EntityResultTools.attributes("TPPERIODO", "FECINI", "DSCRREGION", "DSCRPAIS"),
					TableEntity.getEntityPrivilegedId(ePeriodos), con, EntityResultTools.attributes("FECINI"), true);
			final int periodsCount = periods.calculateRecordNumber();

			final List<Activity> res = new ArrayList<Activity>(activities.size());

			final List<Date> periodBeginDates = (List<Date>) periods.get("FECINI");
			final List<String> periodRegiones = (List<String>) periods.get("DSCRREGION");
			final List<String> periodPaises = (List<String>) periods.get("DSCRPAIS");
			final List<Number> periodTipos = (List<Number>) periods.get("TPPERIODO");
			String pais = null;
			String region = null;
			long periodBeginDate = Long.MIN_VALUE;
			long periodEndDate = Long.MIN_VALUE;
			int index = 0;

			for (final Activity activity : activities) {
				while ((activity.getBeginDate().getTime() >= periodEndDate) && (periodsCount > 0) && (index <= (periodsCount - 1))) {
					int type = periodTipos.get(index).intValue();
					if ((type == 0) || (type == 2) || (type == 4)) {
						// un comienzo
						pais = periodPaises.get(index);
						region = periodRegiones.get(index);
						periodBeginDate = periodBeginDates.get(index).getTime();
						index++;
						while (((type == 0) || (type == 2) || (type == 4)) && (index <= (periodsCount - 1))) {
							type = periodTipos.get(index).intValue();
							index++;
						}
						if (index <= (periodsCount - 1)) {
							periodEndDate = periodBeginDates.get(index).getTime();
						} else {
							periodEndDate = new Date().getTime();
						}
					} else {
						index++;
						pais = null;
						region = null;
					}
				}

				if ((pais != null) && (region != null) && (activity.getBeginDate().getTime() >= periodBeginDate) && (activity.getBeginDate().getTime() < periodEndDate)) {
					res.add(new ActivityExtended(activity, pais, region));
				} else {
					res.add(activity);
				}
			}
			return res;
		} catch (final Exception ex) {
			ActivitiesAnalyzerInServerTask.logger.error(null, ex);
			return activities;
		}
	}

	public static class ActivityIterator extends AbstractActivityIterator<Activity> {
		TimeZone timeZone;

		public ActivityIterator(ResultSet rs, TimeZone timeZone) {
			super(rs);
			this.timeZone = timeZone;
		}

		@Override
		protected Activity convert(ResultSet rs) throws SQLException {

			final String type = rs.getString(1);
			final Timestamp beginDate = rs.getTimestamp(2);
			final Timestamp endDate = rs.getTimestamp(3);
			final String slot = rs.getString(4);
			final String slotStatus = rs.getString(5);
			final String outOfScope = rs.getString(6);
			final String trainTrans = rs.getString(7);
			final String procedencia = rs.getString(8);
			final String origen = rs.getString(9);
			final String regimen = rs.getString(10);
			final String plateNumber = rs.getString(11);
			final String cardNumber = rs.getString(12);

			long beginOffset = 0L;
			long endOffset = 0L;

			if (this.timeZone != null) {
				beginOffset = this.timeZone.getOffset(beginDate.getTime());
				endOffset = this.timeZone.getOffset(endDate.getTime());
			}

			beginDate.setTime(beginDate.getTime() + beginOffset);
			endDate.setTime(endDate.getTime() + endOffset);

			return new Activity( //
					StretchType.fromValue(type == null ? null : Integer.valueOf(type)), //
					endDate, //
					beginDate, //
					RegimenType.fromValue(regimen == null ? null : Integer.valueOf(regimen)), //
					origen, //
					slot, //
					slotStatus, //
					outOfScope, //
					trainTrans, //
					procedencia, //
					plateNumber, //
					cardNumber); //
		}
	}

}
