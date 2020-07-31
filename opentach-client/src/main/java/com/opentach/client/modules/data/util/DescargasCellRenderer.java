package com.opentach.client.modules.data.util;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;

public class DescargasCellRenderer implements CellRendererColorManager {

	private static final Color	TCCOLOR	= new Color(0x70, 0xBD, 0xE7);
	private static final Color	VUCOLOR	= Color.GRAY;

	public DescargasCellRenderer() {
		super();
	}

	@Override
	public Color getBackground(JTable jt, int row, int col, boolean selected) {
		TableColumn tcTipo = jt.getColumn("TIPO");
		int im = tcTipo.getModelIndex();
		Object f = jt.getModel().getValueAt(row, im);
		if ("TC".equals(f)) {
			return DescargasCellRenderer.TCCOLOR;
		} else if ("VU".equals(f)) {
			return DescargasCellRenderer.VUCOLOR;
		}
		return null;
	}

	@Override
	public Color getForeground(JTable jtable, int i, int j, boolean flag) {
		return null;
	}

}
