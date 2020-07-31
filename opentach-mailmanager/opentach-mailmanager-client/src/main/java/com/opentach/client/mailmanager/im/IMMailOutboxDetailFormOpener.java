package com.opentach.client.mailmanager.im;

import java.util.Hashtable;

import com.ontimize.gui.Form;
import com.ontimize.gui.IDetailForm;
import com.utilmize.client.gui.UTabbedDetailForm;
import com.utilmize.client.gui.field.table.IDetailFormOpener;
import com.utilmize.client.gui.field.table.UTable;

public class IMMailOutboxDetailFormOpener implements IDetailFormOpener {
	private static final String	FORM_SEND	= "formMailSend.form";
	private static final String	FORM_SENT	= "formMailSent.form";

	public IMMailOutboxDetailFormOpener(Hashtable<?, ?> parameters) {
		super();
	}

	@Override
	public boolean openDetailForm(UTable table, int rowIndex) {
		Hashtable<?, ?> rowData = table.getRowData(rowIndex);
		boolean sent = "S".equals(rowData.get("MAI_SENT"));
		// Form form = table.getParentForm().getFormManager().getFormCopy(SENT?FORM_SENT:FORM_SEND);
		// form.putInModalDialog(table.getFormName(), table);
		// this.form.deleteDataFields();
		// BasicInteractionManager im = (BasicInteractionManager) this.form.getInteractionManager();
		// im.setUpdateMode();
		// this.form.setDataFieldValues(rowData);
		// this.form.setDataFieldValue("CIF", rowData.get("CIF"));
		// this.form.setDataFieldValue("IDCONDUCTOR", rowData.get("IDCONDUCTOR"));
		// this.form.setDataFieldValue("ID_SURVEY", rowData.get("ID_SURVEY"));
		// ((Table) this.form.getElementReference("ESurDNIDetailResponses")).refreshInThread(0);
		// this.form.getJDialog().setVisible(true);
		// return true;

		IDetailForm detailForm = this.createTabbedDetailForm(table, sent ? IMMailOutboxDetailFormOpener.FORM_SENT : IMMailOutboxDetailFormOpener.FORM_SEND);
		detailForm.setQueryInsertMode();
		Hashtable<?, ?> hFilterKeys = table.getParentKeyValues();
		detailForm.resetParentkeys(table.getParentKeys(true));
		detailForm.setParentKeyValues(hFilterKeys);
		detailForm.setKeys(table.getAttributesAndKeysData(), rowIndex);
		detailForm.setUpdateMode();
		detailForm.showDetailForm();
		return true;
	}

	public IDetailForm createTabbedDetailForm(UTable table, String sFormName) {
		final Form formCopy = table.getParentForm().getFormManager().getFormCopy(sFormName);
		if (formCopy != null) {
			final Hashtable<?, ?> filterKeys = table.getParentKeyValues();
			final Hashtable<Object, Object> hPrimaryKeys = new Hashtable<>();
			final IDetailForm detailForm = new UTabbedDetailForm(formCopy, hPrimaryKeys, table.getKeys(), table, filterKeys, table.codValues());
			return detailForm;
		}
		return null;
	}

	@Override
	public boolean openInsertForm(UTable table) {
		return false;
	}

}
