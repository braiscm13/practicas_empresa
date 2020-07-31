package com.opentach.client.modules.inspec.util;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;

public class DriverCellRenderer implements CellRendererColorManager {

	private static final Color	ERRORCOLOR	= new Color(0xff, 0x66, 0x00);

	public DriverCellRenderer() {}

	@Override
	public Color getBackground(JTable jt, int row, int col, boolean selected) {
		TableColumn tcCond = jt.getColumn("DSCR_COND");
		int im = tcCond.getModelIndex();
		Object f = jt.getValueAt(row, im);
		if (f == null) {
			return DriverCellRenderer.ERRORCOLOR;
		}
		return null;
	}

	@Override
	public Color getForeground(JTable jt, int row, int col, boolean selected) {
		return null;
	}

}
