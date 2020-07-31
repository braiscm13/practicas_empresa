package com.opentach.server.labor.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class ELaborReportWarehouse extends FileTableEntity {

	public ELaborReportWarehouse(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		return super.query(avOrig, av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId, Connection con) throws Exception {
		return super.update(av, cv, sesionId, con);
	}

	@Override
	public EntityResult delete(Hashtable cv, int sessionID, Connection conn) throws Exception {
		return super.delete(cv, sessionID, conn);
	}

	@Override
	public EntityResult insert(Hashtable cv, int sesionId, Connection con) throws Exception {
		return super.insert(cv, sesionId, con);
	}

}
