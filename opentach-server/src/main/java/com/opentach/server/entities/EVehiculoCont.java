package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Types;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.companies.VehicleService;
import com.opentach.server.util.db.FileTableEntity;

public class EVehiculoCont extends FileTableEntity {

	public EVehiculoCont(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		if (!this.getService(VehicleService.class).hasNewVehicleSpace((String) av.get("CIF"))) {
			throw new Exception("NUM_MAX_VEHICLES_SUPERATED");
		}

		EntityResult resInsert;
		String matricula = (String) av.get(OpentachFieldNames.MATRICULA_FIELD);
		if (matricula != null) {
			av.put(OpentachFieldNames.MATRICULA_FIELD, matricula.trim());
		}
		try {
			resInsert = super.insert(av, sesionId, con);
		} catch (Exception ex) {
			Hashtable<String, Object> avv = new Hashtable<String, Object>();
			avv.put(OpentachFieldNames.FECBAJA_FIELD, new NullValue(Types.DATE));
			resInsert = this.update(avv, av, sesionId, con);
		}
		return resInsert;
	}

	@Override
	public EntityResult update(Hashtable attributesValues, Hashtable keysValues, int sessionId, Connection con) throws Exception {
		String matricula = (String) attributesValues.get(OpentachFieldNames.MATRICULA_FIELD);
		if (matricula != null) {
			attributesValues.put(OpentachFieldNames.MATRICULA_FIELD, matricula.trim());
		}
		return super.update(attributesValues, keysValues, sessionId, con);
	}

	@Override
	public EntityResult delete(Hashtable cv, int sesionId, Connection con) throws Exception {
		Hashtable<String, Object> av = new Hashtable<String, Object>(1);
		av.put(OpentachFieldNames.FECBAJA_FIELD, this.getCDateTime(sesionId));
		return super.update(av, cv, sesionId, con);
	}

}
