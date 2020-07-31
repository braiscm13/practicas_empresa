package com.opentach.client.mailmanager.im;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.Table;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.listeners.UOpenTableInsertFormListener;

public class IMMailOutboxNewMailListener extends UOpenTableInsertFormListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMMailOutboxNewMailListener.class);


	public IMMailOutboxNewMailListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		super.actionPerformed(event);
	}

	@Override
	public void parentFormSetted() {
		// this.table = FIMUtils.getComponentsOfType(this.getForm(), MailTable.class).get(0);
		this.table = FIMUtils.getComponentsOfType(this.getForm(), Table.class).get(0);
		super.parentFormSetted();
	}

	@Override
	protected void considerToEnableButton() {
		this.getButton().setEnabled(true);
	}
}
