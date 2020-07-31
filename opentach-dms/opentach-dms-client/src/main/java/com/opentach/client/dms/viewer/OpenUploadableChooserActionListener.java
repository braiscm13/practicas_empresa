package com.opentach.client.dms.viewer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.attachment.JDescriptionPanel;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.ParseUtils;
import com.opentach.client.dms.transfermanager.DmsTransfererManagerFactory;
import com.opentach.client.dms.transfermanager.AbstractDmsTransferable.Status;
import com.opentach.client.dms.upload.AbstractDmsUploadable;
import com.opentach.client.dms.upload.EJFileSaveLastDirectory;
import com.opentach.client.dms.upload.LocalDiskDmsUploadable;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class OpenUploadableChooserActionListener extends AbstractActionListenerButton {

	private static final Logger	logger			= LoggerFactory.getLogger(OpenUploadableChooserActionListener.class);

	public static final String	TRANSFERABLE	= "TRANSFERABLE";
	protected static Dimension	PREFERRED_SIZE	= new Dimension(350, 350);
	protected Form				formDialog		= null;
	protected String			documentationTableAttr;
	protected DocumentationTable		table			= null;
	protected EJFileSaveLastDirectory	fileChooser;

	public OpenUploadableChooserActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	public OpenUploadableChooserActionListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(null, formComponent, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.documentationTableAttr = ParseUtils.getString((String) params.get("documentationtable"), null);
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		final DocumentationTable table = this.getDocumentationTable();
		if (table != null) {
			table.addPropertyChangeListener("enable", new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (!(boolean) evt.getNewValue()) {
						OpenUploadableChooserActionListener.this.getButton().setEnabled(false);
					}
				}
			});
		}
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		try {
			AbstractDmsUploadable uploadable = this.acquireTransferable(ev);
			if (uploadable != null) {
				this.upload(uploadable);
			}
		} catch (Throwable error) {
			MessageManager.getMessageManager().showExceptionMessage(error, OpenUploadableChooserActionListener.logger);
		}
	}

	private AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {
		if (this.fileChooser == null) {
			this.fileChooser = new EJFileSaveLastDirectory();
			this.fileChooser.setMultiSelectionEnabled(false);
			this.fileChooser.setPanel(new JDescriptionPanel(this.getResourceBundle()));
			this.fileChooser.setAccessory(this.fileChooser.getPanel());
			this.fileChooser.setLocale(ApplicationManager.getLocale());
		}
		this.fileChooser.getPanel().clearValues();
		this.fileChooser.setSelectedFile(null);
		if (JFileChooser.APPROVE_OPTION == this.fileChooser.showOpenDialog(this.getForm())) {
			String description = this.fileChooser.getPanel().getDescription();
			Path file = this.fileChooser.getSelectedFile().toPath();
			if (file != null) {
				return new LocalDiskDmsUploadable(file, description);
			}
			return null;
		}
		return null;
	}

	protected void upload(AbstractDmsUploadable uploadable) throws DmsException {
		Serializable idDocument = (Serializable) this.getForm().getDataFieldValue(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		Serializable idFile = (Serializable) this.getForm().getDataFieldValue(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		Serializable idVersion = (Serializable) this.getForm().getDataFieldValue(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		Serializable idCategory = (Serializable) this.getForm().getDataFieldValue(DMSNaming.CATEGORY_ID_CATEGORY);
		final DocumentationTable table = this.getDocumentationTable();
		if (table != null) {
			idDocument = table.getCurrentIdDocument();
			idCategory = table.getCurrentIdCategory();
		}
		if (idDocument == null) {
			throw new DmsException(DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		}
		DocumentIdentifier docIdf = new DocumentIdentifier(idDocument, idFile, idVersion);
		uploadable.setDocumentIdentifier(docIdf);
		uploadable.setCategoryId(idCategory);
		if (table != null) {
			uploadable.addObserver(new Observer() {

				@Override
				public void update(Observable observable, Object arg) {
					CheckingTools.failIf(!(observable instanceof AbstractDmsUploadable), "Observable not instanceof AbstractDmsUploadable");
					AbstractDmsUploadable uploadable = (AbstractDmsUploadable) observable;
					if ((uploadable.getStatus()
							.equals(Status.COMPLETED)) && ((table.getCurrentIdDocument() != null) && table.getCurrentIdDocument().equals(uploadable.getDocumentIdentifier()
									.getDocumentId())) && ((table.getCurrentIdCategory() == null) || (table.getCurrentIdCategory().equals(uploadable.getCategoryId())))) {
						table.refreshInThread(0);
					}
				}
			});
		}
		DmsTransfererManagerFactory.getInstance().transfer(uploadable);
	}

	protected DocumentationTable getDocumentationTable() {
		if (this.table == null) {
			this.table = this.findDocumentationTable(this.documentationTableAttr);
		}
		return this.table;
	}

	public DocumentationTable findDocumentationTable(String attr) {
		if (attr != null) {
			for (Object component : this.getForm().getComponentList()) {
				if ((component instanceof DocumentationTable) && ((DocumentationTable) component).getAttribute().toString().equals(attr)) {
					return (DocumentationTable) component;
				}
			}
		}
		return null;
	}


	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		super.interactionManagerModeChanged(interactionmanagermodeevent);
		if (!this.getDocumentationTable().isEnabled()) {
			this.getButton().setEnabled(false);
		}
	}
}