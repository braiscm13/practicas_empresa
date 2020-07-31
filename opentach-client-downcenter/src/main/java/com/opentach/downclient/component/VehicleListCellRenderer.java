package com.opentach.downclient.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.ontimize.plaf.utils.StyleUtil;
import com.utilmize.client.gui.field.table.render.UXmlObjectCellRenderer;

public class VehicleListCellRenderer extends UXmlObjectCellRenderer {


	protected Font	font;
	protected Color	foreground;


	public VehicleListCellRenderer(Hashtable params) throws Exception {
		super(params);
		this.font = StyleUtil.getFont("VehicleListCellRenderer", "font", "Verdana-BOLD-18");
		this.foreground = StyleUtil.getColor("VehicleListCellRenderer", "foreground", "#ffffff");

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		Component listCellRendererComponent = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);

		if (listCellRendererComponent instanceof JComponent) {
			((JComponent) listCellRendererComponent).setFont(this.font);
			((JComponent) listCellRendererComponent).setForeground(this.foreground);
			((JLabel) listCellRendererComponent).setHorizontalAlignment(SwingConstants.CENTER);
		}

		return listCellRendererComponent;
	}

}
