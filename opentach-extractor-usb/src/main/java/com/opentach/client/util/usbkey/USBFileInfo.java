package com.opentach.client.util.usbkey;

import java.io.File;

public class USBFileInfo {
	private final File	usbFile;
	private final File	hddFile;

	public USBFileInfo(File usbFile, File hddFile) {
		super();
		this.usbFile = usbFile;
		this.hddFile = hddFile;
	}

	public File getUsbFile() {
		return this.usbFile;
	}

	public File getHddFile() {
		return this.hddFile;
	}

	@Override
	public String toString() {
		return this.hddFile.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof USBFileInfo) {
			USBFileInfo d = (USBFileInfo) obj;
			if ((this.hddFile == null) || (d.hddFile == null)) {
				return false;
			}
			return this.hddFile.getName().equals(d.hddFile.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.hddFile == null) {
			return 0;
		}
		return this.hddFile.getName().hashCode();
	}
}
