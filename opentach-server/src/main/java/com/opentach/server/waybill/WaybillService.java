package com.opentach.server.waybill;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.util.ResourceManager;
import com.opentach.common.waybill.IWaybillService;
import com.opentach.common.waybill.WaybillType;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.SecurityTools;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class WaybillService extends UAbstractService implements IWaybillService {

	private static final Logger	logger					= LoggerFactory.getLogger(WaybillService.class);

	private static final String	CERTPDFPATH				= "waybill/CP General1.pdf";
	private static final String	CERTPDFPATHREV			= "waybill/CP General2.pdf";
	private static final String	CERTPDFPATHBULTOS		= "waybill/CP bultos.pdf";
	private static final String	CERT_BASE_PDFPATH		= "waybill/";
	private static final String	CERT_CARTA_PORTE		= "CartaPorte";
	private static final String	CERT_PDF_NAME			= "CP General1";
	private static final String	CERT_PDF_NAME_REV		= "CP General2";
	private static final String	CERT_PDF_NAME_BULTOS	= "CP bultos";

	public WaybillService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public BytesBlock generateWaybill(WaybillType type, Hashtable<String, String> values, String cif, Locale locale, int sessionId) throws Exception {
		try {
			SecurityTools.checkPermissions((IOpentachServerLocator) this.getLocator(), cif, sessionId);
			// Todo check cif valid for sessionid
			BytesBlock reportFile = this.buildWaybill(type, values, locale, sessionId);
			if (!("CARTA_PORTE".equals(type.name()))) {
				this.saveReport(reportFile,type, locale, values, cif, sessionId);
			}
			return reportFile;
		} catch (Exception error) {
			WaybillService.logger.error(null, error);
			throw new Exception("M_PROCESO_INCORRECTO_GENERANDO_INFORME");
		}
	}

	private BytesBlock buildWaybill(WaybillType type, Hashtable<String, String> values, Locale locale, int sessionId) throws Exception, IOException, FileNotFoundException {
		String name = null;
		switch (type) {
		case CARTA_PORTE:
			name = WaybillService.CERT_CARTA_PORTE;
			break;
			case ADR_GENERAL_1:
				name = WaybillService.CERT_PDF_NAME;
				break;
			case ADR_GENERAL_2:
				name = WaybillService.CERT_PDF_NAME_REV;
				break;
			case ADR_BULTOS:
				name = WaybillService.CERT_PDF_NAME_BULTOS;
				break;
			default:
				throw new Exception("E_INVALID_TYPE");
		}
		File fTemporal = File.createTempFile(name, ".pdf");
		// llamar al informe jasper
		BytesBlock bb = generateADRReport(values,name, locale, sessionId);
		FileTools.copyFile(new ByteArrayInputStream(bb.getBytes()), fTemporal);
		return bb;
	}
	
	
	private BytesBlock generateADRReport(Hashtable values, String name, Locale locale, int sessionId) throws Exception {
		
		
		JasperReport jr = JRReportUtil.getJasperReport("waybill/"+locale+"/"+ name +".jasper");
		values.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle rb = ResourceManager.getBundle(locale);
		values.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
		
		EntityResult res = new EntityResult();
		res.addRecord(values);

		JasperPrint jp = JasperFillManager.fillReport(jr, null, new JRTableModelDataSource(EntityResultUtils.createTableModel(res)));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRReportUtil.exportToPDF(jp, baos);
		BytesBlock bb = new BytesBlock(baos.toByteArray());
		WaybillService.logger.trace("finished");
		baos.close();
		return bb;
	}
	
	

	private void saveReport(BytesBlock bb, WaybillType type, Locale locale, Hashtable<String, String> parameters, String cif, int sessionId) throws Exception {

		Hashtable<String, Object> av = new Hashtable<>();
		av.put("CIF", cif);
		av.put("WAY_TYPE", type.toString());
		av.put("WAY_LOCALE", locale.toLanguageTag());
		av.put("WAY_DATA", new String(bb.getBytes(), "UTF-8"));
		new OntimizeConnectionTemplate<Void>() {

			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					WaybillService.this.getEntity("waybill.EWaybill").insert(av, sessionId, con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	private InputStream getTemplateStream(String path, String name, Locale locale) {
		InputStream is = null;
		String completePathAux = path + locale + "/" + name + ".pdf";
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(completePathAux);
		} catch (Exception ex) {
		}
		return is;
	}

	@XmlRootElement
	public static class ReportData {
		Hashtable<String, String> data;

		public ReportData() {
			super();

		}

		public ReportData(Hashtable<String, String> data) {
			this.data = data;
		}

		public Hashtable<String, String> getData() {
			return this.data;
		}

		public void setData(Hashtable<String, String> data) {
			this.data = data;
		}
	}

}
