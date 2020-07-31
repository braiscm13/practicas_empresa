package com.opentach.client.company.fim;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.util.UserTools;
import com.opentach.common.user.IUserData;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMUsuarioEmp extends UBasicFIM {

	@FormComponent(attr = "USUARIO")
	private TextDataField			cUsuario;
	@FormComponent(attr = "PASSWORD")
	private PasswordDataField		cPassword;
	@FormComponent(attr = "NIVEL_CD")
	private UReferenceDataField	cNivel;

	@Override
	public void registerInteractionManager(Form formulario, IFormManager gestorForms) {
		super.registerInteractionManager(formulario, gestorForms);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (!UserTools.isSupervisor() && UserTools.isOperadorAvanzado() && !UserTools.isDistribuidor()) {
			this.managedForm.disableDataFields();
			this.cPassword.setEnabled(true);
		}
		if (this.cUsuario != null) {
			this.cUsuario.setEnabled(false);
		}
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		if (!UserTools.isSupervisor() && UserTools.isOperadorAvanzado() && !UserTools.isDistribuidor()) {
			this.cNivel.setValue(IUserData.NIVEL_EMPRESA, ValueEvent.PROGRAMMATIC_CHANGE);
			this.cNivel.setEnabled(false);
		}
	}
}