package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.activities.InfractionService;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeResumenTiposInfracciones extends FileTableEntity {

	public static final String DO_QUERY = "DO_QUERY";

	public EInformeResumenTiposInfracciones(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {
		try {
			conn.setAutoCommit(false);

			if (cv.containsKey(EInformeResumenTiposInfracciones.DO_QUERY)) {
				return super.query(cv, av, sesionId, conn);
			}
			Date beginDate = (Date) cv.get(OpentachFieldNames.FILTERFECINI);
			Date endDate = (Date) cv.get(OpentachFieldNames.FILTERFECFIN);
			Object cgContrato = cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
			String cif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
			Pair<Object, Vector<Object>> p = this.queryContractAndDrivers(cif, cgContrato, null, sesionId);
			Vector<Object> vdrivers = p.getSecond();

			this.getService(InfractionService.class).analyzeForReport(conn, cgContrato, cif, vdrivers, beginDate, endDate,
					(EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER), sesionId);

			EntityResult res = super.query(new Hashtable(), av, sesionId, conn);
			EntityResult tipos = new QueryJdbcToEntityResultTemplate().execute(conn, "SELECT TIPO, count(*) as NUM FROM CDINFRACCIONES_TEMP group by tipo ");
			EntityResult naturaleza = new QueryJdbcToEntityResultTemplate().execute(conn, "SELECT  NATURALEZA , COUNT(*) AS NUM FROM CDINFRACCIONES_TEMP I GROUP BY NATURALEZA ");
			int nregs = res.calculateRecordNumber();
			if (nregs > 0) {
				Object[] tmp = new Object[nregs];
				tmp[tmp.length - 1] = tipos;
				res.put("TIPOS_INFRAC", new Vector(Arrays.asList(tmp)));
				tmp = new Object[nregs];
				tmp[tmp.length - 1] = naturaleza;
				res.put("HECHOS_SANCIONABLES", new Vector(Arrays.asList(tmp)));
			}
			return res;
		} finally {
			conn.commit();
			conn.setAutoCommit(true);
		}

	}

	private Pair<Object, Vector<Object>> queryContractAndDrivers(final String cif, final Object cgContrato, final Vector<Object> vDrivers, final int sesionId) throws Exception {
		Pair<Object, Vector<Object>> pair = new OntimizeConnectionTemplate<Pair<Object, Vector<Object>>>() {

			@Override
			protected Pair<Object, Vector<Object>> doTask(Connection conn) throws UException {
				try {
					Vector<Object> resDrivers = EInformeResumenTiposInfracciones.this.getDriversToQuery(vDrivers, cif, cgContrato, sesionId, conn);
					return new Pair<Object, Vector<Object>>(cgContrato, resDrivers);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

		}.execute((OpentachServerLocator) this.locator, true);
		return pair;
	}

	private Vector<Object> getDriversToQuery(Vector<Object> vDrivers, String cif, Object cgContrato, int sesionId, Connection conn) throws Exception {
		if ((vDrivers == null) || vDrivers.isEmpty()) {
			// hay que consultar todos los conductores de la empresa
			TableEntity entValidos = (TableEntity) ((OpentachServerLocator) this.locator).getEntityReferenceFromServer("EConductorContValido");
			Hashtable<String, Object> av2 = new Hashtable<String, Object>();
			if (cif != null) {
				av2.put("CIF", cif);
			}
			av2.put("CG_CONTRATO", cgContrato);
			EntityResult resConductores = entValidos.query(av2, EntityResultTools.attributes("IDCONDUCTOR"), sesionId, conn);
			if (resConductores.calculateRecordNumber() > 0) {
				vDrivers = (Vector<Object>) resConductores.get("IDCONDUCTOR");
			}
		}
		return vDrivers;
	}

}
