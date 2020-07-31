package com.opentach.server.downcenterreport;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.TCFile;
import com.imatia.tacho.security.TachoFileVerifier;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.TcReportDto;
import com.opentach.common.tacho.FileParser;
import com.opentach.common.tacho.IFileParser;
import com.opentach.common.tacho.data.Actividad;
import com.opentach.common.tacho.data.Fallo;
import com.opentach.common.tacho.data.Incidente;
import com.opentach.common.tacho.data.RegistroKmConductor;
import com.opentach.common.tacho.data.VehiculoInfrac;
import com.opentach.common.util.DateUtil;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.process.TachoCertificateStoreManager;
import com.opentach.server.process.TachoFileProcessService;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class TcReportGenerator.
 */
public class TcReportGenerator {

	/** The Constant logger. */
	private static final Logger				logger	= LoggerFactory.getLogger(TcReportGenerator.class);

	/** The locator. */
	private final IOpentachServerLocator	locator;

	/**
	 * Instantiates a new tc report generator.
	 *
	 * @param locator
	 *            the locator
	 */
	public TcReportGenerator(IOpentachServerLocator locator) {
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
	public AbstractReportDto fill(Object idFile, FileInfoReport fi, boolean limitedInfo, Connection conn, int sessionID) throws Exception {
		Chronometer ch = new Chronometer();
		ch.start();
		TcReportGenerator.logger.info("queryReportData FILE={}		starts.", idFile);
		try {
			TcReportDto report = new TcReportDto();
			this.fillHeaderDataTC(report, idFile, conn, sessionID);
			this.fillDataTC(report, idFile, fi, limitedInfo, conn, sessionID);
			return report;
		} finally {
			TcReportGenerator.logger.info("queryReportData FILE={}		ends (totaltime={}ms).", idFile, ch.stopMs());
		}
	}

	/**
	 * Fill header data tc.
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
	private void fillHeaderDataTC(final TcReportDto report, Object idFile, Connection conn, int sessionID) throws Exception {
		Chronometer ch = new Chronometer();
		ch.start();
		TcReportGenerator.logger.info("queryReportData FILE={}		fillHeaderDataTC 	starts.", idFile);
		try {
			String sql = new Template("sql/HTMLReportGenerator_headerDataTC.sql").getTemplate();
			new QueryJdbcTemplate<Void>() {

				@Override
				protected Void parseResponse(ResultSet rset) throws UException {
					try {
						if (rset.next()) {
							report.setCompanyName(rset.getString(2));
							report.setDriverDni(rset.getString(3));
							report.setDriverName(rset.getString(4));
							report.setDriverSurname(rset.getString(5));
							report.setDriverId(rset.getString(6));
						}
						return null;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(conn, sql, idFile);
		} finally {
			TcReportGenerator.logger.info("queryReportData FILE={}		fillHeaderDataTC 	ends (totaltime={}ms).", idFile, ch.stopMs());

		}
	}

	/**
	 * Fill data tc.
	 *
	 * @param report
	 *            the report
	 * @param idFile
	 *            the id file
	 * @param fi
	 *            the fi
	 * @param limitedInfo
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private void fillDataTC(TcReportDto report, Object idFile, FileInfoReport fi, boolean limitedInfo, Connection conn, int sessionID) throws Exception {
		Chronometer ch = new Chronometer();
		ch.start();
		TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	starts..", idFile);
		try {
			Future<?> future = this.processFile(idFile, fi, conn, sessionID);
			try {
				future.get(180, TimeUnit.SECONDS);
			} catch (TimeoutException ex) {
				future.cancel(true);
				throw ex;
			}
			future.get();
			report.setCompanyCif(this.getCifFromFile(idFile, conn, sessionID));
			TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	'file' processed (time={}ms).", idFile, ch.elapsedMs());

			if (!limitedInfo) {
				this.fillInfractionsTable(report, conn, sessionID);
				TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	'infractions' processed (time={}ms).", idFile, ch.elapsedMs());
			} else {
				TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	'infractions' ignored (time={}ms).", idFile, ch.elapsedMs());
			}
			// proceso para obtener datos
			try {
				Date dNow = new Date();
				Date dDownload = DateUtil.addDays(dNow, 28);
				report.setNextDownloadDate(dDownload);
				ByteArrayInputStream bais = fi.getFileContent();
				TCFile dcf = (TCFile) TachoFile.readTachoFile(bais);
				new TachoFileVerifier().validateFile(dcf, TachoCertificateStoreManager.getStore());
				IFileParser fp = FileParser.createParserFor(dcf);
				List<Actividad> lActivities = fp.getActividades();
				List<RegistroKmConductor> lKm = fp.getRegistroKmConductor();
				this.fillActivitySummary(report, lActivities, lKm);
				Date dInit = DateUtil.addDays(new Date(), -25);
				Date dEnd = new Date();
				if (!lActivities.isEmpty()) {
					dInit = lActivities.get(0).getFecComienzo();
					dEnd = lActivities.get(lActivities.size() - 1).getFecFin();
				}
				report.setInitDate(dInit);
				report.setEndDate(dEnd);
				List<VehiculoInfrac> lVehicles = fp.getVehiculoInfrac();
				this.fillVehicleSummary(report, lVehicles);
				TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	'vehicles' processed (time={}ms).", idFile, ch.elapsedMs());

				List<Incidente> lIncidencias = fp.getIncidentes();
				report.setHasIncidences(!lIncidencias.isEmpty());
				TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	'incidences' processed (time={}ms).", idFile, ch.elapsedMs());

				List<Fallo> lErrores = fp.getFallos();
				report.setHasErrors(!lErrores.isEmpty());
				TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	'errors' processed (time={}ms).", idFile, ch.elapsedMs());
			} catch (Exception e) {
				TcReportGenerator.logger.error(null, e);
			}
		} finally {
			TcReportGenerator.logger.info("queryReportData FILE={}		fillDataTC 	ends (totaltime={}ms).", idFile, ch.stopMs());
		}
	}

	/**
	 * Fill infractions table.
	 *
	 * @param report
	 *            the report
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private void fillInfractionsTable(TcReportDto report, Connection conn, int sessionID) throws Exception {
		TableEntity eInfrac = (TableEntity) this.locator.getEntityReferenceFromServer("EInformeInfrac");
		Hashtable<String, Object> kv = new Hashtable<String, Object>();
		Date dEnd = new Date();
		dEnd = DateUtil.truncToEnd(dEnd);
		Date dIni = DateUtil.addDays(dEnd, -28);
		kv.put(OpentachFieldNames.FILTERFECINI, dIni);
		kv.put(OpentachFieldNames.FILTERFECFIN, dEnd);
		kv.put(OpentachFieldNames.CIF_FIELD, report.getCompanyCif());
		kv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, report.getDriverId());
		try {
			EntityResult er = eInfrac.query(kv, EntityResultTools.attributes("TIPO", "NATURALEZA", "FECHORAINI", "FECHORAFIN"), sessionID, conn);
			final int count = er.calculateRecordNumber();
			for (int i = 0; i < count; i++) {
				final Hashtable<String, Object> htRow = er.getRecordValues(i);
				final String tipo = (String) htRow.get("TIPO");
				final String nat = (String) htRow.get("NATURALEZA");
				final Date dini = (Date) htRow.get("FECHORAINI");
				final Date dfin = (Date) htRow.get("FECHORAFIN");
				report.addInfraction(tipo, nat, dini, dfin);
			}
		} catch (Exception e) {
			TcReportGenerator.logger.error(null, e);
		}
	}

	/**
	 * Fill activity summary.
	 *
	 * @param report
	 *            the report
	 * @param lActivities
	 *            the l activities
	 * @param lKm
	 *            the l km
	 */
	private void fillActivitySummary(TcReportDto report, List lActivities, List lKm) {
		int nKm = 0;
		for (int i = 0; i < lKm.size(); i++) {
			RegistroKmConductor km = (RegistroKmConductor) lKm.get(i);
			nKm += km.getKilometrosRecorridos();
		}
		int tCond = 0;
		int tDisp = 0;
		int tPausa = 0;
		int tTrab = 0;
		for (int i = 0; i < lActivities.size(); i++) {
			Actividad act = (Actividad) lActivities.get(i);
			Number tpAct = act.getTpActividad();
			switch (tpAct.intValue()) {
				case Actividad.CONDUCCION:
					tCond += act.minutos.intValue();
					break;
				case Actividad.DISPONIBILIDAD:
					tDisp += act.minutos.intValue();
					break;
				case Actividad.PAUSA_DESCANSO:
					tPausa += act.minutos.intValue();
					break;
				case Actividad.TRABAJO:
					tTrab += act.minutos.intValue();
					break;
			}
		}
		report.setActivitySummary(tCond, tDisp, tPausa, tTrab, nKm);
	}

	/**
	 * Fill vehicle summary.
	 *
	 * @param report
	 *            the report
	 * @param lVehicles
	 *            the l vehicles
	 */
	private void fillVehicleSummary(TcReportDto report, List lVehicles) {
		for (int i = 0; i < lVehicles.size(); i++) {
			VehiculoInfrac veh = (VehiculoInfrac) lVehicles.get(i);
			report.addVehicle(veh.matricula);
		}
	}

	/**
	 * Gets the cif from file.
	 *
	 * @param idFile
	 *            the id file
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @return the cif from file
	 * @throws Exception
	 *             the exception
	 */
	private String getCifFromFile(Object idFile, Connection conn, int sessionID) throws Exception {
		String sql = "SELECT CIF FROM CDFICHEROS_CONTRATO, CDVEMPRE_REQ_REALES WHERE CDFICHEROS_CONTRATO.IDFICHERO = ? AND CDVEMPRE_REQ_REALES.NUMREQ = CDFICHEROS_CONTRATO.CG_CONTRATO";
		return new QueryJdbcTemplate<String>() {
			@Override
			protected String parseResponse(ResultSet rset) throws UException {
				try {
					if (rset.next()) {
						return rset.getString(1);
					}
					return null;

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql, ((Number) idFile).intValue());
	}

	/**
	 * Process file.
	 *
	 * @param fileID
	 *            the file id
	 * @param fi
	 *            the fi
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	private Future<?> processFile(Object fileID, FileInfoReport fi, Connection conn, int sessionID) throws Exception {
		Number idFile = (Number) fileID;
		// if (fi.getDProcess() != null) {
		return this.locator.getService(TachoFileProcessService.class).processNowFuture(idFile);
		// }
	}

}
