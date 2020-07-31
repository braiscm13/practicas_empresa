package com.opentach.server.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.GeneratorPCBS;
import com.imatia.tacho.infraction.GeneratorPCD;
import com.imatia.tacho.infraction.GeneratorPCI;
import com.imatia.tacho.infraction.GeneratorPCS;
import com.imatia.tacho.infraction.GeneratorPDD;
import com.imatia.tacho.infraction.GeneratorPDS;
import com.imatia.tacho.infraction.InputActivity;
import com.imatia.tacho.infraction.Period;
import com.imatia.tacho.infraction.Period.PeriodClass;
import com.imatia.tacho.infraction.Rest;
import com.imatia.tacho.infraction.RestClass;
import com.imatia.tacho.infraction.Stretch;
import com.imatia.tacho.infraction.StretchGenerator;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.InfractionAnalyzerInServer;
import com.opentach.server.activities.InfractionAnalyzerInServerTask.ActivityIterator;
import com.opentach.server.companies.ContractService;
import com.opentach.server.entities.EConductoresEmp;
import com.opentach.server.entities.EDfEmp;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailFields;
import com.opentach.server.mail.MailService;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.ContractUtils;
import com.utilmize.server.tools.sqltemplate.BatchUpdateJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToMapTemplate;
import com.utilmize.tools.exception.UException;

public class MobileExpressReportHelper extends AbstractDelegate {

	private static final Logger logger = LoggerFactory.getLogger(MobileExpressReportHelper.class);

	public MobileExpressReportHelper(IOpentachServerLocator opentachServerLocator) {
		super(opentachServerLocator);
	}

	public Map<String, Object> generateMobileExpressReport(final String cif, final String idDriver, final Date analizeFrom, final Date analizeTo) throws Exception {
		CheckingTools.failIfNull(cif, "E_REQUIRED_CIF", new Object[0]);
		CheckingTools.failIfNull(idDriver, "E_REQUIRED_IDDRIVER", new Object[0]);
		CheckingTools.failIfNull(analizeFrom, "E_REQUIRED_ANALIZE_FROM", new Object[0]);
		CheckingTools.failIfNull(analizeTo, "E_REQUIRED_ANALIZE_TO", new Object[0]);
		return new OntimizeConnectionTemplate<Map<String, Object>>() {
			@Override
			protected Map<String, Object> doTask(Connection con) throws UException {
				try {
					return MobileExpressReportHelper.this.queryConduccionDescanso(cif, idDriver, null, new Timestamp(analizeFrom.getTime()), new Timestamp(analizeTo.getTime()),
							con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.interfaces.IGeneraInformeExpressMovil#querySendEmail(java.lang.String, java.lang.String[], int, java.sql.Connection)
	 */
	public void generateAndSendMobileExpressReport(final String idfichero, final String[] additionalEmails) throws Exception {
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					MobileExpressReportHelper.this.generateAndSendMobileExpressReport(idfichero, additionalEmails, con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);

	}

	private void generateAndSendMobileExpressReport(String idfichero, String[] additionalEmails, Connection con) throws Exception {
		// Busco los valores a enviar en el email.
		Hashtable<String, Object> lastFileData = this.queryLastFileData(idfichero, null, null, con);
		if (lastFileData != null) {
			String cif = (String) lastFileData.get("CIF");
			Object idDriver = lastFileData.get("IDORIGEN");

			// Query last date and analize from last activity 28 days back
			Object numReq = this.getNumReq(cif, con);
			numReq = ContractUtils.checkContratoFicticio(this.getLocator(), numReq, -1, con);
			Date endDate = this.getLastActivityDate(idDriver, numReq, con);
			Timestamp analizeFrom = new Timestamp(DateTools.add(endDate, Calendar.DAY_OF_YEAR, -28).getTime());
			Timestamp analizeTo = new Timestamp(DateTools.add(endDate, Calendar.DAY_OF_YEAR, 1).getTime());

			Map<String, Object> params = this.queryConduccionDescanso(cif, idDriver, (Date) lastFileData.get("F_DESCARGA_DATOS"), analizeFrom, analizeTo, con);

			this.sendMail(params, cif, idDriver, additionalEmails, con);
		}
	}

	/**
	 * Query conduccion descanso.
	 *
	 * @param cif
	 *            the cif
	 * @param idDriver
	 *            the id driver
	 * @param con
	 *            the con
	 * @return the map
	 * @throws Exception
	 *             the exception
	 */
	private Map<String, Object> queryConduccionDescanso(final String cif, final Object idDriver, Date lastFileData, Timestamp analizeFrom, Timestamp analizeTo, Connection con)
			throws Exception {
		// Query lastFile data (if not already available)
		if (lastFileData == null) {
			Hashtable<String, Object> data = this.queryLastFileData(null, cif, idDriver, con);
			if (data != null) {
				lastFileData = (Date) data.get("F_DESCARGA_DATOS");
			}
		}

		Object numReq = this.getNumReq(cif, con);
		numReq = ContractUtils.checkContratoFicticio(this.getLocator(), numReq, -1, con);

		// Analize data in this period
		this.analyze(con, numReq, idDriver, analizeFrom, analizeTo);

		// Query report data
		String sql = new Template("sql/EInformeExpressMovil.sql").getTemplate();
		Map<String, Object> res = new QueryJdbcToMapTemplate().execute(con, sql, idDriver, numReq);

		// Finally, set extra data
		SimpleDateFormat sdfecha = new SimpleDateFormat("dd/MM/yyyy");
		res.put("F_ANALIZE_FROM", sdfecha.format(analizeFrom));
		res.put("F_ANALIZE_TO", sdfecha.format(analizeTo));
		try {
			res.put("ULTIMA_ACTIVIDAD", sdfecha.format(this.getLastActivityDate(idDriver, numReq, con)));
		} catch (Exception ex) {
			MobileExpressReportHelper.logger.warn("WARNING_EXPRESS_REPORT", ex);
		}
		res.put("F_DESCARGA_DATOS", lastFileData == null ? null : sdfecha.format(lastFileData));

		return res;
	}

	/**
	 * Send mail.
	 *
	 * @param parameters
	 *            the parameters
	 * @param cif
	 *            the cif
	 * @param idConductor
	 *            the id conductor
	 * @param additionalEmails
	 *            the additional emails
	 * @param conn
	 *            the conn
	 * @throws Exception
	 *             the exception
	 */
	private void sendMail(Map<String, Object> parameters, String cif, Object idConductor, String[] additionalEmails, Connection conn) throws Exception {
		String opentachMail = this.getMailRequestAddress(conn);
		Map<String, Object> companyInfo = ((EDfEmp) this.getEntity(CompanyNaming.ENTITY)).getCompanyInfo(cif, -1, conn, "MAILINFORMEANALISIS", "LOCALE");
		Map<String, Object> driverInfo = ((EConductoresEmp) this.getEntity("EConductoresEmp")).getDriverInfoByIdConductor(idConductor, cif, conn, "ENVIAR_A", "EMAIL");

		List<String> emailAddresses = new ArrayList<String>();
		if (additionalEmails != null) {
			emailAddresses.addAll(Arrays.asList(additionalEmails));
		}

		String companyMail = (String) companyInfo.get("MAILINFORMEANALISIS");
		String local = (String) companyInfo.get("LOCALE");
		String driverMail = (String) driverInfo.get("EMAIL");
		String destinationType = (String) driverInfo.get("ENVIAR_A");
		if (local == null) {
			local = "es_ES";
		}

		MobileExpressReportHelper.logger.debug("Locale={}; Destination type={}; Driver mail={}; Company mail={}", local, destinationType, driverMail, companyMail);

		if (destinationType != null) {
			if ("EMPRESA".equals(destinationType)) {
				emailAddresses.add(companyMail);
			} else if ("CONDUCTOR".equals(destinationType)) {
				emailAddresses.add(driverMail);
			} else if ("AMBOS".equals(destinationType)) {
				emailAddresses.add(companyMail);
				emailAddresses.add(driverMail);
			}
		}
		String mailto = MailComposer.joinMailAddresses(emailAddresses);
		MobileExpressReportHelper.logger.debug("Enviamos informe express movil a {}", mailto);
		if (mailto.length() > 0) {
			this.getService(MailService.class).sendMail(new IGMailComposerMovil(mailto, opentachMail, new Locale("es", "ES"), parameters));
		}
	}

	/**
	 * Gets the mail request address.
	 *
	 * @param conn
	 *            the conn
	 * @return the mail request address
	 * @throws Exception
	 *             the exception
	 */
	private String getMailRequestAddress(Connection conn) throws Exception {
		EPreferenciasServidor ePrefs = (EPreferenciasServidor) this.getEntity("EPreferenciasServidor");
		return ePrefs.getValue(MailFields.MAIL_REQADDRESS, TableEntity.getEntityPrivilegedId(ePrefs), conn);
	}

	/**
	 * Gets the num req.
	 *
	 * @param cif
	 *            the cif
	 * @param conn
	 *            the conn
	 * @return the num req
	 * @throws Exception
	 *             the exception
	 */
	private Object getNumReq(String cif, Connection conn) throws Exception {
		TableEntity entEmpre = (TableEntity) this.getEntity("EEmpreReq");
		String numReq = this.getService(ContractService.class).getContratoVigente(cif, TableEntity.getEntityPrivilegedId(entEmpre), conn);
		if (numReq == null) {
			throw new Exception("No hay contrato asociado");
		}
		return numReq;
	}

	private Date getLastActivityDate(Object idDriver, Object numReq, Connection con) throws Exception {
		return new QueryJdbcTemplate<Date>() {
			@Override
			protected Date parseResponse(ResultSet rs) throws UException {
				try {
					if (rs.next()) {
						java.sql.Date res = rs.getDate(1);
						if (res != null) {
							return res;
						}
					}
				} catch (Exception ex) {
					throw new UException(ex);
				}
				throw new UException("NO_ACTIVITY_INFO_FOUND");
			}
		}.execute(con, "select max(FEC_FIN) as max from cdactividades where idconductor=? and numreq=?", idDriver, numReq);
	}

	private Hashtable<String, Object> queryLastFileData(Object idfichero, String cif, Object driver, Connection conn) throws Exception {
		TableEntity entFile = (TableEntity) this.getEntity("EFicherosRegistro");
		Hashtable<String, Object> cv = new Hashtable<>();
		MapTools.safePut(cv, OpentachFieldNames.IDFILE_FIELD, idfichero);
		MapTools.safePut(cv, "CIF", cif);
		MapTools.safePut(cv, "IDORIGEN", driver);
		Vector<String> order = new Vector<String>(Arrays.asList(new String[] { "F_ALTA" }));
		EntityResult res = entFile.query(cv, EntityResultTools.attributes("CIF", "IDORIGEN", "F_DESCARGA_DATOS"), this.getEntityPrivilegedId(entFile), conn, order, false);
		if (res.calculateRecordNumber() > 0) {
			Hashtable<String, Object> result = res.getRecordValues(0);
			return result;
		}
		return null;
	}

	/**
	 * Analize.
	 *
	 * @param con
	 *            the con
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	private void analyze(final Connection con, Object contract, Object driver, Timestamp beginDate, Timestamp endDate) throws Exception {
		String sql = new Template("sql/InfractionAnalyzerQueryActivities.sql").getTemplate();
		new QueryJdbcTemplate<Void>() {

			@Override
			protected Void parseResponse(ResultSet rs) throws UException {
				try {
					MobileExpressReportHelper.this.analyze(new ActivityIterator(rs),
							InfractionAnalyzerInServer.getInstance(MobileExpressReportHelper.this.getLocator()).getParameters(), con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, con, sql, contract, driver, beginDate, endDate);
	}

	private void analyze(Iterator<InputActivity> activityIterator, AnalysisParameters configuration, Connection con) throws Exception {
		LinkedList<Stretch> stretchs = new StretchGenerator().convert(activityIterator, configuration);
		Map<PeriodClass, List<Period>> periods = new HashMap<>();
		Map<RestClass, List<Rest>> rests = new HashMap<>();
		new GeneratorPCI().generate(stretchs, periods, rests, configuration);
		new GeneratorPCD().generate(stretchs, periods, rests, configuration);
		new GeneratorPDD().generate(stretchs, periods, rests, configuration);
		new GeneratorPDS().generate(stretchs, periods, rests, configuration);
		new GeneratorPCS().generate(stretchs, periods, rests, configuration);
		new GeneratorPCBS().generate(stretchs, periods, rests, configuration);

		this.saveStretchs(stretchs, con);
		this.savePeriods(periods, con);
		if (MobileExpressReportHelper.logger.isDebugEnabled()) {
			this.debug(stretchs, periods, rests);
		}
	}

	private void debug(LinkedList<Stretch> stretchs, Map<PeriodClass, List<Period>> periods, Map<RestClass, List<Rest>> rests) {
		StringBuilder sb = new StringBuilder();
		sb.append("STRETCHS\n");
		for (Stretch stretch : stretchs) {
			sb.append(stretch.toString()).append("\n");
		}
		sb.append("\n");
		for (Entry<PeriodClass, List<Period>> entry : periods.entrySet()) {
			sb.append("Period ").append(entry.getKey()).append("\n");
			for (Period period : entry.getValue()) {
				sb.append(period.toString()).append("\n");
			}
			sb.append("\n");
		}

		for (Entry<RestClass, List<Rest>> entry : rests.entrySet()) {
			sb.append("Rest ").append(entry.getKey()).append("\n");
			for (Rest rest : entry.getValue()) {
				sb.append(rest.toString()).append("\n");
			}
			sb.append("\n");
		}
		MobileExpressReportHelper.logger.warn(sb.toString());

	}

	private void savePeriods(final Map<PeriodClass, List<Period>> periods, final Connection con) throws Exception {
		List<Period> periodList = new ArrayList<Period>();
		for (Entry<PeriodClass, List<Period>> entry : periods.entrySet()) {
			periodList.addAll(entry.getValue());
		}

		new BatchUpdateJdbcTemplate<Period>(periodList) {

			@Override
			protected Object[] beanToParametersArray(int idx, Period period) {
				Object[] array = new Object[7];
				array[0] = idx;
				array[1] = period.getPeriodClass().toString();
				array[2] = period.getBeginPeriodDate();
				array[3] = period.getEndPeriodDate();
				array[4] = period.getPeriodClass().toString().startsWith("PC") ? period.getTime() : 0;
				array[5] = period.getPeriodClass().toString().startsWith("PC") ? 0 : period.getTime();
				array[6] = String.valueOf(period.getPeriodType().getIntValue());
				return array;
			}
		}.execute(con, "insert into cdperiodos1_temp (IDPERIODO,TIPO,FECINI,FECFIN,TCP,TDP,TIPODD) values (?,?,?,?,?,?,?)");
	}

	private void saveStretchs(final LinkedList<Stretch> stretchs, final Connection con) throws Exception {
		new BatchUpdateJdbcTemplate<Stretch>(stretchs) {
			@Override
			protected Object[] beanToParametersArray(int idx, Stretch stretch) {
				Object[] array = new Object[5];
				array[0] = idx;
				array[1] = String.valueOf(stretch.getType().getIntValue());
				array[2] = stretch.getBeginDate();
				array[3] = stretch.getEndDate();
				array[4] = stretch.getDuration();
				return array;
			}
		}.execute(con, "insert into cdtramos_def_temp (IDTRAMO,TIPO,FECINI,FECFIN,MINUTOS_A) values (?,?,?,?,?)");
	}
}
