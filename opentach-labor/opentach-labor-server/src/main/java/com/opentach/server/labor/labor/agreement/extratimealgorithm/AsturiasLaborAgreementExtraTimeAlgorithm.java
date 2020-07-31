package com.opentach.server.labor.labor.agreement.extratimealgorithm;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.labor.DriverContract;
import com.opentach.server.labor.labor.ExtraTimeComputationPeriodMode;

public class AsturiasLaborAgreementExtraTimeAlgorithm extends DefaultLaborAgreementExtraTimeAlgorithm {

	private static final Logger logger = LoggerFactory.getLogger(AsturiasLaborAgreementExtraTimeAlgorithm.class);

	public AsturiasLaborAgreementExtraTimeAlgorithm() {
		super();
	}

	@Override
	public Number computeLimit(ExtraTimeComputationPeriodMode periodMode, String from, String to, List<DailyWorkRecord> dailyRecords, DriverContract contract) throws Exception {
		if (!ExtraTimeComputationPeriodMode.WEEKLY.equals(periodMode)) {
			throw new Exception("INVALID_PERIOD_FOR_ASTURIAS");
		}
		Number limit = super.computeLimit(periodMode, from, to, dailyRecords, contract);
		String firstDay = this.getFirstWeekDayStr(from);
		String lastDay = this.getLastWeekDayStr(to);
		ListIterator<DailyWorkRecord> iterator = dailyRecords.listIterator();
		boolean begin = false;
		int workingDays = 0;
		while (iterator.hasNext()) {
			DailyWorkRecord next = iterator.next();
			if (this.afterOrEquals(next.getDayString(), firstDay)) {
				// empezamos
				begin = true;
			}
			if (begin) {
				if (this.beforeOrEquals(next.getDayString(), lastDay)) {
					workingDays++;
				} else {
					break;
				}
			}
		}
		return (workingDays >= 5) ? limit : (workingDays * (limit.intValue() / 5));
	}

	private boolean beforeOrEquals(String test, String reference) {
		return test.compareTo(reference) <= 0;
	}

	private boolean afterOrEquals(String test, String reference) {
		return test.compareTo(reference) >= 0;
	}

	private String getLastWeekDayStr(String day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DailyWorkRecord.fromDayString(day));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return DailyWorkRecord.toDayString(cal.getTime());
	}

	private String getFirstWeekDayStr(String day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DailyWorkRecord.fromDayString(day));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return DailyWorkRecord.toDayString(cal.getTime());
	}

}
