package com.opentach.client.mailmanager.im.attachments;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.field.UFileProviderDataField;

public class AttachmentEntryComponent extends Row implements DataComponent {
	private static final Logger			logger	= LoggerFactory.getLogger(AttachmentEntryComponent.class);

	protected Object			value;

	// Panel descriptors --------------------------------
	protected Row				mainPanel;

	protected UFileProviderDataField	fileProviderField;

	protected AttachmentEntryComponent(Hashtable parameters) {
		this((Object) parameters);
	}

	public AttachmentEntryComponent(Object value) {
		super(AttachmentEntryComponent.composeParams(value));
		AttachmentEntryComponent.this.setValue(value);
	}

	private static Hashtable composeParams(Object value) {
		Hashtable<Object, Object> params = new Hashtable<>();
		params.put("expand", "no");
		params.put("attr", System.currentTimeMillis());
		return params;
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);

		this.installComponents();
	}

	protected void installComponents() {
		this.setLayout(new GridBagLayout());
		this.add(this.createMainPanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	protected Component createMainPanel() {
		this.mainPanel = this.createRow(EntityResultTools.keysvalues("expand", "no"));

		this.fileProviderField = this.createObject(UFileProviderDataField.class,
				EntityResultTools.keysvalues(//
						"attr", "AttachmentEntry.entry", //
						"dim", "text", //
						"labelvisible", "no", //
						"selectionbutton", "no", //
						"rendercols", MailManagerNaming.MAT_NAME,
						"fileprovider", AttachmentFileProvider.class.getName(), "deletebutton.listener",
						DeleteAttachmentListener.class.getName()));
		this.fileProviderField.getDataField().setFocusable(false);
		this.mainPanel.add(this.fileProviderField,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// this.mainPanel.add(new JPanel(),
		// new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		return this.mainPanel;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// Builder UI utilities ////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected Row createRow(Hashtable<Object, Object> hashtable) {
		return this.createObject(Row.class, hashtable);
	}

	protected <T> T createObject(Class<? extends T> objectClass, Hashtable<Object, Object> hashtable) {
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
		String name = (String) ObjectTools.coalesce(values.get(MailManagerNaming.MAT_NAME), "");

		this.fileProviderField.setValue(this.value);
		((JTextComponent) this.fileProviderField.getDataField()).setText(name);
		((JTextComponent) this.fileProviderField.getDataField()).setToolTipText(name);
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
}
