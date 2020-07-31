package com.opentach.client.comp.render;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.plaf.utils.StyleUtil;
import com.utilmize.client.gui.field.table.UTable;

public class PeriodosCellRenderer implements CellRendererColorManager {

	public static final Color	ERRORCOLOR	= StyleUtil.getColor("PeriodosCellRenderer", "errorColor", "#f98d4c");
	public static final Color	CFCOLOR		= StyleUtil.getColor("PeriodosCellRenderer", "cfColor", "#fcfe56");
	public static final Color	NORMALCOLOR	= StyleUtil.getColor("PeriodosCellRenderer", "normal", "#f5f4f4");


	public PeriodosCellRenderer() {
		super();
	}
	public PeriodosCellRenderer(UTable table, Hashtable params) {
		super();
	}

	@Override
	public Color getBackground(JTable jt, int row, int col, boolean selected) {
		TableColumn tcError = jt.getColumn("ERROR");
		TableColumn tcTipo = jt.getColumn("TPPERIODO");
		int im = tcError.getModelIndex();
		Object f = jt.getValueAt(row, im);
		if ("S".equals(f)) {
			return PeriodosCellRenderer.ERRORCOLOR;
		} else if (this.isNoContinuidadFecha(jt, tcTipo, row)) {
			return PeriodosCellRenderer.CFCOLOR;
		} else {
			return PeriodosCellRenderer.NORMALCOLOR;
		}
	}

	@Override
	public Color getForeground(JTable jt, int row, int col, boolean selected) {
		TableColumn tcTipo = jt.getColumn("TPPERIODO");
		if (selected && this.isNoContinuidadFecha(jt, tcTipo, row)) {
			return Color.RED;
		}
		return null;
	}

	private final boolean isNoContinuidadFecha(JTable jt, TableColumn tcTipo, int row) {
		int cn = tcTipo.getModelIndex();
		Object fini = jt.getValueAt(row, cn);
		Object ffin = jt.getValueAt(row + 1, cn);
		return ObjectTools.safeIsEquals(fini, ffin);
	}

}
