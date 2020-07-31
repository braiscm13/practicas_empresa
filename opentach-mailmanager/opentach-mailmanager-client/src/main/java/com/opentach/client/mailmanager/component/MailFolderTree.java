package com.opentach.client.mailmanager.component;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.mailmanager.dto.MailFolder;
import com.opentach.common.mailmanager.services.IMailManagerService;

public class MailFolderTree extends JTree {

	public MailFolderTree() {
		super(new MailFolderTreeModel());
		this.setRootVisible(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addMouseListener(new MailFolderTreePopupMenuManager(this));
		this.setDropMode(DropMode.ON);
		this.setTransferHandler(new MailFolderTreeTransferHandler(this));
		this.setCellRenderer(new MailFolderTreeRenderer());
	}

	/**
	 * Gets the documentation model.
	 *
	 * @return the documentation model
	 */
	public MailFolderTreeModel getDocumentationModel() {
		return (MailFolderTreeModel) this.getModel();
	}

	public void deleteData() {
		this.getDocumentationModel().setCategoryTree(null);
		this.setSelectionRow(0);
		this.expandRow(0);
	}

	public void refreshModel() throws OpentachException {
		// consultamos el árbol
		MailFolder rootCategory = BeansFactory.getBean(IMailManagerService.class).getUserFolders();
		this.getDocumentationModel().setCategoryTree(rootCategory);

		// Not fire events or ignore it
		this.setSelectionRow(0);
		this.expandRow(0);
	}

}
