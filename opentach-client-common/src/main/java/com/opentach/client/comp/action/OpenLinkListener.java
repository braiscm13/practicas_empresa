package com.opentach.client.comp.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class OpenLinkListener extends AbstractActionListenerMenuItem {

	private static final Logger logger = LoggerFactory.getLogger(OpenLinkListener.class);
	private String				link;

	public OpenLinkListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	protected void init(Hashtable params) throws Exception {
		super.init(params);
		this.link = ParseUtilsExtended.getString((String) params.get("link"), null);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			Desktop.getDesktop().browse(new URI(this.link));
		} catch (Exception e1) {
			OpenLinkListener.logger.error(null, e1);
		}

	}

}
