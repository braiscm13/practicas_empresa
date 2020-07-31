package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.FileTableEntity;

public class EDatosInspeccionOLD extends FileTableEntity {

	public EDatosInspeccionOLD(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult insert(Hashtable av, int idSesion, Connection con) throws Exception {
		Object fechaInicio = av.get(OpentachFieldNames.FECINI_FIELD);
		Object fechaFin = av.get(OpentachFieldNames.FECFIN_FIELD);
		EntityResult res = new EntityResult();
		boolean dOk = EDatosInspeccionOLD.comprobarFechasValidas((Timestamp) fechaInicio, (Timestamp) fechaFin);
		if (!dOk) {
			res.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
			res.setMessage("M_FECHA_FIN_INF_INICIO");
		} else {
			res = super.insert(av, idSesion, con);
			if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				res.put(OpentachFieldNames.IDINSPECCION_FIELD, av.get(OpentachFieldNames.IDINSPECCION_FIELD));
				return res;
			}
		}
		return res;
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int idSesion, Connection con) throws Exception {
		Object fechaInicio = av.get(OpentachFieldNames.FECINI_FIELD);
		Object fechaFin = av.get(OpentachFieldNames.FECFIN_FIELD);
		// si la fecha de inicio del análisis es inferior a la fecha actual
		// devolveremos error
		EntityResult res = new EntityResult();
		boolean dOk = EDatosInspeccionOLD.comprobarFechasValidas((Timestamp) fechaInicio, (Timestamp) fechaFin);
		if (!dOk) {
			res.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
			res.setMessage("M_FECHA_FIN_INF_INICIO");
		} else {
			res = super.update(av, cv, idSesion, con);
		}
		return res;
	}

	/**
	 * Comprueba que la fecha de inicio no es inferior a la fecha actual y que
	 * la fecha fin es superior a la de inicio.
	 */
	private static final boolean comprobarFechasValidas(Timestamp fechaDesde, Timestamp fechaHasta) {
		if ((fechaDesde != null) && (fechaHasta != null)) {
			Calendar cini = Calendar.getInstance();
			cini.setTime(new Date(fechaDesde.getTime()));
			Calendar cfin = Calendar.getInstance();
			cfin.setTime(new Date(fechaHasta.getTime()));
			if ((cfin.get(Calendar.YEAR) >= cini.get(Calendar.YEAR)) || (cfin.get(Calendar.DAY_OF_YEAR) > cini.get(Calendar.DAY_OF_YEAR))) {
				return true;
			}
		}
		return false;
	}

}
