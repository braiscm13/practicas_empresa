package com.opentach.client.comp.activitychart.taskwrapper;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRenderProvider;
import com.opentach.client.comp.activitychart.Marker;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.TaskParser;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.common.tacho.data.Infraccion;

public class InfraccionTaskWrapper extends Task implements TaskParser, ChartDataRenderProvider {

	protected static ChartDataRender	render;
	protected Object					naturaleza;
	protected int						fades, excon;

	public InfraccionTaskWrapper() {}

	public InfraccionTaskWrapper(Infraccion inf) {
		super(Integer.valueOf(0), inf.tipo, inf.fecIni, inf.fecFin, "Infraccion");
		this.naturaleza = inf.naturaleza;
		this.buildDescription();
	}

	private String getRow(String data1, Object data2) {
		if ((data1 != null) && (data2 != null)) {
			return "<tr><td>" + data1 + "</td><td>" + data2 + "</td></tr>";
		}
		return "";
	}

	@Override
	public void parse(Map cv) {
		this.setId(Integer.valueOf(0));
		this.setKind((String) cv.get("TIPO"));
		this.setStart((Date) cv.get("FECHORAINI"));
		this.setEnd((Date) cv.get("FECHORAFIN"));
		this.naturaleza = cv.get("NATURALEZA");
		if (cv.get("FADES") != null) {
			this.fades = ((Number) cv.get("FADES")).intValue();
		}
		if (cv.get("EXCON") != null) {
			this.excon = ((Number) cv.get("EXCON")).intValue();
		}
		this.buildDescription();
	}

	protected void buildDescription() {
		SimpleDateFormat dfs = new SimpleDateFormat("(dd) HH:mm");
		StringBuffer str = new StringBuffer();
		str.append("<html> <body><h4> " + this.getKind() + "  </h4>");
		str.append("<table  border='0' frame='border'>");
		str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Inicio") + "</b>", dfs.format(this.getStart())));
		str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Fin") + "</b>", dfs.format(this.getEnd())));

		if (this.getKind().startsWith("F")) {
			str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Falta_Desc") + "</b>",
					MinutesCellRender.parsearTiempoDisponible(this.fades)));
		} else {
			str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Ex_Cond") + "</b>",
					MinutesCellRender.parsearTiempoDisponible(this.excon)));
		}
		str.append("</table>");
		str.append("</body></html>");
		this.setDescription(str.toString());
	}

	@Override
	public ChartDataRender getChartDataRender() {
		if (InfraccionTaskWrapper.render == null) {
			InfraccionTaskWrapper.render = new InfraccionChartDataRender();
		}
		return InfraccionTaskWrapper.render;
	}

	protected class InfraccionChartDataRender implements ChartDataRender {

		public InfraccionChartDataRender() {
			super();
		}

		@Override
		public void render(Graphics g, List data, Chart chart) {
			for (Object task : data) {
				if (task instanceof InfraccionTaskWrapper) {
					this.getShapes(g, (ChartData) task, chart);
				}
			}
		}

		@Override
		public Shape getShapes(Graphics g, ChartData data, Chart chart) {
			Graphics2D gc = (Graphics2D) g.create();
			try {
				int width = 3;
				/*
				 * if(data.getShape()!=null) return data.getShape();
				 */
				java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
				if (chart.getAffineTransform() != null) {

					RectangularShape r = new Rectangle2D.Double();

					Iterator itx = data.getX().iterator();
					Iterator ity = data.getY().iterator();
					Color itemcolor = Color.red;
					gc.setColor(itemcolor);
					int i = -1;

					while (itx.hasNext()) {
						i = (i + 1) % 2;
						Object xobj = itx.next();
						Object yobj = ity.next();
						double[] dst = this.convertPoint(((Date) xobj).getTime(), ((Date) yobj).getTime(), chart.getAffineTransform());
						double xval = dst[0];
						double yval = dst[1];
						yval += ActividadesTaskWrapper.ACTIVIDADES_OFFSET + ActividadesTaskWrapper.INFRACCIONES_OFFSET;
						if (i == 0) { // the even points are the rectangle start
							// coordinates.
							r.setFrame(0, yval, xval, width);
							if (!itx.hasNext()) { // If nof points are odd
								gc.fill(r);
								gc.draw(r);
								if ((r.getWidth() > 0) && (r.getHeight() > 0)) {
									gp.append((Shape) r.clone(), false);
								}
							}
						}

						if (i == 1) { // the odd points are the rectangle end
							// coordinates.
							r.setFrame(r.getWidth(), r.getY(), xval - r.getWidth(), width);
							gc.setColor(itemcolor);
							gc.fill(r);
							gc.setColor(itemcolor.darker().darker().darker());
							gc.draw(r);
							if ((r.getWidth() > 0) && (r.getHeight() > 0)) {
								gp.append((Shape) r.clone(), false);
							}
						}
					}
				}
				data.setShape(gp);
				return gp;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			} finally {
				gc.dispose();
			}

		}

		private double[] convertPoint(long xval, long yval, AffineTransform at) {
			// transform to the screen pixel coordinates
			double[] src = new double[2];
			double[] dst = new double[2];
			src[0] = xval;
			src[1] = yval;
			at.transform(src, 0, dst, 0, 1);
			return dst;
		}

		@Override
		public void setRenderMarker(Marker[] marker) {}

		@Override
		public void setComposite(Composite c) {}

		@Override
		public void renderIcon(Graphics2D g, Rectangle r) {}

		@Override
		public void showDataDescription(boolean b) {}

	}
}
