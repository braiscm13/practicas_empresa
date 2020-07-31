package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.ObjectDataField;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.companies.ICompanyService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UFormHeaderButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMEmpresaMakeDemoCompanyFixActionListener extends AbstractActionListenerButton {

	@FormComponent(attr = "IS_DEMO")
	private ObjectDataField	isDemoField;

	public IMEmpresaMakeDemoCompanyFixActionListener() throws Exception {
		super();
	}

	public IMEmpresaMakeDemoCompanyFixActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMEmpresaMakeDemoCompanyFixActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmpresaMakeDemoCompanyFixActionListener(UFormHeaderButton button, Hashtable params) throws Exception {
		super(button, button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getForm().question("Q_DO_DEMO_FIXED")) {
			return;
		}
		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Object cif = IMEmpresaMakeDemoCompanyFixActionListener.this.getForm().getDataFieldValue("CIF");
				((OpentachClientLocator) IMEmpresaMakeDemoCompanyFixActionListener.this.getReferenceLocator()).getRemoteService(ICompanyService.class).makeDemoCompanyFix(cif,
						IMEmpresaMakeDemoCompanyFixActionListener.this.getSessionId());
				return null;
			}

			@Override
			protected void done() {
				IMEmpresaMakeDemoCompanyFixActionListener.this.getForm().refreshCurrentDataRecord();
			}
		}.executeOperation(this.getForm());

	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		this.isDemoField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				IMEmpresaMakeDemoCompanyFixActionListener.this.considerToEnableButton();
			}
		});
	}

	@Override
	protected boolean getEnableValueToSet() {
		Object newValue = this.isDemoField.getValue();
		return "S".equals(newValue);
	}

}
