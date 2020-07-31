package com.opentach.server.downcenterreport;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.vu.VUFile;
import com.imatia.tacho.security.TachoFileVerifier;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.VuReportDto;
import com.opentach.common.tacho.FileParser;
import com.opentach.common.tacho.IFileParser;
import com.opentach.common.tacho.data.Actividad;
import com.opentach.common.tacho.data.Calibrado;
import com.opentach.common.tacho.data.Conductor;
import com.opentach.common.tacho.data.Control;
import com.opentach.common.tacho.data.Fallo;
import com.opentach.common.tacho.data.RegistroKmVehiculo;
import com.opentach.common.util.DateUtil;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.process.TachoCertificateStoreManager;
import com.opentach.server.tachofiles.TachoFileTools;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class VuReportGenerator.
 */
public class VuReportGenerator {

	/** The Constant logger. */
	private static final Logger				logger	= LoggerFactory.getLogger(VuReportGenerator.class);

	/** The locator. */
	private final IOpentachServerLocator	locator;

	/**
	 * Instantiates a new vu report generator.
	 *
	 * @param locator
	 *            the locator
	 */
	public VuReportGenerator(IOpentachServerLocator locator) {
		super();
		this.locator = locator;
	}

	/**
	 * Fill.
	 *
	 * @param idFile
	 *            the id file
	 * @param fi
	 *            the fi
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @return the report dto
	 * @throws Exception
	 *             the exception
	 */
	public AbstractReportDto fill(Object idFile, FileInfoReport fi, Connection conn, int sessionID) throws Exception {
		Chronometer ch = new Chronometer();
		ch.start();
		VuReportGenerator.logger.info("queryReportData FILE={}		starts.", idFile);
		try {
			VuReportDto report = new VuReportDto();
			this.fillHeaderDataVU(report, idFile, conn, sessionID);
			this.fillDataVU(report, idFile, fi, conn, sessionID);
			return report;
		} finally {
			VuReportGenerator.logger.info("queryReportData FILE={}		ends (totaltime={}ms).", idFile, ch.stopMs());
		}
	}

	/**
	 * Fill header data vu.
	 *
	 * @param report
	 *            the report
	 * @param idFile
	 *            the id file
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private void fillHeaderDataVU(final VuReportDto report, Object idFile, Connection conn, int sessionID) throws Exception {
		Chronometer ch = new Chronometer();
		ch.start();
		VuReportGenerator.logger.info("queryReportData FILE={}		fillHeaderDataVU 	starts.", idFile);
		try {
			String sql = new Template("sql/HTMLReportGenerator_headerDataVU.sql").getTemplate();
			new QueryJdbcTemplate<Void>() {
				@Override
				protected Void parseResponse(ResultSet rset) throws UException {
					try {
						if (rset.next()) {
							report.setCompanyName(rset.getString(2));
							report.setVehicle(rset.getString(3));
						}
						return null;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(conn, sql, idFile);
		} finally {
			VuReportGenerator.logger.info("queryReportData FILE={}		fillHeaderDataVU 	ends (totaltime={}ms).", idFile, ch.stopMs());
		}
	}

	/**
	 * Fill data vu.
	 *
	 * @param report
	 *            the report
	 * @param idFile
	 *            the id file
	 * @param fi
	 *            the fi
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private void fillDataVU(VuReportDto report, Object idFile, FileInfoReport fi, Connection conn, int sessionID) throws Exception {
		Chronometer ch = new Chronometer();
		ch.start();
		VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	starts.", idFile);
		try {
			Date dNow = new Date();
			Date dDownload = DateUtil.addDays(dNow, 90);
			report.setNextDownloadDate(dDownload);
			ByteArrayInputStream bais = fi.getFileContent();
			VUFile vf = (VUFile) TachoFile.readTachoFile(bais);
			new TachoFileVerifier().validateFile(vf, TachoCertificateStoreManager.getStore());
			IFileParser fp = FileParser.createParserFor(vf);
			this.fillKMTable(report, fp.getRegistroKmVehiculo());
			List<Actividad> lActivities = fp.getActividades();
			Date dInit = DateUtil.addDays(new Date(), -90);
			Date dEnd = new Date();
			if (!lActivities.isEmpty()) {
				dInit = lActivities.get(0).getFecComienzo();
				dEnd = lActivities.get(lActivities.size() - 1).getFecFin();
			}
			report.setInitDate(dInit);
			report.setEndDate(dEnd);
			VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	'basicData' processed (time={}ms).", idFile, ch.elapsedMs());

			this.fillCondTable(report, fp.getConductores());
			VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	'drivers' processed (time={}ms).", idFile, ch.elapsedMs());

			this.fillIncidentesFallosTable(report, fp.getFallos());
			VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	'incidences' processed (time={}ms).", idFile, ch.elapsedMs());

			this.fillControlesTable(report, fp.getControles(), conn, sessionID);
			VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	'checkpoints' processed (time={}ms).", idFile, ch.elapsedMs());

			this.fillCalibradosTable(report, fp.getCalibrados(), conn, sessionID);
			VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	'calibrados' processed (time={}ms).", idFile, ch.elapsedMs());
		} catch (Exception e) {
			VuReportGenerator.logger.error(null, e);
		} finally {
			VuReportGenerator.logger.info("queryReportData FILE={}		fillDataVU 	ends (totaltime={}ms).", idFile, ch.stopMs());
		}
	}

	/**
	 * Fill km table.
	 *
	 * @param report
	 *            the report
	 * @param lKM
	 *            the l km
	 */
	private void fillKMTable(VuReportDto report, List lKM) {
		Collections.sort(lKM);
		int size = lKM.size();
		if (size > 0) {
			RegistroKmVehiculo rkmini = (RegistroKmVehiculo) lKM.get(0);
			RegistroKmVehiculo rkmend = (RegistroKmVehiculo) lKM.get(size - 1);
			final int kmini = rkmini.getKilometros();
			final int kmend = rkmend.getKilometros();
			final int kmtot = kmend - kmini;
			report.setKms(kmini, kmend, kmtot);
		}
	}

	/**
	 * Fill cond table.
	 *
	 * @param report
	 *            the report
	 * @param lCond
	 *            the l cond
	 */
	private void fillCondTable(VuReportDto report, List lCond) {
		for (int i = 0; i < lCond.size(); i++) {
			Conductor cond = (Conductor) lCond.get(i);
			report.addDriver(cond.getIdConductor(), cond.getNombre(), cond.getApellidos(), TachoFileTools.extractDriverDni(cond.getIdConductor(), null));
		}
	}

	/**
	 * Fill incidentes fallos table.
	 *
	 * @param report
	 *            the report
	 * @param lFallos
	 *            the l fallos
	 */
	private void fillIncidentesFallosTable(VuReportDto report, List lFallos) {
		for (int i = 0; i < lFallos.size(); i++) {
			Fallo fallo = (Fallo) lFallos.get(i);
			Integer proposito = fallo.getProposito();
			switch (proposito) {
				case 4:
					report.addIncideteFallo("M_CONDUCCION_SIN_TARJETA", fallo.getfecHoraIni());
					break;
				case 7:
					report.addIncideteFallo("M_EXCESOS_VELOCIDAD", fallo.getfecHoraIni());
					break;
				case 501:
					report.addIncideteFallo("M_EXCESOS_VELOCIDAD", fallo.getfecHoraIni());
					break;
				case 8:
					report.addIncideteFallo("M_INTERRUPCION_CORRIENTE", fallo.getfecHoraIni());
					break;
				case 48:
					report.addIncideteFallo("M_FALLO_CONTROL", fallo.getfecHoraIni());
					break;
				default:
					report.addIncideteFallo("M_FALLO_OTHERS", fallo.getfecHoraIni());
					break;
			}
		}
	}

	/**
	 * Fill controles table.
	 *
	 * @param report
	 *            the report
	 * @param lControles
	 *            the l controles
	 * @param conn
	 *            the conn
	 * @param sessionId
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private void fillControlesTable(VuReportDto report, List lControles, Connection conn, int sessionId) throws Exception {
		Map<Number, String> mControls = this.getTypeControls(conn, sessionId);
		for (int i = 0; i < lControles.size(); i++) {
			Control cond = (Control) lControles.get(i);
			report.addControl(cond.getFechaHora(), mControls.get(cond.getTpControl()), cond.getNumTrjControl());
		}
	}

	/**
	 * Fill calibrados table.
	 *
	 * @param report
	 *            the report
	 * @param lCalibrados
	 *            the l calibrados
	 * @param conn
	 *            the conn
	 * @param sessionId
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private void fillCalibradosTable(VuReportDto report, List lCalibrados, Connection conn, int sessionId) throws Exception {
		Map<Number, String> mCalibrados = this.getTypeCalibrados(conn, sessionId);
		for (int i = 0; i < lCalibrados.size(); i++) {
			Calibrado cond = (Calibrado) lCalibrados.get(i);
			report.addCalibrado(mCalibrados.get(cond.getTpCalibrado()), cond.getNumTrjTaller(), cond.getFecProximo());
		}
	}

	/**
	 * Gets the type controls.
	 *
	 * @param conn
	 *            the conn
	 * @param sessionId
	 *            the session id
	 * @return the type controls
	 * @throws Exception
	 *             the exception
	 */
	private Map<Number, String> getTypeControls(Connection conn, int sessionId) throws Exception {
		TableEntity eControles = (TableEntity) this.locator.getEntityReferenceFromServer("ETipoControl");
		EntityResult res = eControles.query(new Hashtable<>(), EntityResultTools.attributes("TPCONTROL", "DSCR"), sessionId, conn);
		int size = res.calculateRecordNumber();
		if (size > 0) {
			Map<Number, String> mTypeControls = new HashMap<Number, String>();
			for (int i = 0; i < size; i++) {
				Hashtable<String, Object> av = res.getRecordValues(i);
				mTypeControls.put(((Number) av.get("TPCONTROL")).intValue(), (String) av.get("DSCR"));
			}
			return mTypeControls;
		}
		return Collections.EMPTY_MAP;
	}

	/**
	 * Gets the type calibrados.
	 *
	 * @param conn
	 *            the conn
	 * @param sessionId
	 *            the session id
	 * @return the type calibrados
	 * @throws Exception
	 *             the exception
	 */
	private Map<Number, String> getTypeCalibrados(Connection conn, int sessionId) throws Exception {
		TableEntity eCalibrados = (TableEntity) this.locator.getEntityReferenceFromServer("ETipoCalibrado");
		EntityResult res = eCalibrados.query(new Hashtable(), EntityResultTools.attributes("TPCALIBRADO", "DSCR"), sessionId, conn);
		int size = res.calculateRecordNumber();
		if (size > 0) {
			Map<Number, String> mTypeCalibrados = new HashMap<Number, String>();
			for (int i = 0; i < size; i++) {
				Hashtable<String, Object> av = res.getRecordValues(i);
				mTypeCalibrados.put(((Number) av.get("TPCALIBRADO")).intValue(), (String) av.get("DSCR"));
			}
			return mTypeCalibrados;
		}
		return Collections.EMPTY_MAP;
	}
}
