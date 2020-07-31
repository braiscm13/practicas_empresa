package com.opentach.client.tasks.components.attachments;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.client.util.HessianFactory;
import com.opentach.common.tasks.ITaskAttachmentFilesHessianService;
import com.opentach.common.tasks.ITaskAttachmentService;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.field.UFileProviderDataField;
import com.utilmize.client.gui.field.UFileProviderDataField.DeleteButtonListener;
import com.utilmize.client.gui.field.UFileProviderDataField.IFileProvider;
import com.utilmize.client.gui.field.ULabel;
import com.utilmize.client.gui.list.ListComponent;
import com.utilmize.client.tools.UServiceManager;
import com.utilmize.tools.exception.UException;

public class AttachmentEntryComponent extends Row implements DataComponent {
	private final static Logger	logger		= LoggerFactory.getLogger(AttachmentEntryComponent.class);

	protected Object			value;

	// Panel descriptors --------------------------------
	protected Row				mainPanel;

	protected UFileProviderDataField	fileProviderField;

	protected ULabel					labelUser;

	protected ULabel					labelDesc;

	protected AttachmentEntryComponent(Hashtable parameters) {
		this((Object) parameters);
	}

	public AttachmentEntryComponent(Object value) {
		super(AttachmentEntryComponent.composeParams(value));
		AttachmentEntryComponent.this.setValue(value);
	}

	private static Hashtable composeParams(Object value) {
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		params.put("expand", "no");
		params.put("attr", System.currentTimeMillis());
		return params;
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);

		this.installComponents();
	}

	private void installComponents() {
		this.setLayout(new GridBagLayout());
		this.add(this.createMainPanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	private Component createMainPanel() {
		this.mainPanel = this.createRow(EntityResultTools.keysvalues("expand", "no"));

		this.fileProviderField = this.createObject(UFileProviderDataField.class, EntityResultTools.keysvalues("attr", "AttachmentEntry.entry", "dim", "no", "labelvisible", "no",
				"size", "40", "selectionbutton", "no", "rendercols", "TAT_NAME", "fileprovider", AttachmentFileProvider.class.getName(), "deletebutton.listener",
				DeleteAttachmentListener.class.getName()));
		this.fileProviderField.getDataField().setFocusable(false);
		this.mainPanel.add(this.fileProviderField,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));

		this.labelUser = this.createObject(ULabel.class, EntityResultTools.keysvalues("attr", "AttachmentEntry.user", "dim", "no"));
		this.mainPanel.add(this.labelUser,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.labelDesc = this.createObject(ULabel.class, EntityResultTools.keysvalues("attr", "AttachmentEntry.desc", "dim", "text"));
		this.mainPanel.add(this.labelDesc,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.mainPanel.add(new JPanel(),
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		return this.mainPanel;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// Builder UI utilities ////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Row createRow(Hashtable<Object, Object> hashtable) {
		return this.createObject(Row.class, hashtable);
	}

	private <T> T createObject(Class<? extends T> objectClass, Hashtable<Object, Object> hashtable) {
		Hashtable<Object, Object> parameters = DefaultXMLParametersManager.getParameters(objectClass.getName());
		if (hashtable != null) {
			parameters.putAll(hashtable);
		}
		try {
			return objectClass.getDeclaredConstructor(Hashtable.class).newInstance(parameters);
		} catch (Exception e) {
			AttachmentEntryComponent.logger.error(null, e);
			return null;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// Inner Methods ///////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setResourceBundle(ResourceBundle resources) {
		super.setResourceBundle(resources);
		this.fileProviderField.setResourceBundle(resources);
	}

	@Override
	public void setParentForm(Form f) {
		super.setParentForm(f);
		this.fileProviderField.setParentForm(f);
	}

	protected Form getParentForm() {
		return this.parentForm;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// DataComponent Methods //////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
		this.decorateFromValue();
	}

	private void decorateFromValue() {
		// Set values to subcomponents based on this.value
		Map<String, Object> values = (Map<String, Object>) this.value;
		String name = (String) ObjectTools.coalesce(values.get("TAT_NAME"), "");
		String user = "(" + (String) ObjectTools.coalesce(values.get("USUARIO"), "-") + ")";
		String desc = (String) ObjectTools.coalesce(values.get("TAT_DESCRIPTION"), "");

		this.fileProviderField.setValue(this.value);
		((JTextComponent) this.fileProviderField.getDataField()).setText(name);
		((JTextComponent) this.fileProviderField.getDataField()).setToolTipText(desc);

		this.labelUser.getLabel().setText(user);
		this.labelDesc.getLabel().setText(desc);
		this.labelDesc.getLabel().setToolTipText(desc);
	}

	@Override
	public void deleteData() {
		this.setValue(null);
	}

	@Override
	public boolean isEmpty() {
		return this.value == null;
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public void setModifiable(boolean modifiable) {
		// Do nothing
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public int getSQLDataType() {
		return 0;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void setRequired(boolean required) {
		// Do nothing
	}

	public static class AttachmentFileProvider implements IFileProvider {
		protected UFileProviderDataField field;

		public AttachmentFileProvider(UFileProviderDataField field) {
			this.field = field;
		}

		@Override
		public File getFile() throws UException, IOException {
			Object attachId = this.getFileId();
			if (attachId != null) {
				return this.getRemoteFile(attachId);
			}
			return null;
		}

		@Override
		public String getFilename() throws Exception {
			EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
			return ((ITaskAttachmentService) UServiceManager.getService(ITaskAttachmentService.ID)).getFileName(this.getFileId(), locator.getSessionId());
			// return ((ITaskAttachmentService) this.getService()).getFileName(this.getFileId(), locator.getSessionId());
		}

		public File getRemoteFile(Object fileId) {
			try {
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();

				InputStream is = this.getService().downloadFile(fileId, locator.getSessionId());
				if (is == null) {
					this.field.getParentForm().message("W_UNNAVAILABLE_FILE", JOptionPane.WARNING_MESSAGE);
					return null;
				}
				// Try to rename like original
				String fileName = this.getFilename();
				File tempFile = FileTools.toTemporaryFile(is, "." + "bla"/* this.parentField.getFilterExtension() */);
				if (tempFile != null) {
					try {
						File fcdst = new File(tempFile.getParentFile(), fileName);
						FileTools.moveFile(tempFile, fcdst);
						tempFile = fcdst;
					} catch (Exception ex) {
						// Nothing
					}
				}
				return tempFile;
			} catch (Exception ex) {
				AttachmentEntryComponent.logger.error("E_DOWNLOADING_FILE", ex);
				return null;
			}
		}

		protected Object getFileId() throws UException, IOException {
			Object o = this.field.getRealValue();
			if (o instanceof Map) {
				Object attachId = ((Map) o).get("TAT_ID");
				return attachId;
			}
			return null;
		}

		private ITaskAttachmentFilesHessianService getService() {
			return HessianFactory.getFactory().getService(ITaskAttachmentFilesHessianService.class, "/taskAttachment");
		}
	}

	public static class DeleteAttachmentListener extends DeleteButtonListener {

		public DeleteAttachmentListener(UFileProviderDataField parentField, Hashtable params) {
			super(parentField, params);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Form form = this.parentField.getParentForm();
			if (form.question("M_SURE_TO_DELETE_FILE")) {
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
				try {
					this.getService().deleteFile(this.getFileId(), locator.getSessionId());

					// refresh (delete this entry?)
					((ListComponent) SwingUtilities.getAncestorOfClass(ListComponent.class, this.parentField)).refresh();
				} catch (Exception ex) {
					AttachmentEntryComponent.logger.error("E_DELETING_ATTACHMENT", ex);
					this.parentField.getParentForm().message("E_DELETING_ATTACHMENT", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		protected Object getFileId() throws UException, IOException {
			Object o = this.parentField.getRealValue();
			if (o instanceof Map) {
				Object attachId = ((Map) o).get("TAT_ID");
				return attachId;
			}
			return null;
		}

		private ITaskAttachmentFilesHessianService getService() {
			return HessianFactory.getFactory().getService(ITaskAttachmentFilesHessianService.class, "/taskAttachment");
		}
	}
}
