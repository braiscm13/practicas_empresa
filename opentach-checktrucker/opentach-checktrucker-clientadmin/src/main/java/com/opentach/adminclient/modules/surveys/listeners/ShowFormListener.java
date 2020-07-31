package com.opentach.adminclient.modules.surveys.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JDialog;

import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.util.ParseUtils;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

/**
 * The listener interface for receiving cancel events. The class that is interested in processing a cancel event implements this interface, and the
 * object created with that class is registered with a component using the component's <code>addCancelListener<code> method. When the cancel event
 * occurs, that object's appropriate method is invoked.
 *
 * @see CancelEvent
 */
public class ShowFormListener extends AbstractActionListenerButton {

	protected String	title;
	protected String	detailFormName;
	protected Form		detailForm;
	protected JDialog	detailDialog;

	/**
	 * Instantiates a new cancel listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public ShowFormListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.title = ParseUtils.getString((String) params.get("title"), null);
		this.detailFormName = ParseUtils.getString((String) params.get("detailformname"), null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		this.ensureDetailForm((Button) event.getSource());


		// TODO some required??
		// this.detailForm.deleteDataFields();
		// this.detailForm.getInteractionManager().setInitialState();
		// this.detailForm.enableButtons();

		this.detailDialog.setVisible(true);
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		super.interactionManagerModeChanged(interactionmanagermodeevent);
		this.getButton().setEnabled(true);
	}

	protected void ensureDetailForm(Button button) {
		if (this.detailForm == null) {
			this.detailForm = this.getFormManager().getFormCopy(this.detailFormName);

			this.detailDialog = this.detailForm.putInModalDialog(this.title, button);
		}

	}
}
