package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.util.PropertyUtils;

public class EContratoEmpDemo extends EContratoEmp {

	public EContratoEmpDemo(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties("com/opentach/server/entities/prop/EContratoEmp.properties"), null);
	}

	@Override
	public EntityResult insert(Hashtable cv, int sesionId, Connection con) throws Exception {
		Object begin = cv.get("FECINI");
		Object end = cv.get("FECFIN");
		CheckingTools.failIf((begin == null) || (end == null), "M_STABLISH_DURATION");

		return super.insert(cv, sesionId, con);
	}
}
