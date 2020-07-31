package com.opentach.client.comp.activitychart.taskwrapper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRenderProvider;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.TaskParser;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.activities.IActivityService;
import com.opentach.common.tacho.data.Actividad;

/**
 * <p> Título: </p> <p> Descripción: Clase envoltorio de Activades para que sus datos puedan ser representados en un chart. <p> Empresa: Imatia </p>
 *
 * @author Pablo Dorgambide Aviles
 * @version 1.0
 */

public class ActividadesTaskWrapper extends Task implements TaskParser, ChartDataRenderProvider {
	public static Color					COLOR_ACTIVITY_WORK				= new Color(0, 46, 237, 255);
	public static Color					COLOR_ACTIVITY_AVAILABLE		= new Color(85, 213, 0, 255);
	public static Color					COLOR_ACTIVITY_REST				= new Color(255, 254, 0, 255);
	public static Color					COLOR_ACTIVITY_DRIVING			= new Color(225, 28, 0, 255);
	public static Color					COLOR_ACTIVITY_UNDEFINED		= new Color(254, 108, 12, 255);
	public static Color					COLOR_UNDERLINES_BACKGROUND		= new Color(57, 98, 126);
	public static Color					COLOR_ACTIVITY_UNDER_SHADOW		= Color.black;
	public static Color					COLOR_UNDERLINES_REGIMEN		= Color.RED;
	public static Color					COLOR_UNDERLINES_ORIGIN			= Color.YELLOW;
	public static Color					COLOR_UNDERLINES_FUERA_AMBITO	= Color.BLACK;
	public static Color					COLOR_UNDERLINES_TRANS_TREN		= Color.WHITE;

	public static final int				ACTIVITY_RULER_SPACE			= 11;
	public static final int				ACTIVIDADES_HEIGHT				= 17;
	public static final int				ACTIVIDADES_OFFSET				= RulerTaskWrapper.RULER_HEIGHT + ActividadesTaskWrapper.ACTIVITY_RULER_SPACE;
	public static final int				OUTFERRY_OFFSET					= ActividadesTaskWrapper.ACTIVIDADES_HEIGHT + 7;
	public static final int				INFRACCIONES_OFFSET				= ActividadesTaskWrapper.ACTIVIDADES_HEIGHT + 17;
	protected Object					matricula;
	protected Object					idconductor;
	protected int						duracion;
	protected static String[]			act_dscr;
	protected static ChartDataRender	render;
	protected String					origen;
	protected String					regimen;
	protected String					estadoTrjRanura;
	protected String					transTren;
	protected String					fueraAmbito;
	protected String					expandDescription;
	protected boolean					doingQuery;
	private String						numReq;

	static {
		ActividadesTaskWrapper.act_dscr = new String[6];
		ActividadesTaskWrapper.act_dscr[Actividad.CONDUCCION] = ApplicationManager.getTranslation("CONDUCCION");
		ActividadesTaskWrapper.act_dscr[Actividad.PAUSA_DESCANSO] = ApplicationManager.getTranslation("PAUSA_DESCANSO");
		ActividadesTaskWrapper.act_dscr[Actividad.DISPONIBILIDAD] = ApplicationManager.getTranslation("DISPONIBILIDAD");
		ActividadesTaskWrapper.act_dscr[Actividad.INDEFINIDA] = ApplicationManager.getTranslation("INDEFINIDA");
		ActividadesTaskWrapper.act_dscr[Actividad.TRABAJO] = ApplicationManager.getTranslation("TRABAJO");
	}

	protected class ActivityBarChartDataRender extends BarChartDataRender {

		public ActivityBarChartDataRender(int width, int orient, Color cdefault, HashMap colours) {
			super(width, orient, cdefault, colours);
		}

		@Override
		public void render(Graphics g, List data, Chart chart) {
			for (Object task : data) {
				if (task instanceof ActividadesTaskWrapper) {
					// La llamada a getShapes pinta la tarea
					// if (chart.sublegend2.equals("INFRACCIONES")){
					Shape shape = this.getShapes(g, (ChartData) task, chart);
					Rectangle rectangle = shape.getBounds();

					// OTROS
					// creo el background para las infracciones
					this.drawInfractionBackground(g, rectangle, chart);

				}
			}
		}

		protected void drawInfractionBackground(Graphics g, Rectangle r, Chart chart) {
			if (chart.infraccionesBand) {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_BACKGROUND);
				g.fillRect(r.x, r.y + ActividadesTaskWrapper.INFRACCIONES_OFFSET, r.width, 5);
			}
		}

		@Override
		public Shape getShapes(Graphics g, ChartData data, Chart chart) {
			Graphics2D gc = (Graphics2D) g.create();
			try {
				int width = this.width;
				if ((this.widths != null) && this.widths.containsKey(data.getKind())) {
					width = this.widths.get(data.getKind()).intValue();
				}

				java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
				if (chart.getAffineTransform() != null) {
					RectangularShape r = new Rectangle2D.Double();
					if (this.rounded) {
						RoundRectangle2D rr = new RoundRectangle2D.Double();
						rr.setRoundRect(0, 0, 10, 10, 6.0, 6.0);
						r = rr;
					}

					Iterator itx = data.getX().iterator();
					Iterator ity = data.getY().iterator();

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
						double[] dst = this.convertPoint(((Date) itx.next()).getTime(), ((Date) ity.next()).getTime(), chart.getAffineTransform());
						double xval = dst[0];
						double yval = dst[1] + ActividadesTaskWrapper.ACTIVIDADES_OFFSET;
						if (i == 0) { // the even points are the rectangle start
							// coordinates.
							r.setFrame(0, yval, xval, width);
							if (!itx.hasNext()) { // If nof points are odd
								gc.fill(r);
								if ((r.getWidth() > 0) && (r.getHeight() > 0)) {
									gp.append((Shape) r.clone(), false);
								}
								// Pintamos la barra por debajo de fuera de
								// ámbito/transporte en tren
								this.drawSublinesBackground(g, r, (ActividadesTaskWrapper) data, chart);
							}

						}

						if (i == 1) { // the odd points are the rectangle end
							// coordinates.
							// markend = true;
							r.setFrame(r.getWidth(), r.getY(), xval - r.getWidth(), width);

							gc.setColor(itemcolor);
							gc.fill(r);

							if ((r.getWidth() > 0) && (r.getHeight() > 0)) {
								gp.append((Shape) r.clone(), false);
							}

							// Pintamos la barra por debajo de fuera de
							// ámbito/transporte en tren
							this.drawSublinesBackground(g, r, (ActividadesTaskWrapper) data, chart);

						}

						// Pintamos una sombra por abajo
						gc.setColor(ActividadesTaskWrapper.COLOR_ACTIVITY_UNDER_SHADOW);
						Rectangle rb = r.getBounds();
						Composite old = gc.getComposite();
						gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
						int liney = rb.y + rb.height;
						gc.drawLine(rb.x, liney, rb.x + rb.width, liney);
						gc.setComposite(old);

					}
					gc.setColor(oldc);
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

		protected void drawSublinesBackground(Graphics g, RectangularShape r, ActividadesTaskWrapper data, Chart chart) {
			if (chart.sublegend1.equals("FERRY/OUT_TRAIN")) {
				this.drawTransporteTren(g, r.getBounds(), data);
			} else {
				this.drawRegimen(g, r.getBounds(), data);
				this.drawOrigen(g, r.getBounds(), data);
			}
		}

		protected void drawRegimen(Graphics g, Rectangle r, ActividadesTaskWrapper atw) {

			if ("1".equals(atw.regimen) && "0".equals(atw.estadoTrjRanura)) {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_REGIMEN);
			} else {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_BACKGROUND);
			}
			Rectangle rr = r.getBounds();
			g.fillRect(rr.x, rr.y + ActividadesTaskWrapper.INFRACCIONES_OFFSET, rr.width, 5);

		}

		protected void drawOrigen(Graphics g, Rectangle r, ActividadesTaskWrapper atw) {

			if ("M".equals(atw.origen)) {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_ORIGIN);
			} else {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_BACKGROUND);
			}
			Rectangle rr = r.getBounds();
			g.fillRect(rr.x, rr.y + ActividadesTaskWrapper.OUTFERRY_OFFSET, rr.width, 5);

		}

		protected void drawTransporteTren(Graphics g, Rectangle r, ActividadesTaskWrapper atw) {
			if ("S".equals(atw.fueraAmbito)) {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_FUERA_AMBITO);
			} else if ("S".equals(atw.transTren)) {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_TRANS_TREN);
			} else if ("M".equals(atw.origen)) {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_ORIGIN);
			} else {
				g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_BACKGROUND);
			}
			Rectangle rr = r.getBounds();
			g.fillRect(rr.x, rr.y + ActividadesTaskWrapper.OUTFERRY_OFFSET, rr.width, 5);

		}

		protected double[] convertPoint(long xval, long yval, AffineTransform at) {
			// transform to the screen pixel coordinates
			double[] src = new double[2];
			double[] dst = new double[2];
			src[0] = xval;
			src[1] = yval;
			at.transform(src, 0, dst, 0, 1);
			return dst;
		}
	}

	public ActividadesTaskWrapper() {
		super();
		this.expandDescription = null;
		this.doingQuery = false;
	}

	public ActividadesTaskWrapper(Actividad act) {
		super(act.idActividad, "" + act.tpActividad, act.fecComienzo, act.fecFin, null);
		this.duracion = act.getMinutos().intValue();
		this.matricula = act.matricula;
		this.idconductor = act.idConductor;
		this.buildDescription();
		this.expandDescription = null;
		this.doingQuery = false;
	}

	private String getRow(String data1, Object data2) {
		if ((data1 != null) && (data2 != null)) {
			return "<tr><td>" + data1 + "</td><td>" + data2 + "</td></tr>";
		}
		return "";
	}

	@Override
	public Object getId() {
		return null;
	}

	@Override
	public JComponent getVisualComponent() {
		return null;
	}

	@Override
	public void setX(List x) {}

	@Override
	public void setY(List y) {}

	protected void buildDescription() {
		SimpleDateFormat dfs = new SimpleDateFormat("(dd) HH:mm");
		StringBuffer str = new StringBuffer();
		str.append("<html> <body><h3> " + ApplicationManager.getTranslation(ActividadesTaskWrapper.act_dscr[Integer.parseInt(this.getKind())]) + " </h3>");
		str.append("<table title='act_dscr[Integer.parseInt(tipo)]' border='0' bordercolor='#FF0000'  frame='border'>");
		str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Inicio") + "</b>", dfs.format(this.getStart())));
		str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Fin") + "</b>", dfs.format(this.getEnd())));
		if ((this.getStart() != null) && (this.getEnd() != null)) {
			str.append(this.getRow("<b>" + ApplicationManager.getTranslation("Duracion") + "</b>",
					MinutesCellRender.parsearTiempoDisponible(this.duracion)));
		}
		str.append("</table></body></html>");
		this.setDescription(str.toString());
		this.expandDescription = null;

	}

	@Override
	public String getDescription(final Chart chart) {
		if ("2".equals(this.getKind())) {
			if (this.expandDescription != null) {
				return this.expandDescription;
			}
			String desc = ActividadesTaskWrapper.super.getDescription(chart);
			StringBuilder str = new StringBuilder();
			String primer = desc.substring(0, 278);
			String segundo = desc.substring(279, desc.length());
			str.append(primer);
			str.append("<tr><td>" + ApplicationManager.getTranslation("COND2") + "</td><td>" + ApplicationManager.getTranslation("QUERYING") + "</td></tr>");
			str.append(segundo);
			desc = str.toString();
			if (!this.doingQuery) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						ActividadesTaskWrapper.this.doingQuery = true;
						StringBuilder str = new StringBuilder();
						Hashtable av = new Hashtable();
						if (ActividadesTaskWrapper.this.numReq != null) {
							av.put("NUMREQ", ActividadesTaskWrapper.this.numReq);
						}
						av.put("IDCONDUCTOR", ActividadesTaskWrapper.this.getIdConductor());
						av.put("FEC_COMIENZO", ActividadesTaskWrapper.this.getStart());
						av.put("FEC_FIN", ActividadesTaskWrapper.this.getEnd());
						av.put("MATRICULA", ActividadesTaskWrapper.this.getMatricula());
						try {
							UserInfoProvider ocl = (UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator();
							EntityResult res = ocl.getRemoteService(IActivityService.class).getConduccion(av, ocl.getSessionId());
							Object cond2 = ApplicationManager.getTranslation("NO_INFO");
							if ((res != null) && (res.calculateRecordNumber() == 1)) {
								cond2 = res.getRecordValues(0).get("COND2");
								if ((cond2 == null) || "".equals(cond2)) {
									cond2 = ApplicationManager.getTranslation("NO_INFO");
								}
							}
							String desc = ActividadesTaskWrapper.super.getDescription(chart);
							String primer = desc.substring(0, 278);
							String segundo = desc.substring(279, desc.length());
							str.append(primer);
							str.append("<tr><td><b>" + ApplicationManager.getTranslation("COND2") + "</b></td><td>" + cond2 + "</td></tr>");
							str.append(segundo);
							ActividadesTaskWrapper.this.expandDescription = str.toString();

						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void done() {
						Point locationOnScreen = MouseInfo.getPointerInfo().getLocation();
						Point locationOnComponent = new Point(locationOnScreen);
						SwingUtilities.convertPointFromScreen(locationOnComponent, chart);
						// if (label.contains(locationOnComponent)) {
						ToolTipManager.sharedInstance().mouseMoved(
								new MouseEvent(chart, -1, System.currentTimeMillis(), 0, locationOnComponent.x, locationOnComponent.y,
										locationOnScreen.x, locationOnScreen.y, 0, false, 0));
						// }

					}
				};
				worker.execute();
			}
			return desc;
		}
		return super.getDescription(chart);
	}

	@Override
	public void parse(Map cv) {
		this.setKind("" + cv.get("TPACTIVIDAD"));
		this.setStart((Date) cv.get("FECINI"));
		this.setEnd((Date) cv.get("FECFIN"));
		this.matricula = cv.get("MATRICULA");
		this.duracion = ((Number) cv.get("MINUTOS")).intValue();
		this.transTren = (String) cv.get("TRANS_TREN");
		this.origen = (String) cv.get("ORIGEN");
		this.regimen = (String) cv.get("REGIMEN");
		this.estadoTrjRanura = (String) cv.get("ESTADO_TRJ_RANURA");
		this.numReq = (String) cv.get("NUMREQ");
		this.fueraAmbito = (String) cv.get("FUERA_AMBITO");
		this.idconductor = cv.get("IDCONDUCTOR");
		this.buildDescription();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ChartDataRender getChartDataRender() {
		if (ActividadesTaskWrapper.render == null) {
			HashMap cmap1 = this.buildRendererColorMap();
			// Alto en función de la actividad.
			HashMap heights = this.buildRendererTaskHeights();

			BarChartDataRender barrender = new ActivityBarChartDataRender(10, BarChartDataRender.HORIZONTAL_BAR, Color.red, cmap1);
			barrender.setDataWidths(heights);
			barrender.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			barrender.setRoundedRectangle(false);
			ActividadesTaskWrapper.render = barrender;
		}
		return ActividadesTaskWrapper.render;
	}

	protected HashMap<String, Integer> buildRendererTaskHeights() {
		HashMap<String, Integer> heights = new HashMap<>();

		heights.put("" + Actividad.INDEFINIDA, ActividadesTaskWrapper.ACTIVIDADES_HEIGHT);
		heights.put("" + Actividad.CONDUCCION, ActividadesTaskWrapper.ACTIVIDADES_HEIGHT);
		heights.put("" + Actividad.PAUSA_DESCANSO, ActividadesTaskWrapper.ACTIVIDADES_HEIGHT);
		heights.put("" + Actividad.DISPONIBILIDAD, ActividadesTaskWrapper.ACTIVIDADES_HEIGHT);
		heights.put("" + Actividad.TRABAJO, ActividadesTaskWrapper.ACTIVIDADES_HEIGHT);
		return heights;
	}

	protected HashMap<String, Color> buildRendererColorMap() {
		HashMap<String, Color> cmap1 = new HashMap<>();
		cmap1.put("" + Actividad.INDEFINIDA, ActividadesTaskWrapper.COLOR_ACTIVITY_UNDEFINED);
		cmap1.put("" + Actividad.CONDUCCION, ActividadesTaskWrapper.COLOR_ACTIVITY_DRIVING);
		cmap1.put("" + Actividad.PAUSA_DESCANSO, ActividadesTaskWrapper.COLOR_ACTIVITY_REST);
		cmap1.put("" + Actividad.DISPONIBILIDAD, ActividadesTaskWrapper.COLOR_ACTIVITY_AVAILABLE);
		cmap1.put("" + Actividad.TRABAJO, ActividadesTaskWrapper.COLOR_ACTIVITY_WORK);
		return cmap1;
	}

	public int getDuracion() {
		return this.duracion;
	}

	public Object getMatricula() {
		return this.matricula;
	}

	public Object getIdConductor() {
		return this.idconductor;
	}

}
