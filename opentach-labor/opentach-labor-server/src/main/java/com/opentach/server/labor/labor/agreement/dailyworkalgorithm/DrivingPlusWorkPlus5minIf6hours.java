package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

public class DrivingPlusWorkPlus5minIf6hours extends AbstractDrivingPlusWorkPlusFixedTimeIfLaborAgreementAlgorithm {

	public DrivingPlusWorkPlus5minIf6hours(int accumConditionMinutes, int fixTime) {
		super(6 * 60, 5);
	}

}
