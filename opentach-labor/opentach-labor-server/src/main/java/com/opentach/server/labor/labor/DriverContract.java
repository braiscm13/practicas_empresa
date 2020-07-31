package com.opentach.server.labor.labor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.opentach.server.labor.labor.agreement.LaborAgreement;

public class DriverContract implements Serializable {
	public static enum ContractType {
		FULL_TIME, PARTIAL_TIME;

		public static ContractType fromString(String string) {
			switch (string) {
				case "TIEMPO_PARCIAL":
					return PARTIAL_TIME;
				case "TIEMPO_COMPLETO":
					return FULL_TIME;
			}
			throw new RuntimeException("INVALID_CONTRACT_TYPE");
		}
	}

	private LaborAgreement	laborAgreement;
	private Object			idContract;
	private Date			from;
	private Date			to;
	private ContractType	contractType;
	private boolean			allowIrregularJournal;
	private Number			workingAnnualTimeForPartialContract;
	private Number			workingMonthlyTimeForPartialContract;
	private Number			working4MonthlyTimeForPartialContract;
	private Number			workingBiweeklyTimeForPartialContract;
	private Number			working4weeklyTimeForPartialContract;
	private Number			workingWeeklyTimeForPartialContract;
	private Number			workingDailyTimeForPartialContract;
	private Number			complementaryAnnualMinutesForPartialContract;
	private Number			complementaryMonthlyMinutesForPartialContract;
	private Number			complementary4MonthlyMinutesForPartialContract;
	private Number			complementary4weeklyMinutesForPartialContract;
	private Number			complementaryBiweeklyMinutesForPartialContract;
	private Number			complementaryWeeklyMinutesForPartialContract;
	private Number			complementaryDailyMinutesForPartialContract;

	public DriverContract() {
		super();
	}

	public LaborAgreement getLaborAgreement() {
		return this.laborAgreement;
	}

	public void setLaborAgreement(LaborAgreement laborAgreement) {
		this.laborAgreement = laborAgreement;
	}



	public Object getIdContract() {
		return this.idContract;
	}

	public void setIdContract(Object idContract) {
		this.idContract = idContract;
	}

	public Date getFrom() {
		return this.from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return this.to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public ContractType getContractType() {
		return this.contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	public boolean isAllowIrregularJournal() {
		return this.allowIrregularJournal;
	}

	public void setAllowIrregularJournal(boolean allowIrregularJournal) {
		this.allowIrregularJournal = allowIrregularJournal;
	}

	public void setWorkingDailyMinutesForPartialContract(Number workingDailyTimeForPartialContract) {
		this.workingDailyTimeForPartialContract = workingDailyTimeForPartialContract;

	}

	public void setWorkingWeeklyMinutesForPartialContract(Number workingWeeklyTimeForPartialContract) {
		this.workingWeeklyTimeForPartialContract = workingWeeklyTimeForPartialContract;
	}

	public void setWorkingBiweeklyMinutesForPartialContract(Number workingBiweeklyTimeForPartialContract) {
		this.workingBiweeklyTimeForPartialContract = workingBiweeklyTimeForPartialContract;
	}
	
	public void setWorkingFourweeklyMinutesForPartialContract(Number working4weeklyTimeForPartialContract) {
		this.working4weeklyTimeForPartialContract = working4weeklyTimeForPartialContract;
	}

	public void setWorkingMonthlyMinutesForPartialContract(Number workingMonthlyTimeForPartialContract) {
		this.workingMonthlyTimeForPartialContract = workingMonthlyTimeForPartialContract;

	}

	public void setWorkingFourMonthlyMinutesForPartialContract(Number working4MonthlyTimeForPartialContract) {
		this.working4MonthlyTimeForPartialContract = working4MonthlyTimeForPartialContract;

	}
	public void setWorkingAnnualMinutesForPartialContract(Number workingAnnualTimeForPartialContract) {
		this.workingAnnualTimeForPartialContract = workingAnnualTimeForPartialContract;

	}

	public Number getWorkingAnnualTimeForPartialContract() {
		return this.workingAnnualTimeForPartialContract;
	}

	public Number getWorkingBiweeklyTimeForPartialContract() {
		return this.workingBiweeklyTimeForPartialContract;
	}
	
	public Number getWorkingFourweeklyTimeForPartialContract() {
		return this.working4weeklyTimeForPartialContract;
	}

	public Number getWorkingDailyTimeForPartialContract() {
		return this.workingDailyTimeForPartialContract;
	}

	public Number getWorkingMonthlyTimeForPartialContract() {
		return this.workingMonthlyTimeForPartialContract;
	}
	public Number getWorking4MonthlyTimeForPartialContract() {
		return this.working4MonthlyTimeForPartialContract;
	}
	public Number getWorkingWeeklyTimeForPartialContract() {
		return this.workingWeeklyTimeForPartialContract;
	}

	public void setComplementaryDailyMinutesForPartialContract(Number number) {
		this.complementaryDailyMinutesForPartialContract = number;
	}

	public void setComplementaryWeeklyMinutesForPartialContract(Number number) {
		this.complementaryWeeklyMinutesForPartialContract = number;
	}

	public void setComplementaryBiweeklyMinutesForPartialContract(Number number) {
		this.complementaryBiweeklyMinutesForPartialContract = number;
	}
	
	public void setComplementaryFourweeklyMinutesForPartialContract(Number number) {
		this.complementary4weeklyMinutesForPartialContract = number;
	}

	public void setComplementaryMonthlyMinutesForPartialContract(Number number) {
		this.complementaryMonthlyMinutesForPartialContract = number;
	}

	public void setComplementaryFourMonthlyMinutesForPartialContract(Number number) {
		this.complementary4MonthlyMinutesForPartialContract = number;
	}
	
	public void setComplementaryAnnualMinutesForPartialContract(Number number) {
		this.complementaryAnnualMinutesForPartialContract = number;
	}

	Number getComplementaryAnnualMinutesForPartialContract() {
		return this.complementaryAnnualMinutesForPartialContract;
	}

	Number getComplementaryBiweeklyMinutesForPartialContract() {
		return this.complementaryBiweeklyMinutesForPartialContract;
	}
	
	Number getComplementaryFourweeklyMinutesForPartialContract() {
		return this.complementary4weeklyMinutesForPartialContract;
	}

	Number getComplementaryDailyMinutesForPartialContract() {
		return this.complementaryDailyMinutesForPartialContract;
	}

	Number getComplementaryMonthlyMinutesForPartialContract() {
		return this.complementaryMonthlyMinutesForPartialContract;
	}
	
	Number getComplementaryFourMonthlyMinutesForPartialContract() {
		return this.complementary4MonthlyMinutesForPartialContract;
	}

	Number getComplementaryWeeklyMinutesForPartialContract() {
		return this.complementaryWeeklyMinutesForPartialContract;
	}

	public boolean isPartialTime() {
		return ContractType.PARTIAL_TIME.equals(this.getContractType());
	}

	public Number computeExtraTime(ExtraTimeComputationPeriodMode periodMode, String from, String to, List<DailyWorkRecord> dailyRecords) throws Exception {
		return this.laborAgreement.computeExtraTime(periodMode, from, to, dailyRecords, this);
	}

	public Number computeTimeLimit(ExtraTimeComputationPeriodMode mode, String from, String to, List<DailyWorkRecord> dailyRecords) throws Exception {
		return this.laborAgreement.computeTimeLimit(mode, from, to, dailyRecords, this);
	}

	public Number getComplementaryMinutesForPartialContract(ExtraTimeComputationPeriodMode mode) {
		if (!ContractType.PARTIAL_TIME.equals(this.getContractType())) {
			return null;
		}

		switch (mode) {
			case DAILY:
				return this.getComplementaryDailyMinutesForPartialContract();
			case WEEKLY:
				return this.getComplementaryWeeklyMinutesForPartialContract();
			case BIWEEKLY:
				return this.getComplementaryBiweeklyMinutesForPartialContract();
			case FOUR_WEEKLY:
				return this.getComplementaryFourweeklyMinutesForPartialContract();
			case MONTHLY:
				return this.getComplementaryMonthlyMinutesForPartialContract();
			case FOUR_MONTHLY:
				return this.getComplementaryFourMonthlyMinutesForPartialContract();
			case ANNUAL:
				return this.getComplementaryAnnualMinutesForPartialContract();
		}
		return null;
	}
}
