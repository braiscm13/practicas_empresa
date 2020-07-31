package com.opentach.server.activities;

import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EInformeInfrac;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.FunctionJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class InfractionAnalyzerInDatabase extends AbstractDelegate {

	private static final Logger logger = LoggerFactory.getLogger(InfractionAnalyzerInDatabase.class);

	public InfractionAnalyzerInDatabase(IOpentachServerLocator locator) {
		super(locator);
	}

	public EntityResult analyze(final String cgContrato, final Vector<String> vDrivers, final Date fIni, final Date fFin, final Vector<String> av, final int sessionId)
			throws Exception {
		if (cgContrato == null) {
			throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
		}

		return new OntimizeConnectionTemplate<EntityResult>() {
			@Override
			protected EntityResult doTask(Connection conn) throws UException {
				try {
					InfractionAnalyzerInDatabase.this.analyze(cgContrato, vDrivers, fIni, fFin, sessionId, conn);

					// No hace falta ningún filtro porque los resultados de la tabla
					// temporal ya son de la conexión
					return InfractionAnalyzerInDatabase.this.doConsultar(new Hashtable(), av, sessionId, conn);

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	private void analyze(String cgContrato, Vector<String> vDrivers, Date fIni, Date fFin, int sessionId, Connection conn) throws Exception {

		this.insertDriversInQueryTable(vDrivers, cgContrato, conn);

		String msg = new FunctionJdbcTemplate<String>().execute(conn, "FNC_INFRAC_DO_ANALISIS_IMATIA", Types.VARCHAR, cgContrato, new java.sql.Date(fIni.getTime()),
				new java.sql.Date(fFin.getTime()));
		if ((msg != null) && msg.startsWith("KO")) {
			InfractionAnalyzerInDatabase.logger.error("Error calculando infracciones: " + msg);
			if (msg.equalsIgnoreCase("KO. No se han encontrado Actividades para el Requerimiento")) {
				throw new Exception("KO. No se han encontrado Actividades");
			}
			throw new Exception(msg);
		}
	}

	private void insertDriversInQueryTable(Vector<String> drivers, String numreq, Connection con) throws Exception {
		TransactionalEntity eDriversAnalisis = this.getEntity("EConductoresAnalisis");
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put("NUMREQ", numreq);
		for (String driver : drivers) {
			av.put("IDCONDUCTOR", driver);
			eDriversAnalisis.insert(av, this.getEntityPrivilegedId(eDriversAnalisis), con);
		}
	}

	private EntityResult doConsultar(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {
		cv.remove(OpentachFieldNames.FECINI_FIELD);
		cv.remove(OpentachFieldNames.FECFIN_FIELD);
		cv.put(EInformeInfrac.DO_QUERY, new Object());
		EntityResult res = this.getEntity("EInformeInfrac").query(cv, av, sesionId, conn);
		Vector<Object> vNatur = (Vector<Object>) res.get("NATURALEZA");
		if (vNatur != null) {
			for (int i = 0; i < vNatur.size(); i++) {
				if (vNatur.elementAt(i) == null) {
					res.deleteRecord(i);
					i--;
				}
			}
		}
		Vector<Object> vIdConductor = (Vector<Object>) res.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
		if (vIdConductor != null) {
			Vector<Object> vGrupo = new Vector<Object>(vIdConductor.size());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			String idCond = null;
			int numgrupos = 12;
			int count = 0;
			for (int i = 0; i < vIdConductor.size(); i++) {
				idCond = (String) vIdConductor.elementAt(i);
				if (!hm.containsKey(idCond)) {
					hm.put(idCond, Integer.valueOf((count / numgrupos) + 1));
					count++;
				}
				vGrupo.addElement(hm.get(idCond));
			}
			res.put("GRUPO", vGrupo);
		}
		return res;
	}

}
