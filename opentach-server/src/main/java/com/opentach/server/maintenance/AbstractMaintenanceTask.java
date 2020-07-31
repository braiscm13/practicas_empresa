package com.opentach.server.maintenance;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.TableEntity;
import com.opentach.common.maintenance.MaintenanceStatus.MaintenanceStatusType;
import com.opentach.common.maintenance.MaintenanceStatusTask;
import com.opentach.server.OpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public abstract class AbstractMaintenanceTask implements IMaintenanceTask {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractMaintenanceTask.class);
	private boolean				running;
	private String				currentStatusText;
	private float				currentPercent;
	private boolean				error;
	private Object[]			currentStatusTextParameters;

	public AbstractMaintenanceTask() {
		super();
		this.running = false;
		this.error = false;
	}

	@Override
	public void reset() {
		// if (this.running) {
		// throw new Exception("invalid status");
		// }
		this.running = false;
		this.currentPercent = 0;
		this.currentStatusText = "";
	}

	@Override
	public MaintenanceStatusTask getStatus() {
		return new MaintenanceStatusTask(this.getTaskKey(), this.getCurrentPercent(), this.currentStatusText, this.running, this.error, this.currentStatusTextParameters);
	}

	protected float getCurrentPercent() {
		return this.currentPercent;
	}

	protected abstract MaintenanceStatusType getTaskKey();

	protected void updateStatus(String text, float percent, boolean error, Object... textParameters) {
		this.currentStatusText = text;
		this.currentStatusTextParameters = textParameters;
		this.currentPercent = percent;
		if (percent > 0) {
			this.running = true;
		}
		if (percent == 100) {
			this.running = false;
		}
		this.error = error;
	}

	@Override
	public MaintenanceStatusTask doMaintenance(String backupFolder, Timestamp filterDate) {
		try {
			this.error = false;
			this.doInnerMaintenance(backupFolder, filterDate);
		} catch (Exception ex) {
			this.error = true;
			this.currentStatusText = ex.getMessage();
			AbstractMaintenanceTask.logger.error(null, ex);
		}
		return this.getStatus();
	}

	protected abstract void doInnerMaintenance(String backupFolder, Timestamp filterDate) throws Exception;

	protected Entity getEntityReference(String entityName) {
		return OpentachServerLocator.getLocator().getEntityReferenceFromServer(entityName);
	}

	protected int getSessionId(Entity entity) {
		return TableEntity.getEntityPrivilegedId(entity);
	}

	protected static class DeleteTask implements Callable<Integer> {

		private final AtomicInteger	count;
		private final String		sql;
		private final Object[]		parameters;

		public DeleteTask(AtomicInteger count, String sql, Object... parameters) {
			this.count = count;
			this.sql = sql;
			this.parameters = parameters;
		}

		@Override
		public Integer call() throws Exception {
			try {
				return new OntimizeConnectionTemplate<Integer>() {

					@Override
					protected Integer doTask(Connection con) throws UException {

						try {
							int actions = new UpdateJdbcTemplate().execute(con, DeleteTask.this.sql, DeleteTask.this.parameters);
							if (DeleteTask.this.count != null) {
								DeleteTask.this.count.incrementAndGet();
							}
							return actions;
						} catch (Exception ex) {
							throw new UException(ex);
						}
					}
				}.execute(OpentachServerLocator.getLocator(), true);
			} catch (Exception error) {
				AbstractMaintenanceTask.logger.error(null, error);
				throw error;
			}
		}

	}
}
