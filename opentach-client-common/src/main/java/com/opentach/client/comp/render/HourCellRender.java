package com.opentach.client.comp.render;

import java.awt.Component;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.ontimize.gui.table.CellRenderer;
import com.utilmize.client.report.IReportableCellRenderer;

import ar.com.fdvs.dj.domain.CustomExpression;

/**
 * Parmite renderizar una fecha como hora y si le llega un numero lo considera
 * como segundos renderizandolo en el formato de hora HH:mm
 *
 * @author Pablo Dorgambide
 * @company www.imatia.com
 * @email pdorgambide@imatia.com Date: 10-may-2006
 */
public class HourCellRender extends CellRenderer implements IReportableCellRenderer, CustomExpression {
	private final static int	SEGUNDOSHORA	= (60 * 60);

	public final static String	MINUTES			= "MINUTES";
	public final static String	HOURS			= "HOURS";
	public final static String	SECONDS			= "SECONDS";

	static DateFormat			sdf				= SimpleDateFormat.getTimeInstance();
	private String				resolution		= "MINUTES";
	protected String			columnName;

	public static void main(String[] args) {
		//		System.out.println(" Tiempo: " + HourCellRender.parsearTiempoDisponible(985359, "S"));
	}

	public HourCellRender(String columnName) {
		super();
		this.columnName = columnName;
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}

	public void setResolution(String resol) {
		if ((resol != null) && resol.toUpperCase().startsWith("H")) {
			this.resolution = HourCellRender.HOURS;
		} else if ((resol != null) && resol.toUpperCase().startsWith("S")) {
			this.resolution = HourCellRender.SECONDS;
		} else {
			this.resolution = HourCellRender.MINUTES;
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
				labelTemp.setText(HourCellRender.sdf.format(valor));
			} else if (valor instanceof String) {
				labelTemp.setText((String) valor);
			} else {
				labelTemp.setText("-");
			}
		} else {
			labelTemp.setText(this.format(Integer.valueOf(0)));
		}
		labelTemp.setFont(new Font("Arial", Font.PLAIN, 8));
		return comPadre;
	}

	public void setDateFormat(String pattern) {
		if (pattern != null) {
			HourCellRender.sdf = new SimpleDateFormat(pattern);
		}
	}

	public static String parsearTiempoDisponible(Object value, String resolution) {
		String resultado = "";
		if (value == null) {
			return resultado;
		} else if (value instanceof Date) {
			resultado = HourCellRender.sdf.format(value);
		} else if (value instanceof Number) {
			int segundos = ((Number) value).intValue();
			double aux = ((Number) value).doubleValue();
			if (resolution.startsWith("M")) {
				segundos *= 60;
			}
			if (resolution.startsWith("H")) {
				int intval = (int) aux;
				segundos = ((intval) * 36000) + ((int) (aux - intval) * 60);
			}

			boolean neg = (segundos < 0);
			if (neg) {
				resultado = "-";
			}

			segundos = Math.abs(segundos);
			int h = segundos / HourCellRender.SEGUNDOSHORA;
			if (h >= 0) {
				segundos = segundos % HourCellRender.SEGUNDOSHORA;
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
			resultado += tM + (resolution.startsWith("M") ? "" : ":" + tS);
		}
		return resultado;
	}

	public String format(Object value) {
		return HourCellRender.parsearTiempoDisponible(value, this.resolution);
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
		Object valor = fields.get(this.columnName);
		if (valor != null) {
			if (valor instanceof Number) {
				return this.format(valor);
			} else if (valor instanceof Date) {
				return HourCellRender.sdf.format(valor);
			} else if (valor instanceof String) {

				return valor;
			} else {
				return "-";
			}
		}
		return this.format(Integer.valueOf(0));
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
