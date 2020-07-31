package com.opentach.server.report;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailFields;
import com.opentach.server.mail.MailService;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.db.OracleTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JasperPrint;

public class ManagementReportHelper extends AbstractDelegate {

	private static final Logger logger = LoggerFactory.getLogger(ManagementReportHelper.class);

	public ManagementReportHelper(IOpentachServerLocator opentachServerLocator) {
		super(opentachServerLocator);
	}

	public void generateManagementReport(String cif, String empresa, String cgContrato, String sCorreo, Date dInforme, EngineAnalyzer analyzer, Locale locale, boolean send,
			int sessionID) throws Exception {
		ReportService reportService = this.getService(ReportService.class);

		final JRReportDescriptor jrd = reportService.getReportPropertymanager().getDataMap().get(11);
		final List<JRReportDescriptor> lReports = jrd.getLReports();
		final List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		final Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put(IJRConstants.JRNUMREQ, cgContrato);
		mParams.put(IJRConstants.JRCIF, cif);
		mParams.put(IJRConstants.JRCOMPANY, empresa);
		mParams.put(IJRConstants.JRTITLE, "Informe_gestor");
		mParams.put("ppagenumber", false);
		mParams.put(IJRConstants.JRDREPORT, new Timestamp(dInforme.getTime()));
		mParams.put(IInfractionService.ENGINE_ANALYZER, analyzer);
		if (sCorreo != null) {
			mParams.put(IJRConstants.JRSEND, sCorreo);
		}
		if (locale != null) {
			mParams.put(IJRConstants.JRLOCALE, locale);
		}
		for (int i = 0; i < lReports.size(); i++) {
			JRReportDescriptor jru = lReports.get(i);
			Map<String, Object> p = mParams;
			if (i != 0) {
				p.put(IJRConstants.JRTITLE, jru.getDscr());
			}
			JasperPrint jp = reportService.fillReport(jru.getUrl(), p, jru.getMethodAfter(), jru.getMethodBefore(), null, sessionID);
			if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
				jpList.add(jp);
			}
		}
		if (jpList.size() <= 1) {
			throw new Exception("M_NO_SE_HAN_ENCONTRADO_DATOS");
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		File f = null;
		try {
			f = File.createTempFile("InformeGestor.pdf", ".pdf");
			fos = new FileOutputStream(f);
			bos = new BufferedOutputStream(fos);
			JRReportUtil.exportToPDF(jpList, bos);
			bos.flush();
			bos.close();
			fos.close();
			this.saveInformeGestor(f, cgContrato, dInforme, sessionID);
			if (send) {
				mParams.put(IGMailComposer.COMPANY, empresa);
				mParams.put(IGMailComposer.REPORTTITLE, "Informe Gestor");
				mParams.put(IGMailComposer.REPORTDATE, dInforme);
				this.sendMail(f, locale, mParams, sessionID);
			}
		} catch (Exception e) {
			ManagementReportHelper.logger.error(null, e);
			throw e;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
			if (f != null) {
				try {
					f.delete();
				} catch (Exception e) {
				}
			}
		}
	}

	public void saveInformeGestor(final File f, final String cgContrato, final Date dInforme, final int sessionID) throws Exception {
		new OntimizeConnectionTemplate<Void>() {

			@Override
			protected Void doTask(Connection conn) throws UException {
				try {
					ManagementReportHelper.this.saveInformeGestor(f, cgContrato, dInforme, sessionID, conn);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	public void saveInformeGestor(final File f, final String cgContrato, final Date dInforme, final int sessionID, Connection conn) throws Exception {
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
		av.put("F_INFORME", dInforme);
		OracleTableEntity eInformeGestor = (OracleTableEntity) this.getEntity("EInformeGestor");
		EntityResult res = eInformeGestor.insert(av, sessionID, conn);
		eInformeGestor.writeOracleBLOBStream(f, "INFORME", res, conn, true);
	}

	private String getAckMail(Connection conn) throws Exception {
		String opentachMail = null;
		TransactionalEntity ePrefs = this.getEntity("EPreferenciasServidor");
		final int sessionID = this.getEntityPrivilegedId(ePrefs);
		Vector<Object> vq = new Vector<Object>(1);
		vq.add(EPreferenciasServidor.COLUMNA_VALOR);
		Hashtable<String, Object> htq = new Hashtable<String, Object>();
		htq.put(EPreferenciasServidor.COLUMNA_NOMBRE, MailFields.MAIL_REQADDRESS);
		EntityResult erPref = ePrefs.query(htq, vq, sessionID, conn);
		final int count = erPref.calculateRecordNumber();
		if ((erPref.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (count == 1)) {
			Vector<Object> v = (Vector<Object>) erPref.get(EPreferenciasServidor.COLUMNA_VALOR);
			opentachMail = (String) v.firstElement();
		}
		return opentachMail;
	}

	public void sendMail(File f, Locale locale, Map<String, Object> params, int sessionID) throws Exception {

		String opentachMail = new OntimizeConnectionTemplate<String>() {

			@Override
			protected String doTask(Connection conn) throws UException {
				try {
					return ManagementReportHelper.this.getAckMail(conn);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), true);

		String mailto = (String) params.get("enviar");
		if (mailto != null) {
			try (FileInputStream fis = new FileInputStream(f)) {
				params.put(MailComposer.STREAMS, new InputStream[] { fis });
				params.put(MailComposer.FILENAMES, new String[] { "InformeGestor.pdf" });
				this.getService(MailService.class).sendMail(new IGMailComposer(mailto, opentachMail, locale, params));
			}
		}
	}

}
