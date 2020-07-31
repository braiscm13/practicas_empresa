package com.opentach.client.dms.viewer;

import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.dms.DMSCategory;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.IDMSService;

/**
 * The Class DocumentationTree.
 */
public class DocumentationTree extends JTree {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DocumentationTree.class);
	private final boolean		readOnly;

	/**
	 * Instantiates a new documentation tree.
	 */
	public DocumentationTree() {
		this(new Hashtable<>());
	}

	public DocumentationTree(Hashtable<String, Object> params) {
		super(new DocumentationTreeModel(params));
		this.readOnly = ParseUtilsExtended.getBoolean(params.get("readonly"), false);
		this.setRootVisible(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		if (!this.isReadOnly()) {
			this.addMouseListener(new DocumentationTreePopupMenuManager(this));
			this.setDropMode(DropMode.ON);
			this.setTransferHandler(new DocumentationTreeTransferHandler(this));
		}
		this.setCellRenderer(new DocumentationTreeRenderer());
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * Gets the documentation model.
	 *
	 * @return the documentation model
	 */
	public DocumentationTreeModel getDocumentationModel() {
		return (DocumentationTreeModel) this.getModel();
	}

	public void deleteData() {
		this.getDocumentationModel().setCategoryTree(null, null);
		this.setSelectionRow(0);
		this.expandRow(0);
	}

	public void refreshModel(Serializable idDocument) throws Exception {
		if (idDocument == null) {
			this.deleteData();
			return;
		}
		// consultamos el árbol
		DMSCategory rootCategory = this.getDMSService().categoryGetForDocument(idDocument, null, this.getSessionId());
		this.getDocumentationModel().setCategoryTree(rootCategory, idDocument);

		// Not fire events or ignore it
		this.setSelectionRow(0);
		this.expandRow(0);
	}

	protected IDMSService getDMSService() throws DmsException {
		try {
			UserInfoProvider ocl = (UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator();
			return ocl.getRemoteService(IDMSService.class);
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}

	private int getSessionId() throws DmsException {
		try {
			return ApplicationManager.getApplication().getReferenceLocator().getSessionId();
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}
}
