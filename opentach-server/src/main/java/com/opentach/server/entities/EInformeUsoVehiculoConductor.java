package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeUsoVehiculoConductor extends FileTableEntity {

	public EInformeUsoVehiculoConductor(EntityReferenceLocator bref, DatabaseConnectionManager gcon, int port) throws Exception {
		super(bref, gcon, port);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		EntityResult res = null;
		cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, ContractUtils.checkContratoFicticio(this.getLocator(),
				cv.get(OpentachFieldNames.CG_CONTRATO_FIELD), sesionId, con));
		res = super.query(cv, av, sesionId, con);

		EntityResult resOrder = EntityResultTools.doSort(res, "IDCONDUCTOR", "FECINI");

		return resOrder;

	}
}
