package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EFallosResumen extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EFallosResumen.class);

	public EFallosResumen(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(final Hashtable cv, Vector v, int sesionId) throws Exception {
		String aux = "";
		if (cv.get("matricula") instanceof String) {
			aux = " MATRICULA = '" + (String) cv.get("matricula") + "' AND ";
		} else {
			Vector<Object> vMatricula = new Vector<Object>();
			if (cv.get("matricula") instanceof Vector) {
				vMatricula = (Vector<Object>) cv.get("matricula");
			} else if (cv.get("matricula") instanceof SearchValue) {
				vMatricula = (Vector<Object>) ((SearchValue) cv.get("matricula")).getValue();
			}
			aux = "(";
			for (int i = 0; i < vMatricula.size(); i++) {
				aux += "MATRICULA='" + vMatricula.get(i) + "'";
				if ((i + 1) != vMatricula.size()) {
					aux += " OR ";
				}
			}
			aux += " ) AND ";
		}

		final String sql = "SELECT " + "MATRICULA, " + "TPFALLO, " + "DSCR, " + " COUNT (TPFALLO) " + "FROM  CDVFALLOS " + "WHERE " + aux + " NUMREQ 			 = ? " + " AND ((FECINI > ? AND FECINI < ?) OR " + "(FECFIN > ? AND FECINI < ? ) OR " + " (FECFIN > ? AND FECINI < ? )) " + " GROUP BY MATRICULA, TPFALLO, DSCR";
		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection con) throws UException, SQLException {
				final Date fInicio = new Date(((Timestamp) cv.get("f_inicio")).getTime());
				final Date fFin = new Date(((Timestamp) cv.get("f_fin")).getTime());
				return new QueryJdbcTemplate<EntityResult>() {

					@Override
					protected EntityResult parseResponse(ResultSet rset) throws UException {
						try {
							EntityResult res = new EntityResult();
							FileTableEntity.resultSetToEntityResult(rset, res);
							return res;
						} catch (Exception ex) {
							throw new UException(ex);
						}
					}
				}.execute(con, sql, cv.get("numreq"), fFin, fInicio, fInicio, fFin, fFin, fInicio);
			}
		}.execute(this.manager, true);
	}

}
