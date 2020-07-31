package com.opentach.client.modules.tachoinfo;

import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.ObjectDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.fim.UBasicFIM;

public class IMTachoCodes extends UBasicFIM {

	@FormComponent(attr = "PARENT_ID")
	private ObjectDataField	idParentField;

	private Table			tableCountry;
	private Table			tableRegion;

	public IMTachoCodes() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.findTables();

		this.tableCountry.getJTable().getSelectionModel().addListSelectionListener(new CountryTableSelectionListener());
		this.tableCountry.getJTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.idParentField.addValueChangeListener(new IdParentValueChangeListener());
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.tableCountry.setEnabled(true);
		this.tableRegion.setEnabled(true);
		this.tableCountry.refreshInThread(0);
	}

	private void findTables() {
		List<Table> tables = FIMUtils.getTables(this.managedForm);
		for (Table table : tables) {
			if ("ETachoRegions".equals(table.getEntityName())) {
				if (table.getParentKeys().isEmpty()) {
					this.tableCountry = table;
				} else {
					this.tableRegion = table;
				}
			}
		}
	}

	class IdParentValueChangeListener implements ValueChangeListener {

		@Override
		public void valueChanged(ValueEvent e) {
			if (IMTachoCodes.this.idParentField.getValue() == null) {
				IMTachoCodes.this.tableRegion.deleteData();
			} else {
				IMTachoCodes.this.tableRegion.refreshInThread(0);
			}
		}

	}

	class CountryTableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (IMTachoCodes.this.tableCountry.getSelectedRows().length == 1) {
					IMTachoCodes.this.idParentField.setValue(((List) IMTachoCodes.this.tableCountry.getSelectedRowData().get("ID")).get(0));
				} else {
					IMTachoCodes.this.idParentField.setValue(null);
				}
			}
		}

	}
}

