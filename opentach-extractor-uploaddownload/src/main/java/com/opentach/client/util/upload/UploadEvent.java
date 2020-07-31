package com.opentach.client.util.upload;

import java.util.EventObject;
import java.util.List;

import com.opentach.client.util.TGDFileInfo;

public class UploadEvent extends EventObject {

	public static enum UploadEventType {
		FILE_UPLOAD_START, FILE_UPLOAD_END, DRIVER_VEH_ERROR, CONTRACT_ERROR, FILE_UPLOAD_ERROR, FILE_UPLOAD_PROGRESS
	}

	protected final String				message;
	protected final UploadEventType		type;
	protected final List<TGDFileInfo>	files;
	private int							current;
	private int							total;

	public UploadEvent(Object obj, UploadEventType type, String msg) {
		this(obj, type, msg, null);
	}

	public UploadEvent(Object obj, UploadEventType type, String msg, List<TGDFileInfo> files) {
		super(obj);
		this.type = type;
		this.message = msg;
		this.files = files;
	}

	public UploadEvent(Object obj, UploadEventType type, int current, int total) {
		this(obj, type, null, null);
		this.current = current;
		this.total = total;
	}

	public String getMessage() {
		return this.message;
	}

	public UploadEventType getType() {
		return this.type;
	}

	public List<TGDFileInfo> getFiles() {
		return this.files;
	}

	public int getCurrent() {
		return this.current;
	}

	public int getTotal() {
		return this.total;
	}

	@Override
	public String toString() {
		return String.format("%s, [source=%s, message=%s, type=%s, fileCount=%d", this.getClass().getName(), this.source, this.message, this.type,
				this.files == null ? 0 : this.files.size());
	}
}
