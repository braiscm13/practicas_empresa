package com.opentach.client.modules.data;

import javax.swing.tree.TreePath;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.tree.OTreeNode;
import com.utilmize.client.fim.UBasicFIM;

public class IMEmpresaCDO extends UBasicFIM {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.removeInsertListener();
		this.removeDeleteListener();
		this.insertListener = new InsertListener() {
			@Override
			protected void postCorrectInsert(EntityResult res, Entity ent) throws Exception {
				super.postCorrectInsert(res, ent);
				IMEmpresaCDO.this.managedForm.getDetailComponent().getTable().refresh();
				IMEmpresaCDO.this.refreshTree();
			}
		};
		this.deleteListener = new DeleteListener() {
			@Override
			protected void postCorrectDelete(EntityResult res, Entity ent) throws Exception {
				super.postCorrectDelete(res, ent);
				IMEmpresaCDO.this.managedForm.getDetailComponent().getTable().refresh();
				IMEmpresaCDO.this.refreshTree();
			}
		};
		Button bInsert = this.managedForm.getButton(InteractionManager.INSERT_KEY);
		if (bInsert != null) {
			bInsert.addActionListener(this.insertListener);
		}
		Button bDelete = this.managedForm.getButton(InteractionManager.DELETE_KEY);
		if (bDelete != null) {
			bDelete.addActionListener(this.deleteListener);
		}
	}

	protected void refreshTree() {
		TreePath tp = ((FormManager) this.formManager).getTree().getSelectionPath();
		if (tp != null) {
			TreePath tpaux = new TreePath(tp.getPath());
			OTreeNode nd = (OTreeNode) tpaux.getLastPathComponent();
			OTreeNode ndson = (OTreeNode) nd.getChildAt(0);
			if (ndson != null) {
				((FormManager) this.formManager).getTree().updatePath(tpaux.pathByAddingChild(ndson));
			}
		}
	}

}
