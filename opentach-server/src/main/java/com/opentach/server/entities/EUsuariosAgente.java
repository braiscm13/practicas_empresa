package com.opentach.server.entities;

import java.net.URL;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.user.IUserData;
import com.opentach.server.util.db.OracleTableEntity;

public class EUsuariosAgente extends OracleTableEntity {


	public EUsuariosAgente(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void readProperties() throws Exception {
	}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		cv.put("NIVEL_CD", IUserData.NIVEL_AGENTE);
		return ((TransactionalEntity) this.getEntityReference("EUsuariosTodos")).query(cv, v, sessionID, conn);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID) throws Exception {
		cv.put("NIVEL_CD", IUserData.NIVEL_AGENTE);
		return this.getEntityReference("EUsuariosTodos").query(cv, v, sessionID);
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
