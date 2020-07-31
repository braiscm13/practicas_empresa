package com.opentach.client.employee.render;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.gui.table.EJTable;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.utilmize.client.gui.field.table.UTable;

public class EmployeeCellRendererColorManager implements CellRendererColorManager {

	/** The CONSTANT logger */
	private static final Logger	logger				= LoggerFactory.getLogger(EmployeeCellRendererColorManager.class);

	public static Color			NON_CONTRACT_COLOR	= Color.decode("#b70000");

	public EmployeeCellRendererColorManager(Hashtable params) {
	}

	public EmployeeCellRendererColorManager(UTable table, Hashtable params) {
		this(params);
	}

	@Override
	public Color getForeground(JTable t, int row, int col, boolean sel) {
		if (!this.hasContract(t, row, col)) {
			return EmployeeCellRendererColorManager.NON_CONTRACT_COLOR;
		}
		return null;
	}

	@Override
	public Color getBackground(JTable t, int row, int col, boolean sel) {
		return null;
	}

	protected boolean hasContract(JTable t, int row, int col) {
		try {
			Object value = this.getColumnValue(t, row, "CONT_VIGENTE");
			return this.isTrue(value);
		} catch (Exception ex) {
			EmployeeCellRendererColorManager.logger.trace(null, ex);
		}
		return false;
	}

	private Object getColumnValue(JTable t, int row, String column) {
		int columnIndex = ((EJTable) t).getColumnIndex(column);
		if (columnIndex >= 0) {
			return ((EJTable) t).getValueAt(row, columnIndex);
		}
		return null;
	}

	private boolean isTrue(Object value) {
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		} else if (value instanceof String) {
			return ParseUtilsExtended.getBoolean(value, false);
		}
		return false;
	}
}