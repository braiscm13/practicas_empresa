package com.opentach.client.modules.admin;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMWatchFolderCancelActionListener extends AbstractActionListenerButton {

	public IMWatchFolderCancelActionListener() throws Exception {
		super();
	}

	public IMWatchFolderCancelActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMWatchFolderCancelActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMWatchFolderCancelActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getInteractionManager().setUpdateMode();
		SwingUtilities.getWindowAncestor(this.getForm()).setVisible(false);
	}
}
