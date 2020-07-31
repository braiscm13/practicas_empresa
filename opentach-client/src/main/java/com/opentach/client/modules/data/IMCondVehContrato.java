package com.opentach.client.modules.data;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.common.OpentachFieldNames;

public class IMCondVehContrato extends OpentachBasicInteractionManager {

	public Table	tabla;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		String[] fields = { OpentachFieldNames.CG_CONTRATO_FIELD, OpentachFieldNames.CIF_FIELD };
		for (String field : fields) {
			f.setModifiable(field, false);
		}
		this.removeInsertListener();
		this.insertListener = new InsertListener() {
			@Override
			protected void postCorrectInsert(EntityResult res, Entity ent) throws Exception {
				super.postCorrectInsert(res, ent);
				if (IMCondVehContrato.this.tabla != null) {
					IMCondVehContrato.this.tabla.refresh();
				}
			}
		};
		f.getButton(InteractionManager.INSERT_KEY).addActionListener(this.insertListener);
	}

}
