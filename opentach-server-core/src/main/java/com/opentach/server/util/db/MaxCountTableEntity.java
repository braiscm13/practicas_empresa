package com.opentach.server.util.db;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.SQLStatement;
import com.ontimize.locator.EntityReferenceLocator;

public class MaxCountTableEntity extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(MaxCountTableEntity.class);
	private int					maxCount;

	public MaxCountTableEntity(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.maxCount = -1;
		if (this.properties != null) {
			String sMaxCount = this.properties.getProperty("MaxCount");
			try {
				this.maxCount = Integer.parseInt(sMaxCount);
			} catch (Exception e) {
				MaxCountTableEntity.logger.error(null, e);
			}
		}
	}


	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		Hashtable<String,Object> cvAux = (Hashtable<String,Object>) cv.clone();
		SQLStatement stmt = SQLStatementBuilder.createCountQuery(this.queryTable == null ? this.table : this.queryTable, cv, this.wildcardColumns,
				null);
		Vector<Object> values = stmt.getValues();
		EntityResult res = this.executePreparedStatement(stmt.getSQLStatement(), values, con, sesionId);
		if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			Vector<Object> v = (Vector<Object>) res.get(SQLStatementBuilder.COUNT_COLUMN_NAME);
			if (v != null) {
				Number nCount = (Number) v.firstElement();
				if ((nCount != null) && (nCount.intValue() > this.maxCount)) {
					throw new Exception("M_CONSULTA_EXCESIVA");
				}
			}
		}
		return super.query(cvAux, av, sesionId, con);
	}

}
