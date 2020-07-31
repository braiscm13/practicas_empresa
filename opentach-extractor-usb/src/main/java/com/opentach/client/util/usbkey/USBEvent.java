package com.opentach.client.util.usbkey;

import java.util.EventObject;
import java.util.List;

public class USBEvent extends EventObject {

	public static final String	M_ERROR_COPYING_DATA	= "M_ERROR_COPYING_DATA";

	public static final String	M_ERROR_INTERNAL		= "M_ERROR_INTERNAL";

	public static final String	M_KEY_EMPTY				= "M_KEY_EMPTY";

	public static enum USBEventType {
		USB_DOWNLOAD_START, USB_DOWNLOAD_END, USB_DOWNLOAD_ERROR, USB_NOT_FOUND
	}

	protected final USBEventType		type;
	protected final String				msg;
	protected final List<USBFileInfo>	lFileInfo;

	public USBEvent(Object source, USBEventType type) {
		this(source, type, null);
	}

	public USBEvent(Object source, USBEventType type, String msg) {
		this(source, type, msg, null);
	}

	public USBEvent(Object source, USBEventType type, String msg, List<USBFileInfo> lFileInfo) {
		super(source);
		this.type = type;
		this.msg = msg;
		this.lFileInfo = lFileInfo;
	}


	public USBEventType getType() {
		return this.type;
	}

	public String getMessage() {
		return this.msg;
	}

	public List<USBFileInfo> getFileInfo() {
		return this.lFileInfo;
	}

	@Override
	public String toString() {
		return String.format("%s [type=%s, msg=%s, fileCount=%d", this.getClass().getName(), this.type, this.msg,
				this.lFileInfo == null ? -1 : this.lFileInfo.size());
	}

}
