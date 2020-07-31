package com.opentach.common.tacho;

import java.io.File;


public final class TachoFileStore {

	public final static String		TACHOFILE_STORE			= "tgdfile.tgdstore";
	public final static String		TACHOFILE_SAVE2STORE	= "tgdfile.autosave2store";

	private File					TGDStore;

	public void setTGDStore(File store) {
		this.TGDStore = store;
	}

	public File getTGDStore() {
		return this.TGDStore;
	}

}
