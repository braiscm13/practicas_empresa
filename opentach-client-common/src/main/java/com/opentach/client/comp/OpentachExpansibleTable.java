package com.opentach.client.comp;

import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.table.TableColumn;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.i18n.Internationalization;
import com.utilmize.client.gui.field.table.UExpansibleTable;

public class OpentachExpansibleTable extends UExpansibleTable {

	@SuppressWarnings("unchecked")
	public OpentachExpansibleTable(Hashtable params) throws Exception {
		super(params);
		this.orderEnabled = false;
		Hashtable av = new Hashtable();
		this.configureButtons(av);
	}

	@Override
	public void init(Hashtable params) throws Exception {
		super.init(params);
	}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);
		if (this.addButtons != null) {
			for (int i = 0; i < this.addButtons.size(); i++) {
				if (this.addButtons.get(i) instanceof Internationalization) {
					((Internationalization) this.addButtons.get(i)).setResourceBundle(resourceBundle);
				}
			}
		}
		if (this.buttonPlus != null) {
			((Internationalization) this.buttonPlus).setResourceBundle(resourceBundle);
		}
		if (this.buttonPlus2 != null) {
			((Internationalization) this.buttonPlus2).setResourceBundle(resourceBundle);
		}
		if (this.addComponents != null) {
			for (int i = 0; i < this.addComponents.size(); i++) {
				if (this.addComponents.get(i) instanceof Internationalization) {
					((Internationalization) this.addComponents.get(i)).setResourceBundle(resourceBundle);
				}
			}
		}
	}

	@Override
	public void addButtonToControls(AbstractButton button) {
		super.addButtonToControls(button);
		if (button instanceof Button) {
			((Button) button).setResourceBundle(this.getResourceBundle());
		}
	}

	@SuppressWarnings("unchecked")
	public Hashtable getAllValues() {
		if (this.originalValue == null) {
			return null;
		}
		Hashtable result = new Hashtable();
		for (Object key : this.originalValue.keySet()) {
			Vector vData = ((Vector) this.originalValue.get(key));
			if (vData != null) {
				result.put(key, vData);
			}
		}
		return result;
	}

	@Override
	public EntityResult getValueToExport(boolean calculatedRow, boolean translateHeader, boolean useNoStringKeys) {

		EntityResult rs = super.getValueToExport(calculatedRow, translateHeader, useNoStringKeys);
		TableColumn tc = this.table.getColumn(this.expandColumn);
		if (tc != null) {
			Object oText = translateHeader ? tc.getHeaderValue() : tc.getIdentifier().toString();
			// Object text = useNoStringKeys ? new KeyObject(oText) : oText;
			Object[] array = rs.keySet().toArray();
			for (int i = 0; i < array.length; i++) {
				if (array[i].toString().equals(oText)) {
					rs.remove(array[i]);
					List orderColumns = rs.getOrderColumns();
					orderColumns.remove(array[i]);
					rs.setColumnOrder(orderColumns);
				}
			}
		}
		return rs;
	}
}
