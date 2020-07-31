package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.server.labor.labor.DailyWorkRecord;

//@formatter:off
/**
 * Este algoritmo considera horas de trabajo las de conduccion
 * @author joaquin.romero
 *
 */
//@formatter:on
public class DrivingPlus2HoursLaborAgreementAlgorithm implements ILaborAgreementAlgorithm {

	public DrivingPlus2HoursLaborAgreementAlgorithm() {
		super();
	}

	@Override
	public int processDailyWorkRecord(DailyWorkRecord record) {
		List<Stretch> stretchs = record.getStretchs();
		int amount = 0;
		for (Stretch stretch : stretchs) {
			if (ObjectTools.isIn(stretch.getType(), StretchType.DRIVING)) {
				amount += stretch.getDuration();
			}
		}
		return amount + 120;
	}
}
