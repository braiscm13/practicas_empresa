package com.opentach.server.labor.labor.agreement;

import java.io.Serializable;
import java.util.List;

import com.opentach.server.labor.labor.DailyWorkRecord;
import com.opentach.server.labor.labor.DriverContract;
import com.opentach.server.labor.labor.ExtraTimeComputationPeriodMode;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.ILaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.extratimealgorithm.ILaborAgreementExtraTimeAlgorithm;

public class LaborAgreement implements Serializable {

	/**
	 * The Enum LaborAgreementModality.
	 */
	public enum LaborAgreementModality {

		MERCANCIAS, MERCANCIASDISCRECIONAL, MERCANCIASREGULAR, MERCANCIAVIAJERO, VIAJEROS, URBANOSVIAJEROS, VIAJEROSREGDISC, AGENCIATRANS, DISCRTURISMO, AGENCIACARGASFRAC, UNKNOW;

		public static LaborAgreementModality fromString(String string) {
			switch (string) {
				case "MERCANCIAS":
					return MERCANCIAS;
				case "VIAJEROS":
					return VIAJEROS;
				case "MERCANCIASDISCRECIONAL":
					return MERCANCIASDISCRECIONAL;
				case "MERCANCIASREGULAR":
					return MERCANCIASREGULAR;
				case "MERCANCIAVIAJERO":
					return MERCANCIAVIAJERO;
				case "URBANOSVIAJEROS":
					return URBANOSVIAJEROS;
				case "VIAJEROSREGDISC":
					return VIAJEROSREGDISC;
				case "AGENCIATRANS":
					return AGENCIATRANS;
				case "DISCRTURISMO":
					return DISCRTURISMO;
				case "AGENCIACARGASFRAC":
					return AGENCIACARGASFRAC;
				default:
					return UNKNOW;
			}
		}
	}

	private LaborAgreementModality						modality;
	private String										name;
	private Number										dailyTimeLimit;
	private Number										weeklyTimeLimit;
	private Number										biweeklyTimeLimit;
	private Number										fourweeklyTimeLimit;
	private Number										monthlyTimeLimit;
	private Number										fourMonthlyTimeLimit;
	private Number										annualTimeLimit;
	private transient ILaborAgreementAlgorithm			algorithm;
	private transient ILaborAgreementExtraTimeAlgorithm	extraTimeAlgorithm;

	public LaborAgreement() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public LaborAgreementModality getModality() {
		return this.modality;
	}

	public void setModality(LaborAgreementModality modality) {
		this.modality = modality;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlgorithm(ILaborAgreementAlgorithm algorithm2) {
		this.algorithm = algorithm2;
	}

	public void setExtraTimeAlgorithm(ILaborAgreementExtraTimeAlgorithm extraTimeAlgorithm) {
		this.extraTimeAlgorithm = extraTimeAlgorithm;
	}

	public void setDailyTimeLimit(Number dailyTimeLimit) {
		this.dailyTimeLimit = dailyTimeLimit;
	}

	public void setWeeklyTimeLimit(Number weeklyTimeLimit) {
		this.weeklyTimeLimit = weeklyTimeLimit;
	}

	public void setAnnualTimeLimit(Number annualTimeLimit) {
		this.annualTimeLimit = annualTimeLimit;
	}

	public ILaborAgreementAlgorithm getAlgorithm() {
		return this.algorithm;
	}

	public void setBiweeklyTimeLimit(Number biweeklyTimeLimit) {
		this.biweeklyTimeLimit = biweeklyTimeLimit;
	}
	
	public void setFourweeklyTimeLimit(Number fourweeklyTimeLimit) {
		this.fourweeklyTimeLimit = fourweeklyTimeLimit;
	}

	public void setMonthlyTimeLimit(Number monthlyTimeLimit) {
		this.monthlyTimeLimit = monthlyTimeLimit;
	}

	public void setFourMonthlyTimeLimit(Number fourMonthlyTimeLimit) {
		this.fourMonthlyTimeLimit = fourMonthlyTimeLimit;
	}
	
	public Number getDailyTimeLimit() {
		return this.getTimeLimit(this.dailyTimeLimit);
	}

	public Number getWeeklyTimeLimit() {
		return this.getTimeLimit(this.weeklyTimeLimit);
	}

	public Number getAnnualTimeLimit() {
		return this.getTimeLimit(this.annualTimeLimit);
	}

	public Number getBiweeklyTimeLimit() {
		return this.getTimeLimit(this.biweeklyTimeLimit);
	}
	
	public Number getFourweeklyTimeLimit() {
		return this.getTimeLimit(this.fourweeklyTimeLimit);
	}

	public Number getMonthlyTimeLimit() {
		return this.getTimeLimit(this.monthlyTimeLimit);
	}
	
	public Number getFourMonthlyTimeLimit() {
		return this.getTimeLimit(this.fourMonthlyTimeLimit);
	}


	protected Number getTimeLimit(Number time) {
		if ((time == null) || (time.intValue() == 0)) {
			return null;
		}
		return time;
	}

	public int computeDailyWorkingTime(DailyWorkRecord workRecord) {
		return this.algorithm.processDailyWorkRecord(workRecord);
	}

	public Number computeExtraTime(ExtraTimeComputationPeriodMode periodMode, String from, String to, List<DailyWorkRecord> dailyRecords, DriverContract driverContract)
			throws Exception {
		return this.extraTimeAlgorithm.computeExtraTime(periodMode, from, to, dailyRecords, driverContract);
	}

	public Number computeTimeLimit(ExtraTimeComputationPeriodMode mode, String from, String to, List<DailyWorkRecord> dailyRecords, DriverContract contract) throws Exception {
		return this.extraTimeAlgorithm.computeLimit(mode, from, to, dailyRecords, contract);
	}

}
