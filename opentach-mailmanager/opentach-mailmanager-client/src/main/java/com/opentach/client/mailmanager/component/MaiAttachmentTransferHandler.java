package com.opentach.client.mailmanager.component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.mailmanager.MailManagerNaming;

/**
 * The Class DocumentationTableTransferHandler.
 */
public class MaiAttachmentTransferHandler extends TransferHandler {

	/** The Constant logger. */
	private static final Logger		logger						= LoggerFactory.getLogger(MaiAttachmentTransferHandler.class);

	/** The Constant serialVersionUID. */
	private static final long		serialVersionUID			= 1L;

	/** The Constant TRANSFER_HANLDER_HUMAN_ID. */
	public static final String		TRANSFER_HANLDER_HUMAN_ID	= "filesToMove";

	/** The table. */
	private final MailTable	table;

	/**
	 * Instantiates a new documentation table transfer handler.
	 *
	 * @param table
	 *            the table
	 */
	public MaiAttachmentTransferHandler(MailTable table) {
		super(null);
		this.table = table;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	protected MailTable getTable() {
		return this.table;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		ArrayList<Object> fileIds = new ArrayList<>();
		for (int row : this.getTable().getJTable().getSelectedRows()) {
			fileIds.add(this.getTable().getJTable().getValueAt(row, this.getTable().getColumnIndex(MailManagerNaming.MAI_ID)));
		}

		return new DataWrapperTransferable<>(fileIds, this.getTable(), MaiAttachmentTransferHandler.TRANSFER_HANLDER_HUMAN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected void exportDone(JComponent c, Transferable transferable, int act) {
		if (act == TransferHandler.MOVE) {
			for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
				if (MaiAttachmentTransferHandler.TRANSFER_HANLDER_HUMAN_ID.equals(flavor.getHumanPresentableName())) {
					this.exportDoneFlavor(transferable, flavor);
				}
			}
		}
	}

	private void exportDoneFlavor(Transferable transferable, DataFlavor flavor) {
		try {
			DataWrapper<ArrayList<Object>> transferData = (DataWrapper<ArrayList<Object>>) transferable.getTransferData(flavor);
			ArrayList<Object> data = transferData.getData();
			for (Object idDocumentFileVersion : data) {
				Map<String, Object> kv = new Hashtable<>();
				kv.put(MailManagerNaming.MAI_ID, idDocumentFileVersion);
				int index = this.getTable().getRowForKeys((Hashtable) kv);
				this.getTable().deleteRow(index);
			}

		} catch (Exception error) {
			MaiAttachmentTransferHandler.logger.error(null, error);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport support) {
		DataFlavor[] dataFlavors = support.getDataFlavors();
		for (DataFlavor df : dataFlavors) {
			if (df.getMimeType().toLowerCase().startsWith("application/x-java-file")) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean importData(JComponent comp, Transferable t) {
		Serializable idCategory = this.getTable().getCurrentIdCategory();
		boolean someImported = false;
		for (DataFlavor df : t.getTransferDataFlavors()) {
			if (!df.getMimeType().toLowerCase().startsWith("application/x-java-file")) {
				continue;
			}
			someImported |= this.importDataFlavor(t, df, idCategory);
		}
		return someImported;
	}

	private boolean importDataFlavor(Transferable t, DataFlavor df, Serializable idCategory) {
		boolean someImported = false;
		try {
			List<Path> transferData = FileTools.toPath((List<File>) t.getTransferData(df));
			String description = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.descriptioninput"));
			for (Path file : transferData) {
				// LocalDiskDmsUploadable transferable = new LocalDiskDmsUploadable(file, description, docIdf, idCategory);
				// transferable.addObserver(new Observer() {
				//
				// @Override
				// public void update(Observable observable, Object arg) {
				// // TODO intentar sólo añadir la información de la nueva fila sin necesidad de refrescar toda la tabla
				// CheckingTools.failIf(!(observable instanceof AbstractDmsUploadable), "observable not instnaceof AbstractDmsUploadable");
				// AbstractDmsUploadable uploadable = (AbstractDmsUploadable) observable;
				// if (uploadable.getStatus()
				// .equals(Status.COMPLETED) && (MailFolderTableTransferHandler.this.table.getCurrentIdDocument() != null) && MailFolderTableTransferHandler.this.table
				// .getCurrentIdDocument()
				// .equals(uploadable.getDocumentIdentifier()
				// .getDocumentId()) && ((MailFolderTableTransferHandler.this.table
				// .getCurrentIdCategory() == null) || MailFolderTableTransferHandler.this.table.getCurrentIdCategory()
				// .equals(uploadable.getCategoryId()))) {
				// MailFolderTableTransferHandler.this.table.refreshInThread(0);
				// }
				// }
				// });
				// DmsTransfererManagerFactory.getInstance().transfer(transferable);
				someImported = true;
			}
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, MaiAttachmentTransferHandler.logger);
		}
		return someImported;
	}
}