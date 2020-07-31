package com.opentach.server.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.OracleTableEntity;

public class ETachoRegions extends OracleTableEntity {

	private static final String	DEFAULT_NONE		= "NONE";

	private static final String	COLUMN_NAME			= "NAME";

	private static final String	COLUMN_ID			= "ID";

	private static final String	COLUMN_PARENT_ID	= "PARENT_ID";

	private static final Logger	logger	= LoggerFactory.getLogger(ETachoRegions.class);

	private EntityResult		cache;

	public ETachoRegions(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);

	}

	@Override
	protected void readProperties() throws Exception {
		this.loadCache();
	}

	private void loadCache() throws IOException {
		this.cache = new EntityResult();
		EntityResultTools.initEntityResult(this.cache,
				Arrays.asList(new String[] { ETachoRegions.COLUMN_ID, ETachoRegions.COLUMN_PARENT_ID, ETachoRegions.COLUMN_NAME }));
		try (InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("com/opentach/server/entities/prop/ETachoRegionsData.properties")) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				Hashtable<String, String> record = new Hashtable<String, String>();
				line = line.trim();
				if (!line.isEmpty()) {
					String[] split = line.split("\\=");
					if (split.length == 2) {
						record.put(ETachoRegions.COLUMN_PARENT_ID, ETachoRegions.DEFAULT_NONE);
						record.put(ETachoRegions.COLUMN_ID, split[0]);
						record.put(ETachoRegions.COLUMN_NAME, split[1]);
						this.cache.addRecord(record);
					} else if (split.length == 3) {
						record.put(ETachoRegions.COLUMN_PARENT_ID, split[0]);
						record.put(ETachoRegions.COLUMN_ID, split[1]);
						record.put(ETachoRegions.COLUMN_NAME, split[2]);
						this.cache.addRecord(record);
					} else {
						ETachoRegions.logger.warn("Line with more than 2 =");
					}
				}
			}
		}
	}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		return this.query(cv, v, sessionID);
	}

	@Override
	public EntityResult query(Hashtable keysValues, Vector attributes, int sessionId) throws Exception {
		if (keysValues.isEmpty()) {
			keysValues.put(ETachoRegions.COLUMN_PARENT_ID, ETachoRegions.DEFAULT_NONE);
		}
		return EntityResultTools.dofilter(this.cache, keysValues);
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

}
