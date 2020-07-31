package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInformekms extends FileTableEntity {

	// private static final Logger logger = LoggerFactory.getLogger(EInformekms.class);

	public EInformekms(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {

		boolean oldAutocommit = con.getAutoCommit();
		try {
			if (oldAutocommit) {
				con.setAutoCommit(false);
			}

			Timestamp[] fComienzoFfinal = this.getQueryDates(cv);
			Timestamp fComienzo = fComienzoFfinal[0];
			Timestamp fFinal = fComienzoFfinal[1];
			java.sql.Date dcomienzo = new java.sql.Date(fComienzo.getTime());
			java.sql.Date dfin = new java.sql.Date(fFinal.getTime());

			Object cgContrato = cv.get("CG_CONTRATO");
			cgContrato = ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sesionId, con);
			// busco los vehiculos

			String sql1 = "SELECT CDVEHICULO_CONT.MATRICULA FROM CDVEHICULO_CONT,CDVEMPRE_REQ_REALES,CDVEHICULOS_EMP WHERE CDVEHICULO_CONT.CG_CONTRATO = ? " + " AND CDVEHICULO_CONT.CG_CONTRATO = CDVEMPRE_REQ_REALES.CG_CONTRATO AND CDVEMPRE_REQ_REALES.CIF = CDVEHICULOS_EMP.CIF AND CDVEHICULOS_EMP.MATRICULA = CDVEHICULO_CONT.MATRICULA";
			Vector vVehicles = new QueryJdbcTemplate<Vector<String>>() {
				@Override
				protected Vector<String> parseResponse(ResultSet rset) throws UException {
					try {
						Vector<String> vIdDrivers = new Vector();
						while (rset.next()) {
							vIdDrivers.add(rset.getString(1));
						}
						return vIdDrivers;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(con, sql1, cgContrato);

			EntityResult resDrivers = new QueryJdbcTemplate<EntityResult>() {
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
			}.execute(con,
					"SELECT CDCONDUCTOR_CONT.IDCONDUCTOR FROM CDCONDUCTOR_CONT,CDVEMPRE_REQ_REALES,CDCONDUCTORES_EMP WHERE CDCONDUCTOR_CONT.CG_CONTRATO = ? " + " AND CDCONDUCTOR_CONT.CG_CONTRATO = CDVEMPRE_REQ_REALES.CG_CONTRATO AND CDVEMPRE_REQ_REALES.CIF = CDCONDUCTORES_EMP.CIF AND CDCONDUCTORES_EMP.IDCONDUCTOR = CDCONDUCTOR_CONT.IDCONDUCTOR",
					cgContrato);

			String vehiculos = "";
			if ((vVehicles != null) && (vVehicles.size() > 0)) {
				vehiculos = " matricula =";
				for (int i = 0; i < vVehicles.size(); i++) {
					vehiculos += "'" + vVehicles.get(i) + "'" + " or matricula = ";
				}
				if (vehiculos.length() > 15) {
					vehiculos = vehiculos.substring(0, vehiculos.length() - 15);
				}
			}

			String conductores = "";
			if ((resDrivers != null) && (resDrivers.calculateRecordNumber() > 0)) {
				conductores = " idconductor =";

				StringBuilder conductoresProc = new StringBuilder();
				for (int i = 0; i < resDrivers.calculateRecordNumber(); i++) {
					Hashtable av = resDrivers.getRecordValues(i);
					conductores += "'" + av.get("IDCONDUCTOR") + "'" + " or idconductor = ";
					conductoresProc.append(av.get("IDCONDUCTOR")).append(",");
				}
				// me cargo el ultimo " or idconductor = "
				if (conductores.length() > 18) {
					conductores = conductores.substring(0, conductores.length() - 18);
				}

				new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", cgContrato, conductoresProc.toString(), fComienzo, fFinal);

				// usos
				new UpdateJdbcTemplate().execute(con, "DELETE FROM CDUSO_TARJETA_TEMP");

				Object[] parameters = new Object[4];
				Arrays.fill(parameters, null);
				if (cv.get("FECHA") instanceof SearchValue) {
					Vector<Object> vCond = (Vector<Object>) ((SearchValue) cv.get("FECHA")).getValue();
					if (vCond.size() == 2) {
						parameters[2] = vCond.get(0);
						parameters[3] = vCond.get(1);
					}
				}
				parameters[1] = cgContrato;
				Vector vIdDrivers = (Vector) resDrivers.get("IDCONDUCTOR");
				for (int i = 0; i < vIdDrivers.size(); i++) {
					parameters[0] = vIdDrivers.get(i);
					new ProcedureJdbcTemplate().execute(con, "PROC_CDUSO_TARJETA", parameters);
				}
				String sqlconductores = new Template("sql/EInformekmsCond.sql").fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#CONDUCTORES#", conductores));

				resDrivers = new QueryJdbcTemplate<EntityResult>() {
					@Override
					protected EntityResult parseResponse(ResultSet rs) throws UException {
						try {
							EntityResult res = new EntityResult();
							FileTableEntity.resultSetToEntityResult(rs, res);
							return res;
						} catch (Exception ex) {
							throw new UException(ex);
						}
					}
				}.execute(con, sqlconductores, cgContrato, dcomienzo, dfin);
			}

			String sqlvehiculos = new Template("sql/EInformekms.sql").fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#VEHICULOS#", vehiculos));

			EntityResult resVehiculo = new QueryJdbcTemplate<EntityResult>() {
				@Override
				protected EntityResult parseResponse(ResultSet rs) throws UException {
					try {
						EntityResult res = new EntityResult();
						FileTableEntity.resultSetToEntityResult(rs, res);
						return res;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(con, sqlvehiculos, cgContrato, dcomienzo, dfin);

			EntityResult resTotal = EntityResultUtils.merge(resDrivers, resVehiculo);
			resTotal = EntityResultTools.doSort(resTotal, "ORIGEN", "IDORIGEN", "FECHA");

			new UpdateJdbcTemplate().execute(con, "DELETE FROM CDACTIVIDADES_TEMPPRES");

			return resTotal;
		} finally {
			con.rollback();
			if (oldAutocommit) {
				con.setAutoCommit(true);
			}
		}
	}

	public String getSQL(String vehiculos, String entidad, String pre) {

		vehiculos = vehiculos.replace(entidad, pre + "." + entidad);
		return vehiculos;
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

		SearchValue vbFComienzo = (SearchValue) cv.get("FECHA");
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
