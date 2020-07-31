package com.opentach.common.tachofiles;

import java.rmi.Remote;

import com.opentach.common.filereception.UploadSourceType;

/**
 * The Interface ITachoFileService.
 */
public interface ITachoFileRecordService extends Remote {
	/** The Constant ID. */
	final static String			ID			= "TachoFileRecordService";

	public static final String	UPLOAD		= "UP";
	public static final String	DOWNLOAD	= "DW";
	public static final String	PROCESS		= "PR";

	void saveFileRecord(Object idFile, String cgContrato, String type, String name, String obsr, UploadSourceType sourceType, String mail, boolean analize, String notifUrl,
			Number latitude, Number longitude, Object idBlackberry, String ismovil, int sessionID) throws Exception;
}
