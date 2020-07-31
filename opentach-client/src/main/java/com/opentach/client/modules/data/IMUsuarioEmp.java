package com.opentach.client.modules.data;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.IDetailForm;
import com.ontimize.gui.InteractionManager;
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
		if (!this.isDemoCompany() && UserTools.isAgente()) {
			this.setDataFieldsVisible(false, InteractionManager.INSERT_KEY, InteractionManager.DELETE_KEY, InteractionManager.UPDATE_KEY);
			this.managedForm.disableDataFields();
		}
		if (this.isDemoCompany() && UserTools.isAgente()) {
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
		if (!this.isDemoCompany() && UserTools.isAgente()) {
			this.setDataFieldsVisible(false, InteractionManager.INSERT_KEY, InteractionManager.DELETE_KEY, InteractionManager.UPDATE_KEY);
		}
		if (this.isDemoCompany() && UserTools.isAgente()) {
			this.cNivel.setValue(IUserData.NIVEL_EMPRESA);
			this.cNivel.setEnabled(false);
		}
	}


	private boolean isDemoCompany() {
		IDetailForm detailComponent = this.managedForm.getDetailComponent();
		if (detailComponent != null) {
			return "formEmpresaAgenteDemo.xml".equals(detailComponent.getTable().getParentForm().getArchiveName());
		}
		return false;
	}

	@Override
	public boolean checkInsert() {
		boolean resul = super.checkInsert();
		if (resul) {
			this.attributesValues.put("INSERT_RELATION", Boolean.TRUE);
		}
		return resul;
	}

	@Override
	public boolean checkUpdate() {
		boolean resul = super.checkUpdate();
		if (resul) {
			this.attributesValues.put("UPDATE_RELATION", Boolean.TRUE);
		}
		return resul;
	}

}
