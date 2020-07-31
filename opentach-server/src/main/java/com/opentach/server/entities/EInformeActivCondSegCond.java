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
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeActivCondSegCond extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EInformeActivCondSegCond.class);

	public EInformeActivCondSegCond(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(final Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {

		Date sini;
		Date sfin;

		if (cv.get("FEC_COMIENZO") != null) {
			sini = (Date) cv.get("FEC_COMIENZO");
		} else {
			sini = (Date) ((Vector) ((SearchValue) cv.get("FECINI")).getValue()).get(0);
		}
		if (cv.get("FEC_FIN") != null) {
			sfin = (Date) cv.get("FEC_FIN");
		} else {
			sfin = (Date) ((Vector) ((SearchValue) cv.get("FECINI")).getValue()).get(1);
		}

		String sql = new Template("sql/EInformeActivCondSegCondQuery.sql").getTemplate();
		return new QueryJdbcTemplate<EntityResult>() {
			@Override
			protected EntityResult parseResponse(ResultSet rset) throws UException {
				try {
					cv.put("COND2", "");
					EntityResult res2Cond = new EntityResult();
					if (rset.next()) {
						String result = rset.getString(1);
						cv.put("COND2", result);
					}
					res2Cond.addRecord(cv);
					return res2Cond;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(con, sql, cv.get("MATRICULA"), sfin, sini);

	}

}
