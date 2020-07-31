package com.opentach.server.labor.labor;

import java.util.List;

public class LaborResult {
	private DriverSettings			driverSettings;
	private List<DailyWorkRecord>	dailyRecords;

	public LaborResult() {
		super();
	}

	public LaborResult(DriverSettings driverSettings, List<DailyWorkRecord> dailyRecords) {
		super();
		this.driverSettings = driverSettings;
		this.dailyRecords = dailyRecords;
	}

	public DriverSettings getDriverSettings() {
		return this.driverSettings;
	}

	public void setDriverSettings(DriverSettings driverSettings) {
		this.driverSettings = driverSettings;
	}

	public List<DailyWorkRecord> getDailyRecords() {
		return this.dailyRecords;
	}

	public void setDailyRecords(List<DailyWorkRecord> dailyRecords) {
		this.dailyRecords = dailyRecords;
	}

}
