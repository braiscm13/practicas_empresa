package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.MaxCountTableEntity;

public class EFicherosPendientes extends MaxCountTableEntity {

	public EFicherosPendientes(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		Object oIDOrigen = cv.get(OpentachFieldNames.IDORIGEN_FIELD);
		if ((oIDOrigen != null) && (oIDOrigen instanceof String)) {
			String sIDOrigen = (String) oIDOrigen;
			sIDOrigen = "*" + sIDOrigen + "*";
			cv.put(OpentachFieldNames.IDORIGEN_FIELD, sIDOrigen);
		}
		return super.query(cv, v, sesionId, con);
	}

}
