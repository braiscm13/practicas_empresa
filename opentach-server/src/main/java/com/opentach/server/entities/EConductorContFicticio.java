package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.util.PropertyUtils;
import com.opentach.server.util.db.FileTableEntity;

public class EConductorContFicticio extends FileTableEntity {

	public EConductorContFicticio(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties("com/opentach/server/entities/prop/EConductorContFicticio.properties"), null);
	}

	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		return super.query(avOrig, av, sesionId, con);
	}

}
