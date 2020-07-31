package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.MaxCountTableEntity;

public class EFicherosSubidos extends MaxCountTableEntity {

	public EFicherosSubidos(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		if (cv.containsKey(OpentachFieldNames.CG_CONTRATO_FIELD)){
			cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, ContractUtils.checkContratoFicticio(this.getLocator(),
					cv.get(OpentachFieldNames.CG_CONTRATO_FIELD), sesionId, con));
		}
		EntityResult res = super.query(cv, av, sesionId, con);
		return res;
	}
}
