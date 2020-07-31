package com.opentach.server.sessionstatus;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.sessionstatus.ClientStatusDto;
import com.opentach.common.sessionstatus.StatisticsDto;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.BatchUpdateJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class StatisticsManager implements Runnable {

	private static final Logger							logger	= LoggerFactory.getLogger(StatisticsManager.class);

	private final Thread								processThread;
	private final LinkedBlockingQueue<ClientStatusDto>	eventQueue;
	private final IOpentachServerLocator				locator;

	public StatisticsManager(IOpentachServerLocator opentachServerLocator) {
		super();
		this.locator = opentachServerLocator;
		this.eventQueue = new LinkedBlockingQueue<ClientStatusDto>();
		this.processThread = new Thread(this, "statistics-thread");
		this.processThread.setDaemon(true);
		this.processThread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				ClientStatusDto dto = this.eventQueue.take();
				List<StatisticsDto> statistics = dto.getStatistics();
				if ((statistics != null) && (statistics.size() > 0)) {
					this.dumpStatistics(dto);
				}
			} catch (Throwable error) {
				StatisticsManager.logger.error(null, error);
			}
		}
	}

	private void dumpStatistics(final ClientStatusDto dto) throws Exception {
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					StatisticsManager.this.dumStatistics(dto, con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

		}.execute(this.locator.getConnectionManager(), false);
	}

	private void dumStatistics(final ClientStatusDto dto, Connection con) throws Exception {
		// insertar en temporal
		new BatchUpdateJdbcTemplate<StatisticsDto>(dto.getStatistics()) {

			@Override
			protected Object[] beanToParametersArray(int idx, StatisticsDto stat) {
				Object[] array = new Object[6];
				array[0] = dto.getUser();
				array[1] = dto.getStartupTime();
				array[2] = stat.getFormManager();
				array[3] = stat.getForm();
				array[4] = stat.getAction();
				array[5] = stat.getClickCount().get();
				return array;
			}
		}.execute(con, "insert into CDLOGSESIONSTAT_TEMP (USUARIO, F_INI, MANAGER, FORM, ACTION, CLICKS) values (?,?,?,?,?,?)");

		new UpdateJdbcTemplate().execute(con, new Template("sql/sessionstatus/StatisticsManagerMerge.sql").getTemplate());
	}

	public void logOpenSession(final AbstractStatusDto status) throws Exception {
		if (status == null) {
			return;
		}
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					TableEntity eLogSession = (TableEntity) StatisticsManager.this.locator.getEntityReferenceFromServer("ELogSesion");
					TableEntity eUsuario = (TableEntity) StatisticsManager.this.locator.getEntityReferenceFromServer("Usuario");
					Date dNow = status.getStartupTime();
					String user = status.getUser();
					String sourceAddress = status.getSourceAddress();
					// insert
					Hashtable<Object, Object> av = EntityResultTools.keysvalues("USUARIO", user, "F_INI", dNow, "SOURCEADDRESS", sourceAddress, "APP", status.getApp(), "SESSIONID",
							String.valueOf(status.getSessionId()));
					eLogSession.insert(av, TableEntity.getEntityPrivilegedId(eLogSession), con);
					av = EntityResultTools.keysvalues("LASTLOGINDATE", new Date());
					Hashtable<Object, Object> kv = EntityResultTools.keysvalues("USUARIO", user);
					eUsuario.update(av, kv, TableEntity.getEntityPrivilegedId(eUsuario), con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.locator.getConnectionManager(), false);
	}

	public void logCloseSession(final AbstractStatusDto status) throws Exception {
		if (status == null) {
			return;
		}
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					// update
					String sql = "UPDATE CDLOGSESION SET F_FIN = SYSDATE WHERE USUARIO = ? AND TO_CHAR(F_INI,\'dd/mm/yyyy hh24:mi:ss\') = ? AND F_FIN IS NULL AND SESSIONID =?";
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					new UpdateJdbcTemplate().execute(con, sql, status.getUser(), sdf.format(status.getStartupTime()), String.valueOf(status.getSessionId()));
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.locator.getConnectionManager(), false);
	}

	public void clearSessions() {
		try {
			new OntimizeConnectionTemplate<Void>() {
				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						String sql = "UPDATE CDLOGSESION SET F_FIN = SYSDATE WHERE F_FIN IS NULL";
						new UpdateJdbcTemplate().execute(con, sql);
						return null;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(this.locator.getConnectionManager(), false);
		} catch (Exception e) {
			StatisticsManager.logger.error(null, e);
		}
	}

	public void updateStatistics(AbstractStatusDto status) {
		if (status instanceof ClientStatusDto) {
			this.eventQueue.offer((ClientStatusDto) status);
		}
	}
}
