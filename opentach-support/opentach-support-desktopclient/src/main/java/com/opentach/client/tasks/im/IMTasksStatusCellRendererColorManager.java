package com.opentach.client.tasks.im;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JTable;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.gui.table.EJTable;
import com.ontimize.util.ParseUtils;
import com.utilmize.client.gui.field.DeprecatedUIConstants;
import com.utilmize.client.gui.field.table.UTable;

public class IMTasksStatusCellRendererColorManager implements CellRendererColorManager {

	public static Color	CLOSED_FOREGROUND_COLOR	= Color.decode("#b70000");

	protected String	deprecatedColumn;

	public IMTasksStatusCellRendererColorManager(Hashtable params) {
		this.deprecatedColumn = ParseUtils.getString((String) params.get("deprecatedcolumn"), null);
	}

	public IMTasksStatusCellRendererColorManager(UTable table, Hashtable params) {
		this(params);
	}

	@Override
	public Color getForeground(JTable t, int row, int col, boolean sel) {
		if (this.isClosed(t, row, col)) {
			return DeprecatedUIConstants.NON_PRESENT_FOREGROUND_COLOR;
		}
		return null;
	}

	@Override
	public Color getBackground(JTable t, int row, int col, boolean sel) {
		return null;
	}

	protected boolean isClosed(JTable t, int row, int col) {
		if (this.deprecatedColumn != null) {
			try {
				int columnIndex = ((EJTable) t).getColumnIndex(this.deprecatedColumn);
				if (columnIndex >= 0) {
					Object deprecatedValue = ((EJTable) t).getValueAt(row, columnIndex);
					return (deprecatedValue != null);
				}
			} catch (Exception ex) {
			}
		}

		return false;
	}

}