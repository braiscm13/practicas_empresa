package com.opentach.server.util.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.handler.SQLStatementHandler;
import com.ontimize.locator.EntityReferenceLocator;
import com.utilmize.tools.exception.UException;

public class FileTableEntity extends ScopeTableEntity {

	private static final Logger	logger						= LoggerFactory.getLogger(FileTableEntity.class);

	private static final String	COLUMNS_AUTO_FILL_INSERT	= "ColumnsAutoFillInsert";
	private static final String	COLUMS_AUTO_INC_MAX_VALUE	= "ColumsAutoIncMaxValue";
	private static final String	COLUMNS_AUTO_FILL_UPDATE	= "ColumnsAutoFillUpdate";
	private static final String	COLUMNS_AUTO_FILL_QUERY		= "ColumnsAutoFillQuery";

	private static final String	SYSTEMUSR					= "SYSTEM";

	// Columnas que se autocompletan ( si no vienen entre los datos) en el
	// momento de la inserción.
	public Hashtable<String, Object>			autofillcolsinsert;
	// Columnas que se autocompletan ( si no vienen entre los datos) en el
	// momento de la consulta.
	public Hashtable<String, Object>			autofillcolsquery;
	// Columnas que se autocompletan ( si no vienen entre los datos) en el
	// momento de la actualizacion.
	public Hashtable<String, Object>			autofillcolsupdate;

	public Hashtable<String, Object>			autoincmaxval;

	public FileTableEntity(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public FileTableEntity(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop,
			Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}


	/**
	 * Propiedades añadidas para esta entidad:
	 * <ul>
	 * <li>ColumnsAutoFillInsert= Lista de columnas auto rellenables por la
	 * entidad cuando se inserta un registro.
	 * <li>ColumnsAutoFillUpdate= Lista de columnas auto rellenables por la
	 * entidad cuando se actualiza un registro.
	 * <li>ColumsAutoIncMaxValue= Lista de columnas para las que se
	 * autoincrementará el valor maximo del registro coincidente con los valores
	 * de la lista de columnas relacionadas.
	 * </ul>
	 * En los dos primerso casos el formato de definición de la propiedad para
	 * cada columna debe ser: <br>
	 * [columna]:[metodo que retornará el valor a colocar en la entidad]<br>
	 * Las columnas se separaran por punto y coma (
	 * colum1:method1;colum2:method2;...)<br>
	 * Los metodos deberan ser metodos de la entidad tabla con un entero como
	 * parametro. Que corresponderá con el sesionId en el momento de le
	 * invocación. Para el parametro ColumsAutoMaxValue, el formate sera: <br>
	 * [colAutoIncMaxVal]:[col_1_relacionada],[col_2_relacionada];[
	 * colAutonumerico2]:[col_1_relacionada2],...
	 *
	 * @throws Exception
	 */
	@Override
	protected void initialize() {
		super.initialize();
		if (this.properties != null) {
			String str = null;
			StringTokenizer stk, stk2;
			String item;
			if (this.autofillcolsinsert == null) {
				this.autofillcolsinsert = new Hashtable<String, Object>();
			}
			if (this.autofillcolsupdate == null) {
				this.autofillcolsupdate = new Hashtable<String, Object>();
			}
			if (this.autofillcolsquery == null) {
				this.autofillcolsquery = new Hashtable<String, Object>();
			}
			if (this.autoincmaxval == null) {
				this.autoincmaxval = new Hashtable<String, Object>();
			}
			String[] proplist = new String[] { FileTableEntity.COLUMNS_AUTO_FILL_INSERT, FileTableEntity.COLUMNS_AUTO_FILL_UPDATE,
					FileTableEntity.COLUMNS_AUTO_FILL_QUERY };
			Hashtable[] hashlist = new Hashtable[] { this.autofillcolsinsert, this.autofillcolsupdate, this.autofillcolsquery };

			for (int i = 0; i < proplist.length; i++) {
				str = this.properties.getProperty(proplist[i]);
				if (str != null) {
					stk = new StringTokenizer(str, ";");
					while (stk.hasMoreTokens()) {
						item = stk.nextToken();
						hashlist[i].put(item.substring(0, item.indexOf(":")), item.substring(item.indexOf(":") + 1, item.length()));
					}
				}
			}
			str = this.properties.getProperty(FileTableEntity.COLUMS_AUTO_INC_MAX_VALUE);
			if (str != null) {
				Vector<Object> relcols;
				stk = new StringTokenizer(str, ";");
				while (stk.hasMoreTokens()) {
					relcols = new Vector<Object>();
					item = stk.nextToken();
					if (item.indexOf(":") != -1) {
						stk2 = new StringTokenizer(item.substring(item.indexOf(":") + 1, item.length()), ",");
						item = item.substring(0, item.indexOf(":"));
						while (stk2.hasMoreElements()) {
							relcols.add(stk2.nextToken());
						}
					}
					this.autoincmaxval.put(item, relcols);
				}
			}
		}
	}

	public void putAutonumericCols2Hash(Hashtable av, Connection con) throws Exception {
		if (!this.autoincmaxval.isEmpty()) {
			Enumeration<String> en = this.autoincmaxval.keys();
			while (en.hasMoreElements()) {
				String autonumcol = en.nextElement();
				Object max;
				if ((max = this.getSiguienteCodigoTexto(autonumcol, av, con)) != null) {
					if (!av.containsKey(autonumcol)) {
						av.put(autonumcol, max);
					}
				}
			}
		}
	}

	/**
	 * Se completaran las columnas de ColumnsAutoFillInsert con los valores
	 * retornados por sus metodos asociados. Si en el cv estas columnas vienen
	 * ya con valores asignados se respetaran.
	 *
	 * @param av
	 *            Hashtable
	 * @param sesionId
	 *            int
	 * @param con
	 *            Connection
	 * @throws Exception
	 * @return ResultEntidad
	 */
	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		this.autoFillCols2Hash(av, this.autofillcolsinsert, sesionId);
		this.putAutonumericCols2Hash(av, con);
		return super.insert(av, sesionId, con);
	}

	/**
	 * Se actualizan ademas las columnas de ColumnsAutoFillUpdate con los
	 * valores retornados por sus metodos asociados. Si en el cv estas columnas
	 * vienen ya con valores asignados se respetaran.
	 *
	 * @param atributosValoresA
	 *            Hashtable
	 * @param sesionId
	 *            int
	 * @param con
	 *            Connection
	 * @throws Exception
	 * @return ResultEntidad
	 */
	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		this.autoFillCols2Hash(atributosValoresA, this.autofillcolsupdate, sesionId);
		return super.update(atributosValoresA, clavesValoresA, sesionId, con);
	}

	@Override
	public String getUser(int sesionId) {
		if (sesionId == this.privilegedId) {
			return FileTableEntity.SYSTEMUSR;
		}
		return super.getUser(sesionId);
	}

	/**
	 * Autocompleta los valores no recibidos con el resultado de los
	 * procedimientos configurados en el properties.
	 * <ul>
	 * <li>ColumnsAutoFillInsert=COLUM_TAG:Procedure; (...)
	 * <li>ColumnsAutoFillUpdate=COLUM_TAG:Procedure; (...)
	 * </ul>
	 *
	 * @param cv
	 * @param autofillcols
	 * @param sesionId
	 */
	public void autoFillCols2Hash(Hashtable cv, Hashtable autofillcols, int sesionId) {
		for (Enumeration<Object> en = autofillcols.keys(); en.hasMoreElements();) {
			String col = (String) en.nextElement();
			try {
				if (!cv.containsKey(col)) { // Si no existe un dato para ese
					// columna lo busco.
					String methodname = (String) autofillcols.get(col);
					Method method = this.getClass().getMethod(methodname, new Class[] { int.class });
					try {
						Object rtn = method.invoke(this, new Object[] { Integer.valueOf(sesionId) });
						if (rtn != null) {
							cv.put(col, rtn);
						}
					} catch (Exception ex1) {
						FileTableEntity.logger.error(null, ex1);
					}
				}
			} catch (NoSuchMethodException ex) {
				FileTableEntity.logger.error(this.autofillcolsinsert.get(col) + " method NOT FOUND");
			}
		}
	}

	/**
	 * Busca el maximo valor de la columna cuyos registros
	 *
	 * @param cgOrga
	 *            Vector
	 * @param con
	 *            Connection
	 * @throws Exception
	 * @return String
	 */
	public Object getSiguienteCodigoTexto(String colAutonumeric, Hashtable colRelacionadasValues, Connection con) throws Exception {
		String cadena = null;
		Object rval = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			if (this.autoincmaxval.containsKey(colAutonumeric)) {
				String SQLrelcols = " ";
				String andstr = " ";
				Vector<Object> relcols = (Vector<Object>) this.autoincmaxval.get(colAutonumeric); // Columnas
				// relacionadas con el autonumerico.
				if (this.table.equals("CDEMPRE_REQ")){
					SQLrelcols = " WHERE NUMREQ NOT LIKE '%FIC_%' AND ";
				}
				if (!relcols.isEmpty()) {
					if (!this.table.equals("CDEMPRE_REQ")){
						SQLrelcols = " WHERE ";
					}
					Enumeration<Object> en = relcols.elements();
					while (en.hasMoreElements()) {
						String item = (String) en.nextElement();
						if (colRelacionadasValues.containsKey(item)) {
							SQLrelcols += andstr + item + " = '" + colRelacionadasValues.get(item) + "'";
							andstr = " AND ";
						}
					}
				}
				if (SQLrelcols.endsWith("AND ")){
					SQLrelcols = SQLrelcols.substring(0,SQLrelcols.length() -4);
				}
				st = con.createStatement();

				String sql = "SELECT MAX( TO_NUMBER("  + colAutonumeric + ") ) AS MAXIMO FROM " + this.table + SQLrelcols; // where
				// col1=valor1 AND col2=valor2 ...
				rs = st.executeQuery(sql);

				if (rs.next()) {
					cadena = rs.getString("MAXIMO");
				}
				if (cadena == null) {
					cadena = "000";

				}
				// El valor de retorno por defecto es un Integer.
				rval = Integer.valueOf(Integer.valueOf(cadena) + 1);

				ResultSetMetaData resmdat = rs.getMetaData();

				if (resmdat.getColumnClassName(1).endsWith("String")) {
					int size = resmdat.getColumnDisplaySize(1);
					cadena = "";
					for (int i = 0; i < size; i++) {
						cadena += "0";
					}
					cadena += rval.toString();
					cadena = cadena.substring(rval.toString().length(), cadena.length());
					rval = cadena;// Si la columna es de tipo texto tengo qeu
					// rellenar con ceros a la iz y retornar un
					// String.
				}
			}
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
			}
		}
		return rval;

	}

	/**
	 * Cambio cambios los alias de los BasicField por el nombre real de la
	 * columna.
	 */
	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {


		Hashtable<String, Object> cv = (Hashtable<String, Object>) avOrig.clone();

		BasicExpression bex = (BasicExpression) cv.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
		this.substituyeAliasPorColumnas(bex);

		EntityResult res =  super.query(cv, av, sesionId, con);
		return res;
	}

	protected void substituyeAliasPorColumnas(BasicExpression bex) {
		if (bex != null) {
			if (bex.getLeftOperand() instanceof BasicField) {
				BasicField bf = (BasicField) bex.getLeftOperand();
				String column = this.alias.getProperty(bf.toString());
				if (column != null) {
					bex.setLeftOperand(new BasicField(column));
				}
			} else if (bex.getLeftOperand() instanceof BasicExpression) {
				this.substituyeAliasPorColumnas((BasicExpression) bex.getLeftOperand());
			}
			if (bex.getRightOperand() instanceof BasicField) {
				BasicField bf = (BasicField) bex.getRightOperand();
				String column = this.alias.getProperty(bf.toString());
				if (column != null) {
					bex.setRightOperand(new BasicField(column));
				}
			} else if (bex.getRightOperand() instanceof BasicExpression) {
				this.substituyeAliasPorColumnas((BasicExpression) bex.getRightOperand());
			}
		}
	}

	public static void resultSetToEntityResult(ResultSet resultSet, EntityResult entityResult) throws UException {
		SQLStatementHandler currentStatementHandler = SQLStatementBuilder.getSQLStatementHandler(SQLStatementBuilder.DEFAULT_HANDLER);
		try {
			currentStatementHandler.resultSetToEntityResult(resultSet, entityResult, null);
		} catch (Exception err) {
			throw new UException(err.getMessage(), err);
		}
	}


}
