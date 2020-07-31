package com.opentach.client.mailmanager.im.attachments;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.utilmize.client.gui.field.UFileProviderDataField;
import com.utilmize.client.gui.field.UFileProviderDataField.DeleteButtonListener;
import com.utilmize.client.gui.list.ListComponent;

public class DeleteAttachmentListener extends DeleteButtonListener {

	private static final Logger logger = LoggerFactory.getLogger(DeleteAttachmentListener.class);

	public DeleteAttachmentListener(UFileProviderDataField parentField, Hashtable params) {
		super(parentField, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Form form = this.parentField.getParentForm();
		if (form.question(MailManagerNaming.M_SURE_TO_DELETE_FILE)) {
			try {
				BeansFactory.getBean(IMailManagerService.class).attachmentDelete(this.getMatId());
				((ListComponent) SwingUtilities.getAncestorOfClass(ListComponent.class, this.parentField)).refresh();
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(new OpentachException(MailManagerNaming.E_DELETING_ATTACHMENT, ex), DeleteAttachmentListener.logger);
			}
		}
	}

	protected Object getMatId() {
		return ((Map) this.parentField.getRealValue()).get(MailManagerNaming.MAT_ID);
	}

}