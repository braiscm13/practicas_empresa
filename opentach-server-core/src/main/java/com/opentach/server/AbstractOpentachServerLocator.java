package com.opentach.server;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ontimize.db.AdvancedEntity;
import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.DirectSQLQueryEntity;
import com.ontimize.db.Entity;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.server.security.DatabaseUserInformationService;
import com.ontimize.locator.SecureReferenceLocator;
import com.ontimize.util.ParseUtils;
import com.opentach.common.exception.OpentachRuntimeException;
import com.opentach.common.interfaces.IOpentachLocator;
import com.opentach.common.process.ITachoFileProcessService;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.user.IUserData;
import com.opentach.server.demo.TimerTaskNotifyDemoExpiration;
import com.opentach.server.process.TachoFileProcessService;
import com.opentach.server.sessionstatus.SessionStatusService;
import com.opentach.server.util.db.HikariDatabaseConnectionManager;
import com.opentach.server.util.jee.TemporaryOJeeEntityInvocationHandler;
import com.opentach.server.util.spring.OpentachLocatorReferencer;
import com.utilmize.server.UReferenceSeeker;

public abstract class AbstractOpentachServerLocator extends UReferenceSeeker implements IOpentachLocator, IJRConstants, IOpentachServerLocator {

	private static final Logger						logger			= LoggerFactory.getLogger(AbstractOpentachServerLocator.class);
	public static final Locale						DEFAULTLOCALE	= new Locale("es", "ES");

	protected static AbstractOpentachServerLocator	locator;

	protected final Map<Integer, IUserData>			userDataCache		= new Hashtable<>();
	protected final Map<String, Integer>			userSessionCache	= new Hashtable<>();
	private ApplicationContext						springContext;

	public AbstractOpentachServerLocator(int port, Hashtable<String, Object> params) throws Exception {
		super(port, AbstractOpentachServerLocator.fixRandomGeneratorTime(params));
		this.updateMaxUsers(params);
		AbstractOpentachServerLocator.locator = this;
		// Arranco procesado de ficheros
		try {
			final boolean autoprocess = ParseUtils.getBoolean((String) params.get(ITachoFileProcessService.FILE_PROCESS), false);
			this.getService(TachoFileProcessService.class).setProcessEnabled(autoprocess);
		} catch (final Exception exception) {
			AbstractOpentachServerLocator.logger.error(null, exception);
		}

		// start demo notification task
		this.launchTaskNotifyDemoExpiration();

		AbstractOpentachServerLocator.logger.info("Clearing old sessions ...");
		this.getService(SessionStatusService.class).clearSessions();
	}

	protected void onSessionStarted(String user, int id) throws Exception {
		this.userSessionCache.put(user, id);
		this.getUserData(id);
		this.getService(SessionStatusService.class).onSessionStarted(id);
	}

	private void updateMaxUsers(Hashtable<String, Object> params) {
		try {
			this.maximumUsers = ParseUtils.getInteger((String) params.get(SecureReferenceLocator.MAXUSERS), 100);
			new Thread("pseudorandom-identifier-generator-thread") {
				@Override
				public void run() {
					Random random = new Random();
					AbstractOpentachServerLocator.logger.info("reStarting pseudorandom identifier generator for {}...", AbstractOpentachServerLocator.this.maximumUsers);
					for (int i = 0; i < AbstractOpentachServerLocator.this.maximumUsers; i++) {
						Integer idI = random.nextInt(Integer.MAX_VALUE);
						if (AbstractOpentachServerLocator.this.userIdList.contains(idI)) {
							i--;
						} else {
							AbstractOpentachServerLocator.this.userIdList.add(idI);
						}
					}
				}
			}.start();

		} catch (Exception ex) {
			AbstractOpentachServerLocator.logger.error("Error in 'maxusers' parameter. ", ex);
		}
	}

	private static Hashtable fixRandomGeneratorTime(Hashtable<String, Object> params) {
		Hashtable res = new Hashtable();
		res.putAll(params);
		res.put(SecureReferenceLocator.MAXUSERS, "1");
		return res;
	}

	private void launchTaskNotifyDemoExpiration() {
		final Timer timer = new Timer("NotifyDemoExpiration");
		final TimerTask tt = new TimerTaskNotifyDemoExpiration(this);
		timer.schedule(tt, this.getNightDelay(), DateTools.MILLISECONDS_PER_DAY);// 24 hours
		// t.schedule(tt, 0, DateTools.MILLISECONDS_PER_DAY);// 24 hours
	}

	protected long getNightDelay() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		if (new Date().after(cal.getTime())) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return cal.getTimeInMillis() - System.currentTimeMillis();
	}

	@Override
	public void freeServerResources() {
		// stop file processing
		AbstractOpentachServerLocator.logger.info("Stopping file processing");
		try {
			this.getService(TachoFileProcessService.class).shutdown();
		} catch (final Exception exception) {
			AbstractOpentachServerLocator.logger.error(null, exception);
		}
		super.freeServerResources();
	}

	@Override
	public Locale getUserLocale(int sessionID) throws Exception {
		Locale locale = AbstractOpentachServerLocator.DEFAULTLOCALE;
		final IUserData du = this.getUserData(sessionID);
		if (du != null) {
			locale = du.getLocale();
		}
		if (locale == null) {
			locale = AbstractOpentachServerLocator.DEFAULTLOCALE;
		}
		return locale;
	}

	@Override
	public void setLocale(Locale locale, int sessionID) throws Exception {
		final IUserData du = this.userDataCache.get(sessionID);
		if (du != null) {
			du.setLocale(locale);
		}
	}

	public static AbstractOpentachServerLocator getLocator() {
		return AbstractOpentachServerLocator.locator;
	}

	@Override
	public void setSessionIdLastAccessTime(long time, int sessionId) {
		super.setSessionIdLastAccessTime(time, sessionId);
		this.getService(SessionStatusService.class).setSessionIdLastAccessTime(time, sessionId);
	}

	// Used to multitenant config
	@Override
	protected DatabaseConnectionManager createConnectionManager(Hashtable databaseParams) throws Exception {
		this.doIntegrationSpringOnt(databaseParams);
		return new HikariDatabaseConnectionManager(databaseParams);
	}

	@Override
	protected DatabaseConnectionManager createConnectionManager(boolean bStatisticViewer, URL dbPropertiesURL, URL autonumericalURL) throws Exception {
		throw new OpentachRuntimeException("no se puede llamar a este metodo porque perderiamos la integracion con spring");
	}

	private void doIntegrationSpringOnt(Hashtable databaseParams) {
		AbstractOpentachServerLocator.locator = this; // se pone aqui porque es el primer metodo al que llama super
		this.springContext = (ApplicationContext) databaseParams.get(OpentachLocatorReferencer.SPRING_CONTEX);
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		return this.springContext.getBean(clazz);
	}

	public Object getBean(String beanName) {
		return this.springContext.getBean(beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.springContext = context;
	}

	@Override
	public boolean hasSession(int sessionId) {
		boolean hasSession = false;
		try {
			hasSession = super.hasSession(sessionId);
		} finally {
			this.setSecurityContextHolder(hasSession, sessionId);
		}
		return hasSession;
	}

	@Override
	public IUserData getUserData(int sessionID) throws Exception {
		if (sessionID < 0) {
			return null;
		}
		if (!this.hasSession(sessionID)) {
			return null;
		}
		IUserData du = this.userDataCache.get(sessionID);
		if (du == null) {
			du = this.getUserDataFromDB(sessionID);
			this.userDataCache.put(sessionID, du);
		}
		return du;
	}

	@Override
	public IUserData getUserData(String userLogin) throws Exception {
		Integer sesId = this.userSessionCache.get(userLogin);
		if (sesId == null) {
			return null;
		}
		return this.getUserData(sesId);
	}

	@Override
	public int getSessionIdForLogin(String userLogin) {
		return this.userSessionCache.get(userLogin);
	}

	protected abstract IUserData getUserDataFromDB(int sessionID) throws Exception;

	private void setSecurityContextHolder(boolean hasSession, int sessionId) {
		try {
			if (hasSession) {
				String login = this.getUser(sessionId);
				DatabaseUserInformationService dbUserService = (com.ontimize.jee.server.security.DatabaseUserInformationService) this.springContext
						.getBean("databaseUserInformationService");
				UserInformation userInfo = dbUserService.loadUserByUsername(login);

				Authentication auth = new UsernamePasswordAuthenticationToken(userInfo, userInfo.getPassword());
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				SecurityContextHolder.getContext().setAuthentication(null);
			}
		} catch (Exception err) {
			AbstractOpentachServerLocator.logger.error("E_SETTING_SECURITY_CONTEXT_HOLDER", err);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
	}

	@Override
	protected Entity getEntity(String entityName, boolean checkDataBase) {
		if (entityName.equalsIgnoreCase("")) {
			return null;
		}
		if (this.loadedEntities.get(entityName) != null) {
			return (Entity) this.loadedEntities.get(entityName);
		}
		// Checks if reference already exists
		if (entityName.startsWith("ojee.")) {
			Entity proxyEntity = (Entity) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), this.getOntimizeEntityInterfaces(),
					new TemporaryOJeeEntityInvocationHandler(entityName.substring("ojee.".length())));
			synchronized (this.loadedEntities) {
				this.loadedEntities.put(entityName, proxyEntity);
			}
			return proxyEntity;
		}
		return super.getEntity(entityName, checkDataBase);
	}

	protected Class<?>[] getOntimizeEntityInterfaces() {
		return new Class<?>[] { Entity.class, AdvancedEntity.class, DirectSQLQueryEntity.class };
	}

}
