package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.ScopeTableEntity;

public class ECertifActividades extends ScopeTableEntity {

	public ECertifActividades(EntityReferenceLocator locator, DatabaseConnectionManager cm, int puerto) throws Exception {
		super(locator, cm, puerto);

	}

	@Override
	public EntityResult insert(Hashtable av, int sessionId, Connection conn) throws Exception {
		av.put("F_ALTA", new Date());
		av.put("USUARIO_ALTA", this.getUser(sessionId));
		EntityResult er = super.insert(av, sessionId, conn);
		return er;
	}
}
