package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.imatia.tacho.infraction.AnalyzerUtils;
import com.ontimize.jee.common.tools.DateTools;
import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.labor.WorkingPeriod;

//@formatter:off
/**
 * Este algoritmo considera horas de trabajo las de conduccion
 * @author joaquin.romero
 *
 */
//@formatter:on
public class InsertToExtractionLaborAgreementAlgorithm implements ILaborAgreementAlgorithm {

	public InsertToExtractionLaborAgreementAlgorithm() {
		super();
	}

	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		int amount = 0;
		List<WorkingPeriod> periods = record.getWorkingPeriods();
		ListIterator<WorkingPeriod> listIterator = periods.listIterator();
		WorkingPeriod insertPeriod = null;
		while (listIterator.hasNext()) {
			WorkingPeriod period = listIterator.next();
			if (period.getTpPeriodo() == 0) {// insercion
				insertPeriod = period;
			} else if (period.getTpPeriodo() == 1) { // extraccion
				Date from = (insertPeriod == null) ? DateTools.truncate(record.getFrom()) : insertPeriod.getWhen();
				Date to = period.getWhen();
				amount += AnalyzerUtils.minutesBetween(to, from);
				insertPeriod = null;
			}
		}
		if (insertPeriod != null) {
			Date from = insertPeriod.getWhen();
			Date to = DateTools.addDays(DateTools.truncate(record.getFrom()), 1);
			amount += AnalyzerUtils.minutesBetween(to, from);
		}
		return amount;
	}
}