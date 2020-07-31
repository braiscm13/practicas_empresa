package com.opentach.client.comp.render;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.ontimize.gui.table.CellRenderer;

/**
 * Parmite renderizar una fecha como hora y si le llega un numero lo considera
 * como segundos renderizandolo en el formato de hora HH:mm
 *
 * @author Pablo Dorgambide
 * @company www.imatia.com
 * @email pdorgambide@imatia.com Date: 10-may-2006
 */
public class HourCellRenderHourResolution extends CellRenderer {
	static int			SEGUNDOSHORA	= (60 * 60);
	static int			SEGUNDOSDIA		= (HourCellRenderHourResolution.SEGUNDOSHORA * 24);
	static DateFormat	sdf				= SimpleDateFormat.getTimeInstance();
	private String		resolution		= "HOURS";

	public HourCellRenderHourResolution() {
		super();
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}

	public void setResolution(String resol) {
		if ((resol != null) && resol.toUpperCase().startsWith("H")) {
			this.resolution = "HOURS";
		} else if ((resol != null) && resol.toUpperCase().startsWith("S")) {
			this.resolution = "SECONDS";
		} else {
			this.resolution = "MINUTES";
		}
	}

	@Override
	public Component getTableCellRendererComponent(javax.swing.JTable tabla, java.lang.Object valor, boolean seleccionado, boolean tieneFoco,
			int fila, int columna) {
		Component comPadre = super.getTableCellRendererComponent(tabla, valor, seleccionado, tieneFoco, fila, columna);
		JLabel labelTemp = (JLabel) comPadre;

		if (valor != null) {
			if (valor instanceof Number) {
				String textHoras = this.format(valor);
				labelTemp.setText(textHoras);
			} else if (valor instanceof Date) {
				labelTemp.setText(HourCellRenderHourResolution.sdf.format(valor));
			} else if (valor instanceof String) {
				labelTemp.setText((String) valor);
			} else {
				labelTemp.setText("-");
			}
		} else {
			labelTemp.setText(this.format(Integer.valueOf(0)));
		}
		return comPadre;
	}

	public void setDateFormat(String pattern) {
		if (pattern != null) {
			HourCellRenderHourResolution.sdf = new SimpleDateFormat(pattern);
		}
	}

	public String format(Object value) {
		String resultado = "";
		if (value == null) {
			return resultado;
		} else if (value instanceof Date) {
			resultado = HourCellRenderHourResolution.sdf.format(value);
		} else if (value instanceof Number) {
			int segundos = ((Number) value).intValue();
			double aux = ((Number) value).doubleValue();
			if (this.resolution.startsWith("M")) {
				segundos *= 60;
			}
			if (this.resolution.startsWith("H")) {
				segundos = (int) (aux * 3600);
				// segundos=(intval)*36000 +(int) (aux-(double)intval)*60;
			}

			boolean neg = (segundos < 0);
			if (neg) {
				resultado = "-";
			}

			segundos = Math.abs(segundos);

			// int dias = (int) segundos / SEGUNDOSDIA;
			// if (dias > 0) {
			// segundos = segundos % SEGUNDOSDIA;
			// resultado = "" + dias + "D ";
			// }

			int h = segundos / HourCellRenderHourResolution.SEGUNDOSHORA;
			if (h >= 0) {
				segundos = segundos % HourCellRenderHourResolution.SEGUNDOSHORA;
				String tH;
				if (h <= 9) {
					tH = "0" + h;
				} else {
					tH = String.valueOf(h);
				}
				resultado += tH + ":";
			}

			int m = segundos / 60;
			int s = segundos % 60;

			String tM;
			if (m <= 9) {
				tM = "0" + m;
			} else {
				tM = String.valueOf(m);

			}

			String tS;
			if (s <= 9) {
				tS = "0" + s;
			} else {
				tS = String.valueOf(s);

			}
			resultado += tM + (this.resolution.startsWith("M") ? "" : ":" + tS);

		}

		return resultado;
	}

}
