package com.opentach.server.labor.labor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DriverSettings implements Serializable {

	private String					companyName;
	private String					companyCif;
	private String					companyCcc;
	private Object					opentachContract;
	private Object					driverId;
	private String					driverDni;
	private String					driverNaf;
	private String					driverName;
	private String					driverSurname;
	private List<DriverContract>	driverContracts;

	public DriverSettings() {
		super();
	}


	public List<DriverContract> getDriverContracts() {
		return this.driverContracts;
	}

	public void setDriverContracts(List<DriverContract> driverContracts) {
		this.driverContracts = driverContracts;
	}

	public DriverContract getLaborAgreeementForDay(Date day) {
		for (DriverContract contract : this.driverContracts) {
			if ((contract.getFrom().compareTo(day) <= 0) && ((contract.getTo() == null) || (contract.getTo().compareTo(day) >= 0))) {
				return contract;
			}
		}
//		System.out.println("Null"+ day);
		return null;
	}

	public Number computeExtraTime(ExtraTimeComputationPeriodMode periodMode, Date from, Date to, List<DailyWorkRecord> dailyRecords) throws Exception {
		return this.getLaborAgreeementForDay(from).computeExtraTime(periodMode, DailyWorkRecord.toDayString(from), DailyWorkRecord.toDayString(to), dailyRecords);
	}

	public Number computeExtraTime(ExtraTimeComputationPeriodMode periodMode, String strFrom, String strTo, List<DailyWorkRecord> dailyRecords) throws Exception {
		if (this.getLaborAgreeementForDay(DailyWorkRecord.fromDayString(strFrom)) == null)
			return 0;
		return this.getLaborAgreeementForDay(DailyWorkRecord.fromDayString(strFrom)).computeExtraTime(periodMode, strFrom, strTo, dailyRecords);
	}

	public Object getOpentachContract() {
		return this.opentachContract;
	}

	public Object getDriverId() {
		return this.driverId;
	}

	public String getDriverDni() {
		return this.driverDni;
	}

	public String getDriverName() {
		return this.driverName;
	}

	public String getDriverSurname() {
		return this.driverSurname;
	}

	public String getCompanyCif() {
		return this.companyCif;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setCompanyCif(String companyCif) {
		this.companyCif = companyCif;
	}

	public void setDriverSurname(String driverSurname) {
		this.driverSurname = driverSurname;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setDriverDni(String driverDni) {
		this.driverDni = driverDni;
	}

	public void setDriverId(Object driverId) {
		this.driverId = driverId;
	}

	public void setOpentachContract(Object contract) {
		this.opentachContract = contract;
	}

	public String getCompanyCcc() {
		return this.companyCcc;
	}

	public void setCompanyCcc(String companyCcc) {
		this.companyCcc = companyCcc;
	}

	public String getDriverNaf() {
		return this.driverNaf;
	}

	public void setDriverNaf(String driverNaf) {
		this.driverNaf = driverNaf;
	}

}
