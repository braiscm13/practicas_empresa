package com.opentach.client.comp.activitychart.taskwrapper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRenderProvider;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.TaskParser;

public class RulerTaskWrapper extends Task implements TaskParser, ChartDataRenderProvider {

	public static Color					COLOR_LINE_BOTTOM	= new Color(0x7198b4);
	public static Color					COLOR_LINE_UPPER	= new Color(0x2d5b7b);
	public static Color					COLOR_ACTIVITIES_BOX_TOP	= new Color(0x2d5b7b);
	public static Color					COLOR_ACTIVITIES_BOX_BOTTOM	= new Color(0x7198b4);

	public final static int				RULER_HEIGHT		= 7;
	protected static ChartDataRender	render;

	public RulerTaskWrapper() {
		super();
	}

	public RulerTaskWrapper(Date time) {
		this();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("FEC_COMIENZO", time);
		this.parse(map);
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

	@Override
	public void setDescription(String desc) {}

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

	@Override
	public void parse(Map cv) {
		Date dIni = (Date) cv.get("FEC_COMIENZO");
		this.setKind("Ruler");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dIni);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		Date fecIni = cal.getTime();
		this.setStart(fecIni);
		this.setEnd(null);
	}

	@Override
	public ChartDataRender getChartDataRender() {
		if (RulerTaskWrapper.render == null) {
			RulerTaskWrapper.render = new RulerTaskRender();
		}
		return RulerTaskWrapper.render;
	}

	protected class RulerTaskRender extends BarChartDataRender {

		public RulerTaskRender() {
			super(0, 0, Color.black, new HashMap());
		}

		@Override
		public void render(Graphics g, List data, Chart chart) {
			for (Object task : data) {
				Shape shape = this.getShapes(g, (ChartData) task, chart);
				Rectangle r = shape.getBounds();

				// Pintamos la regla superior
				// g.drawLine(r.x, r.y, r.width, 1);
				// sumamos +chart.getRigthYaxisWidth() para que la línea llegue
				// hasta la derecha del todo
				g.setColor(RulerTaskWrapper.COLOR_LINE_UPPER);
				g.drawLine(r.x, r.y, r.x + r.width + chart.getRigthYaxisWidth(), r.y);
				g.setColor(RulerTaskWrapper.COLOR_LINE_BOTTOM);
				g.drawLine(r.x, r.y + 1, r.x + r.width + chart.getRigthYaxisWidth(), r.y + 1);
				int numdiv = 48;
				for (int i = 0; i < (numdiv + 1); i++) {
					int x = r.x + ((i * r.width) / numdiv);
					int height = ((i % 2) == 0) ? RulerTaskWrapper.RULER_HEIGHT : 3;
					g.setColor(RulerTaskWrapper.COLOR_LINE_UPPER);
					g.drawLine(x, r.y, x, r.y + height);
					g.setColor(RulerTaskWrapper.COLOR_LINE_BOTTOM);
					g.drawLine(x + 1, r.y, x + 1, r.y + height);
				}

				// Pintamos la caja de las actividades
				g.setColor(RulerTaskWrapper.COLOR_ACTIVITIES_BOX_TOP);
				int ytop = (r.y + ActividadesTaskWrapper.ACTIVIDADES_OFFSET) - 1;
				int ybottom = ytop + ActividadesTaskWrapper.ACTIVIDADES_HEIGHT + 2;
				g.drawLine(r.x, ytop, r.x + r.width, ytop);
				g.drawLine(r.x, ytop, r.x, ybottom);

				g.setColor(RulerTaskWrapper.COLOR_ACTIVITIES_BOX_BOTTOM);
				g.drawLine(r.x, ybottom, r.x + r.width, ybottom);
				g.drawLine(r.x + r.width + 1, ytop, r.x + r.width + 1, ybottom);
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
					int width = chart.getDataWidth();
					r.setFrame(xval, yval, width, RulerTaskWrapper.RULER_HEIGHT);
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
