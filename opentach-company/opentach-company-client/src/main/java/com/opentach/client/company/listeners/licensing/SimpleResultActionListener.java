package com.opentach.client.company.listeners.licensing;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.ontimize.gui.IDetailForm;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class SimpleResultActionListener extends AbstractActionListenerButton {

	protected String valueToSet;

	public SimpleResultActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.valueToSet = ParseUtilsExtended.getString((String) params.get("result"), null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getForm().setDataFieldValue("RESULT", this.valueToSet);
		this.closeDetailForm();
	}

	/**
	 * Hide dialog.
	 */
	private void closeDetailForm() {
		if (this.getForm().getDetailComponent() != null) {
			IDetailForm detailComponent = this.getForm().getDetailComponent();
			detailComponent.hideDetailForm();
		} else if (this.getForm().getJDialog() != null) {
			this.getForm().getJDialog().setVisible(false);
		} else {
			Window w = SwingUtilities.getWindowAncestor(this.getForm());
			w.setVisible(false);
		}
	}
}
