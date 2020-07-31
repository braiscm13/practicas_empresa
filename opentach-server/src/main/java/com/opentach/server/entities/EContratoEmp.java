package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.FunctionJdbcTemplate;

public class EContratoEmp extends FileTableEntity {

	public EContratoEmp(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EContratoEmp(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		EntityResult res = super.query(avOrig, av, sesionId, con);
		return res;
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId, Connection con) throws Exception {
		return super.update(av, cv, sesionId, con);
	}

	/**
	 * Al dar de baja una Empresa Reque debe asignarsele la fecha en F_BAJA sin eliminar el registro.
	 */

	@Override
	public EntityResult delete(Hashtable cv, int sessionID, Connection conn) throws Exception {
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put(OpentachFieldNames.FECBAJA_FIELD, new Date());
		return super.update(av, cv, sessionID, conn);
	}

	/**
	 * Solo puede haber un contrato de alta por empresa
	 */

	@Override
	public EntityResult insert(Hashtable cv, int sesionId, Connection con) throws Exception {
		// comprobacion existencia contrato de alta para esa empresa
		String cif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
		Hashtable<String, Object> cvaux = new Hashtable<String, Object>();
		cvaux.put(OpentachFieldNames.CIF_FIELD, cif);
		cvaux.put(OpentachFieldNames.FECBAJA_FIELD, new SearchValue(SearchValue.NULL, null));
		EntityResult resConsulta = this.query(cvaux, new Vector(0), sesionId, con);
		if ((resConsulta == null) || ((resConsulta != null) && (resConsulta.getCode() != EntityResult.OPERATION_SUCCESSFUL))) {
			throw new Exception("M_EXISTE_OTRO_CONTRATO_DE_ALTA");
		} else if (resConsulta.calculateRecordNumber() >= 1) {
			throw new Exception("M_EXISTE_OTRO_CONTRATO_DE_ALTA");
		}
		cv.put("F_REQ", cv.get("F_CONTRATO"));
		// Inserción de la inspección
		// TableEntity einsp = (TableEntity) this.getEntityReference("EDatosInspeccion");
		// EntityResult resinsp = einsp.insert(cv, sesionId, con);
		// cv.put(OpentachFieldNames.IDINSPECCION_FIELD, resinsp.get(OpentachFieldNames.IDINSPECCION_FIELD));
		Object rtn = cv.get(OpentachFieldNames.NUMREQ_FIELD);
		// si cv tiene numreq es un contrato ficticio
		if (rtn == null) {
			rtn = new FunctionJdbcTemplate<String>().execute(con, "FNC_INFRAC_NUMREQ", Types.VARCHAR);
		}
		if (rtn != null) {
			cv.put(OpentachFieldNames.NUMREQ_FIELD, rtn);
			cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, rtn);

			EntityResult res = new EntityResult();
			if ((cv.get(OpentachFieldNames.FECINI_FIELD)== null) && (cv.get("FECINID")!=null)){
				cv.put(OpentachFieldNames.FECINI_FIELD, cv.get("FECINID"));
			}
			if ((cv.get(OpentachFieldNames.FECFIN_FIELD)== null) && (cv.get("FECFIND")!=null)){
				cv.put(OpentachFieldNames.FECFIN_FIELD, cv.get("FECFIND"));
			}
			boolean dOk = EContratoEmp.comprobarFechasValidas((Timestamp)cv.get(OpentachFieldNames.FECFIN_FIELD), (Timestamp)cv.get(OpentachFieldNames.FECFIN_FIELD));
			if (!dOk) {
				res.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
				res.setMessage("M_FECHA_FIN_INF_INICIO");
			} else {
				res = super.insert(cv, sesionId, con);
				res.put(OpentachFieldNames.NUMREQ_FIELD, rtn);
				res.put(OpentachFieldNames.CG_CONTRATO_FIELD, rtn);
			}
			return res;
		}
		throw new Exception("ERROR EJECUCION PROC. FNC_INFRAC_NUMREQ: KEY NUMREQ=null");
	}

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
