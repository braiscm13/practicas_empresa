package com.opentach.server.entities;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class ECloudFileEmp extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(ECloudFileEmp.class);

	public ECloudFileEmp(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public ECloudFileEmp(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp)
			throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}


}
