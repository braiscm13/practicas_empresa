package com.opentach.server.entities;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class ENews extends FileTableEntity {


	public ENews(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);

	}


}
