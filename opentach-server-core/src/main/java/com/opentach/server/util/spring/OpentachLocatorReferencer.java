package com.opentach.server.util.spring;

import java.io.IOException;
import java.net.URL;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.TimeZone;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.tool.ILogger;
import com.ontimize.db.Autonumerical;
import com.ontimize.db.DBErrorMessages;
import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.gui.ServerLauncher;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.locator.ReferenceLocator;
import com.ontimize.locator.SecureReferenceLocator;
import com.opentach.common.util.EncryptDecryptUtils;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.UOracleSQLStatementHandler;
import com.utilmize.server.tools.sqltemplate.AbstractJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.OracleParameterSetter;

@Component
public class OpentachLocatorReferencer implements ILocatorReferencer, InitializingBean {
	private static final Logger		logger				= LoggerFactory.getLogger(OpentachLocatorReferencer.class);
	private static final Logger		loggerTachoLibrary	= LoggerFactory.getLogger(TachoFile.class);

	public static final String		SPRING_CONTEX		= "SPRING_CONTEXT";

	private ExtendedServerLauncher	launcher;
	private boolean					running;

	@Autowired
	private ApplicationContext		springContext;
	@Autowired
	private ServletContext			servletContext;
	@Value("${dbmessages.url:com/opentach/server/prop/dbmessages.properties}")
	private String					dbmessagesUrl;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.startupOntimizeServer();
	}

	private void startupOntimizeServer() {
		this.setOntimizeParameters();
		try {
			this.initializer();
		} catch (final ServletException e) {
			OpentachLocatorReferencer.logger.error(null, e);
		}
	}

	public void initializer() throws ServletException {

		if (!this.running) {
			final String prop = this.servletContext.getInitParameter(ServerLauncherServlet.CONTEXT_SERVER_PROPERTIES);
			if (prop == null) {
				throw new ServletException(this.getClass().toString() + " Parameter 'ServerProperties' is required");
			}
			try {
				this.launcher = new ExtendedServerLauncher(prop);
				this.running = true;
				this.servletContext.setAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT, AbstractOpentachServerLocator.getLocator());
				OpentachLocatorReferencer.logger.debug(this.getClass().toString() + " -> Started ServletLanzadorServidor");
			} catch (final Exception ex) {
				throw new ServletException("Error starting " + this.getClass().toString() + " -> " + ex.getMessage(), ex);
			}
		}
	}

	@PreDestroy
	public void onPreDestroy() {
		OpentachLocatorReferencer.logger.warn(this.getClass().toString() + " -> destroy");
		if (this.launcher != null) {
			this.servletContext.removeAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
			this.launcher.closeServer();
		}
		OpentachLocatorReferencer.logger.warn("destroyed");
	}

	@Override
	public IOpentachServerLocator getLocator() {
		return AbstractOpentachServerLocator.getLocator();
	}

	private void setOntimizeParameters() {
		try {
			com.ontimize.security.Utils.SILENT = true;
		} catch (final Exception e) {
			OpentachLocatorReferencer.logger.error(null, e);
		}
		EncryptDecryptUtils.configure("vJz6#Bhs6x5x@Px;");
		OpentachLocatorReferencer.logger.info("TimeZone:     " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
		OpentachLocatorReferencer.logger.info("Version JAVA: " + System.getProperty("java.vm.version"));
		OpentachLocatorReferencer.logger.info("Version SO:   " + System.getProperty("sun.os.patch.level"));
		System.setProperty("OPENTACH_SYSTEM_DEFAULT_TIMEZONE", String.valueOf(TimeZone.getDefault().getOffset(new Date().getTime())));// Save system timezone before changing
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		OpentachLocatorReferencer.logger.info("Changing TimeZone to " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));

		// SQLStatementBuilder.DEBUG = true;
		try {
			DBErrorMessages.setSQLStateMessages(this.dbmessagesUrl);
		} catch (final IOException exception) {
			OpentachLocatorReferencer.logger.error(null, exception);
		}
		final ExtendedSQLConditionValuesProcessor exprProcessor = new ExtendedSQLConditionValuesProcessor();
		SQLStatementBuilder.registerSQLStatementHandler(SQLStatementBuilder.DEFAULT_HANDLER, new UOracleSQLStatementHandler());
		AbstractJdbcTemplate.setParameterSetter(new OracleParameterSetter());
		SQLStatementBuilder.setSQLConditionValuesProcessor(exprProcessor);
		Autonumerical.SYNCHRONIZE = true;
		com.imatia.tacho.tool.LoggerFactory.setLogger(new ILogger() {
			@Override
			public void log(String level, String s) {
				OpentachLocatorReferencer.loggerTachoLibrary.info("[{}] {}", level, s);
			}

			@Override
			public void error(Throwable o) {
				OpentachLocatorReferencer.loggerTachoLibrary.error(null, o);
			}

			@Override
			public void print(Throwable arg0) {
				this.error(arg0);
			}

			@Override
			public void println(String arg0) {
				this.log(ILogger.INFO, arg0);
			}
		});
	}

	private class ExtendedServerLauncher extends ServerLauncher {

		public ExtendedServerLauncher(String pr) throws Exception {
			super(pr);

			// TODO fix problems with autonumerical
			if ((this.locator != null) && (((AbstractOpentachServerLocator) this.locator).getConnectionManager().getAutonumerical() == null)) {
				DatabaseConnectionManager connectionManager = ((AbstractOpentachServerLocator) this.locator).getConnectionManager();

				Properties refereceLocatorProperties = this.loadLocatorProperties(pr);
				String oAutonumericalProp = (String) refereceLocatorProperties.get(ReferenceLocator.AUTONUMERICAL_PROPERTIES);
				if (oAutonumericalProp != null) {
					URL autonumericalURL = this.getClass().getClassLoader().getResource(oAutonumericalProp.toString());
					connectionManager.setAutonumerical(autonumericalURL);
				}
			}
		}

		@Override
		protected Properties loadLocatorProperties(String sLocatorProperties) throws Exception, IOException {
			final Properties properties = super.loadLocatorProperties(sLocatorProperties);
			// superchapuza para poder pasar el contexto de spring al referencelocator
			final Hashtable<String, Object> databaseParams = new Hashtable<>();
			databaseParams.put(OpentachLocatorReferencer.SPRING_CONTEX, OpentachLocatorReferencer.this.springContext);
			databaseParams.put("usej2eeconnections", "yes");
			databaseParams.put("datasourcename", "mainDataSource");
			properties.put("DBProperties"/* SecureReferenceLocator.DATABASE_PROPERTIES */, databaseParams);
			return properties;
		}

		protected void closeServer() {
			if (this.locator instanceof SecureReferenceLocator) {
				((SecureReferenceLocator) this.locator).freeServerResources();
				try {
					UnicastRemoteObject.unexportObject(this.registry, true);
				} catch (final NoSuchObjectException e) {
					OpentachLocatorReferencer.logger.error(null, e);
				}
			}
		}
	}
}
