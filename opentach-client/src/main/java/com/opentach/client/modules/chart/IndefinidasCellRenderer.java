package com.opentach.client.modules.chart;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;

public class IndefinidasCellRenderer implements CellRendererColorManager {

	private static final Color	CFYELLOW	= new Color(0xff, 0xff, 0x00);

	private static final Color	CFGREEN		= new Color(0x55, 0xd5, 0x00);
	private static final Color	CFBLUE		= new Color(0x51, 0x8e, 0xff);

	private static final Color	CFCOLOR		= new Color(0xff, 0xff, 0x9f);
	private static final Color	ERRORCOLOR	= new Color(0xff, 0xa9, 0x70);
	private static final Color	COLOR_BLUE	= new Color(0xa8, 0xda, 0xff);

	public IndefinidasCellRenderer() {}

	@Override
	public Color getBackground(JTable jt, int row, int col, boolean selected) {
		TableColumn tcTipo = jt.getColumn("DSCR_ACT");
		int im = tcTipo.getModelIndex();
		Object f = jt.getValueAt(row, im);

		TableColumn tcMinutos = jt.getColumn("MINUTOS");
		int imMinutos = tcMinutos.getModelIndex();
		Object fMinutos = jt.getValueAt(row, imMinutos);

		if (selected) {
			return null;
		}

		if (("PAUSA/DESCANSO".equals(f)) && (1440 <= ((Long) fMinutos).intValue())) {
			return IndefinidasCellRenderer.CFYELLOW;
		} else if ("PAUSA/DESCANSO".equals(f)) {
			return IndefinidasCellRenderer.CFCOLOR;
		} else if ("DISPONIBILIDAD".equals(f)) {
			return IndefinidasCellRenderer.CFGREEN;
		} else if ("TRABAJO".equals(f)) {
			return IndefinidasCellRenderer.CFBLUE;
		} else {
			return null;
		}
	}

	@Override
	public Color getForeground(JTable jt, int row, int col, boolean selected) {
		return null;
	}

}
