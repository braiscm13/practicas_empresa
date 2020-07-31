package com.opentach.server.report;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.reports.beantools.UsosVehiculoBean;
import com.opentach.common.util.DateUtil;
import com.opentach.common.util.StringUtils;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.InfractionService;
import com.opentach.server.entities.EInformeActivCond;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.BeanPropertyRowMapper;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.INameConverter;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class ReportHelper extends AbstractDelegate implements IJRConstants {

	private static final Logger					logger	= LoggerFactory.getLogger(ReportHelper.class);
	private static final Map<String, Method>	mMethods;

	static {
		Map<String, Method> amMethods = new HashMap<String, Method>();
		Method[] mt = ReportHelper.class.getDeclaredMethods();
		for (int i = 0; i < mt.length; i++) {
			amMethods.put(mt[i].getName(), mt[i]);
		}
		mMethods = Collections.unmodifiableMap(amMethods);
	}

	public ReportHelper(IOpentachServerLocator iReq) {
		super(iReq);
	}

	protected void beforeActividades(Map<String, Object> cv, Connection con, Integer sesionId) throws Exception {
		Vector<Object> vDrivers = (Vector<Object>) cv.get("DRIVER_FILTER");
		Object idConductor = cv.get(IJRConstants.JRIDCONDUCTOR);
		Object numreq = cv.get(IJRConstants.JRNUMREQ);
		Date fFin = this.calculateActividadesFfin(cv);
		Date fIni = this.calculateActividadesFini(cv, fFin);
		numreq = ContractUtils.checkContratoFicticio(this.getLocator(), numreq, sesionId, con);
		if ((idConductor == null) && (vDrivers == null)) {
			// Esto debería pasar sólo en el informe gestor o informes de empresa
			new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_CONT", numreq, new java.sql.Timestamp(fIni.getTime()),
					new java.sql.Timestamp(fFin.getTime()));
		} else {
			Vector<Object> vIdDrivers = vDrivers != null ? vDrivers : EInformeActivCond.getDrivers(idConductor, true);
			String sDrivers = StringUtils.vectorToCommaSeparated(vIdDrivers);
			new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", numreq, sDrivers, new java.sql.Timestamp(fIni.getTime()),
					new java.sql.Timestamp(fFin.getTime()));
		}
	}

	private Date calculateActividadesFini(Map<String, Object> cv, Date ffin) {
		Date fIni = (Date) cv.get("f_inicio");
		if (fIni == null) {
			fIni = (Date) cv.get("f_desde");
		}

		if (fIni == null) {
			fIni = DateUtil.addDays(ffin, -30);
		}
		return fIni;
	}

	protected Date calculateActividadesFfin(Map<String, Object> cv) {
		Date fReport = (Date) cv.get(IJRConstants.JRDREPORT);
		Date ffin = (Date) cv.get("f_fin");
		if (ffin == null) {
			ffin = (Date) cv.get("f_hasta");
		}
		if (ffin == null) {
			ffin = fReport;
		}
		return ffin;
	}

	protected void beforeActividadesExpress(Map<String, Object> cv, Connection con, Integer sesionId) throws Exception {

		Object numreq = cv.get(IJRConstants.JRCGCONTRATO);
		numreq = ContractUtils.checkContratoFicticio(this.getLocator(), numreq, sesionId, con);

		Date fFin = (Date) cv.get(IJRConstants.JRHASTA);
		Date fIni = (Date) cv.get(IJRConstants.JRDESDE);
		Object idConductor = cv.get(IJRConstants.JRIDCONDUCTOR);

		Vector<Object> vIdDrivers = EInformeActivCond.getDrivers(idConductor, true);
		String sDrivers = StringUtils.vectorToCommaSeparated(vIdDrivers);

		new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", numreq, sDrivers, new java.sql.Timestamp(fIni.getTime()),
				new java.sql.Timestamp(fFin.getTime()));

	}

	protected void afterActividades(Map<String, Object> cv, Connection con, Integer sessionID) throws Exception {
		// do nothing
	}

	protected void beforeUsoVehiculoVehiculo(Map<String, Object> cv, Connection con, Integer sessionID) throws Exception {
		// do nothing
	}

	protected void afterUsoVehiculoVehiculo(Map<String, Object> cv, Connection con, Integer sessionID) throws Exception {
		// do nothing
	}

	protected void beforeInfraccionesExpress(Map<String, Object> cv, Connection conn, Integer sessionID) throws Exception {
		Object cgContrato = cv.get(IJRConstants.JRCGCONTRATO);
		cgContrato = ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sessionID, conn);
		Object cif = cv.get(IJRConstants.JRCIF);
		Object idconductor = cv.get(IJRConstants.JRIDCONDUCTOR);
		Date fFin = (Date) cv.get(IJRConstants.JRHASTA);
		Date fIni = (Date) cv.get(IJRConstants.JRDESDE);

		this.getLocator().getService(InfractionService.class).analyzeForReport(conn, cgContrato, cif, Arrays.asList(new Object[] { idconductor }), fIni, fFin,
				(EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER), sessionID);

	}

	protected void beforeInfracciones(Map<String, Object> cv, Connection conn, Integer sessionID) throws Exception {
		Object numreq = cv.get(IJRConstants.JRNUMREQ);
		numreq = ContractUtils.checkContratoFicticio(this.getLocator(), numreq, sessionID, conn);
		Date fReport = (Date) cv.get(IJRConstants.JRDREPORT);
		Date fIni = (Date) cv.get("f_desde");
		Date fFin = (Date) cv.get("f_hasta");
		Object cif = cv.get(IJRConstants.JRCIF);

		if (fFin == null) {
			fFin = fReport;
		}
		if (fIni == null) {
			fIni = DateUtil.addDays(fFin, -30);
		}

		// Consutar conductores de la empresa
		String sql = new Template("sql/ReportHelperQueryCompanyDrivers.sql").getTemplate();
		List<Object> vDrivers = new QueryJdbcTemplate<Vector<Object>>() {
			@Override
			protected Vector<Object> parseResponse(ResultSet rs) throws UException {
				try {
					Vector<Object> vDrivers = new Vector<Object>();
					while (rs.next()) {
						vDrivers.add(rs.getString("IDCONDUCTOR"));
					}
					return vDrivers;

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql, numreq, cif);

		this.getLocator().getService(InfractionService.class).analyzeForReport(conn, numreq, cif, vDrivers, fIni, fFin, (EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER),
				sessionID.intValue());
	}

	protected void beforeTiemposCond(Map<String, Object> cv, Connection conn, Integer sessionID) throws Exception {
		new ReportHelperDriverRestAndDrivings().help(cv, conn, sessionID, this.getLocator());
	}

	protected void beforeInfraccionesCond(Map<String, Object> cv, Connection conn, Integer sessionID) throws Exception {

		Date fIni = (Date) cv.get("f_inicio");
		Date fFin = (Date) cv.get("f_fin");
		Object numreq = cv.get(IJRConstants.JRNUMREQ);
		Object cif = cv.get(IJRConstants.JRCIF);
		numreq = ContractUtils.checkContratoFicticio(this.getLocator(), numreq, sessionID, conn);
		Vector<Object> vDrivers = (Vector<Object>) cv.get("DRIVER_FILTER");
		Object idConductor = cv.get(IJRConstants.JRIDCONDUCTOR);
		if ((vDrivers == null) && (idConductor == null)) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		boolean checkLimit = vDrivers != null;
		List<Object> vIdDrivers = vDrivers != null ? vDrivers : EInformeActivCond.getDrivers(idConductor, true);

		this.getLocator().getService(InfractionService.class).analyzeForReport(conn, numreq, cif, vIdDrivers, fIni, fFin,
				(EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER), sessionID.intValue());
	}

	protected void beforeKmRecorridosCond(Map<String, Object> cv, Connection con, Integer sessionID) throws Exception {
		Vector<Object> vidConductor = (Vector<Object>) cv.get("vconductor");
		if ((vidConductor == null)) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}

		cv.remove("FEC_COMIENZO");
		for (int j = 0; j < vidConductor.size(); j++) {
			new ProcedureJdbcTemplate<Void>().execute(con, "PROC_CDUSO_TARJETA", vidConductor.get(j), cv.get("numreq"), cv.get("f_inicio"), cv.get("f_fin"));
		}
	}

	protected void afterKmRecorridosCond(Map<String, Object> cv, Connection con, Integer sessionID) throws Exception {
		// do nothing
	}

	protected void afterInfracciones(Map<String, Object> cv, Connection conn, Integer sessionID) throws Exception {
		// do nothing
	}

	protected void beforeVehiculosPorConductor(Map<String, Object> cv, Connection con, Integer sessionID) throws Exception {
		Object idContract = cv.get("numreq");
		String idDriver = (String) cv.get("idconductor");
		Object beginDate = cv.get("f_inicio");
		Object endDate = cv.get("f_fin");
		idDriver = idDriver == null ? "" : idDriver;
		Map<String, String> temMap = new HashMap<>();
		temMap.put("#IDCONDUCTOR# ", idDriver);
		String sql = new Template("sql/EInformeUsoVehiculo.sql").fillTemplate(temMap);

		List<UsosVehiculoBean> dataList = new QueryJdbcTemplate<List<UsosVehiculoBean>>() {

			@Override
			protected List<UsosVehiculoBean> parseResponse(ResultSet rs) throws UException {
				List<UsosVehiculoBean> res = new ArrayList<UsosVehiculoBean>();
				BeanPropertyRowMapper<UsosVehiculoBean> rowMapper = new BeanPropertyRowMapper<UsosVehiculoBean>(new INameConverter() {

					@Override
					public String convertToDb(Class<?> beanClass, String beanProperty) {
						return beanProperty;
					}

					@Override
					public String convertToBean(Class<?> beanClass, String dbColumn) {
						return dbColumn;
					}
				}, UsosVehiculoBean.class);

				try {
					int i = 0;
					while (rs.next()) {
						res.add(rowMapper.mapRow(rs, i));
						i++;
					}
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(con, sql, idContract, beginDate, endDate);
		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataList);
		Map<String, List<UsosVehiculoBean>> map = this.splitByDriver(dataList);
		cv.put(JRParameter.REPORT_DATA_SOURCE, ds);
		cv.put("BEAN_MAP", map);
	}

	private Map<String, List<UsosVehiculoBean>> splitByDriver(List<UsosVehiculoBean> dataList) {
		HashMap<String, List<UsosVehiculoBean>> res = new HashMap<>();
		for (UsosVehiculoBean bean : dataList) {
			String idconductor = bean.getIDCONDUCTOR();
			List<UsosVehiculoBean> list = res.get(idconductor);
			if (list == null) {
				list = new ArrayList<UsosVehiculoBean>();
				res.put(idconductor, list);
			}
			list.add(bean);
		}
		return res;
	}

	/**
	 * Fill report.
	 *
	 * @param jr
	 *            the jr
	 * @param params
	 *            the params
	 * @param after
	 *            the after
	 * @param before
	 *            the before
	 * @param rs
	 *            the rs
	 * @param conn
	 *            the conn comes with setAutocommit to false
	 * @param sessionID
	 *            the session id
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	public JasperPrint fillReport(JasperReport jr, Map<String, Object> params, String after, String before, EntityResult rs, Connection conn, int sessionID) throws Exception {
		JasperPrint jp = null;
		if (before != null) {
			if (before.contains(";")) {
				StringTokenizer st = new StringTokenizer(before, ";");
				while (st.hasMoreTokens()) {
					String methodName = st.nextToken();
					Method mtBefore = ReportHelper.mMethods.get(methodName);
					try {
						mtBefore.invoke(this, new Object[] { params, conn, sessionID });
					} catch (InvocationTargetException e) {
						throw (Exception) e.getCause();
					}
				}
			} else {
				Method mtBefore = ReportHelper.mMethods.get(before);
				try {
					mtBefore.invoke(this, new Object[] { params, conn, sessionID });
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof Exception) {
						throw (Exception) e.getCause();
					}
					throw e;
				}
			}
		}
		if (rs == null) {
			jp = JRReportUtil.fillReport(jr, params, conn);
		} else {
			jp = JasperFillManager.fillReport(jr, params, new JRTableModelDataSource(EntityResultUtils.createTableModel(rs)));
		}
		return jp;
	}
}
