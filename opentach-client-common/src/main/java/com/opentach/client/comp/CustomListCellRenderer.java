package com.opentach.client.comp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import com.ontimize.plaf.utils.StyleUtil;

public class CustomListCellRenderer extends DefaultListCellRenderer {

	protected Color	foreground;
	protected Color	selectedForeground;

	public CustomListCellRenderer() {
		this.foreground = StyleUtil.getColor("CustomListCellRenderer", "textForeground", "#000000");
		this.selectedForeground = StyleUtil.getColor("CustomListCellRenderer", "[Selected].textForeground", "#ffffff");
	}


	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (listCellRendererComponent instanceof JComponent) {
			if (isSelected) {
				((JComponent) listCellRendererComponent).setForeground(this.selectedForeground);
			} else {
				((JComponent) listCellRendererComponent).setForeground(this.foreground);
			}
		}

		return listCellRendererComponent;
	}

}
