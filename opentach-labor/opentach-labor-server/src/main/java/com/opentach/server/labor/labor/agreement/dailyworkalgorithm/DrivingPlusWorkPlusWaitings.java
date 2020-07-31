package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;

public class DrivingPlusWorkPlusWaitings implements ILaborAgreementAlgorithm {

	public DrivingPlusWorkPlusWaitings() {
		super();
	}

	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		int amountWaiting = 0;
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), StretchType.DRIVING, StretchType.WORK)) {
				amount += stretch.getDuration();
				amount += amountWaiting;
				amountWaiting = 0;
			} else if (ObjectTools.isIn(stretch.getType(), StretchType.REST)) {
				if (stretch.getDuration() > 60) {
					amountWaiting += stretch.getDuration();
				}
			}

		}
		return amount;
	}
}
