package com.opentach.client.modules.version;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class VersionMenuListener extends AbstractActionListenerMenuItem {

	private VersionDialog versionDialog = null;

	public VersionMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.versionDialog.showNewsVersion();
		this.versionDialog.setVisible(true);
	}

	private void ensureLoad() {
		if (this.versionDialog == null) {
			this.versionDialog = new VersionDialog(ApplicationManager.getApplication().getFrame());
		}
	}
}
