package com.opentach.client.tasks.components;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JTable;

import com.opentach.client.comp.TimezoneDateCellRenderer;
import com.utilmize.client.gui.field.UDurationMaskDataField;

public class TaskClosedRenderer extends TimezoneDateCellRenderer {

	protected static NumberFormat twoNumbersFormat = new DecimalFormat("00");

	public TaskClosedRenderer(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
		boolean pending = value == null;
		String opennedTime = null;
		if (pending) {
			opennedTime = this.getOpennedTime((Date) table.getValueAt(row, table.getColumn("TSK_CREATION_DATE").getModelIndex()));
			value = "¡PENDIENTE! " + opennedTime;
		}
		Component comp = super.getTableCellRendererComponent(table, value, selected, focus, row, column);

		if (pending) {
			comp.setForeground(Color.decode(selected ? "#dc9a9a" : "#b70000"));
			((JLabel) comp).setToolTipText("Lleva " + opennedTime + " abierta!!!");
		}

		return comp;
	}

	private String getOpennedTime(Date startDate) {
		// Calcule from TSK_CREATION_DATE to now
		return TaskClosedRenderer.number2mask(new Date(new Date().getTime() - startDate.getTime()).getTime());
		// return "3d 23h";
	}

	/**
	 * Number2mask.
	 *
	 * @param valor
	 *            the valor
	 * @return the object
	 */
	public static String number2mask(Number valor) {
		if ((valor != null) && (valor.doubleValue() > 0)) {
			StringBuilder sb = new StringBuilder();
			long value = valor.longValue();

			int days = (int) (value / UDurationMaskDataField.DD);
			if (days != 0) {
				sb.append(days + "d ");
			}

			value %= UDurationMaskDataField.DD;
			int hours = (int) (value / UDurationMaskDataField.HH);
			sb.append(TaskClosedRenderer.twoNumbersFormat.format(hours) + ":");

			value %= UDurationMaskDataField.HH;
			int min = (int) (value / UDurationMaskDataField.MM);
			sb.append(TaskClosedRenderer.twoNumbersFormat.format(min) + ":");

			value %= UDurationMaskDataField.MM;
			int secs = (int) value / 1000;
			sb.append(TaskClosedRenderer.twoNumbersFormat.format(secs));
			return sb.toString();
		} else {
			return "";
		}
	}
}
