package com.opentach.client.company.fim;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.tools.InsertTableAddButtonInstaller;
import com.opentach.common.company.naming.DelegationNaming;
import com.utilmize.client.fim.UBasicFIM;

public class IMDelegacion extends UBasicFIM {

	@FormComponent(attr = DelegationNaming.ENTITY)
	private Table usersDelegationTable;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		InsertTableAddButtonInstaller.setListener(this.usersDelegationTable, DelegationNaming.USUARIO);
	}
}
