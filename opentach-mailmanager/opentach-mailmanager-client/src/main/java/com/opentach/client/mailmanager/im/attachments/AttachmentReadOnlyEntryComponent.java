package com.opentach.client.mailmanager.im.attachments;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.utilmize.client.gui.field.UFileProviderDataField;

public class AttachmentReadOnlyEntryComponent extends AttachmentEntryComponent {
	private static final Logger			logger	= LoggerFactory.getLogger(AttachmentReadOnlyEntryComponent.class);

	protected AttachmentReadOnlyEntryComponent(Hashtable parameters) {
		super(parameters);
	}

	public AttachmentReadOnlyEntryComponent(Object value) {
		super(value);
	}


	@Override
	protected Component createMainPanel() {
		this.mainPanel = this.createRow(EntityResultTools.keysvalues("expand", "no"));

		this.fileProviderField = this.createObject(UFileProviderDataField.class,
				EntityResultTools.keysvalues(//
						"attr", "AttachmentEntry.entry", //
						"dim", "text", //
						"labelvisible", "no", //
						"selectionbutton", "no", //
						"deletebutton", "no", //
						"rendercols", MailManagerNaming.MAT_NAME,
						"fileprovider", AttachmentFileProvider.class.getName(), "deletebutton.listener",
						DeleteAttachmentListener.class.getName()));
		this.fileProviderField.getDataField().setFocusable(false);
		this.mainPanel.add(this.fileProviderField,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		return this.mainPanel;
	}

}
