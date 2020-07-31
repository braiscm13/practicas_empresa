package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.util.PropertyUtils;

public class EDfEmpDemo extends EDfEmp {


	public EDfEmpDemo(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties("com/opentach/server/entities/prop/EDfEmp.properties"), null);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		cv.put("IS_DEMO", "S");
		return super.query(cv, av, sesionId, con);
	}

	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId) throws Exception {
		attributesValues.put("IS_DEMO", "S");
		return super.insert(attributesValues, sessionId);
	}
}