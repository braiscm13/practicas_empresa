package com.opentach.client.util.upload;

import java.util.EventListener;

public interface UploadListener extends EventListener {

	public void uploadStatusChange(UploadEvent upe);

}
