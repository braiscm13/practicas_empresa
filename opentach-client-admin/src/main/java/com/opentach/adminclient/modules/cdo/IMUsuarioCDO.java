package com.opentach.adminclient.modules.cdo;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.Row;
import com.ontimize.gui.field.CheckDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.common.user.UserData;
import com.utilmize.client.fim.UBasicFIM;

/**
 * The Class IMUsuario.
 */
public class IMUsuarioCDO extends UBasicFIM {

	/** The c perfil. */
	@FormComponent(attr = "NIVEL_CD")
	private UReferenceDataField	cPerfil;

	/** The tb usu df emp. */
	@FormComponent(attr = "EUsuDfEmpUsuarios")
	private Table					tbUsuDfEmp;

	/** The emp. */
	@FormComponent(attr = "EMPRESAS")
	private Row						fEmp;

	/** The monit. */
	@FormComponent(attr = "MONIT")
	private CheckDataField			monit;

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.BasicInteractionManager#registerInteractionManager(com.ontimize.gui.Form, com.ontimize.gui.manager.IFormManager)
	 */
	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.BasicInteractionManager#checkInsert()
	 */
	@Override
	public boolean checkInsert() {
		if (!this.isMonit()) {
			int result = this.managedForm.message("M_NO_MONIT", Form.QUESTION_MESSAGE);
			if (result == Form.NO) {
				return false;
			}
		}
		return super.checkInsert();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.BasicInteractionManager#checkUpdate()
	 */
	@Override
	public boolean checkUpdate() {
		if (!this.isMonit()) {
			int result = this.managedForm.message("M_NO_MONIT", Form.QUESTION_MESSAGE);
			if (result == Form.NO) {
				return false;
			}
		}
		return super.checkUpdate();
	}

	/**
	 * Checks if is monit.
	 *
	 * @return true, if is monit
	 */
	private boolean isMonit() {
		try {
			if ("S".equals(this.monit.getValue())) {
				String level = (String) this.cPerfil.getValue();
				if (!UserData.isAnonymousUser(level)) {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
