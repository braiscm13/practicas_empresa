package com.opentach.client.comp.render;

import java.awt.Component;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.ontimize.gui.table.CellRenderer;
import com.utilmize.client.report.IReportableCellRenderer;

import ar.com.fdvs.dj.domain.CustomExpression;

public class DaysCellRender extends CellRenderer implements IReportableCellRenderer, CustomExpression {

	protected String	columnName;

	public DaysCellRender(String columnName) {
		super();
		this.columnName = columnName;
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return new MinutesCellRender(this.columnName);
		}
	}

	@Override
	public Component getTableCellRendererComponent(javax.swing.JTable tabla, java.lang.Object valor, boolean seleccionado, boolean tieneFoco,
			int fila, int columna) {
		Component comPadre = super.getTableCellRendererComponent(tabla, valor, seleccionado, tieneFoco, fila, columna);
		JLabel labelTemp = (JLabel) comPadre;

		if ((valor != null) && (valor instanceof Number)) {
			int value = ((Number) valor).intValue();
			String textHoras = DaysCellRender.parsearTiempoDisponible(value);
			labelTemp.setText(textHoras);
		} else {
			labelTemp.setText("");
		}

		return comPadre;
	}

	public static String parsearTiempoDisponible(int value) {
		String resultado = "";
		value = Math.abs(value);
		int d, h, m;
		// Value is the number of minutes.
		// days
		d = value / (60 * 24);
		value = value % (60 * 24);
		// hours
		h = value / 60;
		// minutes
		m = value % (60);
		String tH;
		if (h <= 9) {
			tH = "0" + h;
		} else {
			tH = String.valueOf(h);
		}
		String tM;
		if (m <= 9) {
			tM = "0" + m;
		} else {
			tM = String.valueOf(m);
		}
		resultado = d + ":" + tH + ":" + tM;
		return resultado;
	}

	@Override
	public String getReportPattern(String engineId) {
		return null;
	}

	@Override
	public Object getReportExpression(String engineId) {
		return this;
	}

	@Override
	public Object evaluate(Map fields, Map variables, Map parameters) {
		return DaysCellRender.parsearTiempoDisponible(((Number) fields.get(this.columnName)).intValue());
	}

	@Override
	public String getClassName() {
		return String.class.getName();
	}

	@Override
	public boolean isOperable() {
		return true;
	}
}
