package com.opentach.client.comp.render;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;

import com.ontimize.gui.table.CellRenderer;

public class UTCTimeCellRender extends CellRenderer {

	DateFormat	sdf;

	public UTCTimeCellRender(String formato) {
		super();
		this.sdf = new SimpleDateFormat(formato);
		this.sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public Component getTableCellRendererComponent(javax.swing.JTable tabla, java.lang.Object valor, boolean seleccionado, boolean tieneFoco,
			int fila, int columna) {
		Component comPadre = super.getTableCellRendererComponent(tabla, valor, seleccionado, tieneFoco, fila, columna);
		if (valor instanceof Date) {
			JLabel labelTemp = (JLabel) comPadre;
			Date date = (Date) valor;
			labelTemp.setText(this.sdf.format(date));
		}
		return comPadre;
	}

}