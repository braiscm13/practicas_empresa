package com.opentach.adminclient.labor.modules;

import java.awt.event.ActionEvent;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.OpentachBasicInteractionManager;

public class IMAgreement extends OpentachBasicInteractionManager {

	private static final Logger logger = LoggerFactory.getLogger(IMAgreement.class);

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		// Install delete action
		this.removeDeleteListener();
		this.deleteListener = new DeleteAction();
		if (this.managedForm.getButton("delete")!=null)
		this.managedForm.getButton("delete").addActionListener(this.deleteListener);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		if ((this.formManager instanceof FormManager) && (((FormManager) this.formManager).getTree() != null)) {
			((FormManager) this.formManager).getTree().expandRow(0);
		}
	}

	@Override
	public boolean checkDelete() {
		if (this.showDeleteConfirmMessage) {
			boolean resp = this.managedForm.question("M_UNSUBSCRIBE_AGREEMENT");
			if (resp == false) {
				return false;
			}
		}

		this.keysValues = this.getFormKeyValues();
		return true;
	}

	private class DeleteAction extends DeleteListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (IMAgreement.this.currentMode == InteractionManager.UPDATE) {
				if (IMAgreement.this.checkDelete() == true) {
					IMAgreement.this.managedForm.setDataFieldValue("AGR_F_BAJA", new Date());
					IMAgreement.this.managedForm.getButton("update").doClick();
				}
			}
		}

		@Override
		protected void postCorrectDelete(EntityResult result, Entity entity) throws Exception {

		}
	}
}
