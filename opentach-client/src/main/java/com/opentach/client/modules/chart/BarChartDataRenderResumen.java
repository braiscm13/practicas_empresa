package com.opentach.client.modules.chart;

import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.Marker;

public class BarChartDataRenderResumen extends BarChartDataRender {

	protected int				offset	= 0;
	protected AffineTransform	at		= null;

	public BarChartDataRenderResumen() {
		this(10, BarChartDataRender.HORIZONTAL_BAR, Color.red, new HashMap<String, Color>());
	}

	/**
	 * Establece si se pintan rectangulos redondeados
	 *
	 * @param v
	 */
	@Override
	public void setRoundedRectangle(boolean v) {
		this.rounded = true;
	}

	public BarChartDataRenderResumen(int width, int orient, Color cdefault, HashMap<String, Color> colours) {
		super(width, orient, cdefault, colours);
	}

	public BarChartDataRenderResumen(int offset, int width, int orient, Color cdefault) {
		this(width, orient, cdefault, new HashMap<String, Color>());
	}

	/**
	 * Set the bar width according to the data kind
	 */
	@Override
	public void setDataWidths(Map<String, Integer> widths) {
		this.widths = widths;
	}

	@Override
	public void setComposite(Composite c) {
		this.composite = c;
	}

	public void setAffineTransform(AffineTransform at) {
		this.at = at;
	}

	/**
	 * @param mark
	 *            Marcador para los datos.
	 */
	@Override
	public void setRenderMarker(Marker[] mark) {
		this.datamark = mark;
	}

	@Override
	public void render(Graphics g, List<ChartData> data, Chart chart) {
		super.render(g, data, chart);
	}

	/**
	 * renderIcon
	 *
	 * @param g
	 *            Graphics
	 */
	@Override
	public void renderIcon(Graphics2D g, java.awt.Rectangle r) {
		this.colorDescriptionIcon(g, r);
	}

	private void colorDescriptionIcon(java.awt.Graphics2D g, java.awt.Rectangle r) {
		java.awt.Graphics2D gc = (Graphics2D) g.create();
		try {
			int i = 0;
			// int h = r.height / colours.size();
			Iterator it = this.colours.keySet().iterator();
			FontMetrics fm = gc.getFontMetrics();
			gc.setFont(gc.getFont().deriveFont(-6));

			while (it.hasNext()) {
				Object item = it.next();
				gc.setColor(this.colours.get(it.next()));
				i++;
				gc.drawString((String) item, r.x + 5, r.y + (i * (fm.getAscent() + fm.getLeading())));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		gc.dispose();
	}

	@Override
	public void showDataDescription(boolean b) {
		this.showdatadesc = b;
	}

	/**
	 * Retorna una lista con las formas utilizadas para representar el dato. Un
	 * determinado dato puede tener como representación varios rectangulos no
	 * conectados.
	 *
	 * @param data
	 *            ChartData Dato
	 * @return Lista de formas con las que se representará el dato.
	 */
	@Override
	public Shape getShapes(Graphics g, ChartData data, Chart chart) {
		try {
			int width = this.width;
			if ((this.widths != null) && this.widths.containsKey(data.getKind())) {
				width = this.widths.get(data.getKind()).intValue();
			}

			java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
			if (chart.getAffineTransform() != null) {
				Iterator itx, ity;
				ChartData dt = data;
				Object xobj, yobj;
				double xval = 0, yval = 0;
				double[] src = new double[2];
				double[] dst = new double[2];
				java.util.List xlst, ylst;

				RectangularShape r = new Rectangle2D.Double();
				if (this.rounded) {
					RoundRectangle2D rr = new RoundRectangle2D.Double();
					rr.setRoundRect(0, 0, 10, 10, 6.0, 6.0);
					r = rr;
				}

				xlst = dt.getX();
				ylst = dt.getY();
				itx = xlst.iterator();
				ity = ylst.iterator();
				boolean markstart = true;
				boolean markend = false;
				// boolean first=true;
				Graphics2D gc = (Graphics2D) g;
				Color oldc = gc.getColor();
				Color itemcolor = oldc;
				if (this.colours.containsKey(data.getKind())) {
					itemcolor = this.colours.get(data.getKind());
				} else {
					itemcolor = this.colours.get("defcolour");
				}

				gc.setColor(itemcolor);
				int i = -1;
				while (itx.hasNext()) {
					i = (i + 1) % 2;
					xobj = itx.next();
					yobj = ity.next();
					if (xobj instanceof Integer) {
						xval = ((Integer) xobj).intValue();
					} else if (xobj instanceof Date) {
						xval = ((Date) xobj).getTime();
					}
					if (yobj instanceof Integer) {
						yval = ((Integer) yobj).intValue();
					} else if (yobj instanceof Date) {
						yval = ((Date) yobj).getTime();
					}
					src[0] = xval; // transform to the screen pixel coordinates
					src[1] = yval;
					chart.getAffineTransform().transform(src, 0, dst, 0, 1);
					xval = dst[0];
					yval = dst[1];
					if (this.orientation == BarChartDataRender.HORIZONTAL_BAR) {
						yval += this.getOffset(chart);
					} else {
						xval += this.getOffset(chart);
					}
					if (i == 0) { // the even points are the rectangle start
						// coordinates.
						if (this.orientation == BarChartDataRender.HORIZONTAL_BAR) {
							r.setFrame(0, yval + 10, xval, width);
							// r.setFrame(0, yval + 10, xval - 50, width );
						} else {
							r.setFrame(xval, 0, width, yval);
						}
						if (!itx.hasNext()) { // If nof points are odd
							if (g != null) {
								gc.fill(r);

								gc.draw(r);

								if (this.datamark != null) {
									if (markstart && (this.datamark[0] != null)) {
										this.datamark[0].draw(g, (int) r.getX(), (int) r.getY(), g.getColor());
										markstart = false;
									}
									if (markend && (this.datamark.length > 0) && (this.datamark[1] != null)) {
										this.datamark[1].draw(g, (int) (r.getX() + r.getWidth()), (int) r.getY(), g.getColor());
										markend = false;
									}
								}
							}
							if ((r.getWidth() > 0) && (r.getHeight() > 0)) {
								gp.append((Shape) r.clone(), false);
							}
						}
					}

					if (i == 1) { // the odd points are the rectangle end
						// coordinates.
						// markend = true;
						if (this.orientation == BarChartDataRender.HORIZONTAL_BAR) {
							r.setFrame(r.getWidth(), r.getY() + 10, xval - r.getWidth(), width);
						} else {
							r.setFrame(r.getX(), r.getHeight(), xval - r.getWidth(), width);
						}
						if (!itx.hasNext()) {
							markend = true;
						}
						if (g != null) {
							gc.setColor(itemcolor);
							gc.fill(r);
							gc.setColor(itemcolor.darker().darker().darker());
							gc.draw(r);

							if (this.datamark != null) {
								if (markstart && (this.datamark[0] != null)) {
									this.datamark[0].draw(g, (int) r.getX() - 900, (int) r.getY(), g.getColor());
									markstart = false;
								}
								if (markend && (this.datamark[1] != null)) {
									this.datamark[1].draw(g, (int) (r.getX() + r.getWidth()), (int) r.getY(), g.getColor());
									markend = false;
								}
							}

						}
						if ((r.getWidth() > 0) && (r.getHeight() > 0)) {
							gp.append((Shape) r.clone(), false);
						}
					}
				}
				gc.setColor(oldc);
			}

			data.setShape(gp);

			return gp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public void setBarWidth(int w) {
		this.width = w;
	}

	public double getOffset(Chart arg0) {
		return 0;
	}

}
