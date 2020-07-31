package com.opentach.server.labor.labor.agreement.extratimealgorithm;

import java.util.List;

import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.labor.DriverContract;
import com.opentach.server.labor.labor.ExtraTimeComputationPeriodMode;

public interface ILaborAgreementExtraTimeAlgorithm {

	Number computeExtraTime(ExtraTimeComputationPeriodMode periodMode, String from, String to, List<DailyWorkRecord> dailyRecords, DriverContract driverContract) throws Exception;

	Number computeLimit(ExtraTimeComputationPeriodMode periodMode, String from, String to, List<DailyWorkRecord> dailyRecords, DriverContract contract) throws Exception;

}
