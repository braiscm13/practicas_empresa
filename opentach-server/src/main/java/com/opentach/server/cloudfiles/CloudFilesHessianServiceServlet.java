package com.opentach.server.cloudfiles;

import java.io.InputStream;

import com.opentach.common.cloudfiles.ICloudFilesHessianService;
import com.opentach.server.hessian.DefaultHessianServiceServlet;

public class CloudFilesHessianServiceServlet extends DefaultHessianServiceServlet implements ICloudFilesHessianService {

	/**
	 * Instantiates a new hessian servlet.
	 */
	public CloudFilesHessianServiceServlet() {
		super();
	}


	@Override
	public InputStream downloadFile(Object fileId, int sessionId) throws Exception {
		return this.getService(CloudFilesService.class).downloadFile(fileId, sessionId);
	}
}
