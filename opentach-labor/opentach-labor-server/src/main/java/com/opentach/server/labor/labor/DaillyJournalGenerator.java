package com.opentach.server.labor.labor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.AnalyzerUtils;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.server.labor.labor.DailyWorkRecord.DailyWorkRecordOrigin;

//@formatter:off
/**
 * Stretchs divididos por jornada de trabajo.<br/>
 * @author joaquin.romero
 *
 */
//@formatter:on
public class DaillyJournalGenerator {

	private static final Logger logger = LoggerFactory.getLogger(DaillyJournalGenerator.class);
	/**
	 * Generate.
	 *
	 * @param stretchs
	 *            the stretchs
	 * @param workingPeriods
	 * @param outPeriodMap
	 *            the out period map
	 * @param outRestMap
	 *            the out rest map
	 * @param configuration
	 *            the configuration
	 */
	public List<DailyWorkRecord> generate(LinkedList<Stretch> stretchs, LinkedList<WorkingPeriod> workingPeriods, AnalysisParameters configuration) {
		List<DailyWorkRecord> res = new ArrayList<>();
		ListIterator<Stretch> listIterator = stretchs.listIterator();
		Date cutDate = null;
		Date beginDate = null;
		DailyWorkRecord currentDailyRecord = null;
		int wpi = 0;
		while (listIterator.hasNext()) {
			Stretch curStretch = listIterator.next();
			Date curBeginDate = curStretch.getBeginDate();
			Date curEndDate = curStretch.getEndDate();
			if ((cutDate == null) || curEndDate.after(cutDate)) {
				if (this.areDifferentDays(curBeginDate, curEndDate)) {
					Pair<Stretch,Stretch> pair = this.divideStretch(curStretch);
					if (currentDailyRecord != null) {
						currentDailyRecord.addStretch(pair.getFirst());
					}
					curStretch = pair.getSecond();
					curBeginDate = curStretch.getBeginDate();
					curEndDate = curStretch.getEndDate();
				}
				cutDate = DateTools.truncate(DateTools.add(curBeginDate, Calendar.DAY_OF_MONTH, 1));
				beginDate = AnalyzerUtils.trunc(curBeginDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(curBeginDate);
				currentDailyRecord = new DailyWorkRecord(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DailyWorkRecordOrigin.AUTO);
				wpi = this.insertWorkingPeriodsForDay(workingPeriods, cutDate, beginDate, currentDailyRecord, wpi);
				res.add(currentDailyRecord);
			}
			currentDailyRecord.addStretch(curStretch);
		}
		return res;
	}

	private Pair<Stretch, Stretch> divideStretch(Stretch curStretch) {
		Date dividerTime = DateTools.truncate(curStretch.getEndDate());
		Stretch previous = new Stretch(curStretch.getType(), dividerTime, curStretch.getBeginDate(), curStretch.getRegimen(),
				curStretch.getOrigin(), curStretch.isInFerry());
		Stretch next = new Stretch(curStretch.getType(), curStretch.getEndDate(), dividerTime, curStretch.getRegimen(), curStretch.getOrigin(),
				curStretch.isInFerry());
		return new Pair<>(previous, next);
	}

	private boolean areDifferentDays(Date beginDate, Date endDate) {
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(beginDate);
		calBegin.add(Calendar.MILLISECOND, 1);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		calEnd.add(Calendar.MILLISECOND, -1);
		return (calBegin.get(Calendar.YEAR) != calEnd.get(Calendar.YEAR)) || (calBegin.get(Calendar.DAY_OF_YEAR) != calEnd.get(Calendar.DAY_OF_YEAR));
	}

	private int insertWorkingPeriodsForDay(LinkedList<WorkingPeriod> workingPeriods, Date cutDate, Date beginDate, DailyWorkRecord currentDailyRecord, int wpi) {
		ListIterator<WorkingPeriod> wpit = workingPeriods.listIterator(wpi);
		boolean finished = false;
		while (wpit.hasNext() && !finished) {
			WorkingPeriod wp = wpit.next();
			if (this.isBetween(beginDate, wp.getWhen(), cutDate)) {
				currentDailyRecord.addWorkingPeriod(wp);
			} else if (wp.getWhen().after(cutDate)) {
				finished = true;
			}
			if (!finished) {
				wpi++;
			}
		}
		return wpi;
	}

	private boolean isBetween(Date begin, Date curr, Date end) {
		return (curr.getTime() >= begin.getTime()) && (curr.getTime() <= end.getTime());
	}
}
