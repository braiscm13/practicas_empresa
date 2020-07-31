package com.opentach.server.entities;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.util.PropertyUtils;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeProxCalibrado extends FileTableEntity {

	public EInformeProxCalibrado(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties("com/opentach/server/entities/prop/EInformeProxCalibrado.properties"), null);
	}

}
