package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EUFecha extends TableEntity implements OpentachFieldNames {

	private static final Logger logger = LoggerFactory.getLogger(EUFecha.class);

	public EUFecha(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable av, Vector v, int sessionId, Connection conn) throws Exception {
		String sql = "SELECT MAX(FEC_FIN) FROM CDACTIVIDADES WHERE IDCONDUCTOR = ? AND NUMREQ = ? ";
		return new QueryJdbcTemplate<EntityResult>() {

			@Override
			protected EntityResult parseResponse(ResultSet rset) throws UException {
				try {
					EntityResult res = new EntityResult();
					Date date = null;
					if (rset.next()) {
						date = rset.getDate(1);
					}
					if (date != null) {
						Hashtable<String, Object> r = new Hashtable<String, Object>(1);
						r.put(OpentachFieldNames.FECFIN_FIELD, date);
						res.addRecord(r);
						res = new EntityResult(EUFecha.this.replaceColumnByAlias(res));
					}
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql, av.get(OpentachFieldNames.IDCONDUCTOR_FIELD), av.get(OpentachFieldNames.NUMREQ_FIELD));
	}
}
