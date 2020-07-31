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

public class EIncidentesResumen extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EIncidentesResumen.class);

	public EIncidentesResumen(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(final Hashtable cv, Vector v, int sesionId) throws Exception {

		String sqlTmp = "SELECT " + "TIPO_DSCR_INC, " + "SUM (DURACION_SEGUNDOS) AS DURACION, " + "AVG(DURACION_SEGUNDOS) AS VMED, " + "MAX (DURACION_SEGUNDOS) AS VMAX, " + "(SUM(DURACION_SEGUNDOS)/TOTAL.SEGUNDOS_T)*100 AS PERC" + " FROM CDVINCIDENTES," + "( " + "SELECT " + "SUM (DURACION_SEGUNDOS) AS SEGUNDOS_T " + "FROM CDVINCIDENTES " + "WHERE  CDVINCIDENTES.NUMREQ = ? AND ";

		String sqlpart = "";
		if (cv.get("matricula") instanceof String) {
			sqlpart += " CDVINCIDENTES.MATRICULA = '" + (String) cv.get("matricula") + "' AND ";
		} else {
			SearchValue sv = (SearchValue) cv.get("matricula");
			sv.getStringCondition();
			Vector vValues = (Vector) sv.getValue();
			sqlpart += "( ";
			for (int i = 0; i < vValues.size(); i++) {
				sqlpart += " CDVINCIDENTES.MATRICULA= '" + (String) vValues.get(i) + "'";
				if (i != (vValues.size() - 1)) {
					sqlpart += " OR ";
				}
			}
			sqlpart += ") AND ";
		}
		sqlTmp += sqlpart;
		sqlTmp += "(CDVINCIDENTES.FECINI > ?) " + "AND CDVINCIDENTES.FECFIN < ? " + ")  TOTAL " + "WHERE CDVINCIDENTES.NUMREQ = ? AND ";
		sqlTmp += sqlpart;
		sqlTmp += " (CDVINCIDENTES.FECINI > ?) " + "AND CDVINCIDENTES.FECFIN < ? " + "GROUP BY TIPO_DSCR_INC,TOTAL.SEGUNDOS_T ";
		final String sql = sqlTmp;

		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection con) throws UException, SQLException {
				Date fInicio = new Date(((Timestamp) cv.get("f_inicio")).getTime());
				Date fFin = new Date(((Timestamp) cv.get("f_fin")).getTime());
				String numReq = (String) cv.get("numreq");

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
				}.execute(con, sql, numReq, fInicio, fFin, numReq, fInicio, fFin);
			}
		}.execute(this.getDatabaseConnectionManager(), true);
	}

}
