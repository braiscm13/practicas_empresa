package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeCAP extends FileTableEntity {

	public EInformeCAP(EntityReferenceLocator bref, DatabaseConnectionManager gcon, int port) throws Exception {
		super(bref, gcon, port);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		return super.query(cv, av, sesionId, con);
	}
}
