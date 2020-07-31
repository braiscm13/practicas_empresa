package com.opentach.server.maintenance;

import java.sql.Timestamp;

import com.opentach.common.maintenance.MaintenanceStatusTask;

public interface IMaintenanceTask {

	void reset();

	MaintenanceStatusTask doMaintenance(String backupFolder, Timestamp filterDate);

	MaintenanceStatusTask getStatus();
}
