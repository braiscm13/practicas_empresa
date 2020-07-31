package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.MaxCountTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EFicherosRegistro extends MaxCountTableEntity implements ITGDFileConstants {

	private static final Logger	logger	= LoggerFactory.getLogger(EFicherosRegistro.class);

	public EFicherosRegistro(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public synchronized EntityResult insert(Hashtable atributosValoresA, int sesionId, Connection con) throws Exception {
		Number idFile = (Number) atributosValoresA.get(OpentachFieldNames.IDFILE_FIELD);
		if (idFile != null) {
			// Para sincronizar la creacion de num con el postvolcado
			String sql = "SELECT IDFICHERO FROM CDFICHEROS_REGISTRO WHERE IDFICHERO = ? FOR UPDATE";
			new QueryJdbcTemplate<Void>() {
				@Override
				protected Void parseResponse(ResultSet rs) throws UException {
					return null;
				}
			}.execute(con, sql, idFile.intValue());
		}
		return super.insert(atributosValoresA, sesionId, con);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		String nomb = (String) cv.get(OpentachFieldNames.FILENAME_FIELD);
		String nombp = (String) cv.get(OpentachFieldNames.FILENAME_PROCESSED_FIELD);
		String origen = (String) cv.get(OpentachFieldNames.IDORIGEN_FIELD);
		if (nomb != null) {
			cv.put(OpentachFieldNames.FILENAME_FIELD, nomb + "*");
		}
		if (nombp != null) {
			cv.put(OpentachFieldNames.FILENAME_PROCESSED_FIELD, nombp + "*");
		}
		if (origen != null) {
			cv.put(OpentachFieldNames.IDORIGEN_FIELD, origen + "*");
		}
		EntityResult res = super.query(cv, v, sesionId, con);
		return res;
	}


}
