package com.opentach.client.comp.activitychart.taskwrapper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.ontimize.gui.images.ImageManager;
import com.opentach.client.comp.activitychart.AxisVertical;
import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRenderProvider;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.TaskParser;

public class PaisesTaskWrapper extends Task implements TaskParser, ChartDataRenderProvider {
	private static ImageIcon			marker_begin							= ImageManager
			.getIcon("com/opentach/client/rsc/chart/marker_pais_begin.png");
	private static ImageIcon			marker_begin_reverse					= ImageManager
			.getIcon("com/opentach/client/rsc/chart/marker_pais_begin_reverse.png");
	private static ImageIcon			marker_middle							= ImageManager
			.getIcon("com/opentach/client/rsc/chart/marker_pais_middle.png");
	private static ImageIcon			marker_end								= ImageManager
			.getIcon("com/opentach/client/rsc/chart/marker_pais_end.png");
	private static ImageIcon			marker_end_reverse						= ImageManager
			.getIcon("com/opentach/client/rsc/chart/marker_pais_end_reverse.png");
	// private static ImageIcon marker =
	// ImageManager.getIcon("com/opentach/client/rsc/chart/inicioLargo.png");
	// private static ImageIcon[] availableMarkers = new ImageIcon[] { marker0,
	// marker, marker2,
	// marker3 };
	private static final int			LEFT_SHADOWN_PIXELS_IMAGE_EXPANDED		= 12;
	private static final ImageIcon		IMAGE_MARK_COLLAPSED					= ImageManager
			.getIcon("com/opentach/client/rsc/chart/inicio.png");
	// números de pixels de sombreado que tienen las imágenes por abajo
	private static int					BOTTOM_SHADOWN_PIXELS_IMAGE_COLLAPSED	= 14;

	protected static ChartDataRender	render;
	protected String					region;
	protected int						idperiodo;
	protected String					paisdscr;
	protected String					regiondscr;
	protected String					pais;
	protected boolean					clicked;
	protected Date						fecha;
	protected boolean					reversed;

	public PaisesTaskWrapper() {
		super();
	}

	public String getPais() {
		return this.pais;
	}

	public String getRegion() {
		return this.regiondscr;
	}

	public boolean isClicked() {
		return this.clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	@Override
	public void parse(Map cv) {
		this.setId(Integer.valueOf(0));
		this.setKind((String) cv.get("DSCRTIPO"));
		this.setStart((Date) cv.get("FECINI"));
		this.setEnd(new Date(((Date) cv.get("FECINI")).getTime() + 1500000));
		this.pais = (String) cv.get("PAIS");
		this.paisdscr = (String) cv.get("DSCRPAIS");
		this.clicked = false;
		if (cv.get("IDREGION") != null) {
			this.region = (String) cv.get("IDREGION");
			this.regiondscr = (String) cv.get("DSCRREGION");
		}
		this.idperiodo = ((BigDecimal) cv.get("TPPERIODO")).intValue();
		this.fecha = (Date) cv.get("FECINI");
	}

	@Override
	public String getDescription(Chart chart) {
		return null;
	}

	@Override
	public ChartDataRender getChartDataRender() {
		if (PaisesTaskWrapper.render == null) {
			PaisesTaskWrapper.render = new PaisChartDataRender(30, BarChartDataRender.VERTICAL_BAR, new Color(35, 15, 255, 0), null);
		}
		return PaisesTaskWrapper.render;
	}

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
		cal.setTime(this.getStart());
		// old task for the same day have the y value mark to the start of the
		// day.
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));

		lst.add(cal.getTime());
		return lst;
	}

	protected class PaisChartDataRender extends BarChartDataRender {

		public PaisChartDataRender(int width, int orient, Color cdefault, HashMap colours) {
			super(width, orient, cdefault, colours);
		}

		@Override
		public void render(Graphics g, List data, Chart chart) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			List<PaisesTaskWrapper> selectedTasks = new ArrayList<PaisesTaskWrapper>();
			for (Object task : data) {
				if (task instanceof PaisesTaskWrapper) {
					// Dejamos para el final la tarea que esté selected
					if (((PaisesTaskWrapper) task).isClicked()) {
						selectedTasks.add((PaisesTaskWrapper) task);
					} else {
						this.renderCollapsed(g, (PaisesTaskWrapper) task, chart);
					}
				}
			}
			for (PaisesTaskWrapper task : selectedTasks) {
				this.renderExpanded(g, task, chart);
			}
		}

		private void renderExpanded(Graphics gc, PaisesTaskWrapper paisTask, Chart chart) {
			Graphics g = gc.create();
			try {

				String idpais = paisTask.getPais();
				ImageIcon markerFlag = null;
				try {
					String flagName = Integer.toHexString(Integer.parseInt(idpais)) + ".png";
					markerFlag = ImageManager.getIcon("com/opentach/client/rsc/banderas/" + flagName);
				} catch (Exception e) {
					markerFlag = ImageManager.getIcon("com/opentach/client/rsc/banderas/Desconocido.png");
				}

				SimpleDateFormat sdfDateTime = new SimpleDateFormat("HH'h'mm\"");
				String region = "0".equals(paisTask.region) ? paisTask.paisdscr : paisTask.regiondscr;

				String labelTipo = paisTask.getKind().startsWith("C") ? "I" : "F";
				String labelHoraRegion = sdfDateTime.format(paisTask.fecha) + " " + region;

				g.setFont(AxisVertical.FONT_DAY_NAME);

				int strWidth = g.getFontMetrics().stringWidth(labelTipo + " " + labelHoraRegion);
				int offsetX = 10; // ajustamos a mano para centrarla bien en el
				// icono por culpa de las sombras
				int offsetY = g.getFontMetrics().getHeight() + 1;// ajustamos a
				// mano para
				// centrarla
				// bien en
				// el
				// icono por
				// culpa de
				// las
				// sombras
				int marginRight = 15;
				// Calculamos el ancho total para saber que imagen de fondo
				// utilizar
				int fullInfoWidth = offsetX + strWidth + marginRight;
				if (markerFlag != null) {
					fullInfoWidth += markerFlag.getIconWidth();
				}

				Shape s = this.getShapes(g, paisTask, chart, fullInfoWidth, PaisesTaskWrapper.marker_begin.getIconHeight(),
						PaisesTaskWrapper.LEFT_SHADOWN_PIXELS_IMAGE_EXPANDED);
				Rectangle r = s.getBounds();

				BufferedImage bgImage = new BufferedImage(fullInfoWidth, PaisesTaskWrapper.marker_begin.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics gbg = bgImage.getGraphics();
				if (!paisTask.reversed) {
					gbg.drawImage(PaisesTaskWrapper.marker_begin.getImage(), 0, 0, null);
					gbg.drawImage(PaisesTaskWrapper.marker_end.getImage(), fullInfoWidth - PaisesTaskWrapper.marker_end.getIconWidth(), 0, null);
					for (int i = PaisesTaskWrapper.marker_begin.getIconWidth(); i < (fullInfoWidth - PaisesTaskWrapper.marker_end.getIconWidth()); i++) {
						gbg.drawImage(PaisesTaskWrapper.marker_middle.getImage(), i, 0, null);
					}
				} else {
					gbg.drawImage(PaisesTaskWrapper.marker_end_reverse.getImage(), 0, 0, null);
					gbg.drawImage(PaisesTaskWrapper.marker_begin_reverse.getImage(),
							fullInfoWidth - PaisesTaskWrapper.marker_begin_reverse.getIconWidth(), 0, null);
					for (int i = PaisesTaskWrapper.marker_end_reverse.getIconWidth(); i < (fullInfoWidth - PaisesTaskWrapper.marker_begin_reverse
							.getIconWidth()); i++) {
						gbg.drawImage(PaisesTaskWrapper.marker_middle.getImage(), i, 0, null);
					}
				}

				g.drawImage(bgImage, r.x, r.y, null);

				g.setColor(AxisVertical.FONT_COLOR_DAY_NAME);
				int y = r.y + offsetY + 2;
				int x = r.x + offsetX;
				g.drawString(labelTipo, x, y);
				x += g.getFontMetrics().stringWidth(labelTipo + " ");
				g.setColor(Color.black);
				g.drawString(labelHoraRegion, x, y);
				if (markerFlag != null) {
					int flagx = (r.x + r.width) - markerFlag.getIconWidth() - 7;
					int flagy = r.y + 4;
					g.drawImage(markerFlag.getImage(), flagx, flagy, null);
				}
			} finally {
				g.dispose();
			}
		}

		private void renderCollapsed(Graphics gc, PaisesTaskWrapper task, Chart chart) {
			Graphics g = gc.create();
			try {
				ImageIcon markFlag = PaisesTaskWrapper.IMAGE_MARK_COLLAPSED;
				Shape s = this.getShapes(g, task, chart, markFlag.getIconWidth(), markFlag.getIconHeight(), markFlag.getIconWidth() / 2);
				Rectangle r = s.getBounds();

				String letra = "";
				if (task.getKind().startsWith("C")) {
					letra = "I";
				} else if (task.getKind().startsWith("F")) {
					letra = "F";
				}

				if (markFlag != null) {
					// markFlag.setDescription(atw.getDescription());
					Image img = markFlag.getImage();
					g.drawImage(img, r.x, r.y, markFlag.getImageObserver());

					g.setColor(AxisVertical.FONT_COLOR_DAY_NAME);
					g.setFont(AxisVertical.FONT_DAY_NAME);

					int strWidth = g.getFontMetrics().stringWidth(letra);
					int offsetX = ((markFlag.getIconWidth() - strWidth) / 2) + 1; // ajustamos
					// a
					// mano
					// para
					// centrarla
					// bien
					// en
					// el
					// icono
					// por
					// culpa
					// de
					// las
					// sombras
					int offsetY = g.getFontMetrics().getHeight() + 0;// ajustamos
					// a
					// mano
					// para
					// centrarla
					// bien
					// en el
					// icono
					// por
					// culpa
					// de
					// las
					// sombras
					g.drawString(letra, r.x + offsetX, r.y + offsetY);

				}
			} finally {
				g.dispose();
			}

		}

		public Shape getShapes(Graphics g, PaisesTaskWrapper data, Chart chart, int width, int height, int offsetXToRealMark) {
			try {
				data.reversed = false;
				RectangularShape r = new Rectangle2D.Double();
				if (chart.getAffineTransform() != null) {
					Object xobj, yobj;
					double xval = 0, yval = 0;
					double[] src = new double[2];
					double[] dst = new double[2];

					xobj = data.getX().get(0);
					yobj = data.getY().get(0);

					xval = ((Date) xobj).getTime();
					yval = ((Date) yobj).getTime();

					src[0] = xval; // transform to the screen pixel
					// coordinates
					src[1] = yval;
					chart.getAffineTransform().transform(src, 0, dst, 0, 1);
					xval = dst[0];
					yval = dst[1];
					double rx = xval - offsetXToRealMark;
					double ry = (yval - height) + PaisesTaskWrapper.BOTTOM_SHADOWN_PIXELS_IMAGE_COLLAPSED + RulerTaskWrapper.RULER_HEIGHT
							+ ActividadesTaskWrapper.ACTIVITY_RULER_SPACE;
					if ((rx + width) > chart.getWidth()) {
						data.reversed = true;
						rx = (rx - width) + offsetXToRealMark + 12;
					}
					r.setFrame(rx, ry, width, height);

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
