package com.opentach.client.modules.links;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class LinksMenuListener extends AbstractActionListenerMenuItem {

	private LinksDialog linksDialog = null;

	public LinksMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.linksDialog.showLinks();
		this.linksDialog.setVisible(true);
	}

	private void ensureLoad() {
		if (this.linksDialog == null) {
			this.linksDialog = new LinksDialog(ApplicationManager.getApplication().getFrame());
		}
	}
}
