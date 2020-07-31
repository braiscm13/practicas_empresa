package com.opentach.server.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EPreferenciasServidor;
import com.utilmize.server.services.UAbstractService;

public class AbstractSpringDelegate {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractSpringDelegate.class);
	@Autowired
	private ILocatorReferencer locatorRef;

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
	public AbstractSpringDelegate() {
		super();
	}

	/**
	 * Gets the locator.
	 *
	 * @return the locator
	 */

	public IOpentachServerLocator getLocator() {
		return this.locatorRef.getLocator();
	}


	protected DatabaseConnectionManager getConnectionManager() {
		return this.getLocator().getConnectionManager();
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

	public String getSettingString(String key, String defaultValue) {
		try {
			final EPreferenciasServidor eSettings = (EPreferenciasServidor) this.getEntity("EPreferenciasServidor");
			final String value = eSettings.getValue(key, this.getEntityPrivilegedId(eSettings));
			return value == null ? defaultValue : value;
		} catch (final Exception err) {
			AbstractSpringDelegate.logger.error(null, err);
			return defaultValue;
		}
	}

	public boolean getSettingBoolean(String key, boolean defaultValue) {
		return ParseUtilsExtended.getBoolean(this.getSettingString(key, null), defaultValue);
	}

}
