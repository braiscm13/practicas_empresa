package com.opentach.client.modules.chart;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataField;
import com.opentach.client.comp.activitychart.ChartDataSet;
import com.opentach.client.comp.activitychart.IAfterPaintEffectChart;
import com.opentach.client.comp.activitychart.taskwrapper.ActividadesTaskWrapper;
import com.opentach.client.comp.activitychart.taskwrapper.RulerTaskWrapper;
import com.opentach.client.comp.render.HourCellRender;

public class ActivityRulerMouseListener extends MouseAdapter implements IAfterPaintEffectChart {

	static ImageIcon					marker						= ImageManager.getIcon("com/opentach/client/rsc/chart/inicioPink.png");
	private static final int			BOTTOM_SHADOWN_PIXELS_IMAGE	= 14;
	protected ChartDataField			chartDatafield;
	protected DatePercent				firstStartDate;
	protected DatePercent				secondStartDate;
	protected Date						fecha;
	protected JLabel					labelSelection;

	protected SelectionSummaryComputer	selectionSummaryComputer;

	public ActivityRulerMouseListener(ChartDataField chartDataField) {
		super();
		this.chartDatafield = chartDataField;
		this.chartDatafield.getChart().addAfterPaintEffects(this);
		this.labelSelection = new JLabel("");
		this.labelSelection.setName(ChartDataField.CHART_RESUME_LABEL_NAME);
		JPanel auxpanel = chartDataField.getEmptySlot();
		auxpanel.add(this.labelSelection, BorderLayout.CENTER);
		this.firstStartDate = null;
		this.secondStartDate = null;
		this.selectionSummaryComputer = new SelectionSummaryComputer();
		this.selectionSummaryComputer.computeSummary(new EntityResult(), null, null);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if ((this.firstStartDate != null) && (this.secondStartDate != null)) {
			this.firstStartDate = null;
			this.secondStartDate = null;
		} else {
			if (this.firstStartDate == null) {
				this.firstStartDate = this.getDataAtPoint(e.getPoint());
			} else if (this.secondStartDate == null) {
				this.secondStartDate = this.getDataAtPoint(e.getPoint());
			}
		}
		if ((this.firstStartDate != null) && (this.secondStartDate != null)) {
			if (this.firstStartDate.getDate().getTime() == this.secondStartDate.getDate().getTime()) {
				if (this.firstStartDate.getPercent() > this.secondStartDate.getPercent()) {
					DatePercent tmp = this.firstStartDate;
					this.firstStartDate = this.secondStartDate;
					this.secondStartDate = tmp;
				}
			}
			if (this.firstStartDate.getDate().getTime() > this.secondStartDate.getDate().getTime()) {
				DatePercent tmp = this.firstStartDate;
				this.firstStartDate = this.secondStartDate;
				this.secondStartDate = tmp;
			}
		}
		String selectionText = this.selectionSummaryComputer.computeSummary(this.chartDatafield.getEntityData("EActividades"), this.firstStartDate,
				this.secondStartDate);
		this.labelSelection.setText(selectionText);
		if ((this.firstStartDate != null) && (this.secondStartDate != null)) {
			this.chartDatafield.getChart().repaint();
		}
	}

	public DatePercent getDataAtPoint(Point p) {
		Iterator iter = this.chartDatafield.getChart().getCharts().iterator();
		while (iter.hasNext()) {
			ChartDataSet dataset = (ChartDataSet) iter.next();
			Iterator iter2 = dataset.getData().iterator();
			while (iter2.hasNext()) {
				ChartData data = (ChartData) iter2.next();
				Shape sh = data.getShape();
				if ((sh != null) && (sh.contains(p))) {
					if (data instanceof ActividadesTaskWrapper) {
						ActividadesTaskWrapper activity = (ActividadesTaskWrapper) data;
						double percent = ((p.x - sh.getBounds().x) / (double) (sh.getBounds().width));
						int duracion = activity.getDuracion();
						Date d = new Date(activity.getStart().getTime());
						return new DatePercent(d, percent, duracion);
					}
				}
			}
		}
		return null;
	}

	protected static class DatePercent {
		protected Date		date;
		protected double	percent;
		protected int		duracion;

		public DatePercent(Date date, double percent, int duracion) {
			this.date = date;
			this.percent = percent;
			this.duracion = duracion;
		}

		public Date getDate() {
			return this.date;
		}

		public double getPercent() {
			return this.percent;
		}

		public int getDuracion() {
			return this.duracion;
		}

		@Override
		public String toString() {
			return this.date.toString() + " " + this.percent;
		}
	}

	protected static class SelectionSummaryComputer {
		protected SimpleDateFormat	sdf	= new SimpleDateFormat("dd/MM/yyyy HH:mm");

		private String[]			values;

		public SelectionSummaryComputer() {
			String begin = "";
			String end = "";
			String timeCond = "";
			String timeDis = "";
			String timePaus = "";
			String timeTrab = "";
			String timeIndef = "";

			begin = "Selecc. fecha";
			end = "Selecc. fecha";
			this.values = new String[] { begin, end, timeCond, timePaus, timeTrab, timeDis, timeIndef };
			this.refreshSummary();
		}

		public String computeSummary(EntityResult data, DatePercent from, DatePercent to) {
			if (data == null) {
				return "";
			}
			String begin = "";
			String end = "";
			String timeCond = "";
			String timeDis = "";
			String timePaus = "";
			String timeTrab = "";
			String timeIndef = "";
			String timeTotal = "";

			final int count = data.calculateRecordNumber();
			if ((from != null) && (to != null)) {
				long mouseStartTime = from.getDate().getTime();
				mouseStartTime += from.getDuracion() * 60 * 1000l * from.getPercent();
				long mouseEndTime = to.getDate().getTime();
				mouseEndTime += to.getDuracion() * 60 * 1000l * to.getPercent();

				double tcond = 0;
				double tdis = 0;
				double ttrab = 0;
				double tpaus = 0;
				double tindef = 0;
				double total = 0;

				for (int i = 0; i < count; i++) {
					Hashtable htFila = data.getRecordValues(i);

					Date activityStart = (Date) htFila.get("FECINI");
					long activityStartTime = activityStart.getTime();
					Date activityEnd = (Date) htFila.get("FECFIN");
					long activityEndTime = activityEnd.getTime();
					// Trunco fec comienzo
					Number nTipo = (Number) htFila.get("TPACTIVIDAD");
					Number nMinutos = (Number) htFila.get("MINUTOS");
					double time = 0;
					if ((nTipo != null) && (nMinutos != null)) {
						if ((activityStartTime >= mouseStartTime) && (activityEndTime <= mouseEndTime)) {
							time = nMinutos.doubleValue();

						} else if ((activityStartTime <= mouseStartTime) && (activityEndTime >= mouseEndTime)) {
							// entre
							time = ((to.getPercent() - from.getPercent()) * nMinutos.doubleValue());
						} else if ((activityStartTime >= mouseStartTime) && (activityEndTime >= mouseEndTime) && (activityStartTime < mouseEndTime)) {
							// final
							time = to.getPercent() * nMinutos.doubleValue();
						} else if ((activityStartTime <= mouseStartTime) && (activityEndTime <= mouseEndTime) && (activityEndTime > mouseStartTime)) {
							// inicial
							time = (1 - from.getPercent()) * nMinutos.doubleValue();
						}
						if (time > 0) {
							total += time;
							switch (nTipo.intValue()) {
								case 1: // PAUSA/DESCANSO
									tpaus += time;
									break;
								case 2: // DISPONIBILIDAD
									tdis += time;
									break;
								case 3: // TRABAJO
									ttrab += time;
									break;
								case 4: // CONDUCCION
									tcond += time;
									break;
								case 5: // CONDUCCION
									tindef += time;
									break;
							}
						}
					}
				}
				timeCond = HourCellRender.parsearTiempoDisponible(tcond, HourCellRender.MINUTES);
				timeDis = HourCellRender.parsearTiempoDisponible(tdis, HourCellRender.MINUTES);
				timePaus = HourCellRender.parsearTiempoDisponible(tpaus, HourCellRender.MINUTES);
				timeTrab = HourCellRender.parsearTiempoDisponible(ttrab, HourCellRender.MINUTES);
				timeIndef = HourCellRender.parsearTiempoDisponible(tindef, HourCellRender.MINUTES);
				timeTotal = HourCellRender.parsearTiempoDisponible(total, HourCellRender.MINUTES);

			}
			if (from != null) {
				long mouseStartTime = from.getDate().getTime();
				mouseStartTime += from.getDuracion() * 60 * 1000l * from.getPercent();
				Date mouseStart = new Date(mouseStartTime);
				begin = this.sdf.format(mouseStart);
			} else {
				begin = "Selecc. fecha";
			}
			if (to != null) {
				long mouseEndTime = to.getDate().getTime();
				mouseEndTime += to.getDuracion() * 60 * 1000l * to.getPercent();
				Date mouseEnd = new Date(mouseEndTime);
				end = this.sdf.format(mouseEnd);
			} else {
				end = "Selecc. fecha";
			}

			if ((timeTotal == null) || (timeTotal == "")) {
				timeTotal = "HH:mm";
			}
			this.values = new String[] { begin, end, timeCond, timePaus, timeTrab, timeDis, timeIndef, timeTotal };
			return this.refreshSummary();
		}

		public String refreshSummary() {
			if (this.values != null) {
				String text = ApplicationManager.getTranslation("ACT_SELECTION_TEXT", ApplicationManager.getApplicationBundle(), this.values);
				return text;
			}
			return null;
		}

	}

	@Override
	public void paint(Graphics g, Chart chart) {
		if (this.firstStartDate != null) {
			this.paintMarker(g, BarChartDataRender.HORIZONTAL_BAR, chart.getAffineTransform(), "1", this.firstStartDate);
		}
		if (this.secondStartDate != null) {
			this.paintMarker(g, BarChartDataRender.HORIZONTAL_BAR, chart.getAffineTransform(), "2", this.secondStartDate);
		}
	}

	protected void paintMarker(Graphics g, int orientation, AffineTransform at, String number, DatePercent info) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(info.getDate());
		GregorianCalendar hstartofday = new GregorianCalendar(1970, 0, 1, cal.get(11), cal.get(12), cal.get(13));
		double mouseStartTimeX = hstartofday.getTime().getTime();
		mouseStartTimeX += (info.getDuracion() * 60 * 1000l * info.getPercent());

		cal.setTime(info.getDate());
		cal.set(11, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(12, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(13, cal.getActualMinimum(Calendar.SECOND));
		cal.set(14, cal.getActualMinimum(Calendar.MILLISECOND));

		double mouseStartTimeY = cal.getTime().getTime();

		double[] src = new double[2];
		double[] dst = new double[2];
		src[0] = mouseStartTimeX; // transform to the screen pixel coordinates
		src[1] = mouseStartTimeY;
		at.transform(src, 0, dst, 0, 1);
		double xval = dst[0];
		double yval = dst[1];
		yval += ActividadesTaskWrapper.ACTIVITY_RULER_SPACE + RulerTaskWrapper.RULER_HEIGHT;

		int imagex = (int) xval - (ActivityRulerMouseListener.marker.getIconWidth() / 2);
		int imagey = ((int) yval - ActivityRulerMouseListener.marker.getIconHeight()) + ActivityRulerMouseListener.BOTTOM_SHADOWN_PIXELS_IMAGE;
		g.drawImage(ActivityRulerMouseListener.marker.getImage(), imagex, imagey, null);

		int strWidth = g.getFontMetrics().stringWidth(number);
		int strx = imagex + ((ActivityRulerMouseListener.marker.getIconWidth() - strWidth) / 2) + 1; // ajustamos
		// a
		// mano
		// para
		// centrarla bien en el icono
		// por culpa de las sombras
		int stry = imagey + g.getFontMetrics().getHeight();// ajustamos a mano
		// para centrarla
		// bien en
		// el
		// icono por culpa de las sombras

		g.drawString(number, strx, stry);
	}

}