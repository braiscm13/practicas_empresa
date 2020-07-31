package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;

public class EInformeCompaniesByAgent extends FileTableEntity {

	public EInformeCompaniesByAgent(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void readProperties() throws Exception {
		// Do nothing
	}

	@Override
	public EntityResult query(Hashtable keysValues, Vector av, int sesionId, Connection con) throws Exception {
		Object agent = keysValues.get("AGENTE");
		String sql = new Template("sql/EInformeCompaniesByAgent.sql").getTemplate();
		sql = sql.replaceAll("#WHERE#", agent == null ? "" : " AND cdusu.usuario = ?");
		if (agent == null) {
			return new QueryJdbcToEntityResultTemplate().execute(con, sql);
		} else {
			return new QueryJdbcToEntityResultTemplate().execute(con, sql, agent);
		}
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		throw new Exception("E_OPERATION_NOT_ALLOWED");
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("E_OPERATION_NOT_ALLOWED");
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		throw new Exception("E_OPERATION_NOT_ALLOWED");
	}
}
