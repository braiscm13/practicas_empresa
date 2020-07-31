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
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeUsoTarjeta extends FileTableEntity {

	// private static final Logger logger = LoggerFactory.getLogger(EInformeUsoTarjeta.class);

	public EInformeUsoTarjeta(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
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
			Vector<String> vIdDrivers = EInformeUsoTarjeta.getDrivers(cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
			String conductores = " idconductor =";

			for (int i = 0; i < vIdDrivers.size(); i++) {
				if ((i + 1) == vIdDrivers.size()) {
					conductores += "'" + vIdDrivers.get(i) + "'";
				} else {
					conductores += "'" + vIdDrivers.get(i) + "'" + " or idconductor = ";
				}
			}
			Object cgContrato = cv.get("CG_CONTRATO");
			cgContrato = ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sesionId, con);
			String sqlconductores = new Template("sql/EInformeUsoTarjeta_conductores.sql")
					.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#CONDUCTORES#", conductores));
			String conductoresProc = new QueryJdbcTemplate<String>() {
				@Override
				protected String parseResponse(ResultSet rs) throws UException {
					try {
						StringBuilder conductoresProc = new StringBuilder();
						while (rs.next()) {
							conductoresProc.append(rs.getString(1)).append(",");
						}
						return conductoresProc.toString();
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(con, sqlconductores, dcomienzo, dfin, cgContrato, dcomienzo, dfin, cgContrato);

			new UpdateJdbcTemplate().execute(con, "DELETE FROM CDACTIVIDADES_TEMPPRES");

			if (!"".equals(conductoresProc)) {
				new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", cgContrato, conductoresProc, fComienzo, fFinal);
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
				for (int i = 0; i < vIdDrivers.size(); i++) {
					parameters[0] = vIdDrivers.get(i);
					new ProcedureJdbcTemplate<Void>().execute(con, "PROC_CDUSO_TARJETA", parameters);
				}

				String sqlvehiculos = new Template("sql/EInformeUsoTarjeta_vehiculos.sql").getTemplate();

				return new QueryJdbcToEntityResultTemplate().execute(con, sqlvehiculos);
			}
			return new EntityResult();
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
