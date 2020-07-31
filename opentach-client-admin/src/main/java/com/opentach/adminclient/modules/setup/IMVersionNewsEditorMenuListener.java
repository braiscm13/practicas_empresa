package com.opentach.adminclient.modules.setup;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

/**
 * The listener interface for receiving IMVersionNewsEditorMenu events. The class that is interested in processing a IMVersionNewsEditorMenu event
 * implements this interface, and the object created with that class is registered with a component using the component's
 * <code>addIMVersionNewsEditorMenuListener<code> method. When the IMVersionNewsEditorMenu event occurs, that object's appropriate method is invoked.
 *
 * @see IMVersionNewsEditorMenuEvent
 */
public class IMVersionNewsEditorMenuListener extends AbstractActionListenerMenuItem {

	/** The version form. */
	private Form	versionForm;

	/**
	 * Instantiates a new IM version news editor menu listener.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMVersionNewsEditorMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.menu.AbstractActionListenerMenuItem#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.versionForm.getJDialog().setVisible(true);
	}

	/**
	 * Ensure load.
	 */
	private void ensureLoad() {
		if (this.versionForm == null) {
			this.versionForm = this.getFormManager("versionNewsEditor").getFormCopy("formVersionNewsEditor.xml");
			this.versionForm.putInModalDialog("TITLE_VERSION_NEWS_EDITOR", ApplicationManager.getApplication().getFrame());
			this.versionForm.getJDialog().setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
			this.versionForm.getJDialog().setModal(true);
		}
	}

}
