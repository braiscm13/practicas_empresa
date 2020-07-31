package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EPerfil extends FileTableEntity {

	public EPerfil(EntityReferenceLocator bRefs, DatabaseConnectionManager gc, int puerto) throws Exception {
		super(bRefs, gc, puerto);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		if ((sesionId > 0) && !cv.containsKey("NIVEL_CD")) {
			// Los ususarios solo podran ver los perfiles de su nivel y
			// superiores.
			String sql = "SELECT NIVEL_CD FROM CDUSU WHERE USUARIO = ?";
			String nivelCd = new QueryJdbcTemplate<String>() {
				@Override
				protected String parseResponse(ResultSet rs) throws UException {
					try {
						if (rs.next()) {
							return rs.getString(1);
						}
						return null;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(con, sql, this.getUser(sesionId));
			if (nivelCd != null) {
				SearchValue vb = new SearchValue(SearchValue.MORE_EQUAL, nivelCd);
				cv.put("NIVEL_CD", vb);
			}
		}
		return super.query(cv, v, sesionId, con);
	}

}
