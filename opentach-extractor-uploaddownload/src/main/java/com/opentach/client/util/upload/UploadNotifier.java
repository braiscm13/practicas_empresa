package com.opentach.client.util.upload;

import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.upload.UploadEvent.UploadEventType;

public interface UploadNotifier {

	public void notifyUploadEvent(TGDFileInfo file, UploadEventType type, String msg);

}
