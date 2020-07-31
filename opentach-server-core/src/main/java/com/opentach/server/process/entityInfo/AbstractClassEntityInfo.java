package com.opentach.server.process.entityInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClassEntityInfo<T> {

	private static final Logger			logger	= LoggerFactory.getLogger(AbstractClassEntityInfo.class);

	private final Class<?>				clazz;
	private final String				autonumericCol;
	private final String				userCol;
	private final String				timeStampCol;
	private final String				tabla;
	private final Map<String, String>	columnsfields;

	private String						insertDdlPrefix;
	// Columnas en el orden en el que se monta el sql
	private final List<String>			columns;
	private final AtomicLong			count;

	public AbstractClassEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super();
		this.clazz = clazz;
		this.autonumericCol = autonumericCol;
		this.userCol = userCol;
		this.timeStampCol = timeStampCol;
		this.tabla = tabla;
		this.columnsfields = columnsfields;

		this.count = new AtomicLong(0);
		this.columns = new ArrayList<String>();
		this.compile();
	}

	protected void compile() {
		this.columns.clear();
		StringBuilder pattern = new StringBuilder();
		StringBuilder interogpattern = new StringBuilder();

		for (Iterator<String> iter = this.columnsfields.keySet().iterator(); iter.hasNext();) {
			String colname = iter.next();
			this.columns.add(colname);
			pattern.append(colname + ((iter.hasNext()) ? "," : ""));
			interogpattern.append((iter.hasNext()) ? "?," : "?");
		}
		// Columna de autonumerico. Se autoincrementa para cada valor de
		// la lista de objetos.
		if (this.autonumericCol != null) {
			this.columns.add(this.autonumericCol);
			pattern.append("," + this.autonumericCol);
			interogpattern.append(",?");
		}
		// Columna de usuario
		if (this.userCol != null) {
			this.columns.add(this.userCol);
			pattern.append("," + this.userCol);
			interogpattern.append(",?");
		}
		// Columna de fecha de inserción del registro
		if (this.timeStampCol != null) {
			this.columns.add(this.timeStampCol);
			pattern.append("," + this.timeStampCol);
			interogpattern.append(",?");
		}

		StringBuilder psstr = new StringBuilder();
		psstr.append("INSERT  INTO ");
		psstr.append(this.tabla);
		psstr.append(" (").append(pattern.toString()).append(") VALUES (");
		psstr.append(interogpattern.toString()).append(")");
		this.insertDdlPrefix = psstr.toString();

	}

	public String getTabla() {
		return this.tabla;
	}

	public String getSQLPattern(String errorLogTableName) {
		StringBuilder psstr = new StringBuilder(this.insertDdlPrefix);
		psstr.append(" LOG ERRORS INTO ").append(errorLogTableName);
		psstr.append(" REJECT LIMIT UNLIMITED");
		// /*+APPEND*/ HINT ERROR IN ORACLE 11
		return psstr.toString();
		// this.insertDdl = psstr.toString();
		// return this.insertDdl;
	}

	public Map<String, Object> getCV(Object obj) throws IllegalArgumentException {
		Class<?> c = obj.getClass();
		if (!c.equals(this.clazz)) {
			throw new IllegalArgumentException("Instance of illegal class must be a " + this.clazz + " instance");
		}
		Map<String, Object> cv = new HashMap<>();
		String[] colums = this.columnsfields.keySet().toArray(new String[0]);
		String field, col;
		for (int i = 0; i < colums.length; i++) {
			col = colums[i];
			field = this.columnsfields.get(col);
			try {
				Object value = c.getField(field).get(obj);
				if (value != null) {
					cv.put(col, value);
				}
			} catch (Exception ex) {
				AbstractClassEntityInfo.logger.error(null, ex);
			}
		}
		return cv;
	}

	public String getAutonumericCol() {
		return this.autonumericCol;
	}

	public String getUserCol() {
		return this.userCol;
	}

	public String getTimeStampCol() {
		return this.timeStampCol;
	}

	public Long getCountAndIncrement() {
		return this.count.getAndIncrement();
	}

	public List<String> getColumns() {
		return this.columns;
	}

	public String getObjectClassName() {
		return this.clazz.getName();
	}

	public abstract boolean passFilter(T bean);
}