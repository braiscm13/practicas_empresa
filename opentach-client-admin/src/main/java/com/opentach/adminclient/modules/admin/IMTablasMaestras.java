package com.opentach.adminclient.modules.admin;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ontimize.gui.Form;
import com.ontimize.gui.container.Tab;
import com.ontimize.gui.container.TabPanel;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.CellEditor;
import com.ontimize.gui.table.StringCellEditor;
import com.ontimize.gui.table.Table;
import com.utilmize.client.fim.UBasicFIM;

public class IMTablasMaestras extends UBasicFIM {

	private static final List<String>	TABLES_TO_NOT_REFRESH	= Arrays.asList(new String[] { "ETaskSubClassifier" });

	@com.ontimize.annotation.FormComponent(attr = "ETaskClassifier")
	private Table		tableTaskClassifier;

	@com.ontimize.annotation.FormComponent(attr = "ETaskSubClassifier")
	private Table		tableTaskSubClassifier;

	@com.ontimize.annotation.FormComponent(attr = "TKC_ID")
	private DataField	filterForTaskSubClassifier;

	@Override
	public void registerInteractionManager(Form f, IFormManager fm) {
		super.registerInteractionManager(f, fm);
		FormComponent tabpanel = f.getElementReference("TMTabPanel");
		if (tabpanel != null) {
			((TabPanel) tabpanel).addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					TabPanel tabpane = (TabPanel) e.getSource();
					Tab visibletab = (Tab) tabpane.getSelectedComponent();
					IMTablasMaestras.refreshTables(visibletab);
				}
			});
		}
		Table tbServerConfig = (Table) f.getElementReference("EPreferenciasServidor");
		if (tbServerConfig != null) {
			Hashtable<String, String> params = new Hashtable<>();
			params.put("uppercase", "no");
			params.put(CellEditor.COLUMN_PARAMETER, "VALOR");
			CellEditor ce = new StringCellEditor(params);
			tbServerConfig.setColumnEditor("VALOR", ce);
		}

		this.configureTaskClassifierTables();
	}

	private static void refreshTables(Container container) {
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof Table) {
				Table tb = (Table) components[i];
				if (tb.isEmpty()) {
					if (IMTablasMaestras.TABLES_TO_NOT_REFRESH.indexOf(tb.getEntityName()) >= 0) {
						continue;
					}
					tb.refresh();
				}
			} else if (components[i] instanceof Container) {
				IMTablasMaestras.refreshTables((Container) components[i]);
			}
		}
	}

	@Override
	public void setQueryInsertMode() {
		super.setUpdateMode();
		TabPanel tabpanel = (TabPanel) this.managedForm.getElementReference("TMTabPanel");
		if (tabpanel != null) {
			IMTablasMaestras.refreshTables((Container) tabpanel.getSelectedComponent());
		}

		this.tableTaskSubClassifier.setEnabled(false);
	}

	protected void configureTaskClassifierTables() {
		FilterSelectionListener listener = new FilterSelectionListener("TKC_ID", this.filterForTaskSubClassifier, this.tableTaskClassifier, this.tableTaskSubClassifier);
		this.tableTaskClassifier.getJTable().getSelectionModel().addListSelectionListener(listener);
	}

	public static class FilterSelectionListener implements ListSelectionListener {

		private final String		columnToSet;
		private final DataComponent	componentToSet;
		private final Table			table;
		private final Table			tableToUpdate;

		public FilterSelectionListener(String columnToSet, DataComponent componentToSet, Table table, Table tableToUpdate) {
			super();
			this.columnToSet = columnToSet;
			this.componentToSet = componentToSet;
			this.table = table;
			this.tableToUpdate = tableToUpdate;
		}

		@Override
		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting() && (this.table.getSelectedRows().length == 1)) {
				Map<?, ?> rowData = this.table.getRowData(this.table.getSelectedRow());
				Object value = null;
				if (rowData != null) {
					value = rowData.get(this.columnToSet);
				}
				this.componentToSet.setValue(value);
				if (value == null) {
					this.tableToUpdate.deleteData();
					this.tableToUpdate.getJTable().getSelectionModel().clearSelection();
				} else {
					// this.tableToUpdate.refreshInThread(0); //Ensured by paretkeylistener
				}
				this.tableToUpdate.setEnabled(value != null);
			} else {
				this.componentToSet.setValue(null);
				this.tableToUpdate.deleteData();
				this.tableToUpdate.getJTable().getSelectionModel().clearSelection();
				this.tableToUpdate.setEnabled(false);
			}
		}

	}
}
