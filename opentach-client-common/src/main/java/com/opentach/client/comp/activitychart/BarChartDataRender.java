package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Renderizado de mediante barras coloreadas.
 *
 * @author Pablo Dorgambide Avilés
 * @version 1.0
 */
public class BarChartDataRender implements ChartDataRender {

	public static final int	HORIZONTAL_BAR	= 0;
	public static final int	VERTICAL_BAR	= 1;
	protected int			width			= 0;
	protected Map<String, Integer>		widths;
	protected HashMap<String, Color>	colours			= new HashMap<String, Color>(); // Diferentes
	protected Marker[]		datamark		= null;
	protected Composite		composite		= null;
	protected boolean		showdatadesc	= false;
	protected boolean		rounded			= false;
	protected int			orientation;

	/**
	 * Establece si se pintan rectangulos redondeados
	 *
	 * @param v
	 */
	public void setRoundedRectangle(boolean v) {
		this.rounded = v;
	}

	/**
	 * @param width
	 *            Ancho de las barras en pixels.
	 * @param orient
	 *            Orientación: HORIZONTAL_BAR, VERTICAL_BAR
	 * @param cdefault
	 *            Color por defecto
	 * @param colours
	 *            Claves-valor de los colores para las diferentes Id de los
	 *            datos.
	 */
	public BarChartDataRender(int width, int orient, Color cdefault, HashMap<String, Color> colours) {
		this.width = width;
		if (colours != null) {
			this.colours = colours;
		}
		if (this.colours.containsKey("defcolor") == false) {
			this.colours.put("defcolour", cdefault);
		}
	}

	public BarChartDataRender(int width, int orientation, Color cdefault) {
		this(width, orientation, cdefault, new HashMap<String, Color>());
	}

	/**
	 * Set the bar width according to the data kind
	 */
	public void setDataWidths(Map<String, Integer> widths) {
		this.widths = widths;
	}

	@Override
	public void setComposite(Composite c) {
		this.composite = c;
	}

	/**
	 * @param mark
	 *            Marcador para los datos.
	 */
	@Override
	public void setRenderMarker(Marker[] mark) {
		this.datamark = mark;
	}

	/**
	 * Representa las series de datos dibujando el rectangulo delimintado entre
	 * cada dos valores. Si el numero de valores es inferior a dos el rectagulo
	 * se dibujará con el ancho (width) y horientacion (
	 * HORIZONTAL_BAR,VERTICAL_BAR) parametrizada.
	 *
	 * @param g
	 *            Graphics
	 * @param data
	 *            List
	 */
	@Override
	public void render(Graphics g, List<ChartData> data, Chart chart) {
		Graphics2D gc = (Graphics2D) g;
		try {
			if (chart.getAffineTransform() != null) {
				Iterator<ChartData> itd = data.iterator();
				ChartData dt;

				if (this.composite != null) {
					gc.setComposite(this.composite);
				}

				while (itd.hasNext()) {
					dt = itd.next();

					Shape shape = this.getShapes(g, dt, chart);
					if (this.showdatadesc == true) {
						String desc = dt.getDescription(chart);
						if (desc != null) {
							Rectangle2D rb = shape.getBounds2D();
							Rectangle2D.Double rtxt = (Rectangle2D.Double) gc.getFont().getStringBounds(desc, gc.getFontRenderContext());
							gc.drawString(desc, (int) (rb.getCenterX() - (rtxt.getCenterX() / 2)), (int) (rb.getCenterY() - (rtxt.getCenterY() / 2)));
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * renderIcon
	 *
	 * @param g
	 *            Graphics
	 */
	@Override
	public void renderIcon(java.awt.Graphics2D g, java.awt.Rectangle r) {
		this.colorDescriptionIcon(g, r);
	}

	private void colorDescriptionIcon(java.awt.Graphics2D g, java.awt.Rectangle r) {
		java.awt.Graphics2D gc = (Graphics2D) g.create();
		try {
			int i = 0;
			Iterator<String> it = this.colours.keySet().iterator();
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
				Iterator<Object> itx, ity;
				ChartData dt = data;
				Object xobj, yobj;
				double xval = 0, yval = 0;
				double[] src = new double[2];
				double[] dst = new double[2];
				java.util.List<Object> xlst, ylst;

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
					if (i == 0) { // the even points are the rectangle start
						// coordinates.
						// markstart = true;
						if (this.orientation == BarChartDataRender.HORIZONTAL_BAR) {
							r.setFrame(0, yval, xval, width);
							// if (!first && IsBorderPoint(xobj)) markstart =
							// false;
						} else {
							r.setFrame(xval, 0, width, yval);
						}
						if (!itx.hasNext()) { // If nof points are odd
							if (g != null) {
								gc.fill(r);
								// gc.setColor(gc.getColor().darker().darker().darker());
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
							r.setFrame(r.getWidth(), r.getY(), xval - r.getWidth(), width);
							// if (itx.hasNext() && IsBorderPoint(xobj)) markend
							// = false;
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
									this.datamark[0].draw(g, (int) r.getX(), (int) r.getY(), g.getColor());
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

	public void setBarWidth(int w) {
		this.width = w;
	}

}
