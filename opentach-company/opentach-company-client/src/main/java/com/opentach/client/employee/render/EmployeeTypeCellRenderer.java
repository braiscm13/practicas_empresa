package com.opentach.client.employee.render;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.ontimize.gui.ApplicationManager;
import com.opentach.common.company.beans.EmployeeType;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.field.table.render.UXmlIconCellRenderer;

public class EmployeeTypeCellRenderer extends UXmlIconCellRenderer {

	private static final ImageIcon	DRIVERICON						= ApplicationManager.getIcon("images-employee/dude16.png");
	private static final ImageIcon	VEHICLEWITHTACHOGRAPHICON		= ApplicationManager.getIcon("images-employee/truck16.png");
	private static final ImageIcon	VEHICLEWITHOUTTACHOGRAPHICON	= ApplicationManager.getIcon("images-employee/van16.png");
	private static final ImageIcon	OTHERMOBILESTAFFICON			= ApplicationManager.getIcon("images-employee/car16.png");
	private static final ImageIcon	HEADQUARTERSTAFFICON			= ApplicationManager.getIcon("images-employee/factory16.png");
	private static final ImageIcon	TELEWORKINGSTAFFICON			= ApplicationManager.getIcon("images-employee/teleworking16.png");

	public EmployeeTypeCellRenderer(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);

		comp.setIcon(this.getIconToSet(value));
		comp.setText(null);
		comp.setToolTipText(this.getTootipToSet(table, row));

		return comp;
	}

	protected ImageIcon getIconToSet(Object value) {
		if ((value == null) || (((Number) value).intValue() == EmployeeType.DRIVER_WITH_TACHOGRAPH.toId())) {
			return EmployeeTypeCellRenderer.VEHICLEWITHTACHOGRAPHICON;
		} else if (((Number) value).intValue() == EmployeeType.DRIVER_WITHOUT_TACHOGRAPH.toId()) {
			return EmployeeTypeCellRenderer.VEHICLEWITHOUTTACHOGRAPHICON;
		} else if (((Number) value).intValue() == EmployeeType.OTHER_MOBILE_STAFF.toId()) {
			return EmployeeTypeCellRenderer.OTHERMOBILESTAFFICON;
		} else if (((Number) value).intValue() == EmployeeType.STAFF_IN_HEADQUARTERS.toId()) {
			return EmployeeTypeCellRenderer.HEADQUARTERSTAFFICON;
		} else if (((Number) value).intValue() == EmployeeType.TELEWORKING.toId()) {
			return EmployeeTypeCellRenderer.TELEWORKINGSTAFFICON;
		}
		return EmployeeTypeCellRenderer.DRIVERICON;
	}

	protected String getTootipToSet(JTable table, int row) {
		final UTable uTable = (UTable) SwingUtilities.getAncestorOfClass(UTable.class, table);
		final int columnIndex = uTable.getColumnIndex("EMPLOYEE_TYPE");
		String typeName = null;
		if (columnIndex >= 0) {
			typeName = (String) table.getValueAt(row, columnIndex);
		} else {
			final int columnIndex2 = uTable.getColumnIndex("DESCRIP");
			if (columnIndex2 >= 0) {
				typeName = (String) table.getValueAt(row, columnIndex2);
			}
		}
		if (typeName == null) {
			typeName = "EMPLOYEEDATA.DRIVER_WITH_TACHOGRAPH";
		}
		return ApplicationManager.getTranslation(typeName);
	}
}
