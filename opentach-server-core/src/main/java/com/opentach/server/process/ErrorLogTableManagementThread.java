package com.opentach.server.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class ErrorLogTableManagementThread extends Thread {

	private static final Logger					logger	= LoggerFactory.getLogger(ErrorLogTableManagementThread.class);

	private final LinkedBlockingQueue<String>	tablesToDrop;
	private final IOpentachServerLocator		locator;

	public ErrorLogTableManagementThread(IOpentachServerLocator locator) {
		super("Table dropped thread");
		this.tablesToDrop = new LinkedBlockingQueue<String>();
		this.locator = locator;
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				String tableName = this.tablesToDrop.take();
				this.dropErrorLogTable(tableName);
			} catch (Throwable ex) {
				ErrorLogTableManagementThread.logger.error(null, ex);
			}
		}
	}

	public void addTableToDrop(String tableName) {
		if (tableName == null) {
			return;
		}
		this.tablesToDrop.offer(tableName);
	}

	private void dropErrorLogTable(final String tableName) throws Exception {
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException {
				ErrorLogTableManagementThread.logger.debug("Dropping error log table {}", tableName);
				try (PreparedStatement statement = con.prepareStatement("DROP TABLE " + tableName + " PURGE")) {
					statement.execute();
				} catch (SQLException error) {
					ErrorLogTableManagementThread.logger.error(null, error);
				}
				return null;
			}

		}.execute(this.locator.getConnectionManager(), true);
	}

	public String createErrorLogTable(final String tableName) throws Exception {

		return new OntimizeConnectionTemplate<String>() {

			@Override
			protected String doTask(Connection con) throws UException {
				String errorLogTableName = "ERR_LOG_" + System.currentTimeMillis() + "" + FileProcessTaskInsertDbHelper.posfix.incrementAndGet();
				if (errorLogTableName.length() > 30) {
					FileProcessTaskInsertDbHelper.posfix.set(0);
					errorLogTableName = "ERR_LOG_" + System.currentTimeMillis() + "" + FileProcessTaskInsertDbHelper.posfix.incrementAndGet();
				}
				ErrorLogTableManagementThread.logger.debug("Creating error log table {}", errorLogTableName);

				// DatabaseMetaData meta = conn.getMetaData();
				// ResultSet res = meta.getTables(null, null, errorLogTableName, new String[] { "TABLE" });
				// if (!res.next()) {
				try (PreparedStatement statement = con
						.prepareStatement("BEGIN DBMS_ERRLOG.CREATE_ERROR_LOG('" + tableName + "', '" + errorLogTableName + "'); END;")) {
					statement.execute();
				} catch (Exception err) {
					throw new UException(err);
				}
				// }
				return errorLogTableName;
			}
		}.execute(this.locator.getConnectionManager(), true);

	}

}
