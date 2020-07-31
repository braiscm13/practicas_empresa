package com.opentach.client.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.table.TableAttribute;

public class GraficaExtColor extends JPanel implements DataComponent {

	private class DatosCateg {

		public java.util.List getDatosX() {
			return this.datosX;
		}

		public java.util.List getDatosY() {
			return this.datosY;
		}

		private java.util.List	datosX;
		private java.util.List	datosY;

		public DatosCateg(List datosX, List datosY) {
			this.datosX = null;
			this.datosY = null;
			this.datosX = datosX;
			this.datosY = datosY;
		}
	}

	public GraficaExtColor(Hashtable parametros) {
		this.panelNorte = new JPanel();
		this.panelGrafica = new JPanel(new BorderLayout());
		this.scroll = new JScrollPane();
		this.panelChart = null;
		this.atributo = null;
		this.nombreEjeX = null;
		this.nombresEjeY = null;
		this.visibleSeries = null;
		this.labeltype = null;
		this.tipo = CHART_TYPE.LINE;
		this.valor = null;
		this.labelX = "";
		this.labelY = "";
		this.entidad = null;
		this.prefW = -1;
		this.prefH = -1;
		this.init(parametros);
		this.setLayout(new BorderLayout());
		this.add(this.panelNorte, "North");
		this.add(this.panelGrafica);
		this.setOpaque(false);
	}

	protected void setYAxis(String yAxis[]) {
		this.nombresEjeY.clear();
		for (int i = 0; i < yAxis.length; i++) {
			this.nombresEjeY.add(yAxis[i]);
		}

		this.actualizaGrafica();
	}

	protected void setChartPreferredSize(int w, int h) {
		this.prefH = h;
		this.prefW = w;
		if (this.panelChart != null) {
			this.panelChart.setPreferredSize(new Dimension(w, h));
			this.revalidate();
		}
	}

	protected void setSerieVisible(String s, boolean visible) {
		if (this.visibleSeries == null) {
			this.visibleSeries = (Vector) this.visibleSeries.clone();
		}
		if (visible) {
			if (!this.visibleSeries.contains(s)) {
				this.visibleSeries.add(s);
				this.actualizaGrafica();
			}
		} else if (this.visibleSeries.contains(s)) {
			this.visibleSeries.remove(s);
			this.actualizaGrafica();
		}
	}

	@Override
	public void init(Hashtable parametros) {
		Object entity = parametros.get("entity");
		if (entity != null) {
			this.entidad = entity.toString();
		}
		Object atrib = parametros.get("attr");
		if (atrib == null) {
			if (this.entidad == null) {
				throw new IllegalArgumentException(this.getClass().toString() + " 'attr' es obligatorio");
			}
			this.atributo = this.entidad;
		} else {
			this.atributo = atrib.toString();
		}
		Object xaxis = parametros.get("xaxis");
		if (xaxis == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'xaxis' es obligatorio");
		}
		this.nombreEjeX = xaxis.toString();
		Object yaxis = parametros.get("yaxis");
		if (yaxis == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'yaxis' es obligatorio");
		}
		this.nombresEjeY = ApplicationManager.getTokensAt(yaxis.toString(), ";");
		Object xlabel = parametros.get("xlabel");
		if (xlabel != null) {
			this.labelX = xlabel.toString();
		}
		Object ylabel = parametros.get("ylabel");
		if (ylabel != null) {
			this.labelY = ylabel.toString();
		}
		Object type = parametros.get("type");
		if (type != null) {
			if (type.equals("line")) {
				this.tipo = CHART_TYPE.LINE;
			} else if (type.equals("bar")) {
				this.tipo = CHART_TYPE.BAR;
			} else if (type.equals("bar3d")) {
				this.tipo = CHART_TYPE.BAR3D;
			} else if (type.equals("stacked3d")) {
				this.tipo = CHART_TYPE.STACKED3D;
			} else if (type.equals("pie")) {
				this.tipo = CHART_TYPE.PIE;
			} else if (type.equals("pie3d")) {
				this.tipo = CHART_TYPE.PIE3D;
			} else {
				System.out.println(this.getClass().toString() + " Tipo de gr\341fica no reconocido " + type);
			}
		}
		Object width = parametros.get("width");
		if (width != null) {
			try {
				this.prefW = Integer.parseInt(width.toString());
			} catch (Exception ex) {
				System.out.println(this.getClass().toString() + " Error par\341metro 'width' ");
			}
		}
		Object height = parametros.get("height");
		if (height != null) {
			try {
				this.prefH = Integer.parseInt(height.toString());
			} catch (Exception ex) {
				System.out.println(this.getClass().toString() + " Error par\341metro 'height' ");
			}
		}

		Object distinctCategories = parametros.get("distinctcategories");
		if (distinctCategories != null) {
			this.distinctCategories = ApplicationManager.getTokensAt((String) distinctCategories, ";");
		}
		String activecolors = (String) parametros.get("activecolors");
		if (activecolors != null) {
			try {
				if (activecolors.equalsIgnoreCase("YES") || activecolors.equalsIgnoreCase("TRUE")) {
					this.activeColor = true;
				}
			} catch (Exception e) {
				System.out.println(this.getClass().toString() + " Error par\341metro 'activecolors' ");
			}
		}
		String labeltype = (String) parametros.get("labeltype");
		if (labeltype != null) {
			if (labeltype.equalsIgnoreCase("NO_LABELS")) {
				this.labeltypenum = 1;
			} else if (labeltype.equalsIgnoreCase("NAME_LABELS")) {
				this.labeltypenum = 2;
			} else if (labeltype.equalsIgnoreCase("VALUE_LABELS")) {
				this.labeltypenum = 3;
			} else if (labeltype.equalsIgnoreCase("PERCENT_LABELS")) {
				this.labeltypenum = 4;
			} else if (labeltype.equalsIgnoreCase("NAME_AND_VALUE_LABELS")) {
				this.labeltypenum = 5;
			} else if (labeltype.equalsIgnoreCase("NAME_AND_PERCENT_LABELS")) {
				this.labeltypenum = 6;
			} else if (labeltype.equalsIgnoreCase("VALUE_AND_PERCENT_LABELS")) {
				this.labeltypenum = 7;
			}
		}
	}

	@Override
	public Object getConstraints(LayoutManager layoutPadre) {
		if (layoutPadre instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(5, 5, 5, 5), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		} else {
			return this.valor;
		}
	}

	protected void setTipo(CHART_TYPE tipo) {
		CHART_TYPE tipoAnt = this.tipo;
		this.tipo = tipo;
		if (tipoAnt != this.tipo) {
			this.actualizaGrafica();
		}
	}

	@Override
	public void setValue(Object valor) {
		if (valor instanceof Hashtable) {
			this.valor = (Hashtable<String, Object>) valor;
			this.actualizaGrafica();
		} else {
			this.deleteData();
		}
	}

	protected void actualizaGrafica() {
		if (this.isEmpty()) {
			this.panelGrafica.removeAll();
			this.panelGrafica.revalidate();
			this.panelGrafica.repaint();
		} else {
			this.panelGrafica.removeAll();
			JFreeChart chart = this.createChart();

			if (this.panelChart == null) {
				this.panelChart = new ChartPanel(chart);
				this.panelChart.setOpaque(false);
				Dimension d = this.panelChart.getPreferredSize();
				if (this.prefW != -1) {
					d.width = this.prefW;
				}
				if (this.prefH != -1) {
					d.height = this.prefH;
				}
				this.panelChart.setPreferredSize(d);
			} else {
				this.panelChart.setChart(chart);
			}

			this.scroll.removeAll();
			this.scroll = new JScrollPane(this.panelChart);
			this.scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
			this.panelGrafica.add(this.scroll);
			this.panelGrafica.revalidate();
			this.panelGrafica.repaint();
		}
	}

	@Override
	public void deleteData() {
		this.valor = null;
		this.actualizaGrafica();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public void setRequired(boolean flag) {}

	@Override
	public int getSQLDataType() {
		return 2000;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public void setModifiable(boolean flag) {}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return (this.valor == null) || this.valor.isEmpty();
	}

	@Override
	public String getLabelComponentText() {
		return "";
	}

	@Override
	public void setComponentLocale(Locale locale) {

	}

	// FIXME: esto no funciona bien!!
	@Override
	public void setResourceBundle(ResourceBundle res) {
		this.rb = res;
		this.actualizaGrafica();
	}

	@Override
	public Vector<Object> getTextsToTranslate() {
		Vector<Object> v = new Vector<Object>(0);
		return v;
	}

	@Override
	public Object getAttribute() {
		if (this.entidad != null) {
			TableAttribute atributoTabla = new TableAttribute();
			Vector<Object> cols = (Vector<Object>) this.nombresEjeY.clone();
			cols.add(this.nombreEjeX);
			if (cols.contains("$Count$")) {
				cols.remove("$Count$");
			}
			atributoTabla.setEntityAndAttributes(this.entidad, cols);
			return atributoTabla;
		} else {
			return this.atributo;
		}
	}

	@Override
	public void initPermissions() {}

	@Override
	public boolean isRestricted() {
		return false;
	}

	protected JFreeChart createChart() {
		if (this.visibleSeries == null) {
			this.visibleSeries = (Vector) this.nombresEjeY.clone();
		}
		String labelX = this.labelX;
		String labelY = this.labelY;
		if ((labelX != null) && (labelX.length() > 0)) {
			try {
				labelX = this.rb.getString(labelX);
			} catch (Exception e) {
			}
		}
		if ((labelY != null) && (labelY.length() > 0)) {
			try {
				labelY = this.rb.getString(labelY);
			} catch (Exception e) {
			}
		}
		String colsY[] = new String[this.visibleSeries.size()];
		String description = "";
		JFreeChart chart = null;
		for (int i = 0; i < this.visibleSeries.size(); i++) {
			colsY[i] = (String) this.visibleSeries.get(i);
		}

		switch (this.tipo) {
		case PIE:
		case PIE3D: {
			PieDataset set = this.getPieDataset(this.nombreEjeX, colsY[0]);
			if (CHART_TYPE.PIE.equals(this.tipo)) {
				chart = ChartFactory.createPieChart(description, set, true, true, false);
			} else if (CHART_TYPE.PIE3D.equals(this.tipo)) {
				chart = ChartFactory.createPieChart3D(description, set, true, true, false);
			}
			((PiePlot) chart.getPlot()).setCircular(false);
			StandardPieSectionLabelGenerator spilg = new StandardPieSectionLabelGenerator("{0} ({2})");
			((PiePlot) chart.getPlot()).setLabelGenerator(spilg);
			((PiePlot) chart.getPlot()).setLabelFont(Font.decode("Verdana-BOLD-8"));
			for (int i = 0; i < Math.min(GraficaExtColor.colors.length, set.getKeys().size()); i++) {
				((PiePlot) chart.getPlot()).setSectionPaint((Comparable) set.getKeys().get(i), GraficaExtColor.colors[i]);
			}
			break;
		}

		case BAR:
		case BAR3D: {

			CategoryDataset set = this.getCategoryDataset(this.nombreEjeX, colsY, colsY);
			if (CHART_TYPE.BAR.equals(this.tipo)) {
				chart = ChartFactory.createBarChart(description, labelX, labelY, set, PlotOrientation.VERTICAL, true, true, false);
			} else if (CHART_TYPE.BAR3D.equals(this.tipo)) {
				chart = ChartFactory.createBarChart3D(description, labelX, labelY, set, PlotOrientation.VERTICAL, true, true, false);
			}

			if (chart.getPlot() instanceof CategoryPlot) {
				CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
				if (this.activeColor) {
					categoryPlot.setRenderer(new BarRenderer() {
						@Override
						public Paint getItemPaint(int row, int column) {
							try {
								return GraficaExtColor.colors[column];
							} catch (Exception e) {
								return super.getItemPaint(row, column);
							}
						}
					});
				}
				BarRenderer barRenderer = (BarRenderer) categoryPlot.getRenderer();
				barRenderer.setShadowVisible(false);
				barRenderer.setBarPainter(new StandardBarPainter());

				categoryPlot.getDomainAxis().setTickLabelFont(Font.decode("Verdana-BOLD-7"));
				categoryPlot.getRangeAxis().setTickLabelFont(Font.decode("Verdana-BOLD-8"));
				categoryPlot.getDomainAxis().setMaximumCategoryLabelLines(2);

			}
			break;
		}
		case STACKED3D: {

			CategoryDataset set = this.getCategoryDataset(this.nombreEjeX, colsY, colsY);
			chart = ChartFactory.createStackedBarChart3D(description, labelX, labelY, set, PlotOrientation.VERTICAL, true, true, false);
			break;
		}

		case LINE: {
			CategoryDataset set6;
			if (this.checkXYDataset(this.nombreEjeX, colsY)) {
				set6 = (CategoryDataset) this.getXYDataset(this.nombreEjeX, colsY, colsY);
				chart = ChartFactory
						.createXYLineChart(description, labelX, labelY, (XYDataset) set6, PlotOrientation.VERTICAL, true, true, false);
				break;
			}
			set6 = this.getCategoryDataset(this.nombreEjeX, colsY, colsY);
			chart = ChartFactory.createLineChart(description, labelX, labelY, set6, PlotOrientation.VERTICAL, true, true, false);

			break;
		}

		default: {
			System.out.println(this.getClass().toString() + " ERROR : TIPO DE GRAFICA NO VALIDO ");
			break;
		}
		}
		if (chart.getPlot() instanceof CategoryPlot) {
			((CategoryPlot) chart.getPlot()).getRangeAxis().setLabelInsets(new RectangleInsets(15, 2, 15, 15));
		} else if (chart.getPlot() instanceof XYPlot) {
			((XYPlot) chart.getPlot()).getRangeAxis().setLabelInsets(new RectangleInsets(15, 2, 15, 15));
		}
		chart.setBackgroundPaint(new Color(0xc0c0c0));
		chart.getPlot().setBackgroundPaint(DataComponent.VERY_LIGHT_YELLOW);
		chart.getPlot().setBackgroundImage(null);
		return chart;
	}

	protected CategoryDataset getCategoryDataset(String colX, String colsY[], String seriesNames[]) {
		return this.getCategoryDataset(colX, colsY, seriesNames, null);
	}

	protected CategoryDataset getCategoryDataset(String colX, String colsY[], String seriesNames[], int operations[]) {
		List<Object> datosX = null;
		List<Object> listasDatosY[] = new List[colsY.length];
		for (int i = 0; i < colsY.length; i++) {
			int operacion = 0;
			if ((operations != null) && (operations.length > i)) {
				operacion = operations[i];
			}
			DatosCateg datosCateg = this.getDatosCategorias(colX, colsY[i], operacion);
			datosX = datosCateg.getDatosX();
			listasDatosY[i] = datosCateg.getDatosY();
		}

		DefaultCategoryDataset set = new DefaultCategoryDataset();
		List<Object> datosYDef = listasDatosY[0];
		for (int i = 0; i < datosYDef.size(); i++) {
			for (int j = 0; j < colsY.length; j++) {
				datosYDef = listasDatosY[j];
				set.addValue((Number) datosYDef.get(i), seriesNames[j], (String) datosX.get(i));
			}
		}
		return set;
	}

	protected PieDataset getPieDataset(String colX, String colY) {
		return this.getPieDataset(colX, colY, 0);
	}

	protected PieDataset getPieDataset(String colX, String colY, int operation) {
		Vector<Object> datosX = new Vector<Object>();
		for (Object dato : (Vector<Object>) this.valor.get(colX)) {
			if (dato == null) {
				dato = "";
			}
			datosX.add(dato);
		}

		Vector<Object> datosY = (Vector<Object>) this.valor.get(colY);

		if ((datosX != null) && (datosY != null)) {

			DefaultPieDataset set = new DefaultPieDataset();

			for (int i = 0; i < datosX.size(); i++) {
				Comparable<?> key = (Comparable<?>) datosX.get(i);
				Object value = datosY.get(i);
				Number currentDatasetValue = null;
				try {
					currentDatasetValue = set.getValue(key);
				} catch (UnknownKeyException e) {
					currentDatasetValue = null;
					List<String> names = new ArrayList<String>();
					names.add("CONDUCCION");
					names.add("TRABAJO");
					names.add("DISPONIBILIDAD");
					names.add("PAUSA/DESCANSO");
					names.add("INDEFINIDA");
					for (int j = 0; j < 5; j++) {
						set.setValue(names.get(j), 0);
					}
				}

				if (currentDatasetValue == null) {
					if (value != null) {
						System.out.println("key :" + key);
						set.setValue(key, (Number) value);
					} else if ("$Count$".equals(colY)) {
						set.setValue(key, new Double(1.0D));
					} else {
						set.setValue(key, new Double(0.0D));
					}
				} else {
					if ("$Count$".equals(colY)) {
						double nuevoValor = currentDatasetValue.doubleValue() + 1.0D;
						Double nuevo = new Double(nuevoValor);
						set.setValue(key, nuevo);
					} else {
						double aSumar = 0.0D;
						if (value != null) {
							aSumar = ((Number) value).doubleValue();
						}
						double nuevoValor = currentDatasetValue.doubleValue();
						switch (operation) {
						case 0:
							nuevoValor += aSumar;
							break;

						case 3:
							nuevoValor += aSumar / datosY.size();
							break;

						case 1:
							nuevoValor = Math.max(nuevoValor, aSumar);
							break;

						case 2:
							nuevoValor = Math.min(nuevoValor, aSumar);
							break;

						default:
							nuevoValor += aSumar;
							break;
						}
						Double nuevo = new Double(nuevoValor);
						set.setValue(key, nuevo);
					}
				}
			}
			return set;
		} else {
			return null;
		}
	}

	private List<Object> translateDistinctCategories(List<Object> lt) {
		if (this.rb != null) {
			List<Object> lNew = new ArrayList(lt.size());
			String li = null;
			for (int i = 0; i < lt.size(); i++) {
				li = (String) lt.get(i);
				if (li != null) {
					try {
						lNew.add(this.rb.getString(li));
					} catch (MissingResourceException e) {
						lNew.add(li);
					}
				}
			}
			if (lNew.contains("CONDUCCIÓN")) {
				lNew.add(lNew.indexOf("CONDUCCIÓN"), "CONDUCCION");
				lNew.remove("CONDUCCIÓN");
			}
			return lNew;
		} else {
			return lt;
		}
	}

	protected DatosCateg getDatosCategorias(String colX, String colY, int operation) {
		List<Object> valoresDistintosX = new ArrayList<Object>();
		List<Object> valoresCorrespY = new ArrayList<Object>();
		Hashtable<String, Object> valorMostrado = this.valor;
		Vector<Object> datosX = (Vector<Object>) valorMostrado.get(colX);
		Vector<Object> datosY = (Vector<Object>) valorMostrado.get(colY);
		if (datosX == null) {
			System.out.println(this.getClass().toString() + " : No se encontró la columna: " + colX);
			return new DatosCateg(new Vector<Object>(0), new Vector<Object>(0));
		}
		if ((datosY == null) && !"$Count$".equals(colY)) {
			System.out.println(this.getClass().toString() + " : No se encontró la columna: " + colY);
			return new DatosCateg(new Vector<Object>(0), new Vector<Object>(0));
		}
		if ((this.distinctCategories == null) || (this.distinctCategories.size() == 0)) {
			for (int i = 0; i < datosX.size(); i++) {
				Object dato = datosX.get(i);
				boolean yaEsta = false;
				yaEsta = valoresDistintosX.contains(dato);
				if (!yaEsta) {
					valoresDistintosX.add(dato);
				}
			}
		} else {
			valoresDistintosX = new ArrayList(this.translateDistinctCategories(this.distinctCategories));
		}
		for (int i = 0; i < valoresDistintosX.size(); i++) {
			Object datoX = valoresDistintosX.get(i);
			double resY = 0;
			boolean algunoNoNull = false;
			if ("$Count$".equals(colY)) {
				algunoNoNull = true;
				for (int j = 0; j < datosX.size(); j++) {
					Object dX = datosX.get(j);
					if ((dX == null) || (datoX == null)) {
						if ((dX == null) && (datoX == null)) {
							resY++;
						}
					} else if (datoX.equals(dX)) {
						resY++;
					}
				}

			} else {
				ArrayList<Object> valoresY = new ArrayList<Object>();
				for (int indiceEnDatosX = datosX.indexOf(datoX); indiceEnDatosX >= 0; indiceEnDatosX = datosX.indexOf(datoX, indiceEnDatosX + 1)) {
					valoresY.add(datosY.get(indiceEnDatosX));
				}
				for (int j = 0; j < valoresY.size(); j++) {
					Object valorY = valoresY.get(j);
					if (valorY != null) {
						algunoNoNull = true;
						switch (operation) {
						default:
							break;

						case 0:
							resY += ((Number) valorY).doubleValue();
							break;

						case 3:
							resY += ((Number) valorY).doubleValue() / valoresY.size();
							break;

						case 1:
							if (j == 0) {
								resY = -2147483648D;
							}
							resY = Math.max(resY, ((Number) valorY).doubleValue());
							break;

						case 2:
							if (j == 0) {
								resY = 2147483647D;
							}
							resY = Math.min(resY, ((Number) valorY).doubleValue());
							break;
						}
					}
				}

			}
			if (algunoNoNull) {
				valoresCorrespY.add(i, new Double(resY));
			} else {
				valoresCorrespY.add(i, null);
			}
			if (datoX == null) {
				valoresDistintosX.remove(i);
				valoresDistintosX.add(i, "");
			}
		}
		return new DatosCateg(valoresDistintosX, valoresCorrespY);
	}

	protected boolean checkXYDataset(String colX, String colY[]) {
		for (int i = 0; i < colY.length; i++) {
			DatosCateg dat = this.getDatosCategorias(colX, colY[i], 0);
			java.util.List<Object> x = dat.getDatosX();
			for (int k = 0; k < x.size(); k++) {
				if ((x.get(k) != null) && !(x.get(k) instanceof Number)) {
					return false;
				}
			}

			java.util.List<Object> y = dat.getDatosY();
			for (int k = 0; k < y.size(); k++) {
				if ((y.get(k) != null) && !(y.get(k) instanceof Number)) {
					return false;
				}
			}

		}

		return true;
	}

	protected XYDataset getXYDataset(String colX, String colY[], String series[]) {
		XYSeriesCollection datos = new XYSeriesCollection();
		for (int i = 0; i < series.length; i++) {
			XYSeries serie = new XYSeries(series[i]);
			DatosCateg dat = this.getDatosCategorias(colX, colY[i], 0);
			java.util.List<Object> x = dat.getDatosX();
			java.util.List<Object> y = dat.getDatosY();
			for (int k = 0; k < x.size(); k++) {
				double px = ((Number) x.get(k)).doubleValue();
				if (y.get(k) != null) {
					double py = ((Number) y.get(k)).doubleValue();
					serie.add(px, py);
				}
			}

			datos.addSeries(serie);
		}

		return datos;
	}

	private static final Paint[]	colors			= new Paint[] { Color.red, Color.blue, Color.green, Color.yellow, new Color(230, 175, 50),
			Color.orange.darker(), Color.cyan, Color.orange, Color.blue };

	public static final String		COUNT_COLUMN	= "$Count$";
	public static final int			SUM				= 0;
	public static final int			MAX				= 1;
	public static final int			MIN				= 2;
	public static final int			AVG				= 3;
	public static Color				textColor		= new Color(0x32cc41);
	protected JPanel				panelNorte;
	protected JPanel				panelGrafica;
	protected JScrollPane			scroll;
	protected ChartPanel			panelChart;
	protected Object				atributo;
	protected String				nombreEjeX;
	protected Vector<Object>		nombresEjeY;
	protected Vector<Object>		visibleSeries;
	protected boolean				activeColor		= false;
	protected List<Object>			distinctCategories;
	protected CHART_TYPE			tipo;
	protected Hashtable<String, Object>	valor;
	protected String				labelX;
	protected String				labelY;
	protected String				entidad;
	protected int					prefW;
	protected int					prefH;
	protected String				labeltype;
	protected int					labeltypenum;
	protected ResourceBundle		rb;

	public enum CHART_TYPE {
		PIE, PIE3D, BAR, BAR3D, STACKED3D, LINE
	}
}
