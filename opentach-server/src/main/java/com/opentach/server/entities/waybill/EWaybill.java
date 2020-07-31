package com.opentach.server.entities.waybill;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EWaybill extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EWaybill.class);

	public EWaybill(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EWaybill(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp)
			throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		return super.query(avOrig, av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		return super.update(atributosValoresA, clavesValoresA, sesionId, con);
	}

}
