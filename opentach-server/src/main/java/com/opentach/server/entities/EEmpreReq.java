package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EEmpreReq extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EEmpreReq.class);

	public EEmpreReq(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EEmpreReq(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp)
			throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		return ((TransactionalEntity) this.getEntityReference("EContratoEmp")).insert(av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable clavesValoresA, int sesionId, Connection con)
			throws Exception {
		return ((TransactionalEntity) this.getEntityReference("EContratoEmp")).update(av, clavesValoresA, sesionId, con);
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		return ((TransactionalEntity) this.getEntityReference("EContratoEmp")).delete(keysValues, sessionId, con);
	}
}
