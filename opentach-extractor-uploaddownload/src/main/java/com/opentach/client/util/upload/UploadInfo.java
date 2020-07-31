package com.opentach.client.util.upload;

import java.util.Hashtable;

import com.ontimize.gui.Form;
import com.opentach.client.util.TGDFileInfo;

/**
 * This class stores the info that will be uploaded
 *
 * @author rafael.lopez
 */
public class UploadInfo {
	private final TGDFileInfo	fileInfo;
	private final String		col;
	private final Hashtable		cv;
	private final Form			form;
	private final boolean		showProgress;
	private final String		uriSound;

	public UploadInfo(TGDFileInfo fileInfo, String col, Hashtable cv, Form form, boolean showProgress, String uriSound) {
		this.fileInfo = fileInfo;
		this.col = col;
		this.cv = cv;
		this.form = form;
		this.showProgress = showProgress;
		this.uriSound = uriSound;
	}

	public String getCol() {
		return this.col;
	}

	public Hashtable getCv() {
		return this.cv;
	}

	public TGDFileInfo getFileInfo() {
		return this.fileInfo;
	}

	public Form getForm() {
		return this.form;
	}

	public String getUriSound() {
		return this.uriSound;
	}

	public boolean isShowProgress() {
		return this.showProgress;
	}

	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof UploadInfo)) {
			return this.fileInfo.getOrigFile().getAbsolutePath().equals(((UploadInfo) o).fileInfo.getOrigFile().getAbsolutePath());
		}
		return false;
	}
}