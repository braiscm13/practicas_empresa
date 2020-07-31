package com.opentach.client.mailmanager.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.dto.Mail;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.form.IFormCloseListener;
import com.utilmize.client.gui.form.UFormExt;

public class IMMailSend extends UBasicFIM implements IFormCloseListener {

	private static final Logger logger = LoggerFactory.getLogger(IMMailSend.class);

	public IMMailSend() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		((UFormExt) form).addFormCloseListener(this);
	}

	@Override
	public void setInsertMode() {
		super.setUpdateMode();
		Mail mail = new Mail();
		try {
			Object maiId = BeansFactory.getBean(IMailManagerService.class).mailInsert(mail);
			this.managedForm.setDataFieldValue(MailManagerNaming.MAI_ID, maiId);
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, IMMailSend.logger);
		}
	}

	@Override
	public void onFormClosed() {
		//do nothing
	}

	@Override
	public void onFormWillBeClosed() {
		// do nothing
	}
}
