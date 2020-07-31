package com.opentach.client.comp;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JTable;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.table.CellRenderer;

public class TextComboCellRenderer extends CellRenderer {

	private final TextComboDataField	tdf;

	public TextComboCellRenderer(Hashtable params) {
		this.tdf = new TextComboDataField(params);
		this.tdf.setEditable(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			if (value == null) {
				label.setText(null);
			} else if (value instanceof String) {
				this.tdf.setValue(value);
				label.setText(this.tdf.getText() == null ? "" : ApplicationManager.getTranslation(this.tdf.getText()));
			} else {
				label.setText(value.toString());
			}
		}
		return c;
	}
}
