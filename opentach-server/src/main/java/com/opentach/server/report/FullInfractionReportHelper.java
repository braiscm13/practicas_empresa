package com.opentach.server.report;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.imatia.tacho.infraction.Infraction;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.SecureReferenceLocator;
import com.opentach.common.report.FullInfractionReportStatus;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.activities.InfractionAnalyzerInServer;
import com.opentach.server.activities.InfractionDatabaseSaver;
import com.opentach.server.activities.InfractionSubmit;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.PermissionsUtil;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class FullInfractionReportHelper extends AbstractDelegate {

	private static final Logger								logger								= LoggerFactory.getLogger(FullInfractionReportHelper.class);

	private static final String								FULL_INFRACTION_REPORT_PERMISSION	= "FullInfractionReport";
	private static boolean									running;

	private final Thread									execThread;
	private final LinkedBlockingQueue<FullReportParameters>	queue;

	private int												driverSize;
	private int												processed;
	private long											startTime;

	public FullInfractionReportHelper(OpentachServerLocator locator) {
		super(locator);
		FullInfractionReportHelper.running = false;
		this.queue = new LinkedBlockingQueue<>(1);
		this.execThread = new Thread(new FullReportTask(), "Full report task thread");
		this.execThread.setDaemon(true);
		this.execThread.start();
	}

	public FullInfractionReportStatus getStatus(int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission((SecureReferenceLocator) this.getLocator(), sessionId, FullInfractionReportHelper.FULL_INFRACTION_REPORT_PERMISSION, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		return new FullInfractionReportStatus(FullInfractionReportHelper.running, this.driverSize, this.processed, System.currentTimeMillis() - this.startTime);
	}

	public void doReport(Date from, Date to, int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission((SecureReferenceLocator) this.getLocator(), sessionId, FullInfractionReportHelper.FULL_INFRACTION_REPORT_PERMISSION, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		if (FullInfractionReportHelper.running) {
			throw new Exception("Alreadey running");
		}
		FullInfractionReportHelper.running = true;
		this.queue.add(new FullReportParameters(from, to));
	}

	private class FullReportTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					final FullReportParameters parameters = FullInfractionReportHelper.this.queue.take();
					FullInfractionReportHelper.running = true;
					FullInfractionReportHelper.this.driverSize = 0;
					FullInfractionReportHelper.this.processed = 0;
					FullInfractionReportHelper.this.startTime = System.currentTimeMillis();
					new OntimizeConnectionTemplate<Void>() {
						@Override
						protected Void doTask(Connection conn) throws UException {
							try {

								// 1.- query contracts and process
								List<InfractionSubmit<List<Infraction>>> tasks = FullReportTask.this.queryAndSubmitTasks(parameters, conn);
								// 2.- truncate result table
								new UpdateJdbcTemplate().execute(conn, "truncate table CDINFRACCIONES_FULL");
								// 3.- insert results
								for (InfractionSubmit<List<Infraction>> task : tasks) {
									new InfractionDatabaseSaver().saveInfractions(task.getFuture().get(6, TimeUnit.HOURS), task.getContract(), task.getCif(), task.getDriver(),
											"CDINFRACCIONES_FULL", conn);
									FullInfractionReportHelper.this.processed++;
								}
								return null;
							} catch (Exception ex) {
								throw new UException(ex);
							}
						}
					}.execute(FullInfractionReportHelper.this.getConnectionManager(), true);

				} catch (Exception ex) {
					FullInfractionReportHelper.logger.error(null, ex);
				} finally {
					FullInfractionReportHelper.running = false;
				}
			}
		}

		private List<InfractionSubmit<List<Infraction>>> queryAndSubmitTasks(final FullReportParameters parameters, Connection conn) throws Exception {
			return new QueryJdbcTemplate<List<InfractionSubmit<List<Infraction>>>>() {
				@Override
				protected List<InfractionSubmit<List<Infraction>>> parseResponse(java.sql.ResultSet rs) throws UException {
					try {
						InfractionAnalyzerInServer serverAnalyzer = InfractionAnalyzerInServer.getInstance(FullInfractionReportHelper.this.getLocator());
						List<InfractionSubmit<List<Infraction>>> res = new ArrayList<>();
						List<Object> driversId = new ArrayList<Object>();
						Object lastReq = null;
						Object lastCif = null;
						while (rs.next()) {
							Object curReq = rs.getObject("NUMREQ");
							Object driverId = rs.getObject("IDCONDUCTOR");
							Object curCif = rs.getObject("CIF");
							if ((lastReq == null) || (!lastReq.equals(curReq)) || (lastCif == null) || (!lastCif.equals(curCif))) {
								if (!driversId.isEmpty() && (lastReq != null)) {
									res.addAll(serverAnalyzer.analyzeFuture(lastReq, lastCif, driversId, parameters.getFrom(), parameters.getTo(), AnalyzerEngine.DEFAULT));
								}
								lastReq = curReq;
								lastCif = curCif;
								driversId.clear();
							}
							driversId.add(driverId);
							FullInfractionReportHelper.this.driverSize++;
						}
						if (!driversId.isEmpty() && (lastReq != null) && (lastCif != null)) {
							res.addAll(serverAnalyzer.analyzeFuture(lastReq, lastCif, driversId, parameters.getFrom(), parameters.getTo(), AnalyzerEngine.DEFAULT));
						}
						return res;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(conn, new Template("sql/fullInfractionReport/queryDrivers.sql").getTemplate(), parameters.getTo());
		}
	}

	private static class FullReportParameters {
		private final Date	from;
		private final Date	to;

		public FullReportParameters(Date from, Date to) {
			super();
			this.from = from;
			this.to = to;
		}

		public Date getFrom() {
			return this.from;
		}

		public Date getTo() {
			return this.to;
		}
	}
}
