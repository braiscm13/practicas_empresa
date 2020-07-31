package com.opentach.server.entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;

public class EInformeTiemposDistanciasCond extends FileTableEntity {

	//	private static final Logger	logger	= LoggerFactory.getLogger(EInformeTiemposDistanciasCond.class);

	public EInformeTiemposDistanciasCond(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		//		CallableStatement st = null;
		EntityResult res = null;
		try {
			con.setAutoCommit(false);
			cv.put("FEC_COMIENZO", cv.get("FECHA"));
			// ---------------------------------------------
			Object numReq = cv.get("CG_CONTRATO");
			numReq = ContractUtils.checkContratoFicticio(this.getLocator(), numReq, sesionId, con);
			Timestamp[] fComienzoFfinal = this.getQueryDates(cv);
			Timestamp fComienzo = fComienzoFfinal[0];
			Timestamp fFinal = fComienzoFfinal[1];
			Vector<String> vIdDrivers = EInformeTiemposDistanciasCond.getDrivers(cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
			String sDrivers = EInformeTiemposDistanciasCond.vectorToCommaSeparated(vIdDrivers);

			new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", numReq, sDrivers, fComienzo, fFinal);
			cv.remove("FEC_COMIENZO");

			Timestamp tBegin = null;
			Timestamp tEnd = null;
			if (cv.get("FECHA") instanceof SearchValue) {
				Vector<Object> vCond = (Vector<Object>) ((SearchValue) cv.get("FECHA")).getValue();
				if (vCond.size() == 2) {
					tBegin = (Timestamp) vCond.get(0);
					tEnd = (Timestamp) vCond.get(1);
				}
			}

			for (String idDriver : vIdDrivers) {
				new ProcedureJdbcTemplate<Void>().execute(con, "PROC_CDUSO_TARJETA", idDriver, cv.get("CG_CONTRATO"), tBegin, tEnd);
			}

			// CONSULTO LA VISTA
			res = super.query(new Hashtable(), new Vector(), sesionId, con);
			return this.pivotar(res);
		} finally {
			if (con != null) {
				con.rollback();
				con.setAutoCommit(true);
			}
		}
	}

	private EntityResult pivotar(EntityResult res) {

		EntityResult resultado = new EntityResult();
		Hashtable<Object, Object> av = new Hashtable<Object, Object>();
		for (int i = 0; i < res.calculateRecordNumber(); i++) {
			Hashtable<Object, Object> registro = res.getRecordValues(i);
			Date d = (Date) registro.get("FEC_INI");
			String idcond = (String) registro.get("IDCONDUCTOR");

			av.put("IDCONDUCTOR", idcond);
			av.put("NOMBRE", registro.get("NOMBRE"));
			av.put("APELLIDOS", registro.get("APELLIDOS"));
			av.put("FECHA", registro.get("FEC_INI"));
			av.put("KMS_ACOMPANANTE", registro.get("KMS_ACOMPANANTE"));
			av.put("KMS_CONDUCTOR", registro.get("KMS_CONDUCTOR"));
			av.put("NUMREQ", registro.get("NUMREQ"));

			av.put("CONDUCCION", new BigDecimal(0));
			av.put("TRABAJO", new BigDecimal(0));
			av.put("PAUSA_DESCANSO", new BigDecimal(0));
			av.put("INDEFINIDA", new BigDecimal(0));
			av.put("DISPONIBILIDAD_COND", new BigDecimal(0));
			av.put("DISPONIBILIDAD_ACOMP", new BigDecimal(0));

			int j = i;
			while (idcond.equals(res.getRecordValues(j).get("IDCONDUCTOR")) && d.equals(res.getRecordValues(j).get("FEC_INI"))) {
				double minutos = ((BigDecimal) res.getRecordValues(j).get("MINUTOS")).doubleValue();
				if ("PAUSA/DESCANSO".equals(res.getRecordValues(j).get("ACTIVIDAD"))) {
					av.put("PAUSA_DESCANSO", new BigDecimal(((BigDecimal) av.get("PAUSA_DESCANSO")).doubleValue() + minutos));
				} else {
					av.put(res.getRecordValues(j).get("ACTIVIDAD"),
							new BigDecimal(((BigDecimal) av.get(res.getRecordValues(j).get("ACTIVIDAD"))).doubleValue() + minutos));
				}
				j++;
			}

			resultado.addRecord(av);
			i = j - 1;
		}
		return resultado;
	}

	public static String vectorToCommaSeparated(Vector<String> vIdDrivers) {
		StringBuilder idsConductor = new StringBuilder();
		for (int i = 0; i < vIdDrivers.size(); i++) {
			idsConductor = idsConductor.append(vIdDrivers.elementAt(i));
			if (i != (vIdDrivers.size() - 1)) {
				idsConductor = idsConductor.append(",");
			}
		}
		return idsConductor.toString();
	}

	public static Vector<String> getDrivers(Object oidConductor) throws Exception {
		Vector<String> vDrivers = new Vector<String>();
		if (oidConductor instanceof String) {
			if (oidConductor.equals("%")) {
				throw new Exception("E_MUST_SPECIFY_DRIVER");
			}
			vDrivers.add((String) oidConductor);
		} else if ((oidConductor instanceof SearchValue) && ((((SearchValue) oidConductor).getCondition() == SearchValue.OR) || (((SearchValue) oidConductor)
				.getCondition() == SearchValue.IN) || (((SearchValue) oidConductor).getCondition() == SearchValue.BETWEEN))) {
			Vector<String> v = (Vector<String>) ((SearchValue) oidConductor).getValue();
			vDrivers.addAll(v);
		}

		if (vDrivers.size() == 0) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		return vDrivers;
	}

	private Timestamp[] getQueryDates(Hashtable cv) throws Exception {

		SearchValue vbFComienzo = (SearchValue) cv.get("FEC_COMIENZO");
		if (vbFComienzo == null) {
			vbFComienzo = (SearchValue) cv.get(OpentachFieldNames.FECINI_FIELD);
		}

		Timestamp fComienzo = null;
		Timestamp fFinal = null;

		if (vbFComienzo != null) {
			switch (vbFComienzo.getCondition()) {
				case SearchValue.BETWEEN:
					Vector<Object> vv = (Vector<Object>) vbFComienzo.getValue();
					fComienzo = new Timestamp(((Date) vv.firstElement()).getTime());
					fFinal = new Timestamp(((Date) vv.lastElement()).getTime());
					break;
				case SearchValue.LESS_EQUAL:
					fFinal = (Timestamp) vbFComienzo.getValue();
					break;
				case SearchValue.MORE_EQUAL:
					fComienzo = (Timestamp) vbFComienzo.getValue();
					break;
			}
		}
		if ((fComienzo == null) || (fFinal == null)) {
			throw new Exception("FECHA_NULL");
		}

		return new Timestamp[] { fComienzo, fFinal };
	}
}
