package com.opentach.client;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.AdvancedEntity;
import com.ontimize.db.DirectSQLQueryEntity;
import com.ontimize.db.Entity;
import com.ontimize.gui.ClientWatch;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.client.util.IClientLocatorEventListener;
import com.opentach.client.util.OntimizeJEESessionStarter;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.interfaces.IOpentachLocator;
import com.opentach.common.user.IUserData;
import com.utilmize.client.UClientPermissionReferenceLocator;

public abstract class AbstractOpentachClientLocator extends UClientPermissionReferenceLocator implements UserInfoProvider {

	private static final Logger						logger						= LoggerFactory.getLogger(AbstractOpentachClientLocator.class);

	private final List<IClientLocatorEventListener>	eventListeners;
	private IUserData								userData					= null;
	private String									cdoDscrCache				= null;
	private final Map<Class<?>, Object>				localServices;

	protected OntimizeJEESessionStarter				ontimizeJeeSessionStarter	= null;

	public AbstractOpentachClientLocator(final Hashtable params) {
		super(params);
		this.localServices = new HashMap<>();
		this.eventListeners = new ArrayList<>();
		this.ontimizeJeeSessionStarter = new OntimizeJEESessionStarter();
	}

	public void addLocatorEventListener(final IClientLocatorEventListener listener) {
		this.eventListeners.add(listener);
	}

	protected void notifySessionStartEvent(final int sessionId) {
		for (final IClientLocatorEventListener evl : this.eventListeners) {
			evl.onSessionStarted(sessionId, this);
		}
	}

	protected void notifySessionCloseEvent(final int sessionId) {
		for (final IClientLocatorEventListener evl : this.eventListeners) {
			evl.onSessionClosed(sessionId, this);
		}
	}

	@Override
	public void endSession(final int id) throws Exception {
		super.endSession(id);
		this.notifySessionCloseEvent(id);
	}

	@Override
	public int startSession(final String user, final String password, final ClientWatch cw) throws Exception {
		final Chronometer chrono = new Chronometer().start();
		final int id = super.startSession(user, password, cw);
		AbstractOpentachClientLocator.logger.info("Tiempo en iniciar sesión (Ontimize): " + (chrono.elapsedMs()) + " milisegundos.");
		this.startSessionOJEE(user, password);
		AbstractOpentachClientLocator.logger.info("Tiempo en iniciar sesión (OntimizeJEE): " + (chrono.elapsedMs()) + " milisegundos.");
		if (id > 0) {
			this.userData = null;
			try {
				this.onSessionStarted(user);
			} catch (final Exception e) {
				AbstractOpentachClientLocator.logger.error(null, e);
			}
			this.notifySessionStartEvent(id);
		}
		AbstractOpentachClientLocator.logger.info("Tiempo en iniciar sesión (TOTAL): " + chrono.stopMs() + " milisegundos.");
		return id;
	}

	protected void startSessionOJEE(final String user, final String password) {
		this.ontimizeJeeSessionStarter.startSession(user, password);
	}

	protected abstract void onSessionStarted(String userName) throws Exception;

	@Override
	public IUserData getUserData() throws Exception {
		if ((this.userData == null) && (this.referenceLocatorServer != null)) {
			this.userData = ((IOpentachLocator) this.referenceLocatorServer).getUserData(this.getSessionId());
		}
		return this.userData;
	}

	public void reloadUserData() throws Exception {
		this.userData = null;
		this.getUserData();
	}

	@Override
	public String getUserDescription() {
		if (this.cdoDscrCache == null) {
			this.cdoDscrCache = UserInfoProvider.getUserDescription(this);
		}
		return this.cdoDscrCache;
	}

	@Override
	public <T> T getRemoteService(final Class<T> cl) throws Exception {
		final String remoteReferenceId = (String) ReflectionTools.getFieldValue(cl, "ID");
		return (T) this.getRemoteReference(remoteReferenceId, this.getSessionId());
	}

	@Override
	public <T> T getLocalService(final Class<T> cl) {
		T localService = (T) this.localServices.get(cl);
		if (localService == null) {
			try {
				localService = cl.newInstance();
			} catch (final Exception ex) {
				throw new RuntimeException(ex);
			}
			this.localServices.put(cl, localService);
		}
		return localService;
	}

	@Override
	public Entity getEntityReference(final String entityName) throws Exception {
		if (entityName.startsWith("ojee.")) {
			return (Entity) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), this.getOntimizeEntityInterfaces(),
					new TemporaryOJeePatchHessianEntityInvocationHandler(entityName.substring("ojee.".length())));
		}
		return super.getEntityReference(entityName);
	}

	protected Class<?>[] getOntimizeEntityInterfaces() {
		return new Class<?>[] { Entity.class, AdvancedEntity.class, DirectSQLQueryEntity.class };
	}
}
