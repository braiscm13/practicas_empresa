package com.opentach.client;

import com.opentach.client.util.download.DownloadMonitor;
import com.opentach.client.util.net.NetworkMonitor;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.user.IUserData;
import com.opentach.model.comm.vu.TachoReadMonitor;

public interface MonitorProvider {

	// public SmartCardMonitor getSmartCardMonitor();

	public UploadMonitor getUploadMonitor();

	public DownloadMonitor getDownloadMonitor();

	public USBKeyMonitor getUSBKeyMonitor();

	public TachoReadMonitor getTachoReadMonitor();

	public TachoFileStore getFileStore();

	public NetworkMonitor getNetworkMonitor();

	<T> T getRemoteService(Class<T> cl) throws Exception;

	<T> T getLocalService(Class<T> cl);

	int getSessionId();

	IUserData getUserData() throws Exception;

	String getUserDescription();
}
