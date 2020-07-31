package com.opentach.client.company.listeners;

import java.util.Hashtable;

import javax.swing.SwingUtilities;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.table.TableButton;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.common.company.naming.CompanyNaming;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.field.table.UTable;

public class CooperativeValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "IS_COOPERATIVA")
	protected DataField	isCooperativeDF;
	@FormComponent(attr = "CIF_COOPERATIVA")
	protected DataField	parentCooperativeDF;
	@FormComponent(attr = "CONTRATO_COOP")
	protected DataField	useCooperativeContractDF;
	@FormComponent(attr = CompanyNaming.ENTITY)
	protected UTable	subCompaniesTable;

	public CooperativeValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent e) {
		this.updateCooperativaFields();
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
		super.interactionManagerModeChanged(e);
		this.updateCooperativaFields();
	}

	private void updateCooperativaFields() {
		boolean isCooperative = ParseUtilsExtended.getBoolean(this.isCooperativeDF.getValue(), false);
		boolean usesCooperativeContract = ParseUtilsExtended.getBoolean(this.useCooperativeContractDF.getValue(), false);
		boolean hasParentCooperative = this.parentCooperativeDF.getValue() != null;
		this.isCooperativeDF.setEnabled(!usesCooperativeContract && !hasParentCooperative);
		this.parentCooperativeDF.setEnabled(!isCooperative && !usesCooperativeContract);
		this.useCooperativeContractDF.setEnabled(!isCooperative && hasParentCooperative);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				boolean pendingUnsavedChanges = CooperativeValueChangeListener.this.getForm().getButton("save").isEnabled();
				TableButton insertButton = (TableButton) CooperativeValueChangeListener.this.subCompaniesTable.getTableComponentReference("insertbutton");
				insertButton.setEnabled(!isCooperative && !hasParentCooperative && !pendingUnsavedChanges);
			}
		});
	}
}