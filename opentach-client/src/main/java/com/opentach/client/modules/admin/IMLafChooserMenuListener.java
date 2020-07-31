package com.opentach.client.modules.admin;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class IMLafChooserMenuListener extends AbstractActionListenerMenuItem {

	private Form	lafForm;

	public IMLafChooserMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureLoad();
		this.lafForm.getJDialog().setVisible(true);
	}

	private void ensureLoad() {
		if (this.lafForm == null) {
			this.lafForm = this.getFormManager("lafChooser").getFormCopy("formLafChooser.xml");
			this.lafForm.putInModalDialog("TITLE_LAF_CHOOSER", ApplicationManager.getApplication().getFrame());
			this.lafForm.getJDialog().setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
			this.lafForm.getJDialog().setModal(true);
		}
	}
}
