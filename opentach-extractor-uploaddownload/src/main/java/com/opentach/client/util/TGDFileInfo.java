package com.opentach.client.util;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TGDFileInfo {

	private final File	origFile;
	private File		uploadedFile;
	private String		ownerName;
	private String		ownerId;
	private boolean		success;
	private Date		date;
	private File		usbFile;
	private TGDFileType	fileType;

	public TGDFileInfo(File origFile) {
		this.origFile = origFile;
		this.uploadedFile = null;
		this.ownerName = null;
		this.usbFile = null;
	}

	public TGDFileInfo(File usbFile, File origFile) {
		this(origFile);
		this.usbFile = usbFile;
	}

	public File getUsbFile() {
		return this.usbFile;
	}

	public File getOrigFile() {
		return this.origFile;
	}

	public File getUploadedFile() {
		return this.uploadedFile;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setUploadedFile(File uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setFileType(TGDFileType fileType) {
		this.fileType = fileType;
	}

	public TGDFileType getFileType() {
		return this.fileType;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public static final boolean isSuccess(List<TGDFileInfo> lFPI) {
		for (TGDFileInfo pfi : lFPI) {
			if ((pfi != null) && !pfi.success) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return this.origFile.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TGDFileInfo) {
			TGDFileInfo d = (TGDFileInfo) obj;
			if ((this.origFile == null) || (d.origFile == null)) {
				return false;
			}
			return this.origFile.getName().equals(d.origFile.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.origFile == null) {
			return 0;
		}
		return this.origFile.getName().hashCode();
	}

	public static class TGDFileInfoDateComparator implements Comparator<TGDFileInfo> {

		@Override
		public int compare(TGDFileInfo tf1, TGDFileInfo tf2) {
			Date d1 = tf1.getDate();
			Date d2 = tf2.getDate();
			if ((d1 == null) && (d2 == null)) {
				return 0;
			}
			if (d1 == null) {
				return 1;
			}
			if (d2 == null) {
				return -1;
			}
			return d1.compareTo(d2);
		}
	}

	public static enum TGDFileType {
		TC, VU
	}
}
