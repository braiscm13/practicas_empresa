package com.opentach.client.mailmanager.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.mailmanager.dto.MailAccount;
import com.opentach.common.mailmanager.dto.MailFolder;
import com.opentach.common.mailmanager.services.IMailManagerService;

/**
 * The Class DocumentationTreePopupMenuManager.
 */
public class MailFolderTreePopupMenuManager extends MouseAdapter {

	/** The Constant logger. */
	private static final Logger		logger	= LoggerFactory.getLogger(MailFolderTreePopupMenuManager.class);

	/** The tree. */
	private final MailFolderTree	tree;

	/** The current popup category. */
	private MailFolder				currentPopupCategory;

	private TreePath				currentTreePath;

	/** The popup. */
	private JPopupMenu				popup;
	private JMenuItem				menuItemAdd;
	private JMenuItem				menuItemDelete;

	/**
	 * Instantiates a new documentation tree popup menu manager.
	 *
	 * @param documentationTree
	 *            the documentation tree
	 */
	public MailFolderTreePopupMenuManager(MailFolderTree documentationTree) {
		super();
		this.tree = documentationTree;
	}

	/**
	 * Gets the tree.
	 *
	 * @return the tree
	 */
	protected MailFolderTree getTree() {
		return this.tree;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		if (SwingUtilities.isRightMouseButton(e)) {
			TreePath pathForLocation = this.getTree().getPathForLocation(e.getX(), e.getY());
			this.showPopup(pathForLocation, e.getX(), e.getY());
		}
	}

	/**
	 * Show popup.
	 *
	 * @param treePath
	 *            the tree path
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	protected void showPopup(TreePath treePath, int x, int y) {
		if (this.popup == null) {
			this.buildPopup();
		}

		this.currentTreePath = null;
		this.currentPopupCategory = null;
		this.menuItemAdd.setEnabled(false);
		this.menuItemDelete.setEnabled(false);
		if (treePath.getLastPathComponent() instanceof String) {
			this.menuItemAdd.setEnabled(true);
		} else if (treePath.getLastPathComponent() instanceof MailFolder) {
			this.currentTreePath = treePath;
			this.currentPopupCategory = (MailFolder) treePath.getLastPathComponent();
			if (this.currentPopupCategory.getMfdId() != null) {
				this.menuItemAdd.setEnabled(true);
				this.menuItemDelete.setEnabled(true);
			}
		}

		this.popup.show(this.getTree(), x, y);
	}

	/**
	 * Builds the popup.
	 */
	protected void buildPopup() {
		this.popup = new JPopupMenu("Menu");
		this.menuItemAdd = new JMenuItem(ApplicationManager.getTranslation("dms.newCategory"));
		this.menuItemAdd.addActionListener(new AddCategoryAction());

		this.menuItemDelete = new JMenuItem(ApplicationManager.getTranslation("dms.deleteCategory"));
		this.menuItemDelete.addActionListener(new DeleteCategoryAction());

		this.popup.add(this.menuItemAdd);
		this.popup.add(this.menuItemDelete);
	}

	/**
	 * The Class AddCategoryAction.
	 */
	protected class AddCategoryAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String categoryName = JOptionPane.showInputDialog(MailFolderTreePopupMenuManager.this.getTree(), ApplicationManager.getTranslation("dms.categorynameinput"));
				if (categoryName != null) {
					BigDecimal idParentCategory = MailFolderTreePopupMenuManager.this.currentPopupCategory == null ? null : MailFolderTreePopupMenuManager.this.currentPopupCategory
							.getMfdId();
					BigDecimal idNewCategory = BeansFactory.getBean(IMailManagerService.class).folderUserInsert(categoryName, idParentCategory);
					MailAccount userMailAccount = BeansFactory.getBean(IMailManagerService.class).getUserMailAccount();
					MailFolder newCategory = new MailFolder(idNewCategory, userMailAccount.getMacId(), categoryName, null,
							MailFolderTreePopupMenuManager.this.currentPopupCategory);
					MailFolderTreePopupMenuManager.this.tree.getDocumentationModel().insertCategory(MailFolderTreePopupMenuManager.this.currentPopupCategory, newCategory);
				}
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, MailFolderTreePopupMenuManager.logger);
			}
		}
	}

	/**
	 * The Class AddCategoryAction.
	 */
	protected class DeleteCategoryAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (JOptionPane.showConfirmDialog(MailFolderTreePopupMenuManager.this.getTree(), ApplicationManager.getTranslation("dms.questiondeletecategory"),
						ApplicationManager.getTranslation("dms.questiondeletecategorytitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
					return;
				}
				BeansFactory.getBean(IMailManagerService.class).mailFolderUserDelete(MailFolderTreePopupMenuManager.this.currentPopupCategory.getMfdId());
				MailFolderTreePopupMenuManager.this.getTree().refreshModel();
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, MailFolderTreePopupMenuManager.logger);
			}
		}
	}
}
