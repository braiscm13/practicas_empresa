package com.opentach.server.maintenance;

import java.sql.Timestamp;

import com.opentach.common.maintenance.MaintenanceStatus.MaintenanceStatusType;

public class MaintenanceTaskCertifActiv extends AbstractMaintenanceTask {

	@Override
	protected MaintenanceStatusType getTaskKey() {
		// return MaintenanceStatusType.DELETE_CERTIF_ACTIV;
		return null;
	}

	@Override
	protected void doInnerMaintenance(final String backupFolder, final Timestamp filterDate) throws Exception {
		this.updateStatus("maintenance.deleting", 25, false);
		int numDeletes = new DeleteTask(null, "DELETE FROM CDCERTIF_ACTIVIDADES WHERE F_ALTA <= ?", filterDate).call();
		this.updateStatus("maintenance.deleted", 100, false, numDeletes);
	}
}
