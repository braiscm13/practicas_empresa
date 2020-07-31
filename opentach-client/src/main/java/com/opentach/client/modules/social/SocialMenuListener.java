package com.opentach.client.modules.social;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class SocialMenuListener extends AbstractActionListenerMenuItem {

	private SocialDialog socialDialog = null;

	public SocialMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.socialDialog.showSocialNetworks();
	}

	private void ensureLoad() {
		if (this.socialDialog == null) {
			this.socialDialog = new SocialDialog(ApplicationManager.getApplication().getFrame());
		}
	}
}
