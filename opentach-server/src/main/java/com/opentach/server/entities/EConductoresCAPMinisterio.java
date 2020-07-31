package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.OracleTableEntity;

public class EConductoresCAPMinisterio extends OracleTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EIncidentes.class);

	public EConductoresCAPMinisterio(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}


	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId, Connection con) throws Exception {
		attributesValues.put("IS_MINISTERIO", "S");
		return super.insert(attributesValues, sessionId, con);
	}

}
