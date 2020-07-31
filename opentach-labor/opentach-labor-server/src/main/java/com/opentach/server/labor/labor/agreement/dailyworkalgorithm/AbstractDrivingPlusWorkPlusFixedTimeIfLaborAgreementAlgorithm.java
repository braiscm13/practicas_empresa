package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;

//@formatter:off
/**
 * Este algoritmo considera horas de trabajo las de conduccion+otros trabajos
 * @author joaquin.romero
 *
 */
//@formatter:on
public class AbstractDrivingPlusWorkPlusFixedTimeIfLaborAgreementAlgorithm implements ILaborAgreementAlgorithm {

	private final int	accumConditionMinutes;
	private final int	fixTime;

	public AbstractDrivingPlusWorkPlusFixedTimeIfLaborAgreementAlgorithm(int accumConditionMinutes, int fixTime) {
		super();
		this.accumConditionMinutes = accumConditionMinutes;
		this.fixTime = fixTime;
	}


	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), StretchType.DRIVING, StretchType.WORK)) {
				amount += stretch.getDuration();				
			}
		}
		int ntimes = (int) Math.floor(amount / accumConditionMinutes);
		amount += this.fixTime*ntimes;
		return amount;
	}
}
