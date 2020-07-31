package com.opentach.common.cloudfiles;

import java.io.InputStream;

public interface ICloudFilesHessianService {

	InputStream downloadFile(Object fileId, int sessionId) throws Exception;

}
