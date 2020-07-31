package com.opentach.adminclient.modules.surveys.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.button.Button;
import com.opentach.client.comp.OpentachFormExt;
import com.utilmize.client.gui.buttons.UButton;

/**
 * The listener interface for receiving cancel events. The class that is interested in processing a cancel event implements this interface, and the
 * object created with that class is registered with a component using the component's <code>addCancelListener<code> method. When the cancel event
 * occurs, that object's appropriate method is invoked.
 *
 * @see CancelEvent
 */
public class ShowNewQuestionFormListener extends ShowFormListener {

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
	public ShowNewQuestionFormListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		this.ensureDetailForm((Button) event.getSource());

		// My behaviour
		Object idSurvey = this.getForm().getDataFieldReference("ID_SURVEY").getValue();
		this.detailForm.setDataFieldValue("ID_SURVEY", idSurvey);
		((OpentachFormExt) this.detailForm).setParentForm(((Button) event.getSource()).getParentForm());

		this.detailDialog.setVisible(true);
	}
}
