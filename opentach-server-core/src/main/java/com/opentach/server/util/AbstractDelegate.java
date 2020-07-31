package com.opentach.server.util;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;

public class AbstractDelegate {
	/** The locator. */
	private IOpentachServerLocator locator;

	/**
	 * The Constructor.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public AbstractDelegate(IOpentachServerLocator locator) {
		super();
		this.locator = locator;
	}

	/**
	 * Gets the locator.
	 *
	 * @return the locator
	 */
	protected IOpentachServerLocator getLocator() {
		return this.locator;
	}

	public void setLocator(IOpentachServerLocator locator) {
		this.locator = locator;
	}

	protected DatabaseConnectionManager getConnectionManager() {
		return this.locator.getConnectionManager();
	}

	/**
	 * Gets the entity reference.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the entity reference
	 */
	protected TransactionalEntity getEntity(String entityName) {
		return (TransactionalEntity) this.getLocator().getEntityReferenceFromServer(entityName);
	}

	protected <T extends UAbstractService> T getService(Class<T> clazz) throws Exception {
		return this.getLocator().getService(clazz);
	}

	/**
	 * Gets the session id.
	 *
	 * @param sessionID
	 *            the session id
	 * @param entity
	 *            the entity
	 * @return the session id
	 */
	protected int getSessionId(int sessionID, TransactionalEntity entity) {
		if (sessionID < 0) {
			return TableEntity.getEntityPrivilegedId((Entity) entity);
		}
		return sessionID;
	}

	protected int getEntityPrivilegedId(TransactionalEntity entity) {
		return TableEntity.getEntityPrivilegedId((Entity) entity);
	}

	public <T> T getBean(Class<T> clazz) {
		return this.getLocator().getBean(clazz);
	}


}
