package com.opentach.server.webservice.rest;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.OntimizeConnection;
import com.ontimize.locator.SecureReferenceLocator;
import com.opentach.server.AbstractOpentachServerLocator;
import com.utilmize.tools.exception.UException;

/**
 * The Class OntimizeConnectionTemplate.
 *
 * @param <T>
 *            the generic type
 */
public abstract class OntimizeSessionConnectionTemplate<T> {

	private static final Logger			logger		= LoggerFactory.getLogger(OntimizeSessionConnectionTemplate.class);

	private final AbstractOpentachServerLocator	locator		= AbstractOpentachServerLocator.getLocator();

	/**
	 * The sessionId
	 */
	private int							sessionId	= -1;

	private final String				user;
	private final String				psswd;

	/**
	 * The Constructor.
	 */
	public OntimizeSessionConnectionTemplate(String user, String psswd) {
		super();
		this.user = user;
		this.psswd = psswd;
	}

	/**
	 * Do task.
	 *
	 * @param con
	 *            the con
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	protected abstract T doTask(Connection con) throws UException;

	/**
	 * Execute.
	 *
	 * @param locator
	 *            the locator
	 * @param autocommit
	 *            the autocommit
	 * @return the t
	 * @throws Throwable
	 *             the throwable
	 */
	public T execute(SecureReferenceLocator locator, boolean autocommit) throws Exception {
		return this.execute(locator.getConnectionManager(), autocommit);
	}

	/**
	 * Execute.
	 *
	 * @param connectionManager
	 *            the connection manager
	 * @param autocommit
	 *            the autocommit
	 * @return the t
	 * @throws Throwable
	 *             the throwable
	 */
	public T execute(DatabaseConnectionManager connectionManager, boolean autocommit) throws Exception {
		OntimizeConnection oconn = null;

		try {
			this.setSessionId(this.locator.startSession(this.user, this.psswd, null));

			oconn = connectionManager.getOntimizeConnection();
			Connection conn = oconn.getConnection();
			if (conn.getAutoCommit() != autocommit) {
				conn.setAutoCommit(autocommit);
			}
			try {
				T res = this.doTask(conn);
				if (!autocommit) {
					conn.commit();
				}
				return res;
			} catch (Throwable error) {
				if (!autocommit) {
					conn.rollback();
				}
				try {
					this.onErrorAfterRollback(error, conn);
				} catch (Exception ex) {
					OntimizeSessionConnectionTemplate.logger.error("error in onErrorAfterRollback", ex);
				}
				if (error instanceof Exception) {
					throw (Exception) error;
				}
				throw new InvocationTargetException(error);
			} finally {
				if (conn != null) {
					if (!conn.getAutoCommit()) {
						conn.setAutoCommit(true);
					}
					this.onFinishedAfterAutocommitTrue(conn);
				}
			}
		} finally {
			if (oconn != null) {
				connectionManager.unLock(oconn);
			}
			if ((this.getSessionId() != -1) && (this.locator != null)) {
				this.locator.endSession(this.sessionId);
			}
		}
	}

	protected void onFinishedAfterAutocommitTrue(Connection con) {
		// do nothing
	}

	protected void onErrorAfterRollback(Throwable error, Connection conn) throws Exception {
		// do nothing
	}

	public int getSessionId() {
		return this.sessionId;
	}

	private void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
}
