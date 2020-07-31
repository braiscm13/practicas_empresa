package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.ContractUtils;

public class EInformeActivPeriodosCond extends EInformeActivCond {

	public EInformeActivPeriodosCond(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		String columnName = null;
		if (cv.containsKey("CG_CONTRATO")) {
			columnName = "CG_CONTRATO";
		} else if (cv.containsKey("NUMREQ")) {
			columnName = "NUMREQ";
		}
		if (columnName != null) {
			Object checkContratoFicticio = ContractUtils.checkContratoFicticio(this.getLocator(), cv.get(columnName),
					sesionId, con);
			if (checkContratoFicticio != null) {
				cv.put(columnName, checkContratoFicticio);
			}
		}
		EntityResult res = super.query(cv, v, sesionId, con);
		return res;
	}
}
