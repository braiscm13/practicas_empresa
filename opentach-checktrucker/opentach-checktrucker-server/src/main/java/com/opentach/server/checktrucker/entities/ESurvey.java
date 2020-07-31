package com.opentach.server.checktrucker.entities;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class ESurvey extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(ESurvey.class);

	public ESurvey(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public ESurvey(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}
}
