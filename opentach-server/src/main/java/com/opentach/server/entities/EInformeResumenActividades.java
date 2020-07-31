package com.opentach.server.entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.interfaces.IConsultarResumenActividades;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.DateUtil;
import com.opentach.common.util.ResourceManager;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeResumenActividades extends FileTableEntity implements IConsultarResumenActividades {

	// private static final Logger logger = LoggerFactory.getLogger(EInformeResumenActividades.class);

	public EInformeResumenActividades(EntityReferenceLocator bRefs, DatabaseConnectionManager gc, int puerto) throws Exception {
		super(bRefs, gc, puerto);
	}

	private EntityResult pivotarResultEntidad(EntityResult entrada, String clave, String[] tipos, String[] porcentaje, String[] traduccion) {
		EntityResult salida = new EntityResult(entrada.getCode(), entrada.getType());
		int count = entrada.calculateRecordNumber();
		Hashtable<String, Object> ht = null;
		Hashtable<String, Object> reg = null;
		int cont = 1;
		int aux = 1;
		for (int i = 0; i < count; i++) {
			ht = entrada.getRecordValues(i);
			for (int j = 0; j < tipos.length; j++) {
				reg = new Hashtable<String, Object>();
				reg.put("GRUPO", Integer.valueOf(aux));
				reg.put(clave, ht.get(clave));
				reg.put("DESCRIPCION", traduccion[j]);
				reg.put("VALOR", ht.get(tipos[j]) == null ? Integer.valueOf(0) : ht.get(tipos[j]));
				if (porcentaje != null) {
					reg.put("PORCENTAJE", ht.get(porcentaje[j]) == null ? Integer.valueOf(0) : ht.get(porcentaje[j]));
				}
				salida.addRecord(reg);
			}
			if (cont == 12) {
				cont = 0;
				aux++;
			}
			cont++;
		}
		return salida;
	}

	private StringBuffer getSQLDelegacion(String column, IUserData du) {
		StringBuffer sqlDeleg = new StringBuffer();
		if ((du.getDelegList() != null) && (!du.getDelegList().isEmpty())) {
			sqlDeleg.append("  AND ").append(" ( ");
			Map<String, Number> l = du.getDelegList();
			Set<String> values = l.keySet();

			Iterator<String> itr = values.iterator();
			while (itr.hasNext()) {
				Object key = itr.next();
				Object value = l.get(key);
				if ((value == null) || (((BigDecimal) value).intValue() == -1)) {
					sqlDeleg.append(" ( CIF = ? AND IDDELEGACION IS NULL )");
				} else {
					sqlDeleg.append(" ( CIF = ? AND IDDELEGACION = ? )");
				}
				if (itr.hasNext()) {
					sqlDeleg.append(" OR ");
				} else {
					sqlDeleg.append(" ) ");
				}
			}
		}
		return sqlDeleg;
	}

	private Object[] getQueryParameters(IUserData du, Object... parameters) throws SQLException {
		List<Object> list = new ArrayList<Object>(Arrays.asList(parameters));
		if ((du.getDelegList() != null) && (!du.getDelegList().isEmpty())) {
			Map<String, Number> l = du.getDelegList();

			for (int i = 0; i < l.size(); i++) {

				if (l.get(i).intValue() != -1) {
					list.add(l.get(i));
				}

			}
		}
		return list.toArray();
	}

	@Override
	public EntityResult consultarResumenUsosVehiculo(final String cgContrato, final Date fIni, final Date fFin, final int sessionID) throws Exception {
		final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sessionID);
		if (du == null) {
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
		}

		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection con) throws UException {
				try {
					StringBuffer sqlDeleg = EInformeResumenActividades.this.getSQLDelegacion("C.IDDELEGACION", du);
					String sql = new Template("sql/EInformeResumenActividades_resumenUsosVehiculo.sql")
							.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#SQLDELEG#", sqlDeleg));

					java.sql.Date sFFin = new java.sql.Date(fFin.getTime());
					java.sql.Date sFIni = new java.sql.Date(fIni.getTime());
					EntityResult res = new QueryJdbcToEntityResultTemplate().execute(con, sql,
							EInformeResumenActividades.this.getQueryParameters(du, sFFin, sFIni, cgContrato, sFIni, sFFin, sFIni, sFFin, cgContrato, cgContrato));

					res = new EntityResult(EInformeResumenActividades.this.replaceColumnByAlias(res));
					res = EInformeResumenActividades.this.pivotarResultEntidad(res, "MATRICULA",
							new String[] { "KM_RECORRIDOS", "HORAS_MARCHA", "HORAS_INACTIVIDAD", "MEDIA_DIARIA_KM", "MEDIA_DIARIA_HM" }, null,
							new String[] { "Km_recorridos", "Horas_marcha", "Horas_inactividad", "Media_diaria_km", "Media_diaria_hm" });
					EInformeResumenActividades.this.translate(res, sessionID);
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.manager, false);

	}

	@Override
	public EntityResult consultarResumenActividadesConductores(final String cgContrato, final Date fIni, final Date fFin, final int sessionID) throws Exception {
		final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sessionID);
		if (du == null) {
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
		}
		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection conn) throws UException {
				try {
					new ProcedureJdbcTemplate<Void>().execute(conn, "PRC_INFRAC_ORGANIZA_ACTIV_CONT", cgContrato, fIni, fFin);

					StringBuffer sqlDeleg = EInformeResumenActividades.this.getSQLDelegacion("CDCONDUCTORES_EMP.IDDELEGACION", du);
					String sql = new Template("sql/EInformeResumenActividades_resumenActividadesConductores.sql")
							.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#SQLDELEG#", sqlDeleg));

					EntityResult res = new QueryJdbcToEntityResultTemplate().execute(conn, sql,
							EInformeResumenActividades.this.getQueryParameters(du, new java.sql.Date(fIni.getTime()), new java.sql.Date(fFin.getTime()), cgContrato));

					res = new EntityResult(EInformeResumenActividades.this.replaceColumnByAlias(res));

					// <!-- TODO: IDCONDUCTOR-DNI -->
					res = EInformeResumenActividades.this.pivotarResultEntidad(res, "IDCONDUCTOR",
							new String[] { "CONDUCCION", "TRABAJO", "DISPONIBILIDAD", "DESCANSO", "INDEFINIDA" },
							new String[] { "T_CONDUCCION", "T_TRABAJO", "T_DISPONIBILIDAD", "T_DESCANSO", "T_INDEFINIDA" },
							new String[] { "CONDUCCION", "TRABAJO", "DISPONIBILIDAD", "DESCANSO", "INDEFINIDA" });

					// res = EInformeResumenActividades.this.pivotarResultEntidad(res, "DNI",
					// new String[] { "CONDUCCION", "TRABAJO", "DISPONIBILIDAD", "DESCANSO", "INDEFINIDA" },
					// new String[] { "T_CONDUCCION", "T_TRABAJO", "T_DISPONIBILIDAD", "T_DESCANSO", "T_INDEFINIDA" },
					// new String[] { "CONDUCCION", "TRABAJO", "DISPONIBILIDAD", "DESCANSO", "INDEFINIDA" });

					EInformeResumenActividades.this.translate(res, sessionID);

					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.manager, false);
	}

	@Override
	public EntityResult consultarResumenFicherosCond(final String cgContrato, final Date fIni, final Date fFin, final int sessionID) throws Exception {
		final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sessionID);
		if (du == null) {
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
		}
		return new OntimizeConnectionTemplate<EntityResult>() {
			@Override
			protected EntityResult doTask(Connection con) throws UException {
				try {
					StringBuffer sqlDeleg = EInformeResumenActividades.this.getSQLDelegacion("IDDELEGACION", du);
					String sql = new Template("sql/EInformeResumenActividades_resumenFicherosCond.sql")
							.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#SQLDELEG#", sqlDeleg));
					EntityResult res = new QueryJdbcToEntityResultTemplate().execute(con, sql, EInformeResumenActividades.this.getQueryParameters(du, cgContrato));
					res = new EntityResult(EInformeResumenActividades.this.replaceColumnByAlias(res));
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.manager, false);
	}

	@Override
	public EntityResult consultarResumenFicherosVehi(final String cgContrato, final Date fIni, final Date fFin, final int sessionID) throws Exception {
		final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sessionID);
		if (du == null) {
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
		}

		return new OntimizeConnectionTemplate<EntityResult>() {
			@Override
			protected EntityResult doTask(Connection con) throws UException {
				try {
					StringBuffer sqlDeleg = EInformeResumenActividades.this.getSQLDelegacion("IDDELEGACION", du);
					String sql = new Template("sql/EInformeResumenActividades_resumenFicherosVehi.sql")
							.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#SQLDELEG#", sqlDeleg));
					EntityResult res = new QueryJdbcToEntityResultTemplate().execute(con, sql, EInformeResumenActividades.this.getQueryParameters(du, cgContrato));

					res = new EntityResult(EInformeResumenActividades.this.replaceColumnByAlias(res));
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.manager, false);
	}

	@Override
	public EntityResult consultarResumenIncidentes(final String cgContrato, final Date fIni, final Date fFin, final int sessionID) throws Exception {

		final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sessionID);
		if (du == null) {
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
		}
		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection con) throws UException {
				try {

					StringBuffer sqlDeleg = EInformeResumenActividades.this.getSQLDelegacion("V.IDDELEGACION", du);
					String sql = new Template("sql/EInformeResumenActividades_resumenIncidentes.sql")
							.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#SQLDELEG#", sqlDeleg));
					java.sql.Date sFIni = new java.sql.Date(fIni.getTime());
					java.sql.Date sFFin = new java.sql.Date(fFin.getTime());
					EntityResult res = new QueryJdbcToEntityResultTemplate().execute(con, sql,
							EInformeResumenActividades.this.getQueryParameters(du, cgContrato, sFIni, sFFin, sFFin, sFIni));
					res = new EntityResult(EInformeResumenActividades.this.replaceColumnByAlias(res));
					String sExcVel = "Minutos_exceso_vel";
					String sSinTarj = "Horas_sin_tarjeta";
					String sSumElec = "Horas_sin_sum_elect";
					String sHorasTot = "Horas_totales";
					String sOtrasInc = "Horas_otras_incid";
					sExcVel = ResourceManager.translate(du.getLocale(), sExcVel);
					sSinTarj = ResourceManager.translate(du.getLocale(), sSinTarj);
					sSumElec = ResourceManager.translate(du.getLocale(), sSumElec);
					sHorasTot = ResourceManager.translate(du.getLocale(), sHorasTot);
					sOtrasInc = ResourceManager.translate(du.getLocale(), sOtrasInc);
					res = EInformeResumenActividades.this.pivotarResultEntidad(res, "MATRICULA",
							new String[] { "HORAS_EXCESO_VEL", "HORAS_COND_SIN_TARJ", "HORAS_SIN_SUM_ELEC", "HORAS_TOTALES", "HORAS_OTRAS_INCI" }, null,
							new String[] { sExcVel, sSinTarj, sSumElec, sHorasTot, sOtrasInc });
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.manager, false);

	}

	@Override
	public EntityResult consultarInformeLaboral(final String cgContrato, final Object idCond, final Date fIni, final Date fFin, final int sessionID) throws Exception {
		final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sessionID);
		if (du == null) {
			return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
		}

		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection conn) throws UException {
				try {
					java.sql.Date dIni = new java.sql.Date(DateUtil.trunc(DateUtil.addDays(fIni, -1)).getTime());
					java.sql.Date dFin = new java.sql.Date(DateUtil.truncToEnd(fFin).getTime());

					StringBuffer sbCond = new StringBuffer();
					if (idCond instanceof String) {
						sbCond.append(" = '").append(idCond).append("'");
					} else if (idCond instanceof SearchValue) {
						SearchValue vb = (SearchValue) idCond;
						if ((vb.getCondition() == SearchValue.IN) || (vb.getCondition() == SearchValue.OR)) {
							Vector<Object> v = (Vector<Object>) vb.getValue();
							sbCond.append(" IN ( ");
							for (int i = 0; i < v.size(); i++) {
								sbCond.append("'").append(v.get(i)).append("'");
								if (i != (v.size() - 1)) {
									sbCond.append(',');
								}
							}
							sbCond.append(")");
						}
					}
					final String sql = new Template("sql/EInformeResumenActividades_informeLaboral.sql")
							.fillTemplate(MapTools.newMap(new HashMap<String, String>(), "#SQLCOND#", sbCond.toString()));
					Object realCgContrato = ContractUtils.checkContratoFicticio(EInformeResumenActividades.this.getLocator(), cgContrato, sessionID, conn);
					EntityResult res = new QueryJdbcToEntityResultTemplate().execute(conn, sql, realCgContrato, dIni, dFin, dIni, dIni, dFin, dFin, dIni, realCgContrato,
							realCgContrato, dIni, dFin, dIni, dIni, dFin, dFin, dIni, realCgContrato, realCgContrato, dIni, dFin, dIni, dIni, dFin, dFin, dIni, realCgContrato);

					res = new EntityResult(EInformeResumenActividades.this.replaceColumnByAlias(res));
					EInformeResumenActividades.this.translate(res, sessionID);
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.manager, false);
	}
}