package com.opentach.client.comp.activitychart.taskwrapper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRenderProvider;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.TaskParser;
import com.opentach.client.comp.render.DaysCellRender;
import com.opentach.common.tacho.data.Actividad;

/**
 * <p> Título: </p> <p> Descripción: Clase envoltorio de Activades para que sus datos puedan ser representados en un chart. <p> Empresa: Imatia </p>
 *
 * @author Pablo Dorgambide Aviles
 * @version 1.0
 */

public class ResumenActividadesTaskWrapper extends Task implements TaskParser, ChartDataRenderProvider {
	public static Color					COLOR_SUM_ICON_FOREGROUND	= new Color(0x2d5b7b);
	public static Color					COLOR_SUM_ICON_BORDER		= new Color(0x2d5b7b);
	public static Color					COLOR_SUM_ICON_INNER_BORDER	= new Color(0x9bbad0);
	public static Color					COLOR_SUM_ICON_BACKGROUND	= new Color(0x719dbc);
	protected static ChartDataRender	render;
	protected static final String[]		act_dscr;
	protected static Dimension			MARKER_SIZE					= new Dimension(26, 15);
	public static final int				OFFSET_X					= 5;
	public static final int				OFFSET_Y					= ResumenActividadesTaskWrapper.MARKER_SIZE.height + 5;
	protected Number					tpConduccion				= null;
	protected Number					tpDescanso					= null;
	protected Number					tpTrabajo					= null;
	protected Number					tpDiponibilidad				= null;
	protected Number					tpIndefinida				= null;
	protected Date						fecIni						= null;

	static {
		act_dscr = new String[6];
		ResumenActividadesTaskWrapper.act_dscr[Actividad.CONDUCCION] = ApplicationManager.getTranslation("CONDUCCION");
		ResumenActividadesTaskWrapper.act_dscr[Actividad.PAUSA_DESCANSO] = ApplicationManager.getTranslation("PAUSA/DESCANSO");
		ResumenActividadesTaskWrapper.act_dscr[Actividad.DISPONIBILIDAD] = ApplicationManager.getTranslation("DISPONIBILIDAD");
		ResumenActividadesTaskWrapper.act_dscr[Actividad.TRABAJO] = ApplicationManager.getTranslation("TRABAJO");
		ResumenActividadesTaskWrapper.act_dscr[Actividad.INDEFINIDA] = ApplicationManager.getTranslation("INDEFINIDA");
	}

	public ResumenActividadesTaskWrapper() {
		super();
	}

	private String getRow(String data1, Object data2, int i) {
		if ((data1 != null) && (data2 != null)) {
			if ((i / 2) == 0) {
				return "<tr><td>" + data1 + "</td><td>" + data2 + "</td>";
			}
			return "<td>" + data1 + "</td><td>" + data2 + "</td></tr>";
		}
		return "";
	}

	/**
	 * getId
	 *
	 * @return Object
	 */

	@Override
	public Object getId() {
		return null;
	}

	/**
	 * getVisualComponent
	 *
	 * @return JComponent
	 */
	@Override
	public JComponent getVisualComponent() {
		return null;
	}

	/**
	 * setX
	 *
	 * @param x
	 *            List
	 */
	@Override
	public void setX(List x) {}

	/**
	 * setY
	 *
	 * @param y
	 *            List
	 */
	@Override
	public void setY(List y) {}

	@Override
	public List getX() {
		ArrayList lst = new ArrayList();
		GregorianCalendar cal = new GregorianCalendar();
		GregorianCalendar hstartofday;
		cal.setTime(this.getStart());
		hstartofday = new GregorianCalendar(1970, 0, 1, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		lst.add(hstartofday.getTime());
		return lst;
	}

	@Override
	public List getY() {
		ArrayList lst = new ArrayList();
		GregorianCalendar cal = new GregorianCalendar();
		GregorianCalendar hstartofday;
		cal.setTime(this.getStart());
		hstartofday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		lst.add(hstartofday.getTime());
		return lst;
	}

	protected void buildDescription() {

		SimpleDateFormat dfs = new SimpleDateFormat("dd/MM/yyyy");
		StringBuffer str = new StringBuffer();
		int i = 0;
		String sResumen = ApplicationManager.getTranslation("Resumen_dia");
		str.append("<html> <body><h4> " + sResumen + " " + dfs.format(this.fecIni) + "</h4>");
		str.append("<table border='0' bordercolor='#FF0000'  frame='border'>");
		if (this.tpConduccion != null) {
			String conduccion = ApplicationManager.getTranslation("CONDUCCION");
			str.append(this.getRow("<b>" + conduccion + "</b>", DaysCellRender.parsearTiempoDisponible(this.tpConduccion.intValue()), i));
			i++;
		}
		if (this.tpTrabajo != null) {
			String trabajo = ApplicationManager.getTranslation("TRABAJO");
			str.append(this.getRow("<b>" + trabajo + "</b>", DaysCellRender.parsearTiempoDisponible(this.tpTrabajo.intValue()), i));
		}
		if (this.tpDiponibilidad != null) {
			String disp = ApplicationManager.getTranslation("DISPONIBILIDAD");
			str.append(this.getRow("<b>" + disp + "</b>", DaysCellRender.parsearTiempoDisponible(this.tpDiponibilidad.intValue()), i));
		}
		if (this.tpDescanso != null) {
			String desc = ApplicationManager.getTranslation("PAUSA/DESCANSO");
			str.append(this.getRow("<b>" + desc + "</b>", DaysCellRender.parsearTiempoDisponible(this.tpDescanso.intValue()), i));
		}
		if (this.tpIndefinida != null) {
			String indef = ApplicationManager.getTranslation("INDEFINIDA");
			str.append(this.getRow("<b>" + indef + "</b>", DaysCellRender.parsearTiempoDisponible(this.tpIndefinida.intValue()), i));
		}
		if ((i / 2) == 0) {
			str.append("</td>");
		}
		str.append("</table></body></html>");
		this.setDescription(str.toString());
	}

	@Override
	public void parse(Map cv) {
		Date dIni = (Date) cv.get("FEC_COMIENZO");
		this.fecIni = dIni;
		this.setKind("Resumen");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dIni);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		Date fecIni = cal.getTime();
		// cal.set(Calendar.HOUR_OF_DAY, 23);
		// cal.set(Calendar.MINUTE, 59);
		// cal.set(Calendar.SECOND, 59);
		// Date fecFin = cal.getTime();
		final BigDecimal bZero = new BigDecimal(0d);
		this.setStart(fecIni);
		// this.end = fecFin;
		this.setEnd(null);
		this.tpConduccion = ((Number) cv.get("TCONDUCCION"));
		this.tpDescanso = ((Number) cv.get("TDESCANSO"));
		this.tpDiponibilidad = ((Number) cv.get("TDISPONIBILIDAD"));
		this.tpTrabajo = ((Number) cv.get("TTRABAJO"));
		this.tpIndefinida = ((Number) cv.get("TINDEFINIDA"));
		if (this.tpConduccion == null) {
			this.tpConduccion = bZero;
		}
		if (this.tpDescanso == null) {
			this.tpDescanso = bZero;
		}
		if (this.tpDiponibilidad == null) {
			this.tpDiponibilidad = bZero;
		}
		if (this.tpTrabajo == null) {
			this.tpTrabajo = bZero;
		}
		if (this.tpIndefinida == null) {
			this.tpIndefinida = bZero;
		}
		this.buildDescription();
	}

	@Override
	public ChartDataRender getChartDataRender() {
		if (ResumenActividadesTaskWrapper.render == null) {
			ResumenChartDataRender barrender = new ResumenChartDataRender();
			// {
			// @Override
			// public double getOffset(Chart chart) {
			// return super.getOffset(chart)+50;
			// }
			// };
			// barrender.setRenderMarker(new Marker[] { marker, null });
			// barrender.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			// 1.0f));
			// barrender.setRoundedRectangle(true);
			ResumenActividadesTaskWrapper.render = barrender;
		}
		return ResumenActividadesTaskWrapper.render;
	}

	public static void drawMarkerIcon(Graphics g, int x, int y) {
		g.setColor(ResumenActividadesTaskWrapper.COLOR_SUM_ICON_BACKGROUND);
		g.fillRect(x, y, ResumenActividadesTaskWrapper.MARKER_SIZE.width, ResumenActividadesTaskWrapper.MARKER_SIZE.height);
		g.setColor(ResumenActividadesTaskWrapper.COLOR_SUM_ICON_INNER_BORDER);
		g.drawRect(x + 1, y + 1, ResumenActividadesTaskWrapper.MARKER_SIZE.width - 1, ResumenActividadesTaskWrapper.MARKER_SIZE.height - 1);
		g.setColor(ResumenActividadesTaskWrapper.COLOR_SUM_ICON_BORDER);
		g.drawRect(x, y, ResumenActividadesTaskWrapper.MARKER_SIZE.width, ResumenActividadesTaskWrapper.MARKER_SIZE.height);

		g.setColor(ResumenActividadesTaskWrapper.COLOR_SUM_ICON_FOREGROUND);
		g.drawLine(x + 14, y + 7, x + 20, y + 7);
		g.drawLine(x + 14, y + 9, x + 20, y + 9);
		g.drawLine(x + 14, y + 11, x + 20, y + 11);
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString("\u2211", x + 5, y + 2 + g.getFontMetrics().getAscent());
	}

	protected class ResumenChartDataRender extends BarChartDataRender {

		public ResumenChartDataRender() {
			super(0, 0, Color.black, new HashMap());
		}

		@Override
		public void render(Graphics g, List data, Chart chart) {
			// super.render(g, data, chart);
			for (Object task : data) {
				Shape shape = this.getShapes(g, (ChartData) task, chart);
				Rectangle r = shape.getBounds();
				ResumenActividadesTaskWrapper.drawMarkerIcon(g, r.x, r.y);
			}
		}

		@Override
		public Shape getShapes(Graphics g, ChartData data, Chart chart) {
			try {
				RectangularShape r = new Rectangle2D.Double();
				if (chart.getAffineTransform() != null) {
					Object yobj;
					double xval = 0, yval = 0;
					double[] src = new double[2];
					double[] dst = new double[2];

					yobj = data.getY().get(0);

					xval = 0;
					yval = ((Date) yobj).getTime();

					src[0] = xval; // transform to the screen pixel
					// coordinates
					src[1] = yval;
					chart.getAffineTransform().transform(src, 0, dst, 0, 1);
					xval = dst[0];
					yval = dst[1];

					// La X queremos que esté en la parte de la izquierda
					xval = (xval - chart.getLeftYaxisWidth()) + ResumenActividadesTaskWrapper.OFFSET_X;
					yval = (yval + chart.getCellHeight()) - ResumenActividadesTaskWrapper.OFFSET_Y;

					int width = ResumenActividadesTaskWrapper.MARKER_SIZE.width;
					int height = ResumenActividadesTaskWrapper.MARKER_SIZE.height;
					r.setFrame(xval, yval, width, height);
				}
				data.setShape(r);
				return r;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

	}
}
