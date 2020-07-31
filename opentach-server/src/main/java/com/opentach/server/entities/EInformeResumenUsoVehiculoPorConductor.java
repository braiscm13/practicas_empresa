package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeResumenUsoVehiculoPorConductor extends FileTableEntity {

	//	private static final Logger	logger	= LoggerFactory.getLogger(EInformeResumenUsoVehiculoPorConductor.class);

	public EInformeResumenUsoVehiculoPorConductor(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(final Hashtable cv, final Vector v, final int sesionId) throws Exception {

		final String sql = "select "
				+ "CDVINFORM_USOVEHICULO_TEMP.MATRICULA, "
				+ "NVL(DNI,'??') AS DNI,  "
				+ "SUM(HORAS) AS S_HORAS, "
				+ "SUM(KM_REC) AS S_REC, "
				+ "case when S_HORAS_T != 0 THEN (SUM(HORAS) / S_HORAS_T)*100 ELSE 0 END AS  PERC_HORAS, "
				+ "case when S_REC_T != 0 THEN (SUM(KM_REC) / S_REC_T)*100 ELSE 0 END AS  PERC_REC "
				+ "from CDVINFORM_USOVEHICULO_TEMP, "
				+ "( "
				+ " SELECT "
				+ " MATRICULA , "
				+ "SUM(HORAS) AS S_HORAS_T, "
				+ "SUM(KM_REC) AS S_REC_T "
				+ "FROM CDVINFORM_USOVEHICULO_TEMP "
				+ "WHERE "
				+ " CG_CONTRATO = ? "
				+ "and  to_date(to_char(FECINI,'dd/MM/yyyy hh24:mi:ss'),'dd/MM/yyyy hh24:mi:ss') >= to_date(to_char(?,'dd/MM/yyyy'), 'dd/MM/yyyy hh24:mi:ss') "
				+ "and  to_date(to_char(FECINI,'dd/MM/yyyy hh24:mi:ss'),'dd/MM/yyyy hh24:mi:ss')  < to_date(to_char(? +1,'dd/MM/yyyy'), 'dd/MM/yyyy hh24:mi:ss') "
				+ " group by MATRICULA ) TOTAL "
				+ "where  "
				+ " CG_CONTRATO = ? AND "
				+ " to_date(to_char(FECINI,'dd/MM/yyyy hh24:mi:ss'),'dd/MM/yyyy hh24:mi:ss') >= to_date(to_char(?,'dd/MM/yyyy'), 'dd/MM/yyyy hh24:mi:ss') "
				+ "and "
				+ " to_date(to_char(FECINI,'dd/MM/yyyy hh24:mi:ss'),'dd/MM/yyyy hh24:mi:ss')  < to_date(to_char(? +1,'dd/MM/yyyy'), 'dd/MM/yyyy hh24:mi:ss') "
				+ "GROUP BY CDVINFORM_USOVEHICULO_TEMP.MATRICULA,DNI,S_REC_T,S_HORAS_T";
		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection con) throws UException, SQLException {

				Date fInicio = new Date(((Timestamp) cv.get("f_inicio")).getTime());
				Date fFin = new Date(((Timestamp) cv.get("f_fin")).getTime());
				String numReq = (String) cv.get("numreq");

				new QueryJdbcTemplate<EntityResult>() {

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
				return null;
			}}.execute(this.manager, false);

	}
}
