package com.opentach.server.labor.entities;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.labor.labor.agreement.LaborAgreementsService;
import com.opentach.server.util.db.FileTableEntity;

public class ELaborAgreement extends FileTableEntity {

	public ELaborAgreement(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		return super.query(avOrig, av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId, Connection con) throws Exception {
		try {
			return super.update(av, cv, sesionId, con);
		} finally {
			this.getService(LaborAgreementsService.class).refresh();
		}
	}

	@Override
	public EntityResult delete(Hashtable cv, int sessionID, Connection conn) throws Exception {
		try {
			return super.update(EntityResultTools.keysvalues("AGR_F_BAJA", new Date()), cv, sessionID, conn);
		} finally {
			this.getService(LaborAgreementsService.class).refresh();
		}
	}

	/**
	 * Solo puede haber un contrato de alta por empresa
	 */

	@Override
	public EntityResult insert(Hashtable cv, int sesionId, Connection con) throws Exception {
		try {
			return super.insert(cv, sesionId, con);
		} finally {
			this.getService(LaborAgreementsService.class).refresh();
		}
	}

}
