package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.plaf.utils.StyleUtil;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.Column;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UDataLabel;
import com.utilmize.client.gui.field.ULabel;

public class ActivityChartControlPanel extends Row {

	private static final String	ICO_LAST	= "com/opentach/client/rsc/navigate_right2_18.png";
	private static final String	ICO_RIGHT	= "com/opentach/client/rsc/navigate_right_18.png";
	private static final String	ICO_LEFT	= "com/opentach/client/rsc/navigate_left_18.png";
	private static final String	ICO_FIRST	= "com/opentach/client/rsc/navigate_left2_18.png";
	private static final Logger	logger						= LoggerFactory.getLogger(ActivityChartControlPanel.class);
	public static Font	TITLE_FONT					= Font.decode("ARIAL-BOLD-14");
	public static Color	TITLE_FOREGROUND			= new Color(0x03fb06);
	public static Color	COLOR_TITLE_DATE_BACKGROUND	= new Color(0x4f7b9a);
	public static Color	COLOR_BACGROUND				= new Color(0x5b8aab);

	// Fecha de inicio del rango de datos
	private Date		startDate					= null;
	// Fecha fin del rango de datos.
	private Date		endDate						= null;
	// Fecha inicio del rango de datos visualizados en la grafica
	private Date		chartStartDate				= null;
	// Fecha fin del rango de datos visualizados en la grafica
	private Date		chartEndDate				= null;

	private Column		extraColumn;
	private Button		buttonFirstWeek;
	private Button		buttonLeft;
	private Button		buttonRight;
	private Button		buttonLastWeek;
	private UDataLabel	titleLabel;

	public ActivityChartControlPanel(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public void init(Hashtable parameters) {
		Object title = parameters.remove("title");
		super.init(parameters);
		this.setOpaque(true);
		this.setBackground(ActivityChartControlPanel.COLOR_BACGROUND);

		parameters.put("title", title);
		Column titleColumn = this.createTitleColumn(parameters);
		Row buttonsRow = this.createButtonsRow(parameters);
		this.extraColumn = this.createExtraColumn(parameters);

		super.add(titleColumn, titleColumn.getConstraints(this.getLayout()));
		super.add(buttonsRow, buttonsRow.getConstraints(this.getLayout()));
		super.add(this.extraColumn, this.extraColumn.getConstraints(this.getLayout()));
	}

	private Column createTitleColumn(Hashtable parameters) {
		Hashtable<Object, Object> columnParams = EntityResultTools.keysvalues("expand", "no", "valign", "cener");
		Column column = new Column(columnParams);
		Hashtable<Object, Object> labelParams = EntityResultTools.keysvalues("text", parameters.get("title"));
		ULabel label = new ULabel(labelParams);
		label.getLabel().setFont(ActivityChartControlPanel.TITLE_FONT);
		label.getLabel().setForeground(StyleUtil.getColor("ActivityChart", "titleForeground", "#03fb06"));
		column.add(label, label.getConstraints(column.getLayout()));
		return column;
	}

	private Row createButtonsRow(Hashtable parameters) {
		Hashtable<Object, Object> rowParams = EntityResultTools.keysvalues("expand", "yes", "layout", "flow", "align", "center");
		Row row = new Row(rowParams);
		Hashtable<Object, Object> butonParams = EntityResultTools.keysvalues("key", "bPRIMERA_SEMANA", "tip", "PRIMERA_SEMANA", "icon",
				StyleUtil.getIconPath("ActivityChart", "icoLeft2", ActivityChartControlPanel.ICO_FIRST), "align", "right", "name", "activitygraphbutton");
		this.buttonFirstWeek = new UButton(butonParams);
		butonParams = EntityResultTools.keysvalues("key", "left", "tip", "anterior_semana", "icon",
				StyleUtil.getIconPath("ActivityChart", "icoLeft", ActivityChartControlPanel.ICO_LEFT),
				"align", "right", "name", "activitygraphbutton");
		this.buttonLeft = new UButton(butonParams);

		Hashtable<Object, Object> columnParams = EntityResultTools.keysvalues("margin", "3;10;3;10", "expand", "no", "width", "400");
		Column centerColumn = new Column(columnParams);
		columnParams = EntityResultTools.keysvalues("valign", "center", "expand", "yes");
		Column innerColumn = new Column(columnParams);
		innerColumn.setOpaque(true);
		innerColumn.setBackground(ActivityChartControlPanel.COLOR_TITLE_DATE_BACKGROUND);
		Hashtable<Object, Object> dataLabelParams = EntityResultTools.keysvalues("attr", "ETI_TIT", "text", "", "align", "center", "fontsize", "13",
				"bold", "yes", "fontcolor", "white");
		this.titleLabel = new UDataLabel(dataLabelParams);

		butonParams = EntityResultTools.keysvalues("key", "right", "tip", "siguiente_semana", "icon",
				StyleUtil.getIconPath("ActivityChart", "icoRight", ActivityChartControlPanel.ICO_RIGHT), "align", "left", "name", "activitygraphbutton");
		this.buttonRight = new UButton(butonParams);
		butonParams = EntityResultTools.keysvalues("key", "bULTIMA_SEMANA", "tip", "ULTIMA_SEMANA", "icon",
				StyleUtil.getIconPath("ActivityChart", "icoRight2", ActivityChartControlPanel.ICO_LAST), "align", "left", "name", "activitygraphbutton");
		this.buttonLastWeek = new UButton(butonParams);

		innerColumn.add(this.titleLabel, this.titleLabel.getConstraints(innerColumn.getLayout()));
		centerColumn.add(innerColumn, innerColumn.getConstraints(centerColumn.getLayout()));

		row.add(this.buttonFirstWeek, this.buttonFirstWeek.getConstraints(row.getLayout()));
		row.add(this.buttonLeft, this.buttonLeft.getConstraints(row.getLayout()));

		row.add(centerColumn, centerColumn.getConstraints(row.getLayout()));

		row.add(this.buttonRight, this.buttonRight.getConstraints(row.getLayout()));
		row.add(this.buttonLastWeek, this.buttonLastWeek.getConstraints(row.getLayout()));

		this.buttonFirstWeek.addActionListener(new FirstWeekActionListener());
		this.buttonLastWeek.addActionListener(new LastWeekActionListener());
		this.buttonRight.addActionListener(new NextWeekActionListener());
		this.buttonLeft.addActionListener(new PreviousWeekActionListener());

		return row;
	}

	private Column createExtraColumn(Hashtable parameters) {
		Hashtable<Object, Object> columnParams = EntityResultTools.keysvalues("expand", "no");
		return new Column(columnParams);
	}

	@Override
	public void add(Component comp, Object constraints) {
		this.extraColumn.add(comp, constraints);
	}

	public void setDateRange(Date from, Date to) {
		if ((from != null) && (to != null) && (from.getTime() < to.getTime())) {
			this.setStartDate(DateUtil.trunc(from));
			this.setEndDate(DateUtil.trunc(DateUtil.addDays(to, 1)));
		} else {
			this.setStartDate(null);
			this.setEndDate(null);
		}
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		if (endDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			}
			this.chartEndDate = cal.getTime();
			cal.add(Calendar.DATE, -7);
			this.chartStartDate = cal.getTime();
		}
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		if (startDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			}
			this.chartStartDate = cal.getTime();
			cal.add(Calendar.DATE, 7);
			this.chartEndDate = cal.getTime();
		}
	}

	public void checkButtonStatus() {
		this.buttonFirstWeek.setEnabled(false);
		this.buttonLastWeek.setEnabled(false);
		this.buttonLeft.setEnabled(false);
		this.buttonRight.setEnabled(false);

		if ((this.chartStartDate != null) && (this.startDate != null) && (this.chartStartDate.getTime() > this.startDate.getTime())) {
			this.buttonFirstWeek.setEnabled(true);
			this.buttonLeft.setEnabled(true);
		}
		if ((this.chartEndDate != null) && (this.endDate != null) && (this.chartEndDate.getTime() < this.endDate.getTime())) {
			this.buttonLastWeek.setEnabled(true);
			this.buttonRight.setEnabled(true);
		}
	}

	public void setTitle(Date fini, Date ffin) {
		if ((fini != null) && (ffin != null)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			this.titleLabel.setText(ApplicationManager.getTranslation("Semana_del") + " " + df.format(fini) + " " + ApplicationManager
					.getTranslation("_al_") + " " + df.format(ffin));
		}
	}

	private void refreshChart() {
		try {
			ReflectionTools.invoke(this.parentForm.getInteractionManager(), "refreshChart");
		} catch (ClassCastException ex) {
			ActivityChartControlPanel.logger.error(null, ex);
		}
	}

	private void onLastWeekPressed() {
		Date fecIni = null;
		Date fecFin = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.endDate);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		fecIni = cal.getTime();
		cal.add(Calendar.DATE, 7);
		fecFin = cal.getTime();
		this.chartStartDate = fecIni;
		this.chartEndDate = fecFin;
		this.refreshChart();
	}

	protected void onFirstWeekPressed() {
		Date fecIni = null;
		Date fecFin = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.startDate);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		fecIni = cal.getTime();
		cal.add(Calendar.DATE, 7);
		fecFin = cal.getTime();
		this.chartStartDate = fecIni;
		this.chartEndDate = fecFin;
		this.refreshChart();
	}

	private void onNextWeekPressed() {
		Date fini = this.chartStartDate;
		Calendar cal = Calendar.getInstance();
		cal.setTime(fini);
		cal.add(Calendar.DATE, 7);
		Date dcfi = cal.getTime();
		if (cal.getTime().after(this.endDate)) {
			this.refreshChart();
			return;
		}
		cal.add(Calendar.DATE, 7);
		Date dcff = cal.getTime();
		this.chartStartDate = dcfi;
		this.chartEndDate = dcff;
		this.refreshChart();
	}

	private void onPreviousWeekPressed() {
		Date fini = this.chartStartDate;
		Calendar cal = Calendar.getInstance();
		cal.setTime(fini);
		cal.add(Calendar.DATE, -7);
		Date dcfi = cal.getTime();
		cal.add(Calendar.DATE, 7);
		Date dcff = cal.getTime();
		if (cal.getTime().before(this.startDate)) {
			this.refreshChart();
			return;
		}
		this.chartStartDate = dcfi;
		this.chartEndDate = dcff;
		this.refreshChart();
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public Date getChartEndDate() {
		return this.chartEndDate;
	}

	public Date getChartStartDate() {
		return this.chartStartDate;
	}

	protected class FirstWeekActionListener implements ActionListener {

		public FirstWeekActionListener() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ActivityChartControlPanel.this.onFirstWeekPressed();
		}
	}

	protected class LastWeekActionListener implements ActionListener {

		public LastWeekActionListener() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ActivityChartControlPanel.this.onLastWeekPressed();
		}
	}

	protected class NextWeekActionListener implements ActionListener {

		public NextWeekActionListener() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ActivityChartControlPanel.this.onNextWeekPressed();
		}
	}

	protected class PreviousWeekActionListener implements ActionListener {

		public PreviousWeekActionListener() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ActivityChartControlPanel.this.onPreviousWeekPressed();
		}
	}

}
