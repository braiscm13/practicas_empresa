package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.opentach.client.comp.activitychart.taskwrapper.RulerTaskWrapper;

public class AxisHorizontalBottom extends Axis {

	public static final Font		FONT_HOURS			= new Font("Arial", Font.BOLD, 9);


	// OTROS

	public static Color				FONT_COLOR_HOURS	= new Color(0x111188);

	public static SimpleDateFormat	FORMATTER			= new SimpleDateFormat("HH:mm");
	protected Object				minval;
	protected Object				maxval;

	public AxisHorizontalBottom(Chart chart) {
		super(chart);
		TimeZone tz = TimeZone.getDefault();
		this.setAxisCfg(new java.util.Date(-tz.getRawOffset()), new java.util.Date(-tz.getRawOffset() + 86400000));

	}

	/** @return double Rango del eje */
	@Override
	public double getRange() {
		return (this.getMaxVal() - this.getMinVal());
	}

	/**
	 * Configuracion del eje
	 *
	 * @param minval
	 *            Object Valor minimo.
	 * @param maxval
	 *            Object Valor maximo
	 * @param numcells
	 *            int Numero de celdas o divisiones.
	 */
	@Override
	public void setAxisCfg(Object minval, Object maxval) {
		this.minval = minval;
		this.maxval = maxval;
	}

	/** @return double Valor maximo del eje. */
	@Override
	public double getMaxVal() {
		if (this.maxval instanceof Number) {
			return (((Number) this.maxval).doubleValue());
		} else if (this.maxval instanceof Date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) this.maxval);
			return (((Date) this.maxval).getTime() + cal.get(Calendar.DST_OFFSET));

		}
		return 0;
	}

	/** @return double Valor minimo del eje. */
	@Override
	public double getMinVal() {
		if (this.minval instanceof Number) {
			return (((Number) this.minval).doubleValue());
		} else if (this.minval instanceof Date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) this.minval);
			// return ( ( (Date) minval).getTime());
			return (((Date) this.minval).getTime() + cal.get(Calendar.DST_OFFSET));
		}
		return 0;
	}

	/** @return Clase del tipo de dato que se representado en el eje */
	@Override
	public Class getClassData() {
		return this.minval.getClass();
	}

	@Override
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D gc = (Graphics2D) g.create();
		gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		try {
			super.paintComponent(gc);
			double val = this.getMinVal();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date((long) val));
			int numHours = 24;
			double increment = (this.getRange() / numHours);

			int x = this.chart.getLeftYaxisWidth();
			gc.setColor(RulerTaskWrapper.COLOR_LINE_UPPER);
			gc.drawLine(0, 0, this.getWidth(), 0);
			gc.setColor(RulerTaskWrapper.COLOR_LINE_BOTTOM);
			gc.drawLine(0, 1, this.getWidth(), 1);
			int numdiv = 48;
			for (int i = 0; i < (numdiv + 1); i++) {
				int xMark = x + ((i * this.chart.getDataWidth()) / numdiv);
				int height = ((i % 2) == 0) ? RulerTaskWrapper.RULER_HEIGHT : 3;
				gc.setColor(RulerTaskWrapper.COLOR_LINE_UPPER);
				gc.drawLine(xMark, 0, xMark, height);
				gc.setColor(RulerTaskWrapper.COLOR_LINE_BOTTOM);
				gc.drawLine(xMark + 1, 0, xMark + 1, height);

				if ((i % 2) == 0) {
					String text = AxisHorizontalBottom.FORMATTER.format(calendar.getTime());
					gc.setFont(AxisHorizontalBottom.FONT_HOURS);
					gc.setColor(AxisHorizontalBottom.FONT_COLOR_HOURS);
					FontMetrics fm = gc.getFontMetrics();
					gc.drawString(text, xMark - (fm.stringWidth(text) / 2), height + fm.getHeight());
					calendar.add(Calendar.MILLISECOND, (int) increment);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		gc.dispose();
	}

}
