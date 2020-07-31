package com.opentach.server.entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeResumenEmpresas extends FileTableEntity {

	public EInformeResumenEmpresas(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {

		Date beginDate = (Date) cv.get(OpentachFieldNames.FILTERFECINI);
		Date endDate = (Date) cv.get(OpentachFieldNames.FILTERFECFIN);

		String SOLO_ALTA_BAJA = cv.get("SOLO_ALTA_BAJA") == null ? "N" : (String) cv.get("SOLO_ALTA_BAJA");
		String sql = new Template("sql/EInformeResumenEmpresas.sql").getTemplate();
		EntityResult resResumen = new QueryJdbcTemplate<EntityResult>() {
			@Override
			protected EntityResult parseResponse(ResultSet rs) throws UException {
				try {
					if (rs.next()) {
						EntityResult resResumen = new EntityResult();
						FileTableEntity.resultSetToEntityResult(rs, resResumen);
						return resResumen;
					}
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql, beginDate, endDate, beginDate, endDate, beginDate, endDate, beginDate, endDate, beginDate, endDate);

		if (SOLO_ALTA_BAJA.equals("S")) {
			EntityResult resAltaBaja = new EntityResult();
			Hashtable registro = new Hashtable();
			for (int i = 0; i < resResumen.calculateRecordNumber(); i++) {
				registro = resResumen.getRecordValues(i);
				if (!((((BigDecimal) registro.get("COND_BAJA")).intValue() == 0) && (((BigDecimal) registro.get("VEH_BAJA"))
						.intValue() == 0) && (((BigDecimal) registro.get("COND_ALTA")).intValue() == 0) && (((BigDecimal) registro.get("VEH_ALTA")).intValue() == 0))) {
					resAltaBaja.addRecord(registro);
				}
			}
			return resAltaBaja;
		}

		return resResumen;
	}

}
