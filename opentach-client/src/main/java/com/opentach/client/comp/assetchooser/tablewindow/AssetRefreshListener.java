package com.opentach.client.comp.assetchooser.tablewindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.assetchooser.AssetComboDataField;

public class AssetRefreshListener implements ActionListener {

	/** The CONSTANT logger */
	private static final Logger		logger	= LoggerFactory.getLogger(AssetRefreshListener.class);

	protected AssetTableWindow		tableWindow;
	protected AssetComboDataField	parentDataField;

	public AssetRefreshListener(AssetTableWindow tableWindow, AssetComboDataField parentDataField) {
		super();
		this.tableWindow = tableWindow;
		this.parentDataField = parentDataField;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			this.tableWindow.getSelectedProvider().onRefreshRequested();
		} catch (Exception error) {
			AssetRefreshListener.logger.error(null, error);
			MessageManager.getMessageManager().showMessage(this.tableWindow, "interactionmanager.error_in_query", null, MessageType.ERROR, true);
		}
	}

}
