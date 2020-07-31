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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.Marker;

public class BarChartDataRenderExtended implements ChartDataRender {

	public static final int		HORIZONTAL_BAR	= 0;
	public static final int		VERTICAL_BAR	= 1;
	protected int				offset			= 0;
	protected int				width			= 0;
	protected Map				widths;
	protected int				orientation		= 0;
	protected HashMap			colours			= new HashMap();	// Diferentes
																	// colores
																	// por cada
																	// id del
																	// dato.
	protected AffineTransform	at				= null;
	protected Marker[]			datamark		= null;
	protected Composite			composite		= null;
	protected boolean			showdatadesc	= false;
	protected boolean			rounded			= false;

	public BarChartDataRenderExtended() {
		this(0, 10, BarChartDataRenderExtended.HORIZONTAL_BAR, Color.red, new HashMap());
	}

	/**
	 * Establece si se pintan rectangulos redondeados
	 *
	 * @param v
	 */
	public void setRoundedRectangle(boolean v) {
		this.rounded = true;
	}

	public BarChartDataRenderExtended(int offset, int width, int orient, Color cdefault, HashMap colours) {
		this.offset = offset;
		this.width = width;
		this.orientation = orient;
		if (colours != null) {
			this.colours = colours;
		}
		if (this.colours.containsKey("defcolor") == false) {
			this.colours.put("defcolour", cdefault);
		}
	}

	public BarChartDataRenderExtended(int offset, int width, int orient, Color cdefault) {
		this(offset, width, orient, cdefault, new HashMap());
	}

	/**
	 * Set the bar width according to the data kind
	 */
	public void setDataWidths(Map widths) {
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

	private void coloredIcon(java.awt.Graphics2D g, java.awt.Rectangle r) {
		java.awt.Graphics2D gc = (Graphics2D) g.create();
		try {
			int i = 0;
			int w = r.width / this.colours.size();
			Iterator it = this.colours.keySet().iterator();
			while (it.hasNext()) {
				gc.setColor((Color) this.colours.get(it.next()));
				gc.fill3DRect(r.x + (i * w), r.y, w, r.height, true);
				i++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		gc.dispose();
	}

	private void colorDescriptionIcon(java.awt.Graphics2D g, java.awt.Rectangle r) {
		java.awt.Graphics2D gc = (Graphics2D) g.create();
		try {
			int i = 0;
			// int h = r.height / colours.size();
			Iterator it = this.colours.keySet().iterator();
			FontMetrics fm = gc.getFontMetrics();
			// while(h/fm.getAscent() < 1 && fm.getAscent()>2)
			gc.setFont(gc.getFont().deriveFont(-6));

			while (it.hasNext()) {
				Object item = it.next();
				gc.setColor((Color) this.colours.get(it.next()));
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

	/*
	 * public boolean contains(ChartData data, Shape sh) { try { ArrayList lstsh
	 * = (ArrayList)this.getShape(data); Iterator iter = lstsh.iterator(); while
	 * (iter.hasNext()) { Rectangle2D item = (Rectangle2D) iter.next(); if
	 * (item.contains( (Rectangle2D) sh)) { return true; } } } catch (Exception
	 * ex) { ex.printStackTrace(); } return false; }
	 */

	/**
	 * Retorna una lista con las formas utilizadas para representar el dato. Un
	 * determinado dato puede tener como representación varios rectangulos no
	 * conectados.
	 *
	 * @param data
	 *            ChartData Dato
	 * @return Lista de formas con las que se representará el dato.
	 */
	public Shape getShapes(Graphics g, ChartData data) {
		try {
			int width = this.width;
			if ((this.widths != null) && this.widths.containsKey(data.getKind())) {
				width = ((Integer) this.widths.get(data.getKind())).intValue();
			}

			java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
			if (this.at != null) {
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
				Graphics2D gc = (Graphics2D) g;
				Color oldc = gc.getColor();
				Color itemcolor = oldc;
				if (this.colours.containsKey(data.getKind())) {
					itemcolor = (Color) this.colours.get(data.getKind());
				} else {
					itemcolor = (Color) this.colours.get("defcolour");
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
					this.at.transform(src, 0, dst, 0, 1);
					xval = dst[0];
					yval = dst[1];
					if (this.orientation == BarChartDataRenderExtended.HORIZONTAL_BAR) {
						yval += this.offset;
					} else {
						xval += this.offset;
					}
					if (i == 0) { // the even points are the rectangle start
									// coordinates.
						if (this.orientation == BarChartDataRenderExtended.HORIZONTAL_BAR) {
							r.setFrame(0, yval, xval, width);
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
						if (this.orientation == BarChartDataRenderExtended.HORIZONTAL_BAR) {
							r.setFrame(r.getWidth(), r.getY(), xval - r.getWidth(), width);
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

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setBarWidth(int w) {
		this.width = w;
	}

	/**
	 * Indica si el punto a representar pertenece al borde del grafico.
	 *
	 * @param point
	 *            Object
	 * @return boolean
	 */
	private boolean IsBorderPoint(Object point) {
		if (point instanceof Date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) point);

			if (this.orientation == BarChartDataRenderExtended.HORIZONTAL_BAR) {
				if ((cal.get(Calendar.HOUR_OF_DAY) == cal.getActualMaximum(Calendar.HOUR_OF_DAY))
						&& (cal.get(Calendar.MINUTE) == cal.getActualMaximum(Calendar.MINUTE))
						&& (cal.get(Calendar.SECOND) == cal.getActualMaximum(Calendar.SECOND))
						&& (cal.get(Calendar.MILLISECOND) == cal.getActualMaximum(Calendar.MILLISECOND))) {
					return true;
				} else if ((cal.get(Calendar.HOUR_OF_DAY) == cal.getActualMinimum(Calendar.HOUR_OF_DAY))
						&& (cal.get(Calendar.MINUTE) == cal.getActualMinimum(Calendar.MINUTE))
						&& (cal.get(Calendar.SECOND) == cal.getActualMinimum(Calendar.SECOND))
						&& (cal.get(Calendar.MILLISECOND) == cal.getActualMinimum(Calendar.MILLISECOND))) {
					return true;
				}
			}
		}
		return false;
	}

	public double getOffset(Chart arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Shape getShapes(Graphics arg0, ChartData arg1, Chart arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(Graphics arg0, List arg1, Chart arg2) {
		// TODO Auto-generated method stub

	}
}
