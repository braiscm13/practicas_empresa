package com.opentach.client.employee.render;

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.gui.table.EJTable;
import com.utilmize.client.gui.field.table.UTable;

public class EmployeeContractCellRendererColorManager implements CellRendererColorManager {

	/** The CONSTANT logger */
	private static final Logger	logger					= LoggerFactory.getLogger(EmployeeContractCellRendererColorManager.class);

	public static Color			LAPSED_CONTRACT_COLOR	= Color.decode("#b70000");

	public EmployeeContractCellRendererColorManager(Hashtable params) {
	}

	public EmployeeContractCellRendererColorManager(UTable table, Hashtable params) {
		this(params);
	}

	@Override
	public Color getForeground(JTable t, int row, int col, boolean sel) {
		if (this.lapsedContract(t, row, col)) {
			return EmployeeContractCellRendererColorManager.LAPSED_CONTRACT_COLOR;
		}
		return null;
	}

	@Override
	public Color getBackground(JTable t, int row, int col, boolean sel) {
		return null;
	}

	protected boolean lapsedContract(JTable t, int row, int col) {
		try {
			Object value = this.getColumnValue(t, row, "DRC_DATE_TO");
			return (value != null) && ((Date) value).before(new Date());
		} catch (Exception ex) {
			EmployeeContractCellRendererColorManager.logger.trace(null, ex);
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
}
