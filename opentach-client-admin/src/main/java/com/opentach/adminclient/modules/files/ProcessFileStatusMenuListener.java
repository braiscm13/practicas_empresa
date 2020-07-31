package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class ProcessFileStatusMenuListener extends AbstractActionListenerMenuItem {

	private Form	form;

	/**
	 * Instantiates a new IM version news editor menu listener.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public ProcessFileStatusMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.menu.AbstractActionListenerMenuItem#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.form.getJDialog().setVisible(true);
	}

	/**
	 * Ensure load.
	 */
	private void ensureLoad() {
		if (this.form == null) {
			this.form = this.getFormManager("managerdummy").getFormCopy("files/formProcessFileStatus.xml");
			this.form.putInModalDialog("TITLE_PROCESS_FILE_STATUS", ApplicationManager.getApplication().getFrame());
			this.form.getJDialog().setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
			this.form.getJDialog().setModal(false);
		}
	}
}
