package com.opentach.client.alert.listeners;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.ontimize.gui.Form;
import com.ontimize.gui.TabbedDetailForm;
import com.opentach.common.alert.naming.AlertNaming;
import com.utilmize.client.gui.UTabbedFormManager;
import com.utilmize.client.gui.UTabbedFormManager.UFormTabbedPane;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

/**
 * Open form in insert mode with all values of this alert (tables ignored).
 */
public class CloneAlertListenerButton extends AbstractUpdateModeActionListenerButton {

	public CloneAlertListenerButton(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Form currentForm = this.getForm();
		Form parentForm = currentForm.getDetailComponent().getTable().getParentForm();
		parentForm.getButton("newrecord").doClick();

		// Get the last one
		UFormTabbedPane tabbedPane = ((UTabbedFormManager) this.getForm().getFormManager()).getTabbedPane();
		TabbedDetailForm detailForm = (TabbedDetailForm) tabbedPane.getComponent(tabbedPane.getComponentCount() - 1);

		// Get values to set
		Hashtable dataFieldValues = this.captureValuesToSet(currentForm);
		detailForm.getForm().setDataFieldValues(dataFieldValues);
	}

	protected Hashtable captureValuesToSet(Form currentForm) {
		Hashtable dataFieldValues = currentForm.getDataFieldValues(false);

		List<String> ignoreFields = this.getIgnoreFields();
		for (String s : ignoreFields) {
			dataFieldValues.remove(s);
		}

		return dataFieldValues;
	}

	protected List<String> getIgnoreFields() {
		return Arrays.asList(new String[] { AlertNaming.ALR_ID, AlertNaming.ALR_CODE });
	}

}
