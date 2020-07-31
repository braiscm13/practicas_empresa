package com.opentach.common.tasks;

import java.rmi.RemoteException;

import com.utilmize.services.IUtilmizeService;

/**
 * The Interface ITaskAttachmentService.
 */
public interface ITaskAttachmentService extends IUtilmizeService {
	/** The Constant ID. */
	final static String ID = "TaskAttachmentService";

	// DB COLUMNS
	public static final String	COLUMN_ID		= "TAT_ID";
	public static final String	COLUMN_NAME		= "TAT_NAME";
	public static final String	COLUMN_DESC		= "TAT_DESCRIPTION";
	public static final String	COLUMN_TASKID	= "TSK_ID";
	public static final String	COLUMN_DATE		= "TAT_DATE";
	public static final String	COLUMN_USER		= "USUARIO";

	/**
	 * Returns the file name with extension.
	 *
	 * @param fileId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 * @throws RemoteException
	 */
	String getFileName(Object fileId, int sessionId) throws Exception, RemoteException;
}
