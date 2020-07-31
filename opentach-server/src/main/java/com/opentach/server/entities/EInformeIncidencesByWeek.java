package com.opentach.server.entities;

import java.net.URL;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.BasicExpressionTools.BetweenDateFilter;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.OracleTableEntity;

public class EInformeIncidencesByWeek extends OracleTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EInformeIncidencesByWeek.class);

	public EInformeIncidencesByWeek(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void readProperties() throws Exception {}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sessionID, Connection conn) throws Exception {
		Vector<Object> fields = new Vector<>(cv.keySet());
		fields.remove("FILTERFECINI");
		fields.remove("FILTERFECFIN");
		fields.add(new BetweenDateFilter("FILTERFECINI", "FILTERFECFIN", "TSK_CREATION_DATE"));
		try {
			// return StatementBuilderHelper.doQuery((UReferenceSeeker) this.getLocator(), "sql/admin/AdminIncidencesByWeek",
			// BasicExpressionTools.completeExpresionFromKeys(null, cv, fields), null);
			return null;// Non compilant code
		} catch (Exception error) {
			EInformeIncidencesByWeek.logger.error(null, error);
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT, error.getMessage());
		}
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}
}
