package com.opentach.client.report;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.table.CurrencyCellRenderer;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.RealCellRenderer;
import com.utilmize.client.report.UDynamicJasper5Engine;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;

public class CustomDynamicJasperEngine extends UDynamicJasper5Engine {
	/* chapucilla para que el jasper permita Formatters personalizados en los objetos de tipo número */
	protected final Map<String, NumberFormat> numberRendererMap = new HashMap<String, NumberFormat>();

	public CustomDynamicJasperEngine() {
		super();
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put(JRParameter.REPORT_FORMAT_FACTORY, new DefaultFormatFactory() {
			@Override
			public NumberFormat createNumberFormat(String pattern, Locale locale) {
				NumberFormat ob = CustomDynamicJasperEngine.this.numberRendererMap.get(pattern);
				if (ob != null) {
					return ob;
				}
				return super.createNumberFormat(pattern, locale);
			}
		});
		super.setParameters(parameters);
	}

	@Override
	protected void configureColumnRenders() {
		for (int i = 0; i < this.reportDialog.getModel().getColumnCount(); i++) {
			if ((this.reportDialog.getTable() != null) && (this.model != null)) {
				String columnName = this.model.getColumnName(i);
				TableCellRenderer renderer = this.reportDialog.getTable().getRendererForColumn(columnName);
				if (renderer == null) {
					renderer = this.reportDialog.getTable().getJTable().getDefaultRenderer(this.model.getColumnClass(i));
				}
				if (renderer != null) {
					Object columnPattern = this.getColumnPatternFromRenderer2(renderer);
					if (columnPattern != null) {
						if ((columnPattern instanceof NumberFormat)) {
							this.numberRendererMap.put(columnPattern.toString(), (NumberFormat) columnPattern);
						}
						this.hRenderColumns.put(columnName, columnPattern);
					}
				}
			}
		}
	}

	protected Object getColumnFormatterFromRenderer(TableCellRenderer rendererColumn) {
		if (rendererColumn instanceof IReportableCellRenderer) {
			Object reportExpression = ((IReportableCellRenderer) rendererColumn).getReportExpression("JASPER_6");
			if (reportExpression != null) {
				return reportExpression;
			}
			String pattern = ((IReportableCellRenderer) rendererColumn).getReportPattern("JASPER_6");
			if (pattern != null) {
				return pattern;
			}
		}
		if (rendererColumn instanceof DateCellRenderer) {
			return this.createDatePattern((DateCellRenderer) rendererColumn);
		}
		if (rendererColumn instanceof CurrencyCellRenderer) {
			return this.createDecimalPattern((CurrencyCellRenderer) rendererColumn) + "¤";
		}
		if (rendererColumn instanceof RealCellRenderer) {
			return this.createDecimalPattern((RealCellRenderer) rendererColumn);
		}

		return null;
	}

}
