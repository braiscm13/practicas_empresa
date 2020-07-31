package com.opentach.server.entities;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class ECloudFile extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(ECloudFile.class);

	public ECloudFile(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public ECloudFile(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp)
			throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}


}
