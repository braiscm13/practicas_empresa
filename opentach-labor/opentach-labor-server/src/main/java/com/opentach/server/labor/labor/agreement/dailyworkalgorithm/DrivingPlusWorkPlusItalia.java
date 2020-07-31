package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;

public class DrivingPlusWorkPlusItalia extends AbstractDrivingPlusWorkPlusFixedTimeIfLaborAgreementAlgorithm {

	public DrivingPlusWorkPlusItalia() {
		super(6 * 60, 15);
	}
	
	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), StretchType.DRIVING, StretchType.WORK, StretchType.AVAILABLE)) {
				amount += stretch.getDuration();
			}
		}
		
		int amount4_30 = getAccumConditionMinutes(amount, (4*60) + 30 , 45);
		if (amount>=(6*60) && amount <= (9*60)) {
			amount -=30;
		}else if (amount>(9*60)) {
			amount -=45;
		}
		amount -= amount4_30;
		return amount;
	}
	
	private int getAccumConditionMinutes(int amount, int accumConditionMinutes, int fixTime) {
		int ntimes = (int) Math.floor(amount % accumConditionMinutes);
		return fixTime*ntimes;
		
	}

}
