package com.opentach.server.util.db;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;

public class ScopeTableEntity extends OracleTableEntity {

	private boolean	bHasDeleg;

	public ScopeTableEntity(EntityReferenceLocator locator, DatabaseConnectionManager cm, int puerto) throws Exception {
		super(locator, cm, puerto);
	}

	public ScopeTableEntity(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop,
			Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.bHasDeleg = this.columnNames.contains(OpentachFieldNames.IDDELEGACION_FIELD);
	}

	@Override
	public EntityResult query(Hashtable cvOrig, Vector av, int sesionId, Connection con) throws Exception {
		EntityResult res = null;
		res = super.query(cvOrig, av, sesionId, con);
		return res;
	}
}
