package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.OracleTableEntity;

public class EConductorCont extends OracleTableEntity {

	public EConductorCont(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		return super.query(cv, v, sessionID, conn);
	}

	@Override
	public EntityResult update(Hashtable attributesValues, Hashtable keysValues, int sessionId, Connection con) throws Exception {
		String dni = (String) attributesValues.get(OpentachFieldNames.DNI_FIELD);
		if (dni != null) {
			attributesValues.put(OpentachFieldNames.DNI_FIELD, dni.trim());
		}
		EntityResult res = super.update(attributesValues, keysValues, sessionId, con);
		attributesValues.putAll(keysValues);
		return res;
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		String dni = (String) av.get(OpentachFieldNames.DNI_FIELD);
		if ((dni == null) && !av.containsKey("NEW_SBO")) {
			av.put(OpentachFieldNames.DNI_FIELD, this.computeDriverDni(av, sesionId, con));
		}else if (dni==null){
			av.put(OpentachFieldNames.DNI_FIELD, ((String)av.get("IDCONDUCTOR")).substring(1, 10));
		}
		EntityResult resInsert;
		try {
			resInsert = super.insert(av, sesionId, con);
		} catch (Exception ex) {
			if (ex.getMessage().contains("CDCONDUCTOR_C_PK) violada")) {
				throw new Exception("IDCONDUCTOR_YA_EXISTE_DNI");
			}
			Hashtable<String, Object> avv = new Hashtable<String, Object>(1);
			avv.put(OpentachFieldNames.FECBAJA_FIELD, new NullValue(Types.DATE));
			resInsert = super.update(avv, av, sesionId, con);
		}
		return resInsert;
	}

	private Object computeDriverDni(Hashtable av, int sesionId, Connection con) throws Exception {
		TableEntity entcond = (TableEntity) this.getEntityReference("EConductoresEmp");
		EntityResult resCond = entcond.query(av, new Vector<>(), sesionId, con);
		if (resCond.calculateRecordNumber() > 0) {
			return resCond.getRecordValues(0).get(OpentachFieldNames.DNI_FIELD);
		}
		throw new Exception("IDCONDUCTOR_NO_EXISTE_EMPRESAS");
	}

	@Override
	public EntityResult delete(Hashtable cv, int sessionId, Connection con) throws Exception {
		Hashtable<String, Object> av = new Hashtable<String, Object>(1);
		av.put(OpentachFieldNames.FECBAJA_FIELD, this.getCDateTime(sessionId));
		EntityResult entity = super.update(av, cv, sessionId, con);
		return entity;
	}

}
