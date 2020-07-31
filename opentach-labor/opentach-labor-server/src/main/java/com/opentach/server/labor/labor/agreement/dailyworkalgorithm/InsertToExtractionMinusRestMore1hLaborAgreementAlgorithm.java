package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.AnalyzerUtils;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.labor.WorkingPeriod;

//@formatter:off
/**
 * Este algoritmo considera horas de trabajo las de conduccion
 * @author joaquin.romero
 *
 */
//@formatter:on
public class InsertToExtractionMinusRestMore1hLaborAgreementAlgorithm implements ILaborAgreementAlgorithm {

	public InsertToExtractionMinusRestMore1hLaborAgreementAlgorithm() {
		super();
	}

	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		int amount = 0;
		List<WorkingPeriod> periods = record.getWorkingPeriods();
		ListIterator<WorkingPeriod> listIterator = periods.listIterator();
		WorkingPeriod insertPeriod = null;
		boolean wasExtraction = false; // marca para eliminar dos extracciones consecutivas
		while (listIterator.hasNext()) {
			WorkingPeriod period = listIterator.next();
			if (period.getTpPeriodo() == 0) {// insercion
				insertPeriod = period;
			} else if (period.getTpPeriodo() == 1) { // extraccion
				if ((insertPeriod != null) || !wasExtraction) {
					Date from = (insertPeriod == null) ? DateTools.truncate(record.getFrom()) : insertPeriod.getWhen();
					Date to = period.getWhen();
					amount += this.computeWorkingTimeBetween(from, to, record.getStretchs());
					insertPeriod = null;
				}
				wasExtraction = true;
			}
		}
		if (insertPeriod != null) {
			Date from = insertPeriod.getWhen();
			Date to = DateTools.addDays(DateTools.truncate(record.getFrom()), 1);
			amount += this.computeWorkingTimeBetween(from, to, record.getStretchs());
		}
		return amount;
	}

	private int computeWorkingTimeBetween(Date from, Date to, List<Stretch> stretchs) {
		int totalWorking = 0;
		for (Stretch stretch : stretchs) {
			if ((from.compareTo(stretch.getEndDate()) < 0) && (to.compareTo(stretch.getBeginDate()) > 0)) {
				int amount = 0;
				if ((from.compareTo(stretch.getBeginDate()) >= 0) && (to.compareTo(stretch.getEndDate()) <= 0)) {
					amount = AnalyzerUtils.minutesBetween(to, from);
				} else if ((from.compareTo(stretch.getBeginDate()) <= 0) && (to.compareTo(stretch.getEndDate()) >= 0)) {
					amount = AnalyzerUtils.minutesBetween(stretch.getEndDate(), stretch.getBeginDate());
				} else if ((from.compareTo(stretch.getBeginDate()) >= 0) && (to.compareTo(stretch.getEndDate()) >= 0)) {
					amount = AnalyzerUtils.minutesBetween(stretch.getEndDate(), from);
				} else if ((from.compareTo(stretch.getBeginDate()) <= 0) && (to.compareTo(stretch.getEndDate()) <= 0)) {
					amount = AnalyzerUtils.minutesBetween(to, stretch.getBeginDate());
				}
				if (ObjectTools.isIn(stretch.getType(), StretchType.REST)) {
					if (amount < 60) { // las superiores a 1 h no cuentan
						totalWorking += amount;
					}
				} else {
					totalWorking += amount;
				}
			}
		}
		return totalWorking;
	}
}
