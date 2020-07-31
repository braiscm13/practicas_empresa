package com.opentach.client.dms.viewer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.dms.tools.DataWrapper;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.dms.DMSCategory;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.IDMSService;

/**
 * The Class DocumentationTreeTransferHandler.
 */
public class DocumentationTreeTransferHandler extends TransferHandler {

	/** The Constant serialVersionUID. */
	private static final long		serialVersionUID	= 1L;

	/** The Constant logger. */
	private static final Logger		logger				= LoggerFactory.getLogger(DocumentationTreeTransferHandler.class);

	/** The tree. */
	private final DocumentationTree	tree;

	/**
	 * Instantiates a new documentation tree transfer handler.
	 *
	 * @param tree
	 *            the tree
	 */
	public DocumentationTreeTransferHandler(DocumentationTree tree) {
		super(null);
		this.tree = tree;
	}

	/**
	 * Gets the tree.
	 *
	 * @return the tree
	 */
	protected DocumentationTree getTree() {
		return this.tree;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		for (DataFlavor flavor : transferFlavors) {
			if (DocumentationTableTransferHandler.TRANSFER_HANLDER_HUMAN_ID.equals(flavor.getHumanPresentableName())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean importData(TransferSupport support) {
		Transferable transferable = support.getTransferable();
		for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
			if ("filesToMove".equals(flavor.getHumanPresentableName())) {
				try {
					DataWrapper<ArrayList<Serializable>> transferData = (DataWrapper<ArrayList<Serializable>>) transferable.getTransferData(flavor);
					Vector<Serializable> data = new Vector<>(transferData.getData());
					TreePath pathForLocation = this.getTree().getPathForLocation(support.getDropLocation().getDropPoint().x,
							support.getDropLocation().getDropPoint().y);
					if (pathForLocation == null) {
						return false;
					}
					Object lastPathComponent = pathForLocation.getLastPathComponent();
					if (!(lastPathComponent instanceof DMSCategory)) {
						return false;
					}

					DMSCategory category = (DMSCategory) lastPathComponent;
					this.getDMSService().moveFilesToCategory(category.getIdCategory(), data, this.getSessionId());

				} catch (Exception error) {
					DocumentationTreeTransferHandler.logger.error(null, error);
				}
				return true;
			}
		}
		return false;
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