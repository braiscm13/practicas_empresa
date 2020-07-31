package com.opentach.server.activities;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.activity.Activity;
import com.imatia.tacho.infraction.AnalysisResult;
import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.FullInfractionReportStatus;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.companies.ContractService;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.report.FullInfractionReportHelper;
import com.utilmize.server.services.UAbstractService;

public class InfractionService extends UAbstractService implements IInfractionService {

	private static final Logger	logger	= LoggerFactory.getLogger(InfractionService.class);
	private final FullInfractionReportHelper	fullInfractionReportHelper;

	public InfractionService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.fullInfractionReportHelper = new FullInfractionReportHelper((OpentachServerLocator) erl);
	}

	@Override
	public EntityResult analyzeInfractions(final String cif, final Object cgContrato, Date beginDate, Date endDate, final Vector<Object> vDrivers, EngineAnalyzer engineOpen,
			final int sesionId, Vector<String> av) throws Exception {
		try {
			Pair<Object, Vector<Object>> pair = this.getService(ContractService.class).queryContractAndDrivers(cif, cgContrato, vDrivers, sesionId);

			Object newCgContrato = pair.getFirst();
			Vector<Object> newVDrivers = pair.getSecond();

			if ((newVDrivers == null) || newVDrivers.isEmpty()) {
				throw new Exception("E_MUST_SPECIFY_DRIVER");
			}
			if (newCgContrato == null) {
				throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
			}

			this.checkDriversLimit(newVDrivers);

			Chronometer chrono = new Chronometer().start();
			// InfractionAnalyzerInDatabase analyzer = new InfractionAnalyzerInDatabase(this.getLocator());
			// EntityResult dbInfractions = analyzer.analyze(newCgContrato, newVDrivers, beginDate, endDate, av, sesionId);
			// ActivityService.logger.error("Time database: {}", System.currentTimeMillis() - time);

			// time = System.currentTimeMillis();
			AnalyzerEngine engine = this.convertEngine(engineOpen);

			InfractionAnalyzerInServer serverAnalyzer = InfractionAnalyzerInServer.getInstance((IOpentachServerLocator) this.getLocator());
			EntityResult serverInfractions = serverAnalyzer.analyze(newCgContrato, cif, newVDrivers, beginDate, endDate, av, engine, sesionId);
			InfractionService.logger.warn("Analyze {} drivers infractions time server: {} ms", newVDrivers.size(), chrono.stopMs());
			return serverInfractions;

		} catch (Exception e) {
			InfractionService.logger.error(null, e);
			throw e;
		}
	}

	public void analyzeForReport(Connection conn, Object numreq, Object cif, List<Object> drivers, Date beginDate, Date endDate, EngineAnalyzer engineOpen, int sesionId)
			throws Exception {

		if ((drivers == null) || drivers.isEmpty()) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		if (numreq == null) {
			throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
		}

		long time = System.currentTimeMillis();
		// InfractionAnalyzerInDatabase analyzer = new InfractionAnalyzerInDatabase(this.getLocator());
		// EntityResult dbInfractions = analyzer.analyze(newCgContrato, newVDrivers, beginDate, endDate, av, sesionId);
		// ActivityService.logger.error("Time database: {}", System.currentTimeMillis() - time);

		// time = System.currentTimeMillis();
		InfractionAnalyzerInServer serverAnalyzer = InfractionAnalyzerInServer.getInstance((IOpentachServerLocator) this.getLocator());

		AnalyzerEngine engine = this.convertEngine(engineOpen);

		serverAnalyzer.analyzeForReport(conn, numreq, cif, drivers, beginDate, endDate, engine, sesionId);
		InfractionService.logger.error("Time server: {}", System.currentTimeMillis() - time);

		// Integer num = new QueryJdbcTemplate<Integer>() {
		// @Override
		// protected Integer parseResponse(ResultSet rs) throws Exception {
		// if (rs.next()) {
		// return rs.getInt(1);
		// }
		// return null;
		// }
		// }.execute(conn, "SELECT count(*) FROM CDINFRACCIONES_TEMP");
		// System.out.println(num);

	}
	
	
	public void analyzeForWS(Connection conn, Object numreq, Object cif, List<Object> drivers, Date beginDate, Date endDate, EngineAnalyzer engineOpen, int sesionId)
			throws Exception {

		if ((drivers == null) || drivers.isEmpty()) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		if (numreq == null) {
			throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
		}

		long time = System.currentTimeMillis();
		InfractionAnalyzerInServer serverAnalyzer = InfractionAnalyzerInServer.getInstance((IOpentachServerLocator) this.getLocator());

		AnalyzerEngine engine = this.convertEngine(engineOpen);

//		serverAnalyzer.analyzeForWS(conn, numreq, cif, drivers, beginDate, endDate, engine, sesionId);
		InfractionService.logger.error("Time server: {}", System.currentTimeMillis() - time);
	}

	public Pair<AnalysisResult, List<Activity>> analyzeFullInfo(Connection conn, Object numreq, Object cif, Object driver, Date beginDate, Date endDate, EngineAnalyzer engineOpen,
			int sesionId)
					throws Exception {

		if (driver == null) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		if (numreq == null) {
			throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
		}

		long time = System.currentTimeMillis();
		// InfractionAnalyzerInDatabase analyzer = new InfractionAnalyzerInDatabase(this.getLocator());
		// EntityResult dbInfractions = analyzer.analyze(newCgContrato, newVDrivers, beginDate, endDate, av, sesionId);
		// ActivityService.logger.error("Time database: {}", System.currentTimeMillis() - time);

		// time = System.currentTimeMillis();
		InfractionAnalyzerInServer serverAnalyzer = InfractionAnalyzerInServer.getInstance((IOpentachServerLocator) this.getLocator());

		AnalyzerEngine engine = this.convertEngine(engineOpen);

		Pair<AnalysisResult, List<Activity>> analyzeFullInfo = serverAnalyzer.analyzeFullInfo(conn, numreq, cif, driver, beginDate, endDate, engine, sesionId);
		InfractionService.logger.error("Time server: {}", System.currentTimeMillis() - time);
		return analyzeFullInfo;
	}

	private AnalyzerEngine convertEngine(EngineAnalyzer engineOpen) {
		AnalyzerEngine engine = AnalyzerEngine.DEFAULT;
		if (engineOpen != null) {
			switch (engineOpen) {
				case ISLANDS:
					engine = AnalyzerEngine.SPANISH_ISLANDS;
					break;
				default:
					engine = AnalyzerEngine.DEFAULT;
					break;
			}
		}
		return engine;
	}

	@Override
	public FullInfractionReportStatus getFullInfractionReportStatus(int sessionId) throws Exception {
		return this.fullInfractionReportHelper.getStatus(sessionId);
	}

	@Override
	public void doFullInfractionReport(Date from, Date to, int sessionId) throws Exception {
		this.fullInfractionReportHelper.doReport(from, to, sessionId);
	}

	private void checkDriversLimit(Vector<Object> vDrivers) throws Exception {
		int maxDrivers = this.getNumDriversLimit();
		if (vDrivers.size() > maxDrivers) {
			throw new Exception("E_MAX_DRIVERS_LIMIT_" + maxDrivers);
		}
	}

	private int getNumDriversLimit() {
		try {
			EPreferenciasServidor ePreferences = (EPreferenciasServidor) this.getEntity("EPreferenciasServidor");
			String snum = ePreferences.getValue(EPreferenciasServidor.ANALYZE_MAXDRIVERS, TableEntity.getEntityPrivilegedId(ePreferences));
			return Integer.parseInt(snum);
		} catch (Exception ex) {
			InfractionService.logger.error(null, ex);
			return 1000;
		}
	}

	public void shutdown() {
		InfractionAnalyzerInServer.getInstance((IOpentachServerLocator) this.getLocator()).shutdown();
	}
}
