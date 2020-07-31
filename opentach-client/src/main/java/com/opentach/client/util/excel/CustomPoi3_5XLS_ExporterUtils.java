package com.opentach.client.util.excel;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Types;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ontimize.gui.field.document.CurrencyDocument;
import com.ontimize.gui.table.CurrencyCellRenderer;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.ImageCellRenderer;
import com.ontimize.gui.table.PercentCellRenderer;
import com.ontimize.gui.table.RealCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.util.xls.AbstractXLSExporter;
import com.ontimize.util.xls.Poi3_5XLSExporterUtils;
import com.ontimize.util.xls.XLSExporter;
import com.opentach.client.comp.render.MinutesCellRender;

/**
 * Extension to support correctly MinutesCellRender. Super methods must not be static, else cannot be extended!!!!!!! Will be fixed in Ontimize, then only the method
 * "fixColumnSytles" and "setColumnStyle" will be required, can be romeved all variables and methods. See the class CustomAbstractXLSExporter, already exported to fix it.
 */
public class CustomPoi3_5XLS_ExporterUtils extends Poi3_5XLSExporterUtils /* extends CustomAbstractXLSExporter */ implements XLSExporter {

	public static CellStyle cs_minutes = null;

	public CustomPoi3_5XLS_ExporterUtils() {
		super();

		// Solve problem with time zone
		this.sdfHour.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
		this.sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
	}

	private List fixColumnSytles(Workbook wb, List orderColumns, List columnStyles, Hashtable hColumnRenderers) {
		if (columnStyles == null) {
			columnStyles = new ArrayList<>();
		}
		for (int i = 0; i < orderColumns.size(); i++) {
			CellStyle cellStyle = null;

			Object cellType = hColumnRenderers.get(orderColumns.get(i).toString());
			if ((cellType != null) && (cellType instanceof MinutesCellRender)) {
				DataFormat dataFormat = wb.createDataFormat();
				cellStyle = wb.createCellStyle();
				// cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("[h]:mm"));
				cellStyle.setDataFormat(dataFormat.getFormat("[h]:mm"));

				this.cs_real = wb.createCellStyle();
				this.cs_real.setDataFormat(wb.createDataFormat().getFormat(AbstractXLSExporter.currencyPattern));
			}

			columnStyles.add(cellStyle);
		}
		return columnStyles;
	}

	// ///////////////////////// METHODS OVERRIDED in order to avoid static method calls!!!!!! /////////
	@Override
	protected void setColumnStyle(Workbook wb, Sheet sheet, List orderColumns, List columnStyles, Hashtable hColumnRenderers, Hashtable hColumnTypes) {
		// Path : Set Column style for columns with render of type MinutesCellRenderer
		columnStyles = this.fixColumnSytles(wb, orderColumns, columnStyles, hColumnRenderers);

		Row row = sheet.createRow(sheet.getLastRowNum());
		int column = 0;

		for (int i = 0; i < orderColumns.size(); i++) {
			if (columnStyles != null) {
				try {
					Object style = columnStyles.get(i);
					if (style instanceof CellStyle) {
						sheet.setDefaultColumnStyle(i, (CellStyle) style);
						continue;
					}
				} catch (Exception x) {}
			}

			Cell cell = null;
			try {
				cell = row.createCell((short) (column++));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			switch (this.getOverridedCellType(orderColumns.get(i).toString(), true, hColumnRenderers, hColumnTypes)) {

				case DATE_CELL:
				case DATE_HOUR_CELL:

					this.cs_date_hour = wb.createCellStyle();
					this.cs_date_hour.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

					this.cs_date = wb.createCellStyle();
					this.cs_date.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

					sheet.setDefaultColumnStyle(i, this.cs_date);

					break;

				case CURRENCY_CELL:

					this.cs_currency = wb.createCellStyle();

					this.cs_currency.setDataFormat(wb.createDataFormat().getFormat(AbstractXLSExporter.currencyPattern + " " + CurrencyDocument.defaultCurrencySymbol));

					sheet.setDefaultColumnStyle(i, this.cs_currency);

					break;

				case PERCENT_CELL:
					this.cs_percent = wb.createCellStyle();
					this.cs_percent.setDataFormat(wb.createDataFormat().getFormat(AbstractXLSExporter.currencyPattern + "\" %\""));
					sheet.setDefaultColumnStyle(i, this.cs_percent);
					break;

				case REAL_CELL:
					this.cs_real = wb.createCellStyle();
					this.cs_real.setDataFormat(wb.createDataFormat().getFormat(AbstractXLSExporter.currencyPattern));
				case REAL_MINUTES:
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void writeLineWithoutStyle(Workbook wb, Sheet sheet, List values, List orderColumns, Hashtable hColumnRenderers, List columnStyles, Hashtable hColumnTypes) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		int column = 0;
		for (int i = 0; i < values.size(); i++) {
			Object ob = values.get(i);
			Cell cell = null;
			try {
				cell = row.createCell((short) (column++));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			switch (this.getOverridedCellType(orderColumns.get(i).toString(), ob, hColumnRenderers, hColumnTypes)) {

				case DECIMAL_CELL:
					if (ob != null) {
						if (ob instanceof Number) {
							if (ob instanceof Long) {
								cell.setCellValue(((Long) ob).longValue());
							}
							if (ob instanceof Float) {
								cell.setCellValue(((Float) ob).floatValue());
							}
							if (ob instanceof Double) {
								cell.setCellValue(((Double) ob).doubleValue());
							}
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						} else {
							try {
								Number number = (this.decimalFormat.parse(ob.toString()));
								if (number instanceof Long) {
									cell.setCellValue(number.longValue());
								}
								if (number instanceof Float) {
									cell.setCellValue(number.floatValue());
								}
								if (number instanceof Double) {
									cell.setCellValue(number.doubleValue());
								}
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							} catch (Exception e) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								cell.setCellValue(ob.toString());
							}
						}

					}
					break;
				case NUMERIC_CELL:
					if (ob != null) {
						if (ob instanceof Number) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(((Number) ob).intValue());
						} else {
							try {
								int value = this.numericFormat.parse(ob.toString()).intValue();
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellValue(value);
							} catch (Exception e) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								cell.setCellValue(ob.toString());
							}
						}

					}
					break;

				case DATE_CELL:
				case DATE_HOUR_CELL:
					if (ob != null) {
						if (ob.toString().contains(":")) {
							if (ob instanceof Date) {
								cell.setCellValue((Date) ob);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellStyle(this.cs_date_hour);
							} else {
								try {
									cell.setCellValue(this.sdfHour.parse(ob.toString()));
									cell.setCellType(Cell.CELL_TYPE_NUMERIC);
									cell.setCellStyle(this.cs_date_hour);
								} catch (ParseException e) {
									cell.setCellType(Cell.CELL_TYPE_STRING);
									cell.setCellValue(String.valueOf(ob));
								}
							}
						} else {
							if (ob instanceof Date) {
								cell.setCellValue((Date) ob);
								cell.setCellStyle(this.cs_date);
							} else {
								try {
									cell.setCellValue(this.sdf.parse(ob.toString()));
									cell.setCellStyle(this.cs_date);
								} catch (ParseException e) {
									cell.setCellType(Cell.CELL_TYPE_STRING);
									cell.setCellValue(String.valueOf(ob));
								}
							}

						}
					}

					break;

				case CURRENCY_CELL:
					ParsePosition pp = new ParsePosition(0);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);

					if (ob != null) {
						String cellValue = ((String) ob).replaceAll(this.getCurrencySymbol((String) ob), "").trim();
						Number number = this.decimalFormat.parse(String.valueOf(cellValue).trim(), pp);
						// cellValue = cellValue.replaceAll(",", "");
						cell.setCellStyle(this.cs_currency);
						if (number != null) {
							cell.setCellValue(number.doubleValue());
						}
					}
					break;
					// since 5.3.8
				case IMAGE_CELL:
					byte[] bytesImage = new byte[0];
					try {
						ByteBuffer bb = ByteBuffer.wrap(ob.toString().getBytes(Charset.forName("ISO-8859-1")));
						bytesImage = bb.array();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (bytesImage.length > 0) {
						int pictureIdx = wb.addPicture(bytesImage, Workbook.PICTURE_TYPE_JPEG);

						// add a picture shape
						ClientAnchor anchor = this.helper.createClientAnchor();

						// set top-left corner of the picture,
						// subsequent call of Picture#resize() will operate relative
						// to it
						anchor.setCol1(column - 1);
						anchor.setRow1(sheet.getLastRowNum());

						anchor.setCol2(column);
						anchor.setRow2(sheet.getLastRowNum() + 1);

						Picture pict = this.drawing.createPicture(anchor, pictureIdx);
						row.setHeight((short) 1000);
					}
					break;

				case PERCENT_CELL:

					ParsePosition ppt = new ParsePosition(0);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);

					if (ob != null) {

						String cellValue = ((String) ob).replaceAll(this.getPercentSymbol((String) ob), "").trim();
						Number number = this.decimalFormat.parse(String.valueOf(cellValue).trim(), ppt);
						if (number != null) {
							cell.setCellValue(number.doubleValue());
						}

						cell.setCellStyle(this.cs_percent);
					}
					break;

				case REAL_CELL:
					cell.setCellStyle(this.cs_real);
				case REAL_MINUTES:
					if (ob != null) {
						if (ob instanceof Number) {
							if ((ob instanceof Double) || (ob instanceof Float) || (ob instanceof Long)) {
								cell.setCellValue(((Double) ob).doubleValue());
							}
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						} else {
							try {
								Number number = (this.decimalFormat.parse(ob.toString()));
								if ((number instanceof Double) || (number instanceof Float) || (number instanceof Long)) {
									cell.setCellValue(number.doubleValue());
								}
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							} catch (Exception e) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								cell.setCellValue(ob.toString());
							}
						}
					}
					break;

				case TEXT_CELL:
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(String.valueOf(ob));
					break;
				default:
					try {
						ParsePosition parsePos = new ParsePosition(0);
						Number number = this.decimalFormat.parse(String.valueOf(ob).trim(), parsePos);
						if (parsePos.getIndex() < String.valueOf(ob).trim().length()) {
							throw new Exception("Parse not complete");
						}
						cell.setCellValue(number.doubleValue());
					} catch (Exception e) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(String.valueOf(ob));
					}
					break;
			}
		}
	}

	@Override
	protected void writeLine(Workbook wb, Sheet sheet, List values, List orderColumns, Hashtable hColumnRenderers, List columnStyles, Hashtable hColumnTypes) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		int column = 0;
		for (int i = 0; i < values.size(); i++) {
			Object ob = values.get(i);
			Cell cell = null;
			try {
				cell = row.createCell((short) (column++));
				CellStyle cs_style = columnStyles != null ? (CellStyle) columnStyles.get(i) : wb.createCellStyle();
				cell.setCellStyle(cs_style);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			switch (this.getOverridedCellType(orderColumns.get(i).toString(), ob, hColumnRenderers, hColumnTypes)) {
				case DECIMAL_CELL:
					if (ob != null) {
						if (ob instanceof Number) {
							if (ob instanceof Long) {
								cell.setCellValue(((Long) ob).longValue());
							}
							if (ob instanceof Float) {
								cell.setCellValue(((Float) ob).floatValue());
							}
							if (ob instanceof Double) {
								cell.setCellValue(((Double) ob).doubleValue());
							}
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						} else {
							try {
								Number number = (this.decimalFormat.parse(ob.toString()));
								if (number instanceof Long) {
									cell.setCellValue(number.longValue());
								}
								if (number instanceof Float) {
									cell.setCellValue(number.floatValue());
								}
								if (number instanceof Double) {
									cell.setCellValue(number.doubleValue());
								}
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							} catch (Exception e) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								cell.setCellValue(ob.toString());
							}
						}

					}
					break;
				case NUMERIC_CELL:
					if (ob != null) {
						if (ob instanceof Number) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(((Number) ob).intValue());
						} else {
							try {
								int value = this.numericFormat.parse(ob.toString()).intValue();
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellValue(value);
							} catch (Exception e) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								cell.setCellValue(ob.toString());
							}
						}

					}
					break;

				case DATE_CELL:
				case DATE_HOUR_CELL:
					if (ob != null) {
						if (ob.toString().contains(":")) {
							if (ob instanceof Date) {
								cell.setCellValue((Date) ob);
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								CellStyle cs_date_hour = cell.getCellStyle();
								cs_date_hour.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
								cell.setCellStyle(cs_date_hour);
							} else {
								try {
									cell.setCellValue(this.sdfHour.parse(ob.toString()));
									cell.setCellType(Cell.CELL_TYPE_NUMERIC);
									CellStyle cs_date_hour = cell.getCellStyle();
									cs_date_hour.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
									cell.setCellStyle(cs_date_hour);
								} catch (ParseException e) {
									cell.setCellType(Cell.CELL_TYPE_STRING);
									cell.setCellValue(String.valueOf(ob));
								}
							}
						} else {
							if (ob instanceof Date) {
								cell.setCellValue((Date) ob);
								CellStyle cs_date = cell.getCellStyle();
								cs_date.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
								cell.setCellStyle(cs_date);
							} else {
								try {
									cell.setCellValue(this.sdf.parse(ob.toString()));
									CellStyle cs_date = cell.getCellStyle();
									cs_date.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
									cell.setCellStyle(cs_date);
								} catch (ParseException e) {
									cell.setCellType(Cell.CELL_TYPE_STRING);
									cell.setCellValue(String.valueOf(ob));
								}
							}
						}
					}

					break;

				case CURRENCY_CELL:
					ParsePosition pp = new ParsePosition(0);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);

					if (ob != null) {

						String cellValue = ((String) ob).replaceAll(this.getCurrencySymbol((String) ob), "").trim();
						Number number = this.decimalFormat.parse(String.valueOf(cellValue).trim(), pp);
						// cellValue = cellValue.replaceAll(",", "");
						if (number != null) {
							CellStyle cs_currency = cell.getCellStyle();
							cs_currency
									.setDataFormat(wb.createDataFormat().getFormat(AbstractXLSExporter.currencyPattern + " " + this.getCurrencySymbol((String) ob)));
							cell.setCellStyle(cs_currency);
							cell.setCellValue(number.doubleValue());
						}
					}
					break;
					// since 5.3.8
				case IMAGE_CELL:
					byte[] bytesImage = new byte[0];
					try {
						ByteBuffer bb = ByteBuffer.wrap(ob.toString().getBytes(Charset.forName("ISO-8859-1")));
						bytesImage = bb.array();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (bytesImage.length > 0) {
						int pictureIdx = wb.addPicture(bytesImage, Workbook.PICTURE_TYPE_JPEG);

						// add a picture shape
						ClientAnchor anchor = this.helper.createClientAnchor();
						// set top-left corner of the picture,
						// subsequent call of Picture#resize() will operate relative
						// to it
						anchor.setCol1(column - 1);
						anchor.setRow1(sheet.getLastRowNum());
						Picture pict = this.drawing.createPicture(anchor, pictureIdx);

						// auto-size picture relative to its top-left corner
						pict.resize(0.3);
					}
					break;
				case TEXT_CELL:
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(String.valueOf(ob));
					break;
				default:
					try {
						ParsePosition parsePos = new ParsePosition(0);
						Number number = this.decimalFormat.parse(String.valueOf(ob).trim(), parsePos);
						if (parsePos.getIndex() < String.valueOf(ob).trim().length()) {
							throw new Exception("Parse not complete");
						}
						cell.setCellValue(number.doubleValue());
					} catch (Exception e) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(String.valueOf(ob));
					}
					break;
			}
		}
	}

	@Override
	protected void writeLine(Object wb, org.apache.poi.xssf.usermodel.XSSFSheet sheet, List values, List orderColumns, Hashtable hColumnRenderers, List columnStyles,
			Hashtable hColumnTypes) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		int column = 0;
		for (int i = 0; i < values.size(); i++) {
			Object ob = values.get(i);
			Method method = null;
			Cell cell = null;
			try {
				method = Row.class.getMethod("createCell", new Class[] { int.class });
				cell = (Cell) method.invoke(row, new Object[] { new Short((short) (column++)) });
				if (columnStyles != null) {
					cell.setCellStyle((org.apache.poi.xssf.usermodel.XSSFCellStyle) columnStyles.get(i));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			switch (this.getOverridedCellType(orderColumns.get(i).toString(), ob, hColumnRenderers, hColumnTypes)) {
				case NUMERIC_CELL:
					try {
						cell.setCellValue(((Number) ob).doubleValue());
					} catch (Exception e) {
						if (ob != null) {
							if (!(ob instanceof Number)) {
								cell.setCellValue(new Double(ob.toString()));
							}
						}
					}
					break;
				case DATE_CELL:
					cell.setCellValue((Date) ob);
					CellStyle style = cell.getCellStyle();
					DataFormat dataFormat = ((Workbook) wb).createDataFormat();
					style.setDataFormat(dataFormat.getFormat(this.dateFormat));
					cell.setCellStyle(style);
					break;
				case CURRENCY_CELL:
					CellStyle cs = cell.getCellStyle();
					String cellValue = ((String) ob).replaceAll(this.getCurrencySymbol((String) ob), "").trim();
					ParsePosition pp = new ParsePosition(0);
					Number number = this.decimalFormat.parse(String.valueOf(cellValue).trim(), pp);
					// cellValue = cellValue.replaceAll(",", "");
					cell.setCellStyle(cs);
					if (number != null) {
						cs.setDataFormat(((Workbook) wb).createDataFormat().getFormat(AbstractXLSExporter.currencyPattern + this.getCurrencySymbol((String) ob)));
						cell.setCellValue(number.doubleValue());
					}
					break;
				default:
					try {
						pp = new ParsePosition(0);
						number = this.decimalFormat.parse(String.valueOf(ob).trim(), pp);
						if (pp.getIndex() < String.valueOf(ob).trim().length()) {
							throw new Exception("Parse not complete");
						}
						cell.setCellValue(number.doubleValue());
					} catch (Exception e) {
						cell.setCellValue(String.valueOf(ob));
						if (columnStyles != null) {
							cell.setCellStyle((CellStyle) columnStyles.get(i));
						}
					}
					break;
			}
		}
	}

	////// ********************************************************************* //////
	////// ******************* OVERRIED FROM AbstractXLSExporter *************** //////
	////// ********************************************************************* //////

	public static final int REAL_MINUTES = 9;

	public int getOverridedCellType(String columnName, Object columnValue, Hashtable hColumnRenderers, Hashtable hColumnTypes) {
		if ((columnValue == null) || (columnValue instanceof Table.KeyObject) || (hColumnTypes == null) || ((((Integer) hColumnTypes
				.get(columnName)) != null) && (Types.VARCHAR == ((Integer) hColumnTypes.get(columnName)).intValue()))) {
			return AbstractXLSExporter.TEXT_CELL;
		}
		Object cellType = hColumnRenderers.get(columnName);
		if (cellType != null) {

			if (cellType instanceof CurrencyCellRenderer) {
				return AbstractXLSExporter.CURRENCY_CELL;
			}
			if (cellType instanceof DateCellRenderer) {
				return AbstractXLSExporter.DATE_CELL;
			}
			if (cellType instanceof ImageCellRenderer) {
				return AbstractXLSExporter.IMAGE_CELL;
			}
			if (cellType instanceof PercentCellRenderer) {
				return AbstractXLSExporter.PERCENT_CELL;
			}

			if (cellType instanceof RealCellRenderer) {
				return AbstractXLSExporter.REAL_CELL;
			}

			// Fixed here
			if (cellType instanceof MinutesCellRender) {
				return CustomPoi3_5XLS_ExporterUtils.REAL_MINUTES;
			}

		} else {
			if (hColumnTypes.get(columnName) == null) {
				return AbstractXLSExporter.TEXT_CELL;
			}
			int columnType = ((Integer) hColumnTypes.get(columnName)).intValue();
			switch (columnType) {
				case Types.VARCHAR:
					return AbstractXLSExporter.TEXT_CELL;
				case Types.LONGVARCHAR:
					return AbstractXLSExporter.TEXT_CELL;
				case Types.CLOB:
					return AbstractXLSExporter.TEXT_CELL;

				case Types.INTEGER:
					return AbstractXLSExporter.NUMERIC_CELL;
				case Types.SMALLINT:
					return AbstractXLSExporter.NUMERIC_CELL;
				case Types.TINYINT:
					return AbstractXLSExporter.NUMERIC_CELL;
				case Types.BIGINT:
					return AbstractXLSExporter.NUMERIC_CELL;

				case Types.BIT:
					return AbstractXLSExporter.NUMERIC_CELL;
				case Types.BOOLEAN:
					return AbstractXLSExporter.NUMERIC_CELL;

				case Types.DOUBLE:
					return AbstractXLSExporter.DECIMAL_CELL;
				case Types.DECIMAL:
					return AbstractXLSExporter.DECIMAL_CELL;
				case Types.REAL:
					return AbstractXLSExporter.DECIMAL_CELL;
				case Types.NUMERIC:
					return AbstractXLSExporter.DECIMAL_CELL;
				case Types.FLOAT:
					return AbstractXLSExporter.DECIMAL_CELL;

				case Types.DATE:
					return AbstractXLSExporter.DATE_CELL;
				case Types.TIME:
					return AbstractXLSExporter.DATE_HOUR_CELL;
				case Types.TIMESTAMP:
					return AbstractXLSExporter.DATE_HOUR_CELL;

				case Types.BINARY:
					return AbstractXLSExporter.IMAGE_CELL;
				case Types.LONGVARBINARY:
					return AbstractXLSExporter.TEXT_CELL;
				case Types.VARBINARY:
					return AbstractXLSExporter.TEXT_CELL;
				case Types.ARRAY:
					return AbstractXLSExporter.TEXT_CELL;
				case Types.BLOB:
					return AbstractXLSExporter.TEXT_CELL;
				case Types.OTHER:
					return AbstractXLSExporter.TEXT_CELL;
			}
		}
		return AbstractXLSExporter.TEXT_CELL;
	}
}
