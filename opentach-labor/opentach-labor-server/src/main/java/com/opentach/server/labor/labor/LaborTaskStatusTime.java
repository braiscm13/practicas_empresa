package com.opentach.server.labor.labor;

import java.sql.Connection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.labor.util.IntervalDate;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.ActivitiesAnalyzerInServerTask;

public class LaborTaskStatusTime extends LaborTask {

	private static final Logger		logger	= LoggerFactory.getLogger(ActivitiesAnalyzerInServerTask.class);

	private final List<StretchType>	stretchTypes;

	public LaborTaskStatusTime(DriverSettings driverSettings, IntervalDate queryInterval, boolean groupDays, List<StretchType> stretchTypes, IOpentachServerLocator locator,
			int priority) {
		super(driverSettings, queryInterval, groupDays, locator, priority);
		this.stretchTypes = stretchTypes;
	}

	/**
	 * Generate auto daily work records.
	 *
	 * @param driverSettings
	 *            the driver settings
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param con
	 *            the con
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	@Override
	protected List<DailyWorkRecord> generateAutoDailyWorkRecords(Connection con) throws Exception {
		// Obtenemos todos los tramos de actividades del conductor
		// en el begin y en el end hay que meter mas días para asegurar y luego limpiar
		Date fromExt = DateTools.addDays(this.getQueryInterval().getFrom(), -5);
		Date toExt = DateTools.addDays(this.getQueryInterval().getTo(), 5);
		// al meter más días nos podemos ir fuera de rango del contrato y peta
		LinkedList<Stretch> stretchs = this.computeStretchs(fromExt, toExt, con);
		LinkedList<WorkingPeriod> workingPeridos = this.computeWorkingPeriods(fromExt, toExt, con);

		// Separamos los stretchs en jornadas de trabajo
		AnalysisParameters parameters = this.getService(LaborService.class).getParameters();
		parameters.setConvertIndeterminateToRest(false);
		List<DailyWorkRecord> dailyWorkRecords = new DaillyJournalGenerator().generate(stretchs, workingPeridos, parameters);
		dailyWorkRecords = this.cleanExtraRecords(dailyWorkRecords);
		// Ahora hay que procesar cada jornada segun el convenio
		ListIterator<DailyWorkRecord> listIterator = dailyWorkRecords.listIterator();
		while (listIterator.hasNext()) {
			DailyWorkRecord record = listIterator.next();
			record.setWorkingMinutes(this.computeDailyWorkingTime(record));
		}
		return dailyWorkRecords;
	}

	private int computeDailyWorkingTime(DailyWorkRecord record) {
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), this.stretchTypes.toArray())) {
				amount += stretch.getDuration();
			}
		}
		return amount;
	}

}