package com.opentach.server.certificadoActividades;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.certificadoActividades.ICertificadoActividadesService;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

/**
 * The Class DowncenterReportGenerator.
 */
public class CertificadoActividadesService extends UAbstractService implements ICertificadoActividadesService {

	/** The Constant logger. */
	private static final Logger	logger		= LoggerFactory.getLogger(CertificadoActividadesService.class);

	public CertificadoActividadesService(int port, EntityReferenceLocator locator, Hashtable params) throws Exception {
		super(port, locator, params);
	}

	@Override
	public BytesBlock generateCertificadoActividadesServiceReport(Hashtable<String,String> parameters, Locale locale, int sessionId) throws Exception {

		return new OntimizeConnectionTemplate<BytesBlock>() {

			@Override
			protected BytesBlock doTask(Connection con) throws UException, SQLException {
				try {
					return CertificadoActividadesService.this.generateCertificadoActividadesReport(parameters,locale, sessionId, con);
				} catch (UException err) {
					CertificadoActividadesService.logger.error(null, err);
					throw err;
				} catch (Exception err) {
					CertificadoActividadesService.logger.error(null, err);
					throw new UException(err.getMessage(), err);
				}
			}

		}.execute(this.getConnectionManager(), true);

	}

	private BytesBlock generateCertificadoActividadesReport(Hashtable<String, String> parameters, Locale locale, int sessionId, Connection con) throws Exception {
		JasperReport jr = null;
		try {
			jr = JRReportUtil.getJasperReport("activitycert/" + locale + "/CertificadoActividades.jasper");
		} catch (Exception err) {
			CertificadoActividadesService.logger.error(null, err);
			jr = JRReportUtil.getJasperReport("activitycert/es_ES/CertificadoActividades.jasper");
		}

		EntityResult res = new EntityResult();
		res.addRecord(parameters);
		jr.setProperty("net.sf.jasperreports.export.text.isInputForm", "true");
		JasperPrint jp = JasperFillManager.fillReport(jr, null, new JRTableModelDataSource(EntityResultUtils.createTableModel(res)));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRReportUtil.exportToPDF(jp, baos);
		BytesBlock bb = new BytesBlock(baos.toByteArray());
		CertificadoActividadesService.logger.trace("finished");
		baos.close();
		return bb;
	}
}
