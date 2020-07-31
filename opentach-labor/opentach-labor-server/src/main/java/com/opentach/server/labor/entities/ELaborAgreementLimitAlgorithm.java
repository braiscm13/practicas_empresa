package com.opentach.server.labor.entities;

import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.labor.labor.agreement.LaborAgreementsService;
import com.opentach.server.util.db.OracleTableEntity;
import com.utilmize.server.UReferenceSeeker;

public class ELaborAgreementLimitAlgorithm extends OracleTableEntity {

	public ELaborAgreementLimitAlgorithm(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void readProperties() throws Exception {}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		this.checkPermissions(sessionID, TableEntity.QUERY_ACTION);
		return this.createResponse();
	}

	private EntityResult createResponse() {
		List<String> agreementAlgorithmImplementations = ((UReferenceSeeker) this.locator).getService(LaborAgreementsService.class).getAgreementLimitAlgorithmImplementations();
		Collections.sort(agreementAlgorithmImplementations);
		EntityResult er = new EntityResult();
		EntityResultTools.initEntityResult(er, Arrays.asList("NAME"));
		er.put("NAME", new Vector(agreementAlgorithmImplementations));
		return er;
	}

	@Override
	public EntityResult query(Hashtable keysValues, Vector attributes, int sessionID) throws Exception {
		this.checkPermissions(sessionID, TableEntity.QUERY_ACTION);
		return this.createResponse();
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
