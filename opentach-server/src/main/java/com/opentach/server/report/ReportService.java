package com.opentach.server.report;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.report.IOpentachReportService;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.util.ResourceManager;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class ReportService extends UAbstractService implements IReportService, IOpentachReportService {
	/** The Constant logger. */
	private static final Logger				logger	= LoggerFactory.getLogger(ReportService.class);

	private final ReportHelper				reportHelper;
	private final ManagementReportHelper	managementReportHelper;
	private final MobileExpressReportHelper	mobileExpressReportHelper;
	private final JRPropertyManager			jpm;

	/**
	 *
	 * @param locator
	 *            the locator
	 */
	public ReportService(int port, EntityReferenceLocator locator, Hashtable params) throws Exception {
		super(port, locator, params);
		this.jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		this.reportHelper = new ReportHelper((IOpentachServerLocator) this.getLocator());
		this.managementReportHelper = new ManagementReportHelper((IOpentachServerLocator) this.getLocator());
		this.mobileExpressReportHelper = new MobileExpressReportHelper((IOpentachServerLocator) this.getLocator());
	}

	public JRPropertyManager getReportPropertymanager() {
		return this.jpm;
	}

	@Override
	public JasperPrint fillReport(String urlJR, Map<String, Object> params, String after, String before, EntityResult res, int sessionID) throws Exception {
		try {
			JasperReport jr = JRReportUtil.getJasperReport(urlJR);
			JasperPrint jasperPrint = this.fillReport(jr, res, params, after, before, sessionID);
			return jasperPrint;
		} catch (Exception ex) {
			ReportService.logger.error(null, ex);
			throw ex;
		}
	}

	private JasperPrint fillReport(final JasperReport jr, final EntityResult res, final Map<String, Object> params, final String after, final String before, final int sessionID)
			throws Exception {

		return new OntimizeConnectionTemplate<JasperPrint>() {

			@Override
			protected JasperPrint doTask(Connection con) throws UException {
				try {
					return ReportService.this.fillReport(jr, res, params, after, before, sessionID, con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	private JasperPrint fillReport(final JasperReport jr, final EntityResult res, final Map<String, Object> params, final String after, final String before, final int sessionID,
			Connection con) throws Exception {

		Locale locale = (Locale) params.get(IJRConstants.JRLOCALE);
		if (locale == null) {
			locale = ((IOpentachServerLocator) this.getLocator()).getUserLocale(sessionID);
		}
		params.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle rb = ResourceManager.getBundle(locale);
		if (rb != null) {
			params.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
		}
		String title = (String) params.get(IJRConstants.JRTITLE);
		if (title != null) {
			params.put(IJRConstants.JRTITLE, ResourceManager.translate(locale, title));
		}
		params.put("SUBREPORT_DIR", "com/opentach/reports/");
		JasperPrint jp = ReportService.this.reportHelper.fillReport(jr, params, after, before, res, con, sessionID);
		return jp;
	}

	@Override
	public JasperPrint generaInformeExpressCond(String cif, String empresa, String cgContrato, String idconductor, String nombreConductor, String dni, Date fdesde, Date fhasta,
			int sessionID) throws Exception {
		final JRReportDescriptor jrd = this.jpm.getDataMap().get(13);
		final List<JRReportDescriptor> lReports = jrd.getLReports();
		final List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		final Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put(IJRConstants.JRCGCONTRATO, cgContrato);
		mParams.put(IJRConstants.JRCIF, cif);
		mParams.put(IJRConstants.JRCOMPANY, empresa);
		mParams.put(IJRConstants.JRTITLE, ResourceManager.translate(((IOpentachServerLocator) this.getLocator()).getUserLocale(sessionID), "EXPRESSREPORTCOND"));
		mParams.put(IJRConstants.JRDESDE, new Timestamp(fdesde.getTime()));
		Calendar calhasta = Calendar.getInstance();
		calhasta.setTime(fhasta);
		calhasta.set(Calendar.HOUR_OF_DAY, 23);
		calhasta.set(Calendar.MINUTE, 59);
		calhasta.set(Calendar.SECOND, 59);
		mParams.put(IJRConstants.JRHASTA, new Timestamp(calhasta.getTime().getTime()));
		mParams.put(IJRConstants.JRIDCONDUCTOR, idconductor);
		mParams.put(IJRConstants.JRCONDUCTOR, nombreConductor);
		mParams.put(IJRConstants.JRDNI, dni);

		JRReportDescriptor jru = lReports.get(0);
		Map<String, Object> p = mParams;
		JasperPrint jp = this.fillReport(jru.getUrl(), p, jru.getMethodAfter(), jru.getMethodBefore(), null, sessionID);
		if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
			return jp;
		}
		if (jpList.size() <= 0) {
			throw new Exception("M_NO_SE_HAN_ENCONTRADO_DATOS");
		}
		return null;
	}

	@Override
	public JasperPrint generaInformeExpressVeh(String cif, String empresa, String cgContrato, String matricula, Date fdesde, Date fhasta, int sessionID) throws Exception {
		final JRReportDescriptor jrd = this.jpm.getDataMap().get(15);
		final List<JRReportDescriptor> lReports = jrd.getLReports();
		final List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		final Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put(IJRConstants.JRCGCONTRATO, cgContrato);
		mParams.put(IJRConstants.JRCIF, cif);
		mParams.put(IJRConstants.JRCOMPANY, empresa);
		mParams.put(IJRConstants.JRTITLE, ResourceManager.translate(((IOpentachServerLocator) this.getLocator()).getUserLocale(sessionID), "EXPRESSREPORTVEH"));
		mParams.put(IJRConstants.JRDESDE, new Timestamp(fdesde.getTime()));
		Calendar calhasta = Calendar.getInstance();
		calhasta.setTime(fhasta);
		calhasta.set(Calendar.HOUR_OF_DAY, 23);
		calhasta.set(Calendar.MINUTE, 59);
		calhasta.set(Calendar.SECOND, 59);
		mParams.put(IJRConstants.JRHASTA, new Timestamp(calhasta.getTime().getTime()));
		mParams.put(IJRConstants.JRMATRICULA, matricula);

		JRReportDescriptor jru = lReports.get(0);
		Map<String, Object> p = mParams;
		JasperPrint jp = this.fillReport(jru.getUrl(), p, jru.getMethodAfter(), jru.getMethodBefore(), null, sessionID);
		if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
			return jp;
		}
		if (jpList.size() <= 0) {
			throw new Exception("M_NO_SE_HAN_ENCONTRADO_DATOS");
		}
		return null;
	}

	@Override
	public JasperPrint generaInformeConsultaFicheros(String usuario_alta, Date fdesde, Date fhasta, int sessionID) throws Exception {
		final JRReportDescriptor jrd = this.jpm.getDataMap().get(17);
		final List<JRReportDescriptor> lReports = jrd.getLReports();
		final List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		final Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put(IJRConstants.JRCUSUARIO_ALTA, usuario_alta);
		mParams.put(IJRConstants.JRTITLE, "Informe Consulta Ficheros");
		mParams.put(IJRConstants.JRDESDE, new Timestamp(fdesde.getTime()));
		mParams.put(IJRConstants.JRHASTA, new Timestamp(fhasta.getTime()));

		JRReportDescriptor jru = lReports.get(0);
		Map<String, Object> p = mParams;
		JasperPrint jp = this.fillReport(jru.getUrl(), p, jru.getMethodAfter(), jru.getMethodBefore(), null, sessionID);
		if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
			return jp;
		}
		if (jpList.size() <= 0) {
			throw new Exception("M_NO_SE_HAN_ENCONTRADO_DATOS");
		}
		return null;
	}

	@Override
	public void generateManagementReport(String cif, String empresa, String cgContrato, String sCorreo, Date dInforme, EngineAnalyzer analyzer, Locale locale, boolean send,
			int sessionID) throws Exception {
		this.managementReportHelper.generateManagementReport(cif, empresa, cgContrato, sCorreo, dInforme, analyzer, locale, send, sessionID);
	}

	public void generateAndSendMobileExpressReport(String idfichero, String[] additionalEmails) throws Exception {
		this.mobileExpressReportHelper.generateAndSendMobileExpressReport(idfichero, additionalEmails);
	}

	public Map<String, Object> generateMobileExpressReport(String cif, String idDriver, Date analizeFrom, Date analizeTo) throws Exception {
		return this.mobileExpressReportHelper.generateMobileExpressReport(cif, idDriver, analizeFrom, analizeTo);
	}

}
