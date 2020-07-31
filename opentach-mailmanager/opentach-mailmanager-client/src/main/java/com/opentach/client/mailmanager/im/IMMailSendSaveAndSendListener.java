package com.opentach.client.mailmanager.im;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.IDetailForm;
import com.ontimize.gui.field.SubForm;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.ontimize.util.ParseUtils;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.dto.Mail;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.utilmize.client.gui.UDetailForm;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.tableform.UTableForm;

/**
 * The listener interface for receiving save events. The class that is interested in processing a save event implements this interface, and the object created with that class is
 * registered with a component using the component's <code>addSaveListener<code> method. When the save event occurs, that object's appropriate method is invoked.
 *
 * @see SaveEvent
 */
public class IMMailSendSaveAndSendListener extends AbstractActionListenerButton {

	protected static final int		TIME_TO_SHOW_MESSAGE_IN_STATUS	= 3000;

	/** The Constant logger. */
	protected static final Logger	logger							= LoggerFactory.getLogger(IMMailSendSaveAndSendListener.class);

	private boolean					doSend;

	/**
	 * Instantiates a new save listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMMailSendSaveAndSendListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#init(java.util.Map)
	 */
	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.doSend = ParseUtils.getBoolean((String) params.get("doSend"), true);
	}

	/**
	 * Hide dialog.
	 */
	protected void hideDialog() {
		this.getForm().getDetailComponent().hideDetailForm();
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			// Check pending unsaved data in "subforms" or "USplitDetailTable"
			boolean hasChanges = this.hasChanges();
			if (hasChanges && !this.getForm().question(UDetailForm.M_MODIFIED_DATA_SAVE_AND_LOST_CHANGES)) {
				return;
			}

			this.insertToServer(event);
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMMailSendSaveAndSendListener.logger);
		} finally {
			this.considerToEnableButton();
		}
	}

	private boolean hasChanges() {
		boolean hasChanges = false;
		for (Object o : this.getForm().getDataComponents()) {
			if (o instanceof UTableForm) {
				hasChanges = ((UTableForm) o).hasChanges();
				break;
			} else if (o instanceof SubForm) {
				hasChanges = !((SubForm) o).getForm().getInteractionManager().getModifiedFieldAttributes().isEmpty();
				break;
			}
		}
		return hasChanges;
	}

	/**
	 * Insert to server.
	 *
	 * @param event
	 *            the e
	 * @throws Exception
	 *             the exception
	 */
	private void insertToServer(ActionEvent event) throws Exception {
		boolean canInsert = true;
		if (this.getInteractionManager() instanceof BasicInteractionManager) {
			canInsert = this.getBasicInteractionManager().checkInsert();
		}
		if (!canInsert) {
			return;
		}
		Form form = this.getForm();
		try {
			form.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Mail mail = this.getValueToInsert();
			BeansFactory.getBean(IMailManagerService.class).mailUpdate(mail);
			if (this.doSend) {
				BeansFactory.getBean(IMailManagerService.class).mailSend(mail.getMaiId());
			}
			IDetailForm detailForm = form.getDetailComponent();
			detailForm.getTable().refreshInThread(0);
			this.hideDialog();
		} finally {
			form.setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Gets the attributes values to insert.
	 *
	 * @param generateKeys
	 *            the generate keys
	 * @return the attributes values to insert
	 */
	private Mail getValueToInsert() {
		Mail res = new Mail();
		res.setMaiId((BigDecimal) this.getForm().getDataFieldValue(MailManagerNaming.MAI_ID));
		// res.setMfdId(mfdId);
		res.setMaiSubject((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_SUBJECT));
		res.setMaiBody((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_BODY));
		res.setMaiTo((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_TO));
		res.setMaiCc((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_CC));
		// res.setMaiBcc(maiBcc);
		res.setCif((String) this.getForm().getDataFieldValue(MailManagerNaming.CIF));
		return res;
	}

	@Override
	protected boolean getEnableValueToSet() {
		return true;
	}

	@Override
	protected void considerToEnableButton() {
		this.getButton().setEnabled(true);
	}

}
