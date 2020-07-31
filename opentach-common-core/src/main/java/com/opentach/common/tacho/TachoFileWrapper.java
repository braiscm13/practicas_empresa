package com.opentach.common.tacho;

import java.io.File;

import com.imatia.tacho.model.TachoFile;

public class TachoFileWrapper {

	private TachoFile	tachofile;
	private File		file;

	public TachoFileWrapper() {
		super();
	}

	public TachoFileWrapper(TachoFile tachofile, File file) {
		super();
		this.tachofile = tachofile;
		this.file = file;
	}

	public void setTachofile(TachoFile tachofile) {
		this.tachofile = tachofile;
	}

	public TachoFile getTachofile() {
		return this.tachofile;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}
}
