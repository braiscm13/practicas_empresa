package com.opentach.adminclient.modules.surveys.listeners;

import java.util.Hashtable;

import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.utilmize.client.gui.field.table.IDetailFormOpener;
import com.utilmize.client.gui.field.table.UTable;

public class DetailFormOpenerDataFromTable implements IDetailFormOpener {
	protected Form form;

	public DetailFormOpenerDataFromTable(Hashtable<?, ?> parameters) {
		super();
		this.form = null;
	}

	@Override
	public boolean openDetailForm(UTable table, int row) {
		Hashtable rowData = table.getRowData(row);
		if (this.form == null) {
			this.form = table.getParentForm().getFormManager().getFormCopy(table.getFormName());
			this.form.putInModalDialog(table.getFormName(), table);
		}
		this.form.deleteDataFields();
		BasicInteractionManager im = (BasicInteractionManager) this.form.getInteractionManager();
		im.setUpdateMode();
		this.form.setDataFieldValues(rowData);
		this.form.setDataFieldValue("CIF", rowData.get("CIF"));
		this.form.setDataFieldValue("IDCONDUCTOR", rowData.get("IDCONDUCTOR"));
		this.form.setDataFieldValue("IDPERSONAL", rowData.get("IDPERSONAL"));
		this.form.setDataFieldValue("ID_SURVEY", rowData.get("ID_SURVEY"));
		((Table) this.form.getElementReference("ESurDNIDetailResponses")).refreshInThread(0);
		this.form.getJDialog().setVisible(true);
		return true;
	}

	@Override
	public boolean openInsertForm(UTable table) {
		return true;
	}
}
