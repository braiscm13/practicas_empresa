package com.opentach.common.interfaces;

import java.io.InputStream;
import java.rmi.Remote;
import java.util.Map;

/**
 * The Interface IFilesService.
 */
public interface IFilesService extends Remote {

	/**
	 * Upload new file to server, and save related values
	 *
	 * @param values
	 * @param inputStream
	 * @return the FileId.
	 * @throws Exception
	 */
	Object uploadFile(Map<Object, Object> values, int sessionId, InputStream inputStream) throws Exception;

	/**
	 * Download file from server.
	 *
	 * @param fileId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	InputStream downloadFile(Object fileId, int sessionId) throws Exception;

	/**
	 * Delete this file from server.
	 *
	 * @param fileId
	 * @param sessionId
	 * @throws Exception
	 */
	void deleteFile(Object fileId, int sessionId) throws Exception;
}
