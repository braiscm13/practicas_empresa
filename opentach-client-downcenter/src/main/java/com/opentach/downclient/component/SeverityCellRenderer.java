package com.opentach.downclient.component;

import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JTable;

import com.utilmize.client.gui.field.table.render.UXmlObjectCellRenderer;

public class SeverityCellRenderer extends UXmlObjectCellRenderer {

	public SeverityCellRenderer(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		Component comp = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);

		if (comp instanceof Component) {
			Color fg = comp.getForeground();

			// Customize colores based on values
			if ("MG".equals(value)) {
				fg = Color.decode("#d00000");
			} else if ("G".equals(value)) {
				fg = Color.orange;
			} else if ("L".equals(value)) {
				fg = Color.green;
			}

			comp.setForeground(fg);
		}
		return comp;
	}
}
