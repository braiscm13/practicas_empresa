package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.report.ReportHelperDriverRestAndDrivings;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeConduDescCond extends FileTableEntity {

	public EInformeConduDescCond(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}


	@Override
	public EntityResult query(Hashtable keysValues, Vector av, int sesionId, Connection con) throws Exception {
		Object conductor = keysValues.get("IDCONDUCTOR");
		keysValues.remove("IDCONDUCTOR");
		keysValues.put("numreq",keysValues.get("CG_CONTRATO"));
		EntityResult resQuery = new EntityResult();
		if (conductor instanceof String) {
			new ReportHelperDriverRestAndDrivings().help(keysValues, con, sesionId, this.getLocator());
			
		}else if (conductor instanceof SearchValue) {
			Vector vconductores = (Vector)((SearchValue) conductor).getValue();
			for (int i = 0 ; i<vconductores.size();i++) {
				keysValues.put("idconductor", vconductores.get(i));
				new ReportHelperDriverRestAndDrivings().help(keysValues, con, sesionId, this.getLocator());
				resQuery.addRecord(keysValues);
			}
				
		}
		
		return resQuery;
	}
}
