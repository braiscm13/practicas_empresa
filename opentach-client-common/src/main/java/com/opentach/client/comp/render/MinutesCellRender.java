package com.opentach.client.comp.render;

import java.awt.Component;
import java.awt.LayoutManager;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.table.CellRenderer;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.field.table.IExportableRenderer;
import com.utilmize.client.gui.field.table.render.IXmlTableCellRenderer;
import com.utilmize.client.report.IReportableCellRenderer;

public class MinutesCellRender extends CellRenderer implements IReportableCellRenderer, IExportableRenderer, FormComponent, IXmlTableCellRenderer, Freeable {

	private boolean		secondsresolution	= false;

	protected String	columnName;

	/** The global id. */
	protected Object	globalId;

	public MinutesCellRender(Hashtable params) throws Exception {
		super();
		this.init(params);
	}

	public MinutesCellRender(String columnName) {
		this(columnName, false);
	}

	public MinutesCellRender(String columnName, boolean secondsResolution) {
		super();
		this.columnName = columnName;
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setSecondsResolution(secondsResolution);
	}

	public void setSecondsResolution(boolean v) {
		this.secondsresolution = v;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return new MinutesCellRender(this.columnName);
		}
	}

	@Override
	public Component getTableCellRendererComponent(javax.swing.JTable tabla, java.lang.Object valor, boolean seleccionado, boolean tieneFoco,
			int fila, int columna) {
		Component comPadre = super.getTableCellRendererComponent(tabla, valor, seleccionado, tieneFoco, fila, columna);
		JLabel labelTemp = (JLabel) comPadre;
		if ((valor != null) && (valor instanceof Number)) {
			int value = ((Number) valor).intValue();
			String textHoras = DateUtil.parsearTiempoDisponible(value, this.secondsresolution);
			labelTemp.setText(textHoras);
		} else {
			labelTemp.setText("");
		}

		labelTemp.setHorizontalAlignment(SwingConstants.CENTER);
		return comPadre;
	}

	public static String parsearTiempoDisponible(int value) {
		return DateUtil.parsearTiempoDisponible(value, false);
	}

	@Override
	public String getReportPattern(String engineId) {
		return null;
	}

	@Override
	public Object getReportExpression(String engineId) {
		return this.reportFormat;
	}

	public Format getReportFormat() {
		return this.reportFormat;
	}

	@Override
	public boolean isOperable() {
		return true;
	}

	Format	reportFormat	= new NumberFormat() {

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
			String s = DateUtil.parsearTiempoDisponible(obj == null ? 0 : ((Number) obj).intValue(), MinutesCellRender.this.secondsresolution);
			toAppendTo.append(s);
			return toAppendTo;
		}

		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			return this.format((Object) number, toAppendTo, pos);
		}

		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			return this.format((Object) number, toAppendTo, pos);
		}

		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			return null;
		}

	};

	@Override
	public String getHTMLExportValue(JTable table, Object value, int row, int column) {
		Component c = this.getTableCellRendererComponent(table, value, false, false, row, column);
		if (c instanceof JLabel) {
			return ((JLabel) c).getText();
		}
		return value.toString();
	}

	@Override
	public Object getReportExportValue(JTable table, Object value, int row, int column) {
		return value;
	}

	@Override
	public Object getExcelExportValue(JTable table, Object value, int row, int column) {

		// Export to apply format "[h]:mm" and allow to operate
		// That is, export number with "days" (for instance, 02:24 -> 0.04)
		// The value received is a number with "minutes"

		return value != null ? (((Number) value).doubleValue() / 60 / 24) : value;
	}

	@Override
	public Object getChartExportValue(JTable table, Object value, int row, int column) {
		return value;
	}

	@Override
	public void setComponentLocale(Locale l) {
		// Do nothing by now
	}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {
		// Do nothing by now
	}

	@Override
	public Vector getTextsToTranslate() {
		// Do nothing by now
		return null;
	}

	@Override
	public void free() {
		// Do nothing by now
	}

	@Override
	public void init(Hashtable parameters) throws Exception {
		this.columnName = (String) parameters.get("column");
		this.globalId = parameters.get("globalid");
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		return this.columnName;
	}

	@Override
	public Object getIdentifier() {
		return this.globalId;
	}
}
