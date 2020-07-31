package com.opentach.client.comp;

import java.awt.Component;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JTable;

import com.utilmize.client.gui.field.table.IExportableRenderer;
import com.utilmize.client.gui.field.table.render.UXmlDateCellRenderer;

public class TimezoneDateCellRenderer extends UXmlDateCellRenderer implements IExportableRenderer {

	public TimezoneDateCellRenderer(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		value = TimezoneDateCellRenderer.addaptValueToSystemTimezone(value);
		return super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
	}

	public static Object addaptValueToSystemTimezone(Object value) {
		if ((value != null) && (value instanceof Date)) {
			// Try to addapt date value with system timezone (must be saved when app starts, look for property "OPENTACH_SYSTEM_DEFAULT_TIMEZONE")
			String timezoneOffsetMs = System.getProperty("OPENTACH_SYSTEM_DEFAULT_TIMEZONE");
			if (timezoneOffsetMs != null) {
				int offset = Integer.valueOf(timezoneOffsetMs);
				value = new Date(((Date) value).getTime() + offset);
			}
		}
		return value;
	}

	@Override
	public String getHTMLExportValue(JTable table, Object value, int row, int column) {
		Component c = this.getTableCellRendererComponent(table, value, false, false, row, column);
		if (c instanceof JLabel) {
			return ((JLabel) c).getText();
		}
		return value.toString();
	}

	@Override
	public Object getReportExportValue(JTable table, Object value, int row, int column) {
		return this.getHTMLExportValue(table, value, row, column);
	}

	@Override
	public Object getExcelExportValue(JTable table, Object value, int row, int column) {
		return TimezoneDateCellRenderer.addaptValueToSystemTimezone(value);
	}

	@Override
	public Object getChartExportValue(JTable table, Object value, int row, int column) {
		return this.getHTMLExportValue(table, value, row, column);
	}

}
