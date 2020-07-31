package com.opentach.server.entities;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.util.PropertyUtils;

public class EConductoresEmpDemo extends EConductoresEmp {
	public EConductoresEmpDemo(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties("com/opentach/server/entities/prop/EConductoresEmp.properties"), null);
	}

}
