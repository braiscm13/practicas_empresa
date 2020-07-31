package com.opentach.client.comp.activitychart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.border.CompoundBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.table.Table;

/**
 * Sobreescrito para que con el locale cambie la leyenda y para que haga el resumen
 *
 * @author rafael.lopez
 */
public class ChartDataField extends JPanel implements AccessForm, IdentifiedElement, FormComponent
/* IdentifiedAbstractFormComponent */{

	private static final Logger	logger					= LoggerFactory.getLogger(ChartDataField.class);
	public static final String	CHART_RESUME_LABEL_NAME	= "\"CHART_RESUME_LABEL\"";

	public interface ISummaryComputer {

		public String computeSummary(EntityResult data, Date from, Date to);

		public String refreshSummary();
	}

	private static final String	SUMMARYCLASS	= "summaryclass";
	private static final String	SUMMARYENTITY	= "summaryentity";

	// Nuevas propiedades entidades de donde saca las tareas y las clases
	// utilizadas para representar los
	// datos.
	private final static String	ENTITIES		= "entities";
	private final static String	TASKS			= "tasks";

	protected Chart				chart;
	protected JLabel			infoLabel;
	protected JLabel			jlSummary;
	protected JPanel			auxpanel;
	protected List<Object>		entitiesDesc	= new ArrayList<Object>();

	protected ISummaryComputer	summaryComputer;
	protected String			summaryEntity;
	protected int				cellHeight		= 64;
	private Object				attribute;
	private Form				parentForm;
	private JPanel				emptySlot;

	public ChartDataField(Hashtable<String, Object> params) {
		super();
		this.setLayout(new BorderLayout());
		this.init(params);
		ToolTipManager.sharedInstance().unregisterComponent(this);
	}

	/**
	 * Propiedades del componente: <ul> <li>xfontsize: Tamaño fuente eje X. <li>yfontsize: Tamaño fuente eje Y. <li>xlabelformat: Formato de la fuente
	 * eje X. <li>ylabelformat: Formato de la fuente eje Y. <li>legendimage: Imagen utilizada en la leyenda. <li>showinfo: 'true' muestra la
	 * información del elemento seleccionado. <li>entities: Lista de entidades separadas por ';' de las que se recuperarán los datos. <li>task: Lista
	 * de tareas separadas por ';' que se representarán los datos. Las clases deben extender de Task e implementar las interfaces TaskParser,
	 * ChartDataRenderProvider. </ul>
	 */

	@Override
	public void init(Hashtable params) {
		this.configurarChart(new Date(), new Date(), 7, (String) params.get("sublegend1"), (String) params.get("sublegend2"));
		this.attribute = params.get("attr");

		this.auxpanel = new JPanel(new GridBagLayout());
		this.auxpanel.setOpaque(false);
		this.auxpanel.setMinimumSize(new Dimension(180, Integer.MAX_VALUE));
		this.auxpanel.setMaximumSize(new Dimension(180, Integer.MAX_VALUE));
		this.auxpanel.setPreferredSize(new Dimension(180, Integer.MAX_VALUE));

		this.add(this.auxpanel, BorderLayout.EAST);

		// Borde entre el panel lateral y el chart
		JPanel separator = new JPanel();
		separator.setMinimumSize(new Dimension(3, Integer.MAX_VALUE));
		separator.setMaximumSize(new Dimension(3, Integer.MAX_VALUE));
		separator.setPreferredSize(new Dimension(3, Integer.MAX_VALUE));
		separator.setBackground(Chart.COLOR_SPACE);
		separator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Chart.COLOR_SEPARATOR_FIRST_BORDER),
				BorderFactory.createMatteBorder(0, 0, 0, 1, Chart.COLOR_SEPARATOR_SECOND_BORDER)));
		CompoundBorder cborder = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Chart.COLOR_SEPARATOR_FIRST_BORDER),
				BorderFactory.createMatteBorder(1, 0, 0, 0, Chart.COLOR_SEPARATOR_SECOND_BORDER));
		// resumen
		this.jlSummary = new JLabel();
		this.jlSummary.setName(ChartDataField.CHART_RESUME_LABEL_NAME);
		JPanel jpSummary = new JPanel(new BorderLayout());
		jpSummary.setOpaque(false);
		jpSummary.add(this.jlSummary, BorderLayout.SOUTH);
		jpSummary.setBorder(cborder);

		// empty slot
		this.emptySlot = new JPanel(new BorderLayout());
		this.emptySlot.setOpaque(false);
		this.emptySlot.setBorder(cborder);

		// fill panel
		JPanel fillPanel = new JPanel();
		fillPanel.setOpaque(false);
		fillPanel.setBorder(cborder);

		int y = 0;
		this.getParent();
		LegendPanel legend = new LegendPanel((String) params.get("sublegend1"), (String) params.get("sublegend2"));
		legend.setOpaque(false);
		legend.setBorder(cborder);
		this.auxpanel.add(legend, new GridBagConstraints(1, y++, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,
				0, 0, 0), 0, 0));
		this.auxpanel.add(jpSummary, new GridBagConstraints(1, y++, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				0, 0, 0, 0), 0, 0));
		this.auxpanel.add(this.emptySlot, new GridBagConstraints(1, y++, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		this.auxpanel.add(fillPanel, new GridBagConstraints(1, y++, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0,
				0, 0), 0, 0));

		this.auxpanel.add(separator, new GridBagConstraints(0, 0, 1, y, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0,
				0), 0, 0));

		if ("true".equals(params.get("showinfo"))) {
			this.infoLabel = new JLabel("<html><br><br><bold>Pulse los datos para <br>visualizar información</html>");
			this.infoLabel.setForeground(Color.black);
			this.infoLabel.setName("ChartInfoLabel");
			this.auxpanel.add(this.infoLabel);
		}

		// Poder definir en el xml las tablas o entidades sobre las que
		// representará los datos.
		try {
			List<Object> entities = ApplicationManager.getTokensAt((String) params.get(ChartDataField.ENTITIES), ";");
			List<Object> tasks = ApplicationManager.getTokensAt((String) params.get(ChartDataField.TASKS), ";");
			int i = 0;
			for (Iterator<Object> iterator = entities.iterator(); iterator.hasNext(); i++) {
				String entityName = (String) iterator.next();
				this.entitiesDesc.add(new EntityDataSet(entityName, (String) tasks.get(i)));
			}
		} catch (NullPointerException nex) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String summaryClass = (String) params.get(ChartDataField.SUMMARYCLASS);
		this.summaryEntity = (String) params.get(ChartDataField.SUMMARYENTITY);
		if (summaryClass != null) {
			this.summaryComputer = this.configSummaryClass(summaryClass);
		}

		this.setBackground(Chart.COLOR_BACKGROUND);
	}

	public void configurarChart(Date from, Date to, int numVerticalDivisions, String subLegend1, String subLegend2) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		Calendar endcal = Calendar.getInstance();
		endcal.setTime(to);

		Chart chart = this.getChart();

		if (chart == null) {
			chart = new Chart(subLegend1, subLegend2);
			this.setChart(chart);
			chart.setVisible(Chart.DATA_DESCRIPTION, false);
		}
		// Update Y Axis cells.
		chart.setVerticalAxisCfg(cal.getTime(), endcal.getTime(), numVerticalDivisions);
		this.updateChart();

		if ((this.summaryComputer != null) && (this.summaryEntity != null)) {
			EntityResult data = null;
			for (Iterator<Object> iterator = this.entitiesDesc.iterator(); iterator.hasNext();) {
				EntityDataSet ed = (EntityDataSet) iterator.next();
				if (this.summaryEntity.equals(ed.getEntity())) {
					data = ed.getData();
					break;
				}
			}
			String summary = this.summaryComputer.computeSummary(data, from, to);
			this.setSummary(summary);
		}
	}

	private ISummaryComputer configSummaryClass(String clazz) {
		try {
			Class<ISummaryComputer> cls = (Class<ISummaryComputer>) Class.forName(clazz);
			if (ISummaryComputer.class.isAssignableFrom(cls)) {
				return cls.newInstance();
			}
			return null;
		} catch (Exception e) {
			System.err.println("Bad SummaryComputer class");
			return null;
		}
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension ms = super.getMinimumSize();
		ms.height = (this.cellHeight * this.chart.getNumVerticalDivisions()) + this.chart.getTopXaxisHeight() + this.chart.getBottomXaxisHeight();
		return ms;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension ms = super.getPreferredSize();
		ms.height = (this.cellHeight * this.chart.getNumVerticalDivisions()) + this.chart.getTopXaxisHeight() + this.chart.getBottomXaxisHeight();
		return ms;
	}

	@Override
	public Dimension getMaximumSize() {
		Dimension ms = super.getMaximumSize();
		ms.height = (this.cellHeight * this.chart.getNumVerticalDivisions()) + this.chart.getTopXaxisHeight() + this.chart.getBottomXaxisHeight();
		return ms;
	}

	@Override
	public void setResourceBundle(ResourceBundle resourcebundle) {
		if (this.summaryComputer != null) {
			String summary = this.summaryComputer.refreshSummary();
			this.setSummary(summary);
		}
	}

	public JPanel getAuxpanel() {
		return this.auxpanel;
	}

	public JPanel getEmptySlot() {
		return this.emptySlot;
	}

	// Se recuperan los datos de las tablas del formulario y se repinta el
	// grafico con lso valores presentes
	// en dichos datos.
	public void updateChart() {
		try {
			if (!this.entitiesDesc.isEmpty()) {

				Form form = this.parentForm;
				if (form != null) {
					ArrayList<ChartDataSet> datalist = new ArrayList<>();

					for (Iterator<Object> iterator = this.entitiesDesc.iterator(); iterator.hasNext();) {
						try {
							EntityDataSet ed = (EntityDataSet) iterator.next();
							EntityResult res = ed.getData();
							if (res == null) {
								Table tb = (Table) form.getElementReference(ed.getEntity());
								if (tb != null) {
									Hashtable<String, Object> data = (Hashtable<String, Object>) tb.getShownValue();
									if (data instanceof EntityResult) {
										res = (EntityResult) data;
									} else {
										res = new EntityResult(data);
									}
								}
							}
							if (res != null) {
								int numreg = res.calculateRecordNumber();
								ArrayList<Task> tasks = new ArrayList<Task>();
								for (int i = 0; i < numreg; i++) {
									Hashtable<String, Object> reg = res.getRecordValues(i);
									Task newtask = ed.getnewTaskInstance();
									try {
										TaskParser tp = (TaskParser) newtask;
										tp.parse(reg);
									} catch (ClassCastException cex) {
										System.err.println("Task must implements TaskParse");
									}
									tasks.add(newtask);
								}
								if (!tasks.isEmpty()) {
									BasicChartDataSet bardataset = new BasicChartDataSet(ed.getTaskName(), tasks);
									// Establezco el render de la serie.
									ChartDataRender render = ((ChartDataRenderProvider) tasks.get(0)).getChartDataRender();
									// render.setBarWidth(barwidth);
									// baroffset += offsetstep;
									datalist.add(bardataset);
									bardataset.setChartDataRender(render);
								}
							}
						} catch (Exception ex) {
							ChartDataField.logger.error(null, ex);
						}
					}
					this.chart.setData(datalist);
				}
			}
		} catch (NullPointerException nex) {

		}

	}

	public Chart getChart() {
		return this.chart;
	}

	public void setInfoLabel(JLabel info) {
		if (this.infoLabel != null) {
			this.auxpanel.remove(this.infoLabel);
		}
		this.infoLabel = info;
		this.auxpanel.add(this.infoLabel);
	}

	public void clearData() {
		for (Iterator<Object> iterator = this.entitiesDesc.iterator(); iterator.hasNext();) {
			EntityDataSet ed = (EntityDataSet) iterator.next();
			if (ed.getData() != null) {
				ed.getData().clear();
			}
		}
	}

	public void setChart(Chart c) {
		if (this.chart != null) {
			this.remove(this.chart);
		}
		this.chart = c;
		this.add(this.chart);
		this.doLayout();
		if (this.infoLabel != null) {
			this.chart.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					String info = ChartDataField.this.chart.getDataAtPoint(e.getPoint());
					if ((info != null) && (info.length() > 0)) {
						ChartDataField.this.infoLabel.setText(info);
					} else {
						ChartDataField.this.infoLabel.setText("<html><br><br><h4>Pulse los datos <br> para visualizar <br>la información</b></html>");
					}
					ChartDataField.this.infoLabel.setForeground(Color.black);
				}
			});
		}
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		if (this.chart != null) {
			this.chart.refresh();
		}
	}

	public void clearEntityData(String entity) {
		for (Iterator<Object> iterator = this.entitiesDesc.iterator(); iterator.hasNext();) {
			EntityDataSet ed = (EntityDataSet) iterator.next();
			if (entity.equals(ed.getEntity())) {
				ed.getData().clear();
			}
			break;
		}
	}

	public void setEntityData(String entity, EntityResult res) {
		for (Iterator<Object> iterator = this.entitiesDesc.iterator(); iterator.hasNext();) {
			EntityDataSet ed = (EntityDataSet) iterator.next();
			if (entity.equals(ed.getEntity())) {
				ed.setData(res);
			}
		}
	}

	public EntityResult getEntityData(String entity) {
		for (Iterator<Object> iterator = this.entitiesDesc.iterator(); iterator.hasNext();) {
			EntityDataSet ed = (EntityDataSet) iterator.next();
			if (entity.equals(ed.getEntity())) {
				return ed.getData();
			}
		}
		return null;
	}

	public void setSummary(String summary) {
		this.jlSummary.setText(summary);
	}

	@Override
	public void initPermissions() {}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Vector<Object> getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(Locale arg0) {}

	@Override
	public Object getConstraints(LayoutManager arg0) {
		return new GridBagConstraints(-1, -1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void setParentForm(Form arg0) {
		this.parentForm = arg0;

	}
}
