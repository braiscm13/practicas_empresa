package com.opentach.client.util;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;

//public class blackberryRenderer implements CellRendererColorManager {
//
//	private static final Color ERRORCOLOR = new Color(0x86, 0x94, 0x97);
//
//	public blackberryRenderer(){
//	}
//
//	public Color getBackground(JTable jt, int row, int col, boolean selected) {
//		TableColumn tcError = jt.getColumn("PIN");
//		int im 		= tcError.getModelIndex();
//    Object f  = jt.getValueAt(row, im);
//    if (((String)f)==null){
//    	return ERRORCOLOR;
//
//    }
//
//	return null;
//    }
//
//	public Color getForeground(JTable jt, int row, int col, boolean selected) {
//		return null;
//	}
//
//
//}

public class blackberryRenderer implements CellRendererColorManager {

	private static final Color	ERRORCOLOR	= new Color(0x86, 0x94, 0x97);

	public blackberryRenderer() {}

	@Override
	public Color getBackground(JTable jt, int row, int col, boolean selected) {
		TableColumn tcError = jt.getColumn("PIN");
		int im = tcError.getModelIndex();
		Object f = jt.getValueAt(row, im);
		if (((String) f) == null) {
			return blackberryRenderer.ERRORCOLOR;

		}

		return null;
	}

	@Override
	public Color getForeground(JTable jt, int row, int col, boolean selected) {
		return null;
	}

}
