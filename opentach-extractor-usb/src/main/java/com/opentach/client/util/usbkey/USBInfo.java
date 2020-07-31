package com.opentach.client.util.usbkey;
public class USBInfo {

	private final String	deviceName;
	private final String	path;

	public USBInfo(String deviceName, String path) {
		this.deviceName = deviceName;
		this.path = path;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public String getPath() {
		return this.path;
	}

}