package com.opentach.client.mailmanager.component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.mailmanager.dto.MailFolder;
import com.opentach.common.mailmanager.services.IMailManagerService;

/**
 * The Class DocumentationTreeTransferHandler.
 */
public class MailFolderTreeTransferHandler extends TransferHandler {

	/** The Constant serialVersionUID. */
	private static final long		serialVersionUID	= 1L;

	/** The Constant logger. */
	private static final Logger		logger				= LoggerFactory.getLogger(MailFolderTreeTransferHandler.class);

	/** The tree. */
	private final MailFolderTree	tree;

	/**
	 * Instantiates a new documentation tree transfer handler.
	 *
	 * @param tree
	 *            the tree
	 */
	public MailFolderTreeTransferHandler(MailFolderTree tree) {
		super(null);
		this.tree = tree;
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
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		for (DataFlavor flavor : transferFlavors) {
			if (MaiAttachmentTransferHandler.TRANSFER_HANLDER_HUMAN_ID.equals(flavor.getHumanPresentableName())) {
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
					ArrayList<Serializable> data = transferData.getData();
					TreePath pathForLocation = this.getTree().getPathForLocation(support.getDropLocation().getDropPoint().x, support.getDropLocation().getDropPoint().y);
					if (pathForLocation == null) {
						return false;
					}
					Object lastPathComponent = pathForLocation.getLastPathComponent();
					if (!(lastPathComponent instanceof MailFolder)) {
						return false;
					}

					MailFolder category = (MailFolder) lastPathComponent;
					BeansFactory.getBean(IMailManagerService.class).moveMailsToFolder(category.getMfdId(), data);

				} catch (Exception error) {
					MailFolderTreeTransferHandler.logger.error(null, error);
				}
				return true;
			}
		}
		return false;
	}
}