package com.opentach.common.tasks;

import java.io.InputStream;
import java.util.Map;

import com.opentach.common.interfaces.IFilesService;

public interface ITaskAttachmentFilesHessianService extends IFilesService {
	/**
	 * Upload new file to server, and save related values
	 *
	 * @param values
	 * @param inputStream
	 * @return the FileId.
	 * @throws Exception
	 */
	@Override
	Object uploadFile(Map<Object, Object> values, int sessionId, InputStream inputStream) throws Exception;

	/**
	 * Download file from server.
	 *
	 * @param fileId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@Override
	InputStream downloadFile(Object fileId, int sessionId) throws Exception;

	/**
	 * Delete this file from server.
	 *
	 * @param fileId
	 * @param sessionId
	 * @throws Exception
	 */
	@Override
	void deleteFile(Object fileId, int sessionId) throws Exception;
}
