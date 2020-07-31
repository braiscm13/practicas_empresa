package com.opentach.server.tasks.services;

import java.io.InputStream;
import java.util.Map;

import com.opentach.common.tasks.ITaskAttachmentFilesHessianService;
import com.opentach.server.hessian.DefaultHessianServiceServlet;


public class TaskAttachmentFilesHessianServiceServlet extends DefaultHessianServiceServlet implements ITaskAttachmentFilesHessianService {

	/**
	 * Instantiates a new hessian servlet.
	 */
	public TaskAttachmentFilesHessianServiceServlet() {
		super();
	}

	@Override
	public Object uploadFile(Map<Object, Object> values, int sessionId, InputStream inputStream) throws Exception {
		return this.getService(TaskAtachmentFilesService.class).uploadFile(values, sessionId, inputStream);
	}

	@Override
	public InputStream downloadFile(Object fileId, int sessionId) throws Exception {
		return this.getService(TaskAtachmentFilesService.class).downloadFile(fileId, sessionId);
	}

	@Override
	public void deleteFile(Object fileId, int sessionId) throws Exception {
		this.getService(TaskAtachmentFilesService.class).deleteFile(fileId, sessionId);
	}

}
