package com.opentach.common.maintenance;

import java.io.Serializable;

import com.opentach.common.maintenance.MaintenanceStatus.MaintenanceStatusType;

public class MaintenanceStatusTask implements Serializable {

	private final MaintenanceStatusType	key;
	private final float		percent;
	private final String	currentStatus;
	private final boolean	running;
	private final boolean	error;
	private final Object[]	currentStatusParameters;

	public MaintenanceStatusTask(MaintenanceStatusType key, float percent, String currentStatus, boolean running, boolean error, Object[] currentStatusTextParameters) {
		super();
		this.key = key;
		this.percent = percent;
		this.currentStatus = currentStatus;
		this.running = running;
		this.error = error;
		this.currentStatusParameters = currentStatusTextParameters;
	}

	public MaintenanceStatusType getKey() {
		return this.key;
	}

	public float getPercent() {
		return this.percent;
	}

	public String getCurrentStatus() {
		return this.currentStatus;
	}

	public boolean isRunning() {
		return this.running;
	}

	public boolean isError() {
		return this.error;
	}

	public Object[] getCurrentStatusParameters() {
		return this.currentStatusParameters;
	}
}
