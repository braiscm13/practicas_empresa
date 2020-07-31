package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EConfInformeGestor extends FileTableEntity {

	public EConfInformeGestor(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId, Connection conn) throws Exception {
		EntityResult res = super.update(av, cv, sesionId, conn);
		if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
			Hashtable<String, Object> insertcv = new Hashtable<String, Object>();
			insertcv.putAll(av);
			insertcv.putAll(cv);
			res = super.insert(insertcv, sesionId, conn);
		}
		return res;
	}
}
