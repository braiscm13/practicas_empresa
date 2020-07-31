package com.opentach.server.util.db;

import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.OntimizeConnection;
import com.opentach.server.AbstractOpentachServerLocator;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

/**
 * Allows to create and manage a database connection pool. Parameters of 'database.properties' are parsed here.
 *
 * @see #DatabaseConnectionManager(boolean, boolean, URL, URL, boolean)
 *
 * @author Imatia Innovation
 */
public class HikariDatabaseConnectionManager extends DatabaseConnectionManager {


	private static final Logger logger = LoggerFactory.getLogger(HikariDatabaseConnectionManager.class);


	public HikariDatabaseConnectionManager(boolean statisticViewer, boolean connectionsInfoToFile, java.net.URL urlPropDB, java.net.URL urlAutonumerical) {
		super(statisticViewer, connectionsInfoToFile, urlPropDB, urlAutonumerical);
	}

	public HikariDatabaseConnectionManager(Hashtable parameters) throws Exception {
		super(parameters);
	}


	public HikariDatabaseConnectionManager(java.net.URL urlDBProp, java.net.URL urlAutonumerical, int minConnections, int maxConnections, int connectionIncrement,
			long timeoutBlockConnection, long timeoutConnection, boolean statisticViewer) throws Exception {
		super(urlDBProp, urlAutonumerical, minConnections, maxConnections, connectionIncrement, timeoutBlockConnection, timeoutConnection, statisticViewer);
	}


	@Override
	protected Object createDataSource(String dataSourceName, Properties prop) throws Exception {
		return AbstractOpentachServerLocator.getLocator().getBean(DataSource.class);
	}

	@Override
	public int getOntimizeConnectionNumber() {
		HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource) AbstractOpentachServerLocator.getLocator().getBean(DataSource.class)).getHikariPoolMXBean();
		return hikariPoolMXBean.getTotalConnections();// getActiveConnections() + hikariPoolMXBean.getIdleConnections();
	}

	@Override
	public int getLockedOntimizeConnectionNumber() {
		HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource) AbstractOpentachServerLocator.getLocator().getBean(DataSource.class)).getHikariPoolMXBean();
		return hikariPoolMXBean.getActiveConnections();
	}

	/**
	 * Unlock the connection
	 *
	 * @param ontimizeConnection
	 *            Connection to unlock
	 */
	@Override
	public void unLock(OntimizeConnection ontimizeConnection) {

		if ((ontimizeConnection != null) && (ontimizeConnection.isLocked() || this.useJ2EEConnection)) {
			try {
				/**
				 * Synchronize the connections check thread and the free connections thread if it is possible
				 */
				if ((ontimizeConnection.getConnection() != null) && !ontimizeConnection.getConnection().getAutoCommit()) {
					HikariDatabaseConnectionManager.logger.warn(
							"Connection pool has received a connection with autocommit set to FALSE. The locked connection stack is: {}." + "If it's NULL change com.ontimize.db.OntimizeConnection logger to debug level ",
							ontimizeConnection.getConnectionLockStack());

					if (DatabaseConnectionManager.AUTOCOMMIT_ON_UNLOCK) {
						ontimizeConnection.getConnection().setAutoCommit(true);
					}
				}
			} catch (final Exception e) {
				HikariDatabaseConnectionManager.logger.error(null, e);
			}
		}

		// If it uses JEE then close the connection
		try {
			if (this.useJ2EEConnection) {
				HikariDatabaseConnectionManager.logger.debug("Closing JEE connection");
				ontimizeConnection.close();
			}
		} catch (final Exception e) {
			HikariDatabaseConnectionManager.logger.error(null, e);
		}

		if ((ontimizeConnection != null) && ontimizeConnection.isLocked()) {
			ontimizeConnection.setLock(false);
			// Check the lock connection time and calculate the average between
			// all the connections
			if (this.lockTime.size() >= this.connections.size()) {
				final double dRandom = Math.random();
				this.lockTime.remove((int) (dRandom * this.lockTime.size()));
				// Add the connection info
				final Long lLockTime = new Long(System.currentTimeMillis() - ontimizeConnection.getLastUseTime());
				this.lockTime.add(lLockTime);
			} else {
				// Adds connection info
				final Long lLockTime = new Long(System.currentTimeMillis() - ontimizeConnection.getLastUseTime());
				this.lockTime.add(lLockTime);
			}
		}

	}
}
