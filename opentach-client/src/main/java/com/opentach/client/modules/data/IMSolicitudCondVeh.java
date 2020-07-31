package com.opentach.client.modules.data;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;

public class IMSolicitudCondVeh extends OpentachBasicInteractionManager {

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		Button bInsert = form.getButton(InteractionManager.INSERT_KEY);
		if (bInsert != null) {
			bInsert.removeActionListener(this.insertListener);
			InsertListener il = new InsertListener() {
				@Override
				protected void postCorrectInsert(EntityResult result, Entity entity) throws Exception {
					super.postCorrectInsert(result, entity);
					IMSolicitudCondVeh.this.managedForm.message("M_SOLICITUD_ENVIADA", Form.INFORMATION_MESSAGE);
					Window w = SwingUtilities.getWindowAncestor(IMSolicitudCondVeh.this.managedForm);
					if (w instanceof JDialog) {
						w.setVisible(false);
					}
				}
			};
			bInsert.addActionListener(il);
			this.insertListener = il;
		}
	}

	@Override
	public void setInitialState() {
		this.setInsertMode();
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		Button b = this.managedForm.getButton("boton");
		if (b != null) {
			b.setEnabled(true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkInsert() {
		final boolean b = super.checkInsert();
		if (b) {
			try {
				IUserData ud = ((UserInfoProvider) this.formManager.getReferenceLocator()).getUserData();
				String companyCif = ud.getCIF();
				if (companyCif == null) {
					companyCif = this.chooseCompany(ud);
					if (companyCif == null) {
						return false;
					}
				}
				this.attributesValues.put(OpentachFieldNames.CG_CONTRATO_FIELD, ud.getActiveContract(companyCif));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return b;
	}

	private String chooseCompany(IUserData ud) {
		Object[] companyCifArray = ud.getCompaniesList().toArray();
		Object[] companyNamesArray = ud.getCompanyNameList().toArray();
		String message = ApplicationManager.getTranslation("M_SEL_COMPANY", this.managedForm.getResourceBundle());
		String title = ApplicationManager.getTranslation("T_SEL_COMPANY", this.managedForm.getResourceBundle());
		Object sel = JOptionPane.showInputDialog(this.managedForm, message, title, JOptionPane.QUESTION_MESSAGE, null, companyNamesArray,
				companyNamesArray[0]);
		if (sel != null) {
			for (int i = 0; i < companyNamesArray.length; i++) {
				if (sel.equals(companyNamesArray[i])) {
					return (String) companyCifArray[i];
				}
			}
		}
		return null;
	}
}
