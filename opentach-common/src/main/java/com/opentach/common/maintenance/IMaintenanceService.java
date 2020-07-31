package com.opentach.common.maintenance;

import java.rmi.Remote;
import java.sql.Timestamp;

/**
 * The Interface ITachoFileService.
 */
public interface IMaintenanceService extends Remote {
	/** The Constant ID. */
	final static String	ID	= "MaintenanceService";

	MaintenanceStatus getStatus(int sessionId) throws Exception;

	void doClear(String backupFilesFolder, Timestamp filterDate, int sessionId) throws Exception;
}
