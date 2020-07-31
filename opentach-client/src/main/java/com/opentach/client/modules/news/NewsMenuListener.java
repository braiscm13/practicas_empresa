package com.opentach.client.modules.news;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class NewsMenuListener extends AbstractActionListenerMenuItem {

	private NewsDialog newsDialog = null;

	public NewsMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.newsDialog.showNews(true);
	}

	private void ensureLoad() {
		if (this.newsDialog == null) {
			this.newsDialog = new NewsDialog(ApplicationManager.getApplication().getFrame());
		}
	}
}
