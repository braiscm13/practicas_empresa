package com.opentach.server.infractionwarning;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.infractionwarning.IInfractionWarningService;
import com.opentach.common.util.ResourceManager;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.InfractionService;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

/**
 * The Class DowncenterReportGenerator.
 */
public class InfractionWarningService extends UAbstractService implements IInfractionWarningService {
	private static final String	REPORT_PATH	= "infractionwarning/carta_advertencia_";

	/** The Constant logger. */
	private static final Logger	logger		= LoggerFactory.getLogger(InfractionWarningService.class);

	public InfractionWarningService(int port, EntityReferenceLocator locator, Hashtable params) throws Exception {
		super(port, locator, params);
	}

	@Override
	public BytesBlock generateInfractionWarningReport(Object driverId, String cif, String cgContrato, Date from, Date to, int sessionId) throws Exception {

		return new OntimizeConnectionTemplate<BytesBlock>() {

			@Override
			protected BytesBlock doTask(Connection con) throws UException, SQLException {
				try {
					return InfractionWarningService.this.generateInfractionWarningReport(driverId, cif, cgContrato, from, to, sessionId, con);
				} catch (UException err) {
					InfractionWarningService.logger.error(null, err);
					throw err;
				} catch (Exception err) {
					InfractionWarningService.logger.error(null, err);
					throw new UException(err.getMessage(), err);
				}
			}

		}.execute(this.getConnectionManager(), true);

	}

	private BytesBlock generateInfractionWarningReport(Object driverId, String cif, String cgContrato, Date from, Date to, int sessionId, Connection con) throws Exception {
		CheckingTools.failIf(driverId == null, "E_DRIVER_MANDATORY", new Object[] {});
		CheckingTools.failIf(cif == null, "E_CIF_MANDATORY", new Object[] {});
		CheckingTools.failIf(cgContrato == null, "E_CONTRACT_MANDATORY", new Object[] {});
		CheckingTools.failIf(from == null, "E_BEGIN_DATE_MANDATORY", new Object[] {});
		CheckingTools.failIf(to == null, "E_ENDDATE_MANDATORY", new Object[] {});

		SimpleDateFormat sdf = new SimpleDateFormat("dd'/'MM'/'yyyy HH':'mm");

		Hashtable<String, Object> dataFields = new Hashtable<String, Object>();
		Hashtable<String, Object> tableFields = new Hashtable<String, Object>();

		EntityResult resDriver = this.getEntity("EConductorContFicticio").query(//
				EntityResultTools.keysvalues("IDCONDUCTOR", driverId, "CIF", cif), //
				EntityResultTools.attributes("DNI", "APELLIDOS", "NOMBRE"), sessionId, con);
		CheckingTools.checkValidEntityResult(resDriver, "E_DRIVER_NOT_FOUND", true, false, new Object[] {});
		Hashtable avCond = resDriver.getRecordValues(0);
		dataFields.put("CATEGORIA_COND", " ");
		dataFields.put("NIF_COND", avCond.get("DNI"));
		dataFields.put("NOMBRE_COND", ((String)avCond.get("NOMBRE")).trim()+ " " + avCond.get("APELLIDOS"));
		dataFields.put("DOMICILIO_COND", "");
		dataFields.put("COD_POSTAL_LOC_COND", " ");
		dataFields.put("PROVINCIA_COND", " ");
		Locale locale = ((IOpentachServerLocator) this.getLocator()).getUserLocale(sessionId);

		EntityResult resCompany = this.getEntity(CompanyNaming.ENTITY).query(//
				EntityResultTools.keysvalues("CIF", cif), //
				EntityResultTools.attributes("CIF", "NOMB", "DIRECCION", "CG_POSTAL", "POBL", "PROVINCIA"), sessionId, con);
		CheckingTools.checkValidEntityResult(resDriver, "E_DRIVER_NOT_FOUND", true, false, new Object[] {});
		Hashtable avCif = resCompany.getRecordValues(0);
		dataFields.put("CIF", avCif.get("CIF"));
		dataFields.put("NOMBRE_EMPRESA", avCif.get("NOMB"));
		dataFields.put("COD_POSTAL_LOCALIDAD_EMP",
				(avCif.containsKey("CG_POSTAL") ? (String) avCif.get("CG_POSTAL") : " ") + " " + (avCif.containsKey("POBL") ? (String) avCif.get("POBL") : " "));
		dataFields.put("DOMICILIO_EMPRESA", avCif.containsKey("DIRECCION") ? (String) avCif.get("DIRECCION") : "");
		dataFields.put("PROVINCIA_EMP", avCif.containsKey("PROVINCIA") ? (String) avCif.get("PROVINCIA") : "");
		dataFields.put("LOCALIDAD_EMP", avCif.containsKey("POBL") ? (String) avCif.get("POBL") : "");
		dataFields.put("F_DESDE", sdf.format(from));
		dataFields.put("F_HASTA", sdf.format(to));
		dataFields.put("DIA_HOY", new SimpleDateFormat("dd").format(new Date()));
		dataFields.put("MES_HOY", new SimpleDateFormat("MMMM", locale).format(new Date()));
		dataFields.put("ANYO_HOY", new SimpleDateFormat("yyyy").format(new Date()));

		EntityResult resInfrac = this.analyzeInfractions(cif, cgContrato, from, to, driverId, sessionId);
		int cont = resInfrac.calculateRecordNumber();
		EntityResult resInfracProcessed = new EntityResult();
		EntityResultTools.initEntityResult(resInfracProcessed, "F_INICIO", "F_FIN", "INFRACCION", "SANCION");
		InfractionWarningService.logger.trace("Found {} infractions", cont);
		DecimalFormat df = new DecimalFormat("#0.00 \u20ac");
		for (int i = 0; i < cont; i++) {
			Hashtable<String, Object> tables = new Hashtable<>();
			Hashtable registro = resInfrac.getRecordValues(i);
			MapTools.safePut(tables, "F_INICIO", sdf.format(registro.get("FECHORAINI")));
			MapTools.safePut(tables, "F_FIN", sdf.format(registro.get("FECHORAFIN")));
			// :TODO REVISAR ESTAS TRADUCCIONES
			if (locale.equals(Locale.ITALY)) {
				MapTools.safePut(tables, "INFRACCION", getTranslationInfraction((String)registro.get("TIPO")));
			}else {
					MapTools.safePut(tables, "INFRACCION", registro.get("TIPO"));
			}
			Object importe = registro.get("IMPORTE");
			MapTools.safePut(tables, "SANCION", importe == null ? null : df.format(importe));
			resInfracProcessed.addRecord(tables);
		}
		InfractionWarningService.logger.trace("Generated new entityresult", cont);
		tableFields.put("ESanciones", resInfracProcessed);

		JasperReport jr = JRReportUtil.getJasperReport(this.getReportPath(locale));
		dataFields.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle rb = ResourceManager.getBundle(locale);
		dataFields.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);

		JasperPrint jp = JasperFillManager.fillReport(jr, dataFields, new JRTableModelDataSource(EntityResultUtils.createTableModel(resInfracProcessed)));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRReportUtil.exportToPDF(jp, baos);
		BytesBlock bb = new BytesBlock(baos.toByteArray());
		InfractionWarningService.logger.trace("finished");
		baos.close();
		return bb;
	}
	
	private String getTranslationInfraction(String tipo) {
		
		switch (tipo) {
		case "ECD":
			return  "STGG";
		case "FDD":
			return "IRG";
		case "ECS":
			return "STGS";
		case "ECB":
			return  "STGB";
		case "ECI":
			return "MRI";
		case "FDS":
			return "IRS";
		case "FDS45":
			return "IRS45";
		case "FDSR":
			return "IRSR";
		default:
			break;
		}
		return tipo;
	}

	private String getReportPath(Locale locale) {
		if (Locale.ITALY.equals(locale)) {
			return InfractionWarningService.REPORT_PATH + "it_IT.jasper";
		}
		return InfractionWarningService.REPORT_PATH + "es_ES.jasper";
	}

	private EntityResult analyzeInfractions(String cif, String cgContrato, Date beginDate, Date endDate, Object driverId, int sessionId) throws Exception {
		Vector<String> attributes = EntityResultTools.attributes(OpentachFieldNames.DNI_FIELD, OpentachFieldNames.NAME_FIELD, OpentachFieldNames.SURNAME_FIELD, "NUM_TRJ_CONDU",
				"TIPO_DSCR", "DSCR_TIPO_INFRAC", "TIPO", "FECHORAINI", "FECHORAFIN", "NATURALEZA", "FADES", "EXCON", "TCP", "TDP", "IMPORTE");
		Vector<Object> drivers = new Vector<Object>(Arrays.asList(new Object[] { driverId }));
		return this.getService(InfractionService.class).analyzeInfractions(cif, cgContrato, beginDate, endDate, drivers, EngineAnalyzer.DEFAULT, sessionId, attributes);
	}


}
