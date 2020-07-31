package com.opentach.client.comp.render;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

import com.ontimize.gui.table.CellRenderer;

public class TimeCellRender extends CellRenderer {
	private final DateFormat	sdf;

	public TimeCellRender(String formato) {
		super();
		this.sdf = new SimpleDateFormat(formato);
	}

	@Override
	public Component getTableCellRendererComponent(javax.swing.JTable tabla, java.lang.Object valor, boolean seleccionado, boolean tieneFoco,
			int fila, int columna) {
		Component comPadre = super.getTableCellRendererComponent(tabla, valor, seleccionado, tieneFoco, fila, columna);
		if (valor instanceof Date) {
			JLabel labelTemp = (JLabel) comPadre;
			labelTemp.setText(this.sdf.format(valor));
		}
		return comPadre;
	}

}
