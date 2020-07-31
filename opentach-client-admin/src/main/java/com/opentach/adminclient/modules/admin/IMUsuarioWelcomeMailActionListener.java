package com.opentach.adminclient.modules.admin;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.user.IUserService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMUsuarioWelcomeMailActionListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMUsuarioWelcomeMailActionListener.class);

	public IMUsuarioWelcomeMailActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (this.getForm().getButton("save").isEnabled()) {
			MessageManager.getMessageManager().showMessage(this.getForm(), "SAVE_FIRST", MessageType.ERROR, new Object[] {});
			return;
		}
		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				((OpentachClientLocator) IMUsuarioWelcomeMailActionListener.this.getReferenceLocator()).getRemoteService(IUserService.class)
				.sendWelcomeMail((String) IMUsuarioWelcomeMailActionListener.this.getForm().getDataFieldValue("USUARIO"),
						IMUsuarioWelcomeMailActionListener.this.getSessionId());
				return null;
			}

			@Override
			protected void done() {
				try {
					this.uget();
					MessageManager.getMessageManager().showMessage(IMUsuarioWelcomeMailActionListener.this.getForm(), "MAIL_SENT", MessageType.INFORMATION, new Object[] {}, false);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMUsuarioWelcomeMailActionListener.logger);
				}

			}
		}.executeOperation(this.getForm());
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		super.interactionManagerModeChanged(interactionmanagermodeevent);
		this.getButton().setEnabled(true);
	}
}
