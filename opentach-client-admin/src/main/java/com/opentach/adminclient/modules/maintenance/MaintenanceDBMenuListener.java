package com.opentach.adminclient.modules.maintenance;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

/**
 * The listener interface for receiving IMVersionNewsEditorMenu events. The class that is interested in processing a IMVersionNewsEditorMenu event
 * implements this interface, and the object created with that class is registered with a component using the component's
 * <code>addIMVersionNewsEditorMenuListener<code> method. When the IMVersionNewsEditorMenu event occurs, that object's appropriate method is invoked.
 *
 * @see IMVersionNewsEditorMenuEvent
 */
public class MaintenanceDBMenuListener extends AbstractActionListenerMenuItem {

	private MaintenanceDBFrame	maintenanceFrame	= null;

	/**
	 * Instantiates a new IM version news editor menu listener.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public MaintenanceDBMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.menu.AbstractActionListenerMenuItem#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.maintenanceFrame.setVisible(true);
	}

	/**
	 * Ensure load.
	 */
	private void ensureLoad() {
		if (this.maintenanceFrame == null) {
			this.maintenanceFrame = new MaintenanceDBFrame();
		}
	}

}
