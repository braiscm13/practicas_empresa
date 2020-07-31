package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.util.PropertyUtils;
import com.opentach.server.activities.InfractionService;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeInfracNextRest extends FileTableEntity {

	public EInformeInfracNextRest(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties("com/opentach/server/entities/prop/EInformeInfrac.properties"),null);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {
		Date beginDate = (Date) cv.get(OpentachFieldNames.FILTERFECINI);
		Date endDate = (Date) cv.get(OpentachFieldNames.FILTERFECFIN);
		String cgContrato = (String) cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		String cif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
		Vector<Object> drivers = EInformeActivCond.getDrivers(cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD), false);
		drivers = ((drivers.size() == 0)) ? null : drivers;
		this.getService(InfractionService.class).analyzeForWS(conn, cgContrato, cif, drivers, beginDate, endDate, (EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER), sesionId);
		EntityResult res = query(new Hashtable(),new Vector(), sesionId,conn);
		return res;
		
	}

}
