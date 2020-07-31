package com.opentach.server.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class ContractUtils {
	// TODO borrar esta clase

	private static final Logger logger = LoggerFactory.getLogger(ContractUtils.class);

	public static Object checkContratoFicticio(IOpentachServerLocator locator, Object cgContrato, int sesionId, Connection conn) {
		try {

			Pair<String, String> ficticioPair = new QueryJdbcTemplate<Pair<String, String>>() {
				@Override
				protected Pair<String, String> parseResponse(ResultSet rs) throws UException {
					try {
						if (rs.next()) {
							return new Pair<String, String>(rs.getString(1), rs.getString(2));
						}
						return null;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(conn, "SELECT FICTICIO,CIF FROM CDEMPRE_REQ WHERE NUMREQ = ?", cgContrato);

			if ((ficticioPair != null) && (ficticioPair.getFirst() != null)) {
				if (ficticioPair.getFirst().equals("S")) {
					ContractUtils.logger.error("Es ficticio {}", cgContrato, new Exception());
					TableEntity eCifEmpreReq = (TableEntity) locator.getEntityReferenceFromServer("ECifEmpreReq");
					Hashtable avfic = new Hashtable<String, Object>();
					avfic.put("CIF", ficticioPair.getSecond());
					int ses = sesionId;
					if (sesionId < 0) {
						ses = TableEntity.getEntityPrivilegedId(eCifEmpreReq);
					}
					EntityResult resCif = eCifEmpreReq.query(avfic, new Vector<Object>(), ses, conn);
					if (resCif.calculateRecordNumber() > 0) {
						return resCif.getRecordValues(0).get("CG_CONTRATO");
					}
				} else {
					return cgContrato;
				}
			}

		} catch (Exception e) {
			ContractUtils.logger.error(null, e);
		}
		return null;
	}

}
