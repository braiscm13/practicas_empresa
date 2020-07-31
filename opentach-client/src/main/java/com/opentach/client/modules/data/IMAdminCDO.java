package com.opentach.client.modules.data;

import javax.swing.tree.TreePath;

import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.tree.Tree;
import com.opentach.client.comp.render.CompanyTreeCellRenderer;
import com.utilmize.client.fim.UBasicFIM;

public class IMAdminCDO extends UBasicFIM {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		if (((FormManager) gf).getTree() != null) {
			((FormManager) gf).getTree().setCellRenderer(new CompanyTreeCellRenderer());
		}
	}

	protected void hideTreeNode() {
		Tree tree = ((FormManager) this.formManager).getTree();
		if (tree != null) {
			TreePath pr = tree.getPathForRow(0).getParentPath();
			if (pr == null) {
				tree.expandPath(tree.getPathForRow(0));
			}
			tree.setRootVisible(false);
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.hideTreeNode();
	}
}
