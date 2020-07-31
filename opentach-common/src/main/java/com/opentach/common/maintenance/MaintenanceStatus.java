package com.opentach.common.maintenance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MaintenanceStatus implements Serializable {
	public enum MaintenanceStatusType { //
		BACKUP_DELETE_TACHO_FILES, //
		DELETE_MANAGEMENT_REPORTS, //
		DELETE_VEHICLE_USES, //
		DELETE_WORKING_PERIODS, //
		DELETE_CARD_USES, //
		DELETE_CALIBRATIONS, //
//		DELETE_CERTIF_ACTIV, //
		DELETE_CONTROLS, //
		DELETE_FAILS, //
		DELETE_REG_KM_DRIVER, //
		DELETE_REG_KM_VEHICLE, //
		DELETE_SPEED, //
		DELETE_ACTIVITIES
	}

	/** The task status. */
	private final Map<MaintenanceStatusType, MaintenanceStatusTask>	taskStatuses;
	private final boolean											available;
	/**
	 * Instantiates a new maintenance status.
	 */
	public MaintenanceStatus(boolean available) {
		super();
		this.taskStatuses = new HashMap<>();
		this.available = available;
	}

	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * Adds the status.
	 *
	 * @param type
	 *            the type
	 * @param taskStatus
	 *            the task status
	 */
	public void addStatus(MaintenanceStatusTask taskStatus) {
		this.taskStatuses.put(taskStatus.getKey(), taskStatus);
	}

	/**
	 * Gets the status.
	 *
	 * @param type
	 *            the type
	 * @return the status
	 */
	public MaintenanceStatusTask getStatus(MaintenanceStatusType type) {
		return this.taskStatuses.get(type);
	}
}
