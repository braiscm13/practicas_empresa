package com.opentach.server.remotevehicle.uploader;

import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;

public interface IUploader {
	public void uploadFile(byte[] file, String originalFilename, TachoFileUploadRequest parameters);
}
