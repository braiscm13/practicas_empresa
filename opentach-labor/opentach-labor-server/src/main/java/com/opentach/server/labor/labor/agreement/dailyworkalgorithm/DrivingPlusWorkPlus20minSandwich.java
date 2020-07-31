package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.util.IntervalHour;

public class DrivingPlusWorkPlus20minSandwich implements ILaborAgreementAlgorithm {

	public DrivingPlusWorkPlus20minSandwich() {
		super();
	}

	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		int minPause = 20;
		boolean addSandwich = false;
		IntervalHour interval = new IntervalHour(10 * 60 * 60, 14 * 60 * 60);
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), StretchType.DRIVING, StretchType.WORK)) {
				amount += stretch.getDuration();
			}
			if (ObjectTools.isIn(stretch.getType(), StretchType.REST) && interval.isInto(stretch.getBeginDate()) && interval
					.isInto(stretch.getEndDate()) && (stretch.getDuration() >= minPause)) {
				addSandwich = true;
			}
		}
		return amount + (addSandwich ? minPause : 0);
	}
}