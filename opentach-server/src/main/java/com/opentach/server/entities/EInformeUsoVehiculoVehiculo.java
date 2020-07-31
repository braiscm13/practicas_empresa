package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeUsoVehiculoVehiculo extends FileTableEntity {

	public EInformeUsoVehiculoVehiculo(EntityReferenceLocator bref, DatabaseConnectionManager gcon, int port) throws Exception {
		super(bref, gcon, port);
	}

	@Override
	public EntityResult query(Hashtable av, Vector cv, int sessionID, Connection conn) throws Exception {
		EntityResult res = super.query(av, cv, sessionID, conn);
		return res;
	}
}
