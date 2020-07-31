package com.opentach.server.labor.labor.agreement.extratimealgorithm;

import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.labor.DriverContract;
import com.opentach.server.labor.labor.ExtraTimeComputationPeriodMode;

public class DefaultLaborAgreementExtraTimeAlgorithm implements ILaborAgreementExtraTimeAlgorithm {

	private static final Logger logger = LoggerFactory.getLogger(DefaultLaborAgreementExtraTimeAlgorithm.class);

	public DefaultLaborAgreementExtraTimeAlgorithm() {
		super();
	}

	@Override
	public Number computeExtraTime(ExtraTimeComputationPeriodMode periodMode, String beginStr, String endStr, List<DailyWorkRecord> dailyRecords, DriverContract driverContract)
			throws Exception {
		int workingTime = 0;
		if (this.checkPeriodMode(periodMode, driverContract)) {
			// puede que sea mejor lanzar excepcion
			return 0;
		}
		Number limit = this.computeLimit(periodMode, beginStr, endStr, dailyRecords, driverContract);
		ListIterator<DailyWorkRecord> iterator = dailyRecords.listIterator();
		boolean begin = false;
		while (iterator.hasNext()) {
			DailyWorkRecord next = iterator.next();
			if (next.getDayString().equals(beginStr)) {
				// empezamos
				begin = true;
			}
			if (begin) {
				workingTime += next.getWorkingMinutes();
				if (next.getDayString().equals(endStr)) {
					int extraTime = workingTime - limit.intValue();
					return extraTime < 0 ? 0 : extraTime;
				}
			}
		}
		return 0;
	}

	private boolean checkPeriodMode(ExtraTimeComputationPeriodMode periodMode, DriverContract contract) {
		return false;
	}

	@Override
	public Number computeLimit(ExtraTimeComputationPeriodMode periodMode, String from, String to, List<DailyWorkRecord> dailyRecords, DriverContract contract) throws Exception {
		Number limit = null;
		switch (contract.getContractType()) {
			case PARTIAL_TIME:
				switch (periodMode) {
					case DAILY:
						limit = contract.getWorkingDailyTimeForPartialContract();
						break;
					case WEEKLY:
						limit = contract.getWorkingWeeklyTimeForPartialContract();
						break;
					case BIWEEKLY:
						limit = contract.getWorkingBiweeklyTimeForPartialContract();
						break;
					case FOUR_WEEKLY:
						limit = contract.getWorkingFourweeklyTimeForPartialContract();
						break;
					case MONTHLY:
						limit = contract.getWorkingMonthlyTimeForPartialContract();
						break;
					case FOUR_MONTHLY:
						limit = contract.getWorking4MonthlyTimeForPartialContract();
						break;
					case ANNUAL:
						limit = contract.getWorkingAnnualTimeForPartialContract();
						break;
				}
				break;
			case FULL_TIME:
				switch (periodMode) {
					case DAILY:
						limit = contract.getLaborAgreement().getDailyTimeLimit();
						break;
					case WEEKLY:
						limit = contract.getLaborAgreement().getWeeklyTimeLimit();
						break;
					case BIWEEKLY:
						limit = contract.getLaborAgreement().getBiweeklyTimeLimit();
						break;
					case FOUR_WEEKLY:
						limit = contract.getLaborAgreement().getFourweeklyTimeLimit();
						break;
					case MONTHLY:
						limit = contract.getLaborAgreement().getMonthlyTimeLimit();
						break;
					case FOUR_MONTHLY:
						limit = contract.getLaborAgreement().getFourMonthlyTimeLimit();
						break;
					case ANNUAL:
						limit = contract.getLaborAgreement().getAnnualTimeLimit();
						break;
				}
				break;
		}
		if ((limit == null) || (limit.intValue() == 0)) {
			DefaultLaborAgreementExtraTimeAlgorithm.logger.error("limit {} not found for {} in mode {}", periodMode, contract.getLaborAgreement().getName(),
					contract.getContractType());
			throw new OntimizeJEEException("E_LABOR_LIMIT_NOT_FOUND",
					new Object[] { periodMode.toString(), contract.getLaborAgreement().getName(), contract.getContractType().toString() });
		}
		return limit;
	}

}
