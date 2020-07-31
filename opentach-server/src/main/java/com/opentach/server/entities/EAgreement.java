package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.security.GeneralSecurityException;
import com.ontimize.security.NotInPeriodException;
import com.ontimize.security.SessionNotFoundException;
import com.opentach.server.util.db.FileTableEntity;

public class EAgreement extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EAgreement.class);

	public EAgreement(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EAgreement(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable kv, Vector av, int sesionId, Connection con) throws Exception {
		Object value = kv.get("AGR_NAME");
		if (StringTools.isEmpty((String) value)) {
			kv.put("AGR_NAME", "DEFAULT");
		}
		return super.query(kv, av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void checkPermissions(int sessionId, String action) throws NotInPeriodException, GeneralSecurityException, SessionNotFoundException {
		return;
	}

}
