package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;

public class EHuecosFicheros extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EHuecosFicheros.class);

	public EHuecosFicheros(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {
		EntityResult res = this.createEntityResultForSessionId(sesionId);
		Date fecfin = (Date) cv.get(OpentachFieldNames.FILTERFECFIN);
		Date fecini = (Date) cv.get(OpentachFieldNames.FILTERFECINI);
		if ((fecfin == null) && (fecini == null)) {
			fecfin = (Date) cv.get(OpentachFieldNames.FECFIN_FIELD);
			fecini = (Date) cv.get(OpentachFieldNames.FECINI_FIELD);
		}
		Object cgcontratoFic = cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		Object cgcontrato = ContractUtils.checkContratoFicticio(this.getLocator(), cgcontratoFic, sesionId, conn);
		if (!cgcontrato.equals(cgcontratoFic)){
			cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgcontrato);
		}
		try {
			res = super.query(cv, av, sesionId, conn);
			res = this.fillWithHoles(res, cgcontrato, fecini, fecfin, sesionId);
			this.translate(res, sesionId);
			return res;
		} catch (Exception e) {
			EHuecosFicheros.logger.error(null, e);
			throw e;
		}
	}

	private EntityResult fillWithHoles(EntityResult in, Object cgContrato, Date fecIni, Date fecFin, int sessionID) {
		EntityResult out = this.createEntityResultForSessionId(sessionID);
		Hashtable<String, Object> inFila = null;
		String idOrigen = null;
		String lidOrigen = null;
		String tipo = null;
		String lTipo = null;
		Date dIni = null;
		Date dFin = null;
		Date lIni = null;
		Date lFin = null;
		Date mFin = null; // Maxima fecha hasta el momento
		final String sHueco = "HUECO";
		final Boolean bDatos = Boolean.TRUE;
		int inCount = in.calculateRecordNumber();
		Enumeration<Object> en = in.keys();
		while (en.hasMoreElements()) {
			out.put(en.nextElement(), new Vector(inCount));
		}
		out.put("DATOS", new Vector(inCount));
		for (int i = 0; i < inCount; i++) {
			inFila = in.getRecordValues(i);
			idOrigen = (String) inFila.get(OpentachFieldNames.IDORIGEN_FIELD);
			dIni = (Date) inFila.get(OpentachFieldNames.FECINI_FIELD);
			dFin = (Date) inFila.get(OpentachFieldNames.FECFIN_FIELD);
			tipo = (String) inFila.get("TIPO");
			if (idOrigen.equals(lidOrigen)) {
				if ((dIni != null) && (dFin != null) && (lIni != null) && (mFin != null)) {
					if (dIni.after(mFin)) {
						Hashtable<String, Object> fila = new Hashtable<String, Object>(9);
						fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
						fila.put(OpentachFieldNames.IDORIGEN_FIELD, lidOrigen);
						fila.put(OpentachFieldNames.FECINI_FIELD, mFin);
						fila.put(OpentachFieldNames.FECFIN_FIELD, dIni);
						fila.put("TIPO", lTipo);
						fila.put("NOMB", sHueco);
						fila.put("OBSR", sHueco);
						out.addRecord(fila);
					}
				}
				// holes in the middle
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
					fila.put("TIPO", lTipo);
					fila.put("NOMB", sHueco);
					fila.put("OBSR", sHueco);
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
					fila.put("TIPO", tipo);
					fila.put("NOMB", sHueco);
					fila.put("OBSR", sHueco);
					out.addRecord(fila);
				}
			}
			lidOrigen = idOrigen;
			lTipo = tipo;
			lIni = dIni;
			lFin = dFin;
			inFila.put("DATOS", bDatos);
			out.addRecord(inFila);
		}
		if (mFin == null) {
			mFin = lFin;
		}
		// hueco al finalisimo
		if ((inCount > 0) && (lidOrigen != null) && (lFin != null) && mFin.before(fecFin)) {
			Hashtable<String, Object> fila = new Hashtable<String, Object>();
			fila.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
			fila.put(OpentachFieldNames.IDORIGEN_FIELD, lidOrigen);
			fila.put(OpentachFieldNames.FECINI_FIELD, mFin);
			fila.put(OpentachFieldNames.FECFIN_FIELD, fecFin);
			fila.put("TIPO", lTipo);
			fila.put("NOMB", sHueco);
			fila.put("OBSR", sHueco);
			out.addRecord(fila);
		}
		return out;
	}
}
