/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.utilmize.client.report;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.table.CurrencyCellRenderer;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.PercentCellRenderer;
import com.ontimize.gui.table.RealCellRenderer;
import com.ontimize.report.DefaultReportDialog;
import com.ontimize.report.engine.dynamicjasper5.CustomJasperViewerToolbar;
import com.ontimize.report.engine.dynamicjasper5.DynamicJasperEngine;
import com.utilmize.client.gui.field.table.UTable;

import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.constants.ImageScaleMode;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import net.sf.jasperreports.engine.JasperPrint;

public class UDynamicJasper5Engine extends DynamicJasperEngine {

	@Override
	public void createViewer(JasperPrint jp, DefaultReportDialog reportDialog) {
		this.fixSaveOptions();
		super.createViewer(jp, reportDialog);
	}

	/**
	 * Avoid the contributors nont valid for users (JASPERPRINT)
	 */
	protected void fixSaveOptions() {
		String property = System.getProperty("com.ontimize.report.saveContributors");
		if ((property == null) || "".equals(property)) {
			String[] contributors = new String[] { CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_PDF, CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_CSV, CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_DOCX, CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_HTML, CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_ODT, CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XLS_SINGLE_SHEET, CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XLS_MULTIPLE_SHEET };
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < contributors.length; i++) {
				sb.append(contributors[i]);
				if (i < (contributors.length - 1)) {
					sb.append(";");
				}
			}
			System.setProperty("com.ontimize.report.saveContributors", sb.toString());
		}
	}

	@Override
	public AbstractColumn setPatterns(AbstractColumn column, Class columnClass) {
		Object pattern = this.getPatternForColumn2(column.getName());
		if (pattern instanceof Format) {
			column.setTextFormatter((Format) pattern);
		} else if (pattern != null) {
			column.setPattern((String) pattern);
		}
		return column;
	}

	/**
	 * CHANGE: the supper method only supports to return String. SEE next methods overrided due to use this new methods
	 *
	 * @param rendererColumn
	 * @return
	 */
	protected Object getColumnPatternFromRenderer2(TableCellRenderer rendererColumn) {
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
			return this.createDecimalPattern((CurrencyCellRenderer) rendererColumn) + " " + ((CurrencyCellRenderer) rendererColumn).getCurrencySymbol();
		}
		if (rendererColumn instanceof RealCellRenderer) {
			return this.createDecimalPattern((RealCellRenderer) rendererColumn);
		}

		if (rendererColumn instanceof PercentCellRenderer) {
			return this.createDecimalPattern((PercentCellRenderer) rendererColumn);
		}
		return null;
	}

	/**
	 * Override to use getColumnPatternFromRenderer2 function
	 */
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
						this.hRenderColumns.put(columnName, columnPattern);
					}
				}
			}
		}
	}

	/**
	 * CHANGE: the supper method only supports to return String. SEE next methods overrided due to use this new methods
	 *
	 * @param columnName
	 * @return
	 */
	public Object getPatternForColumn2(String columnName) {
		if (this.isVirtualColumn(columnName)) {
			columnName = this.getColumnFromVirtualColumn(columnName);
		}
		Object renderPattern = this.hRenderColumns.get(columnName);
		if (renderPattern != null) {
			return renderPattern;
		}
		if (this.isDateColumn(this.getColumnClassForColumn(columnName))) {
			return this.getDatePattern();
		}
		return null;
	}

	/**
	 * Override to use getPatternForColumn2 function
	 */
	@Override
	public Vector generateVirtualColumnValues(String column, Vector originalColumnValues) {
		Vector virtualColumnValues = new Vector();
		Vector virtualColumnValuesForOrdering = new Vector();
		Vector vGlobal = new Vector();
		Integer operation = (Integer) this.reportDialog.getSelectedDateGroupingColumns().get(column);
		if (operation == null) {
			// not selected operation
			operation = new Integer(0);
		}
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < originalColumnValues.size(); i++) {
			String value = null;
			String valueOrdering = null;
			if (originalColumnValues.get(i) != null) {
				if (!(originalColumnValues.get(i) instanceof Date)) {
					return null;
				}
				calendar.setTime((java.util.Date) originalColumnValues.get(i));
				switch (operation.intValue()) {
					case 0:
						// Complete date (day, month and year) and hour
						SimpleDateFormat sdfDateTime = new SimpleDateFormat((String) this.getPatternForColumn2(column));
						value = sdfDateTime.format(calendar.getTime());
						SimpleDateFormat sdfDateTimeOrdering = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						valueOrdering = sdfDateTimeOrdering.format(calendar.getTime());
						break;
					case 1:
						// Date without hour
						SimpleDateFormat sdfDate = new SimpleDateFormat(this.getGroupingDatePattern());
						value = sdfDate.format(calendar.getTime());
						SimpleDateFormat sdfDateOrdering = new SimpleDateFormat("yyyy-MM-dd");
						valueOrdering = sdfDateOrdering.format(calendar.getTime());
						break;
					case 2:
						// Month
						value = this.getMonthText(calendar.get(Calendar.MONTH) + 1);
						valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY, this.reportDialog
						        .getBundle()) + DynamicJasperEngine.dateNameSeparator + this.getCompleteValueForDate(Integer.toString(calendar.get(Calendar.MONTH) + 1));
						;
						break;
					case 3:
						// Month and year
						value = this.getMonthText(calendar.get(Calendar.MONTH) + 1) + " " + Integer.toString(calendar.get(Calendar.YEAR));
						valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
						        this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator + Integer
						                .toString(calendar.get(Calendar.YEAR)) + DynamicJasperEngine.groupDateSeparator + ApplicationManager.getTranslation(
						                        DefaultReportDialog.GROUP_BY_MONTH_KEY, this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator + this
						                                .getCompleteValueForDate(Integer.toString(calendar.get(Calendar.MONTH) + 1));
						break;
					case 4:
						// Quarter
						value = this.getQuarterText(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);
						valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
						        this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator + Integer.toString(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);
						;
						break;
					case 5:
						// Quarter and year
						value = this.getQuarterText(((calendar.get(Calendar.MONTH) + 1) / 4) + 1) + " " + Integer.toString(calendar.get(Calendar.YEAR));
						valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
						        this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator + Integer
						                .toString(calendar.get(Calendar.YEAR)) + DynamicJasperEngine.groupDateSeparator + ApplicationManager.getTranslation(
						                        DefaultReportDialog.GROUP_BY_QUARTER_KEY, this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator + Integer
						                                .toString(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);
						break;
					case 6:
						// Year
						value = Integer.toString(calendar.get(Calendar.YEAR));
						valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
						        this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator + Integer.toString(calendar.get(Calendar.YEAR));
						;
						break;
				}
			}
			virtualColumnValues.add(value);
			virtualColumnValuesForOrdering.add(valueOrdering);
		}
		vGlobal.add(virtualColumnValues);
		vGlobal.add(virtualColumnValuesForOrdering);
		return vGlobal;
	}

	protected String getCompleteValueForDate(String sOriginalValue) {
		if (sOriginalValue.length() == 1) {
			return sOriginalValue = "0" + sOriginalValue;
		}
		return sOriginalValue;
	}

	/**
	 * Override to use getPatternForColumn2 function
	 */
	@Override
	public ColumnBuilder configureColumnBuilder(ColumnBuilder cb, String columnName, Class columnClass) {
		if (this.isImageColumn(columnClass)) {
			cb.setColumnProperty(columnName, "java.awt.Image");
			cb.setColumnType(ColumnBuilder.COLUMN_TYPE_IMAGE);
		} else if (this.isBooleanImageColumn(columnClass)) {
			cb.setColumnProperty(columnName, "java.awt.Image");
			cb.setColumnType(ColumnBuilder.COLUMN_TYPE_IMAGE);
			cb.setImageScaleMode(ImageScaleMode.NO_RESIZE);
		} else {
			// Jasperreports engine not supports java.sql.Date (throws a
			// ValidationException)
			if (columnClass.isAssignableFrom(java.sql.Date.class)) {
				cb.setColumnProperty(columnName, "java.util.Date");
			} else {
				cb.setColumnProperty(columnName, columnClass.getName());
			}

			// CHANGE: support to object method
			Object pattern = this.getPatternForColumn2(columnName);
			if (pattern instanceof Format) {
				cb.setTextFormatter((Format) pattern);
			} else if (pattern != null) {
				cb.setPattern((String) pattern);
			}
		}
		cb.setTitle(ApplicationManager.getTranslation(columnName, this.reportDialog.getBundle()));
		cb.setStyle(this.createColumnDataStyle(columnName, columnClass));

		cb.setWidth(new Integer(70));
		return cb;
	}

	/**
	 * Override to use getPatternForColumn2 function
	 */
	@Override
	public ColumnBuilder configureGroupColumnBuilder(ColumnBuilder cb, String groupColumn, Class groupClass) {
		// defines the field of the data source that this column will show, also its type (java.sql.Date) not supported
		cb = this.setColumnGroupClass(cb, groupColumn, groupClass);

		// Used to allow group by in dates
		// This column requires special evaluation depending on the type of grouping date selected. Conditions are evaluated in getEvaluationValueForGroupingDate
		if (this.isVirtualColumn(groupColumn)) {
			cb = this.setDateGroupingExpression(cb, groupColumn);
		}

		// CHANGE: support to object method
		Object pattern = this.getPatternForColumn2(groupColumn);
		if (pattern instanceof Format) {
			cb.setTextFormatter((Format) pattern);
		} else if (pattern != null) {
			cb.setPattern((String) pattern);
		}

		cb.setTitle(ApplicationManager.getTranslation(this.getColumnFromVirtualColumn(groupColumn), this.reportDialog.getBundle()));

		cb.setWidth(DynamicJasperEngine.columnWidth);
		cb.setStyle(this.defaultHeaderForGroupStyle);
		return cb;
	}

	@Override
	public void buildColumns() throws Exception {
		// We must iterate for all possible printing columns
		for (int i = 0; i < this.reportDialog.getPrintingColumns().size(); i++) {
			String columnName = this.reportDialog.getPrintingColumns().get(i).toString();
			Class columnClass = this.getColumnClassForColumn(columnName);

			if (this.isShowedRowNumber() && (i == 0)) {
				// Create the row that shows number of column
				this.createRowNumberColumn();
			}

			this.configureTemplate();
			// Store the original position for column
			this.keepOriginalColumnPositions(i);

			ColumnBuilder cb = ColumnBuilder.getNew();
			cb = this.configureColumnBuilder(cb, columnName, columnClass);

			// Builds the column from column builder
			AbstractColumn column = cb.build();

			// Configure the column
			column = this.configureColumn(column, columnName, columnClass);

			if (this.reportDialog.getSelectedPrintingColumns().contains(columnName)) {
				// When column is printed we must add a column to report
				this.drb.addColumn(column);
			} else {
				// when column is not printed in report we must register a field
				// for this column because could be create a group with this
				// column
				// or show a function column. Fields are only used internally by
				// jasperreports but not showed in report
				this.drb = this.registerField(columnName, columnClass);
			}
			// For date columns, we must create a virtual column that allows to
			// group by date. This column will be hidden (width = 0) but it is
			// used for calculation
			if (this.isDateColumn(columnClass)) {
				this.drb = this.configureVirtualColumn(this.drb, columnName, columnClass);
			}

			// Adds function for these column columns when not exists groups, one row at the end of the report
			// Builds the footer of report.
			// CHANGE: look for function over column not generic
			if (this.existFunctionFor(columnName) && this.isNumericClass(columnClass)) {
				column = cb.build();
				column.setBlankWhenNull(Boolean.TRUE);
				column.setName(columnName);
				column.setWidth(new Integer(500));
				this.applyFunctionForColumn(column, null);
			}
		}
	}

	protected boolean existFunctionFor(String columnName) {
		return this.reportDialog.getSelectedFunctionColumns().keySet().contains(columnName);
	}

	/**
	 * Override in order to ensure paint in the report the column text properly (when column text is different from column attr).
	 */
	@Override
	public AbstractColumn configureColumn(AbstractColumn column, String columnName, Class columnClass) {
		AbstractColumn configureColumn = super.configureColumn(column, columnName, columnClass);
		if ((this.reportDialog != null) && (this.reportDialog.getTable() instanceof UTable)) {
			String columnText = ((UTable) this.reportDialog.getTable()).getColumnText(columnName);
			column.setTitle(ApplicationManager.getTranslation(columnText, this.reportDialog.getBundle()));
		}

		return configureColumn;
	}

	@Override
	public AbstractColumn configureGroupColumn(AbstractColumn column, String groupColumn, Class groupClass, int groupIndex) {
		AbstractColumn configureGroupColumn = super.configureGroupColumn(column, groupColumn, groupClass, groupIndex);
		if ((this.reportDialog != null) && (this.reportDialog.getTable() instanceof UTable)) {
			String columnText = ((UTable) this.reportDialog.getTable()).getColumnText(groupColumn);
			column.setTitle(ApplicationManager.getTranslation(columnText, this.reportDialog.getBundle()));
		}
		return configureGroupColumn;
	}
}