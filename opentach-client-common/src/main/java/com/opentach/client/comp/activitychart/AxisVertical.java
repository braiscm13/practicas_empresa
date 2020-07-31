package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.comp.activitychart.taskwrapper.ActividadesTaskWrapper;
import com.opentach.client.comp.activitychart.taskwrapper.ResumenActividadesTaskWrapper;
import com.opentach.client.comp.activitychart.taskwrapper.RulerTaskWrapper;

/**
 * Eje de datos del Chart.
 *
 * @author Pablo Dorgambide
 * @version 1.0
 */

public class AxisVertical extends Axis {

	public static Font					FONT_LEGEND						= new Font("Arial", Font.BOLD, 8);
	public static Color					FONT_COLOR_DATE					= Color.white;
	public static Font					FONT_DATE						= new Font("Arial", Font.BOLD, 12);
	public static Color					BG_GRADIENT_DATE_SECOND_COLOR	= new Color(0x4c7795);
	public static Color					BG_GRADIENT_DATE_FIRST_COLOR	= new Color(0x2d5b7b);
	public static Color					FONT_COLOR_DAY_NAME				= new Color(0x194767);
	public static Font					FONT_DAY_NAME					= new Font("Arial", Font.BOLD, 10);
	protected static SimpleDateFormat	labelsformat					= new SimpleDateFormat("dd/MM/yyyy");
	protected Object					minval;
	protected Object					maxval;

	public AxisVertical(Chart chart) {
		super(chart);
		this.setOpaque(false);
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
			// return ( ( (Date) maxval).getTime());
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

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D gc = (Graphics2D) g.create();
		gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		try {
			String text = "";
			int textwidth = 0;
			double increment = this.getRange() / this.chart.getNumVerticalDivisions();
			double val = this.getMinVal();
			super.paintComponent(gc);

			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date((long) val));

			int cellHeight = this.chart.getCellHeight();
			// Set Stroke
			for (int i = 0; i < this.chart.getNumVerticalDivisions(); i++) {
				// int x = data.orig.x - offsetY;
				int initialY = this.getHeight() - (cellHeight * (i + 1));
				int y = initialY;

				// Linea superior (continuando a la regla
				gc.setColor(RulerTaskWrapper.COLOR_LINE_UPPER);
				gc.drawLine(0, y, this.getWidth(), y);
				gc.setColor(RulerTaskWrapper.COLOR_LINE_BOTTOM);
				gc.drawLine(0, y + 1, this.getWidth(), y + 1);

				// Nombre del día
				String dayNames[] = new DateFormatSymbols().getWeekdays();
				String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
				gc.setFont(AxisVertical.FONT_DAY_NAME);
				gc.setColor(AxisVertical.FONT_COLOR_DAY_NAME);
				FontMetrics fm = gc.getFontMetrics();
				y += fm.getHeight();
				gc.drawString(dayName, 5, y);

				// Fecha
				y = initialY + ActividadesTaskWrapper.ACTIVIDADES_OFFSET;
				int boxHeight = ActividadesTaskWrapper.ACTIVIDADES_HEIGHT;
				gc.setPaint(new GradientPaint(0, y, AxisVertical.BG_GRADIENT_DATE_FIRST_COLOR, 0, y + boxHeight,
						AxisVertical.BG_GRADIENT_DATE_SECOND_COLOR));
				int boxWidth = this.getWidth() - 10;
				int boxX = 5;
				gc.fill(new Rectangle(boxX, y, boxWidth, boxHeight));

				gc.setFont(AxisVertical.FONT_DATE);
				gc.setColor(AxisVertical.FONT_COLOR_DATE);
				text = AxisVertical.labelsformat.format(calendar.getTime());
				calendar.add(Calendar.MILLISECOND, (int) increment);
				fm = gc.getFontMetrics();
				textwidth = fm.stringWidth(text);
				gc.drawString(text, boxX + ((boxWidth - textwidth) / 2), (y + fm.getHeight()) - 2);
				y += boxHeight;

				val += increment;

				// Textos de infracciones y transporte ferry

				gc.setFont(AxisVertical.FONT_LEGEND);
				gc.setColor(AxisVertical.FONT_COLOR_DAY_NAME);
				fm = gc.getFontMetrics();
				text = ApplicationManager.getTranslation(this.chart.sublegend1);
				int xtext = this.getWidth() - 5 - fm.stringWidth(text);
				int ytext = (initialY + ActividadesTaskWrapper.ACTIVIDADES_OFFSET + ActividadesTaskWrapper.OUTFERRY_OFFSET + fm.getHeight()) - 4;
				gc.drawString(text, xtext, ytext);

				text = ApplicationManager.getTranslation(this.chart.sublegend2);
				xtext = this.getWidth() - 5 - fm.stringWidth(text);
				ytext = (initialY + ActividadesTaskWrapper.ACTIVIDADES_OFFSET + ActividadesTaskWrapper.INFRACCIONES_OFFSET + fm.getHeight()) - 4;
				gc.drawString(text, xtext, ytext);

				// Hacemos la chapuza de pintar aquí el marcador del resumen de
				// actividades, como lo hemos
				// colocado aquí shape en la tarea, el tooltip va a funcionar
				ResumenActividadesTaskWrapper.drawMarkerIcon(gc, ResumenActividadesTaskWrapper.OFFSET_X,
						(initialY + cellHeight) - ResumenActividadesTaskWrapper.OFFSET_Y);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		gc.dispose();
	}

	/** @return Clase del tipo de dato que se representado en el eje */
	@Override
	public Class getClassData() {
		return this.minval.getClass();
	}
}
