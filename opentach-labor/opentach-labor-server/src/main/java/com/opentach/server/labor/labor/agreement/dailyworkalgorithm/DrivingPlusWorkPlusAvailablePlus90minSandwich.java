package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;

public class DrivingPlusWorkPlusAvailablePlus90minSandwich implements ILaborAgreementAlgorithm {

	public DrivingPlusWorkPlusAvailablePlus90minSandwich() {
		super();
	}

	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		int minPause = 90;
		boolean addSandwich = false;
//		IntervalHour launchInterval = new IntervalHour(12 * 60 * 60, 16 * 60 * 60);
//		IntervalHour dinnerInterval = new IntervalHour(19 * 60 * 60, 23 * 60 * 60);
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), StretchType.DRIVING, StretchType.WORK, StretchType.AVAILABLE)) {
				amount += stretch.getDuration();
			}
			if (ObjectTools.isIn(stretch.getType(), StretchType.REST)  && (stretch.getDuration() >= minPause)
//					&& launchInterval.isInto(stretch.getBeginDate()) && launchInterval.isInto(stretch.getEndDate())
					) {
				addSandwich = true;
			}
		}
		return amount - (addSandwich ? minPause : 0);
	}
}