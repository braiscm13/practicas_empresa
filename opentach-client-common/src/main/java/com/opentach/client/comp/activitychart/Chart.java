package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contenedor de series de datos ({@link ChartDataSet ChartDataSet}) para la representación gráfica. En el chart podrán añadirse todas aquellas series
 * de datos que se deseen representar. La leyenda mostrará la descripción cada una de las series de datos <br> En la configuración del chart debe
 * establecerse el rango de los ejes y las series de datos añadidas deberán estar en concordancia con los tipos de datos representados en el chart.
 *
 * @author Pablo Dorgambide
 * @version 1.0
 * @see Chart.Axis
 * @see ChartDataSet
 */
public class Chart extends JPanel {

	private static final Logger				logger							= LoggerFactory.getLogger(Chart.class);

	public static Color						COLOR_BACKGROUND				= new Color(0x5b8aab);
	public static Color						COLOR_SPACE						= new Color(0x406e8e);
	public static Color						COLOR_SEPARATOR_SECOND_BORDER	= new Color(0x719dbc);
	public static Color						COLOR_SEPARATOR_FIRST_BORDER	= new Color(0x2d5b7b);

	public static final byte				TITLE							= 0;
	public static final byte				DATA_DESCRIPTION				= 1;

	protected Axis							xAxisBottom, xAxisTop, yAxisLeft;
	protected AffineTransform				at								= null;
	protected ArrayList<ChartDataSet>		charts							= new ArrayList<>();
	protected boolean						showtitle						= false;
	public String							sublegend1						= null;
	public String							sublegend2						= null;
	public boolean							infraccionesBand				= true;
	protected BufferedImage					offImg							= null;

	protected List<IAfterPaintEffectChart>	afterPaintEffects;

	/**
	 * Coordenadas del origen del chart en pixels desde la esquina inferior izquierda.
	 */
	protected int							topXaxisHeight					= 17;
	protected int							bottomXaxisHeight				= 27;
	protected int							leftYaxisWidth					= 100;
	protected int							numVerticalDivisions			= 7;
	protected int							rigthYaxisWidth					= 17;

	public Chart(String sub1, String sub2) {
		super();
		this.afterPaintEffects = new ArrayList<IAfterPaintEffectChart>();
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setDismissDelay(1000 * 60 * 5);
		this.sublegend1 = sub1;
		this.sublegend2 = sub2;
		if (!"INFRACCIONES".equals(sub2)) {
			this.infraccionesBand = false;
		}

		this.xAxisBottom = new AxisHorizontalBottom(this);
		this.yAxisLeft = new AxisVertical(this);
		this.xAxisTop = new AxisHorizontalTop(this);
		// set defaults
		this.setLayout(null);
		this.setBackground(Chart.COLOR_BACKGROUND);
		this.add(this.xAxisBottom);
		this.add(this.xAxisTop);
		this.add(this.yAxisLeft);
		this.showtitle = false;

	}

	public void setNumVerticalDivisions(int numVerticalDivisions) {
		this.numVerticalDivisions = numVerticalDivisions;
	}

	public int getCellHeight() {
		return (this.getHeight() - this.topXaxisHeight - this.bottomXaxisHeight) / this.numVerticalDivisions;
	}

	public int getDataWidth() {
		return this.getWidth() - this.leftYaxisWidth - this.rigthYaxisWidth;
	}

	public int getRigthYaxisWidth() {
		return this.rigthYaxisWidth;
	}

	public int getDataHeight() {
		return this.getCellHeight() * this.getNumVerticalDivisions();
	}

	public int getNumVerticalDivisions() {
		return this.numVerticalDivisions;
	}

	public int getLeftYaxisWidth() {
		return this.leftYaxisWidth;
	}

	public int getTopXaxisHeight() {
		return this.topXaxisHeight;
	}

	public int getBottomXaxisHeight() {
		return this.bottomXaxisHeight;
	}

	/**
	 * Activa desactiva la visuliazacion de: <ul> <li>Legenda ( Chart.LEGEND ) <li>Descripción de los datos ( Chart.DATA_DESCRIPTION ) <li>Titulo del
	 * Grafico ( Chart.TITLE ) </ul>
	 */
	public void setVisible(byte code, boolean b) {
		switch (code) {
			case Chart.TITLE:
				this.showtitle = b;
				break;
			case Chart.DATA_DESCRIPTION:
				for (ChartDataSet item : this.charts) {
					item.getChartDataRender().showDataDescription(b);
				}
				break;
		}
	}

	/**
	 * @param xy
	 *            int Codigo del eje
	 * @return Axis Referencia al eje solicitado
	 */
	public void setVerticalAxisCfg(Object minval, Object maxval, int numVerticalDivisions) {
		this.setNumVerticalDivisions(numVerticalDivisions);
		this.yAxisLeft.setAxisCfg(minval, maxval);
	}

	public void setHorizontalAxisCfg(Object minval, Object maxval) {
		this.xAxisTop.setAxisCfg(minval, maxval);
		this.xAxisBottom.setAxisCfg(minval, maxval);
		this.refresh();

	}

	/**
	 * Calcula la transformacion que deberan pasar los datos para la obtencion de sus coordenadas en pixels.
	 */
	private void calculateAffineTransform() {
		if (this.at == null) {
			this.at = new AffineTransform();
		} else {
			this.at.setToIdentity();
		}
		double rangoX = this.xAxisBottom.getRange();
		double rangoY = this.yAxisLeft.getRange();
		double minX = this.xAxisBottom.getMinVal();
		double minY = this.yAxisLeft.getMinVal();

		/*
		 * MATRIZ DE TRANSFORMACION (xpixel) (1 0 orig.getX() ) ( ) (1 0 -minX) ( Xvalue) (ypixel) = (0 1 getHeight()-orig.getY()) (scale) (0 1 -minY)
		 * ( Yvalue) ( 1 ) (0 0 1 ) ( ) (0 0 1 ) ( 1 )
		 */
		this.at.translate(this.leftYaxisWidth, this.getHeight() - this.bottomXaxisHeight - this.getCellHeight());
		this.at.scale(this.getDataWidth() / rangoX, -this.getDataHeight() / rangoY);
		this.at.translate(-minX, -minY);
	}

	/**
	 * Devuelve la transformación afin para obtener las coordenadas en pixels de los valores a representar.
	 *
	 * @return Transformación afin para obtener las coordenadas en pixels.
	 */
	public AffineTransform getAffineTransform() {
		return this.at;
	}

	/**
	 * Añade una serie de datos ({@link ChartDataSet ChartDataSet}) al grafico. La serie debe poseer los mismos tipos de datos que los configurados en
	 * el gráfico, de lo contrario lanzará una excepción.
	 *
	 * @param ch
	 *            Serie de datos
	 * @throws IllegalArgumentException
	 *             Si los datos son de diferente tipo a los representados en el gráfico.
	 */
	public void addData(ChartDataSet ch) throws IllegalArgumentException {
		this.addData(ch, true);
	}

	protected void addData(ChartDataSet ch, boolean repaint) throws IllegalArgumentException {
		try {
			// only add data representable with this axis.
			if (ch.getXClass().equals(this.xAxisBottom.getClassData()) && ch.getYClass().equals(this.yAxisLeft.getClassData())) {
				this.charts.add(ch);
				if (repaint) {
					this.paintBufferedImg();
					this.repaint();
				}
			} else {
				throw new IllegalArgumentException("ChartDataSet not representable in this chart");
			}
		} catch (NullPointerException nex) {
			Chart.logger.error(null, nex);
		}
	}

	/**
	 * Elimina todas las series de datos de chart.
	 */
	public void removeAllData() {
		this.charts.removeAll(this.charts);
		this.paintBufferedImg();
		this.repaint();
	}

	/**
	 * Elimina una serie de datos del chart.
	 *
	 * @param data
	 *            ChartDataSet Serie a eliminar
	 * @throws IllegalArgumentException
	 */

	public void removeData(ChartDataSet data) throws IllegalArgumentException {
		if (data.getXClass().equals(this.xAxisBottom.getClassData()) && data.getYClass().equals(this.yAxisLeft.getClassData())) {
			this.charts.remove(data);
		} else {
			throw new IllegalArgumentException("ChartDataSet not representable in this chart");
		}
	}

	public void setData(List<ChartDataSet> datalist) throws IllegalArgumentException {
		ArrayList<Object> dscopy = (ArrayList<Object>) this.charts.clone();
		try {
			this.charts.removeAll(this.charts);
			for (ChartDataSet item : datalist) {
				this.addData(item, false);
			}
			this.refresh();
		} catch (IllegalArgumentException ex) {
			this.charts = (ArrayList<ChartDataSet>) dscopy.clone();
			throw new IllegalArgumentException("ChartDataSet not representable in this chart");
		}
	}

	public void setData(ChartDataSet data) throws IllegalArgumentException {
		try {
			if (data.getXClass().equals(this.xAxisBottom.getClassData()) && data.getYClass().equals(this.yAxisLeft.getClassData())) {
				this.charts.removeAll(this.charts);
				this.addData(data);
			} else {
				throw new IllegalArgumentException("ChartDataSet not representable in this chart");
			}
		} catch (Exception ex) {
			Chart.logger.error(null, ex);
		}
	}

	@Override
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		try {
			super.setBounds(x, y, width, height);
			this.xAxisBottom.setBounds(0, height - this.bottomXaxisHeight, width, this.bottomXaxisHeight);
			this.xAxisTop.setBounds(0, 0, width, this.topXaxisHeight);
			this.yAxisLeft.setBounds(0, this.topXaxisHeight, this.leftYaxisWidth, height - this.topXaxisHeight - this.bottomXaxisHeight);
			this.refresh();
		} catch (Exception ex) {
			Chart.logger.error(null, ex);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int width = this.getSize().width;
		int height = this.getSize().height;
		if ((this.offImg == null) || (this.offImg.getWidth() != width) || (this.offImg.getHeight() != height)) {
			this.paintBufferedImg();
		}
		Shape oldClip = g2.getClip();
		g2.setClip(this.leftYaxisWidth, this.topXaxisHeight, this.getWidth(), this.getDataHeight());
		g2.drawImage(this.offImg, 0, 0, width, height, this);
		g2.setClip(oldClip);
	}

	private void paintBufferedImg() {
		try {
			int width = this.getSize().width;
			int height = this.getSize().height;
			if ((width > 0) && (height > 0)) {

				Graphics2D g2 = null;
				if ((this.offImg == null) || (this.offImg.getWidth() != width) || (this.offImg.getHeight() != height)) {
					this.offImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);// createImage(width,
				}
				if (this.offImg != null) {
					g2 = this.offImg.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					Rectangle r = this.getBounds();
					// RAFA: la cuestion
					g2.setColor(new Color(0, 0, 0, 1));
					g2.fill(r);
					g2.setColor(this.getBackground());
					r.x = this.leftYaxisWidth;
					r.y = this.topXaxisHeight;
					r.width = this.getWidth();
					r.height = this.getCellHeight() * this.getNumVerticalDivisions();

					g2.fill(r);
					for (ChartDataSet ds : this.charts) {
						if ((ds.getData() != null) && !ds.getData().isEmpty() && (ds.getChartDataRender() != null)) {
							ds.getChartDataRender().render(g2, ds.getData(), this);
						}
					}
					g2.dispose();
				}
			}
		} catch (Exception ex) {
			Chart.logger.error(null, ex);
		}

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		return this.getDataAtPoint(e.getPoint());
	}

	public String getDataAtPoint(Point p) {
		for (ChartDataSet dataset : this.charts) {
			for (ChartData data : dataset.getData()) {
				Shape sh = data.getShape();
				if ((sh != null) && sh.contains(p)) {
					return data.getDescription(this);
				}
			}
		}
		return null;
	}

	public void refresh() {
		this.calculateAffineTransform();
		this.paintBufferedImg();
		this.repaint();
	}

	@Override
	public void repaint() {
		super.repaint();
	}

	public ArrayList<ChartDataSet> getCharts() {
		return this.charts;
	}

	public void addAfterPaintEffects(IAfterPaintEffectChart effect) {
		this.afterPaintEffects.add(effect);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		for (IAfterPaintEffectChart effect : this.afterPaintEffects) {
			effect.paint(g, this);
		}
	}

}
