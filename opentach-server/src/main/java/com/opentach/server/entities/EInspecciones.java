package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInspecciones extends FileTableEntity {

	public EInspecciones(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		// Obtengo vehículos y ficheros
		// condiciones fechas
		CheckingTools.failIf(!cv.containsKey(OpentachFieldNames.FILTERFECINI) || !cv.containsKey(OpentachFieldNames.FILTERFECFIN),
				"E_FILTER_DATE_REQUIRED");

		EntityResult resv = super.query(cv, av, sesionId, con);
		String cgContrato = (String) cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		Date fecIni = (Date) cv.get(OpentachFieldNames.FILTERFECINI);
		Date fecFin = (Date) cv.get(OpentachFieldNames.FILTERFECFIN);
		Object origen = cv.get(OpentachFieldNames.IDORIGEN_FIELD);
		resv = this.addOriginsNotPresent(resv, origen, fecIni, fecFin, cgContrato, "VU", sesionId);
		resv = this.fillWithHoles(resv, cgContrato, fecIni, fecFin, sesionId);
		Vector<Object> avv = new Vector<Object>(1);
		avv.add(OpentachFieldNames.IDCONDUCTOR_FIELD);
		Set<Object> hsMat = new HashSet((Vector<Object>) resv.get(OpentachFieldNames.IDORIGEN_FIELD));
		cv.put(OpentachFieldNames.IDORIGEN_FIELD, new SearchValue(SearchValue.IN, new Vector(hsMat)));
		Map<String, String> mcond = this.getConductoresVehiculo(cv, avv, sesionId, con);
		Vector<String> vcond = new Vector<String>(mcond.keySet());
		if (vcond.size() > 0) {
			Hashtable<String, Object> cvc = this.addFilterValuesFicheroConductor(fecIni, fecFin);
			SearchValue vbc = new SearchValue(SearchValue.IN, vcond);
			cvc.put(OpentachFieldNames.IDORIGEN_FIELD, vbc);
			cvc.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
			EntityResult resc = this.getFicherosConductores(cvc, av, sesionId, con);
			if (resc.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				if (resc.calculateRecordNumber() > 0) {
					resc = this.fillWithHoles(resc, cgContrato, fecIni, fecFin, sesionId);
				}
				resc = this.addOriginsNotPresent(resc, mcond, fecIni, fecFin, cgContrato, "TC", sesionId);
				this.translate(resc, sesionId);
				resv.put("EInspeccionesCondu", resc);
			}
		}

		return resv;
	}

	private EntityResult addOriginsNotPresent(EntityResult in, Object origen, Date fecini, Date fecfin, String cgContrato, String tipo, int sessionID)
			throws Exception {
		EntityResult out = new EntityResult(in);
		Vector<Object> vSearch = null;
		Map<Object, Object> mb = null;
		if (origen instanceof String) {
			vSearch = new Vector<Object>();
			vSearch.add(origen);
		} else if (origen instanceof Map) {
			mb = (Map<Object, Object>) origen;
			vSearch = new Vector<Object>(mb.keySet());
		} else if (origen instanceof SearchValue) {
			vSearch = (Vector<Object>) ((SearchValue) origen).getValue();
		} else {
			throw new IllegalArgumentException("param origen invalid");
		}
		Vector<Object> vPresent = (Vector<Object>) in.get(OpentachFieldNames.IDORIGEN_FIELD);
		if ((vPresent != null) && (vPresent.size() > 0)) {
			Set<Object> sPresent = new HashSet<Object>();
			Set<Object> sSearch = new HashSet<Object>();
			sPresent.addAll(vPresent);
			sSearch.addAll(vSearch);
			sSearch.removeAll(sPresent);
			vSearch = new Vector<Object>(sSearch);
		}
		int size = vSearch.size();
		Hashtable<String, Object> fila = null;
		final String sHueco = "HUECO";
		String name;
		if (out.isEmpty()) {
			EntityResultTools.initEntityResult(out, OpentachFieldNames.IDORIGEN_FIELD, "DSCR_COND_EXTRA",
					OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD, OpentachFieldNames.TYPE_FIELD,
					OpentachFieldNames.CG_CONTRATO_FIELD, OpentachFieldNames.FILENAME_FIELD);
		}
		for (int i = 0; i < size; i++) {
			name = (String) vSearch.get(i);
			fila = new Hashtable<String, Object>();
			fila.put(OpentachFieldNames.IDORIGEN_FIELD, name);
			if (mb != null) {
				Object dscr = mb.get(name);
				if (dscr != null) {
					fila.put("DSCR_COND_EXTRA", dscr);
				}
			}
			fila.put(OpentachFieldNames.FECINI_FIELD, fecini);
			fila.put(OpentachFieldNames.FECFIN_FIELD, fecfin);
			fila.put(OpentachFieldNames.TYPE_FIELD, tipo);
			fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
			fila.put(OpentachFieldNames.FILENAME_FIELD, sHueco);
			out.addRecord(fila);
		}
		this.translate(out, sessionID);
		return out;
	}

	private Hashtable<String, Object> addFilterValuesFicheroConductor(Date fIni, Date fFin) {
		Hashtable<String, Object> cvv = new Hashtable<String, Object>();
		SearchValue vbIni = new SearchValue(SearchValue.LESS_EQUAL, fFin);
		SearchValue vbFin = new SearchValue(SearchValue.MORE_EQUAL, fIni);
		cvv.put(OpentachFieldNames.FECINI_FIELD, vbIni);
		cvv.put(OpentachFieldNames.FECFIN_FIELD, vbFin);
		return cvv;
	}

	private Map<String, String> getConductoresVehiculo(Hashtable cv, Vector av, int sessionID, Connection conn) throws Exception {
		Timestamp fecini = (Timestamp) cv.get(OpentachFieldNames.FILTERFECINI);
		Timestamp fecfin = (Timestamp) cv.get(OpentachFieldNames.FILTERFECFIN);
		Object idOrigen = cv.get(OpentachFieldNames.IDORIGEN_FIELD);
		String cgContrato = (String) cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		String sql1 = " SELECT DISTINCT IDCONDUCTOR,NOMBRE,APELLIDOS  FROM CDUSO_VEHICULO WHERE FECINI < ?  AND FECFIN > ? AND NUMREQ = ? ";
		String sql2 = " SELECT DISTINCT IDCONDUCTOR,NOMBRE,APELLIDOS  FROM CDUSO_TARJETA  WHERE FEC_INS < ?  AND FEC_EXT > ? AND NUMREQ = ?";
		StringBuffer sbIn = new StringBuffer(" AND MATRICULA IN ( ");
		Vector<Object> vMat = (Vector<Object>) ((SearchValue) idOrigen).getValue();
		int size = vMat.size();
		for (int i = 0; i < size; i++) {
			sbIn.append('\'').append(vMat.get(i)).append('\'');
			if (i != (size - 1)) {
				sbIn.append(',');
			}
		}
		sbIn.append(')');
		String sql = "SELECT IDCONDUCTOR, " + "(CASE WHEN "
				+ "	(SELECT COUNT (*) AS C FROM CDVCONDUCTORES WHERE CDVCONDUCTORES.IDCONDUCTOR = A.IDCONDUCTOR)<>0 " + "	THEN "
				+ "	(SELECT MIN(CDVCONDUCTORES.APELLIDOS || ', ' || CDVCONDUCTORES.NOMBRE) "
				+ "	FROM CDVCONDUCTORES WHERE CDVCONDUCTORES.IDCONDUCTOR = A.IDCONDUCTOR)" + " ELSE  " + " A.APELLIDOS || ', ' || A.NOMBRE"
				+ " END )   AS DSCR_COND_EXTRA " + "FROM ( " + sql1 + sbIn.toString() + " UNION " + sql2 + sbIn.toString() + " ) A";

		return new QueryJdbcTemplate<Map<String, String>>() {

			@Override
			protected Map<String, String> parseResponse(ResultSet rset) throws UException {
				try {
					Map<String, String> mResult = new HashMap<String, String>();
					while (rset.next()) {
						String idCond = rset.getString(1);
						String name = rset.getString(2);
						mResult.put(idCond, name);
					}
					return mResult;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql, fecfin, fecini, cgContrato, fecfin, fecini, cgContrato);
	}

	private EntityResult getFicherosConductores(Hashtable cv, Vector av, int sessionID, Connection conn) throws Exception {
		EntityResult res = super.query(cv, av, sessionID, conn);
		return res;
	}

	private EntityResult fillWithHoles(EntityResult in, String cgContrato, Date fecIni, Date fecFin, int sessionID) {
		EntityResult out = this.createEntityResultForSessionId(sessionID);
		Hashtable<String, Object> inFila = null;
		String idOrigen = null;
		String lidOrigen = null;
		String dscr = null;
		String dscrExtra = null;
		String lDscr = null;
		String lDscrExtra = null;
		String tipo = null;
		String lTipo = null;
		Date dIni = null;
		Date dFin = null;
		Date lIni = null;
		Date lFin = null;
		Date mFin = null; // Maxima fecha hasta el momento
		final String sHueco = "HUECO";
		int inCount = in.calculateRecordNumber();
		Enumeration<Object> en = in.keys();
		while (en.hasMoreElements()) {
			out.put(en.nextElement(), new Vector<Object>());
		}
		for (int i = 0; i < inCount; i++) {
			inFila = in.getRecordValues(i);
			idOrigen = (String) inFila.get(OpentachFieldNames.IDORIGEN_FIELD);
			dIni = (Date) inFila.get(OpentachFieldNames.FECINI_FIELD);
			dFin = (Date) inFila.get(OpentachFieldNames.FECFIN_FIELD);
			dscr = (String) inFila.get("DSCR_COND");
			dscrExtra = (String) inFila.get("DSCR_COND_EXTRA");
			tipo = (String) inFila.get(OpentachFieldNames.TYPE_FIELD);
			// si el origen es el mismo
			if (idOrigen.equals(lidOrigen)) {
				// CASO HUECO NORMAL
				if ((dIni != null) && (dFin != null) && (lIni != null) && (mFin != null)) {
					if (dIni.after(mFin)) {
						Hashtable<String, Object> fila = new Hashtable<String, Object>(9);
						fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
						fila.put(OpentachFieldNames.IDORIGEN_FIELD, lidOrigen);
						fila.put(OpentachFieldNames.FECINI_FIELD, mFin);
						fila.put(OpentachFieldNames.FECFIN_FIELD, dIni);
						fila.put(OpentachFieldNames.TYPE_FIELD, lTipo);
						fila.put(OpentachFieldNames.FILENAME_FIELD, sHueco);
						if (lDscr != null) {
							fila.put("DSCR_COND", lDscr);
						}
						if (lDscrExtra != null) {
							fila.put("DSCR_COND_EXTRA", lDscrExtra);
						}
						out.addRecord(fila);
					}
				}
				// holes by the middle
				if ((mFin == null) || mFin.before(dFin)) {
					mFin = dFin;
				}
			} else {
				// añadir hueco al final
				if ((lidOrigen != null) && (mFin != null) && mFin.before(fecFin)) {
					Hashtable<String, Object> fila = new Hashtable<String, Object>(9);
					fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
					fila.put(OpentachFieldNames.IDORIGEN_FIELD, lidOrigen);
					fila.put(OpentachFieldNames.FECINI_FIELD, mFin);
					fila.put(OpentachFieldNames.FECFIN_FIELD, fecFin);
					fila.put(OpentachFieldNames.TYPE_FIELD, lTipo);
					fila.put(OpentachFieldNames.FILENAME_FIELD, sHueco);
					if (lDscr != null) {
						fila.put("DSCR_COND", lDscr);
					}
					if (lDscrExtra != null) {
						fila.put("DSCR_COND_EXTRA", lDscrExtra);
					}
					out.addRecord(fila);
				}
				// nuevo origen
				// añadir hueco al principio
				if ((dIni != null) && dIni.after(fecIni)) {
					mFin = dFin;
					Hashtable<String, Object> fila = new Hashtable<String, Object>(9);
					fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
					fila.put(OpentachFieldNames.IDORIGEN_FIELD, idOrigen);
					fila.put(OpentachFieldNames.FECINI_FIELD, fecIni);
					fila.put(OpentachFieldNames.FECFIN_FIELD, dIni);
					fila.put(OpentachFieldNames.TYPE_FIELD, tipo);
					fila.put(OpentachFieldNames.FILENAME_FIELD, sHueco);
					if (dscr != null) {
						fila.put("DSCR_COND", dscr);
					}
					if (dscrExtra != null) {
						fila.put("DSCR_COND_EXTRA", dscrExtra);
					}
					out.addRecord(fila);
				} else {
					mFin = dFin;
				}
			}
			lidOrigen = idOrigen;
			lDscr = dscr;
			lDscrExtra = dscrExtra;
			lTipo = tipo;
			lIni = dIni;
			lFin = dFin;
			out.addRecord(inFila);
		}
		if (mFin == null) {
			mFin = lFin;
		}
		if ((inCount > 0) && (lidOrigen != null) && (lFin != null) && mFin.before(fecFin)) {
			Hashtable<String, Object> fila = new Hashtable<String, Object>(9);
			fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
			fila.put(OpentachFieldNames.IDORIGEN_FIELD, lidOrigen);
			fila.put(OpentachFieldNames.FECINI_FIELD, mFin);
			fila.put(OpentachFieldNames.FECFIN_FIELD, fecFin);
			fila.put(OpentachFieldNames.TYPE_FIELD, lTipo);
			fila.put(OpentachFieldNames.FILENAME_FIELD, sHueco);
			if (lDscr != null) {
				fila.put("DSCR_COND", lDscr);
			}
			if (lDscrExtra != null) {
				fila.put("DSCR_COND_EXTRA", lDscrExtra);
			}
			out.addRecord(fila);
		}
		this.translate(out, sessionID);
		return out;
	}
}
