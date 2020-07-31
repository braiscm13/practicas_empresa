package com.opentach.server;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.tool.ILogger;
import com.ontimize.db.Autonumerical;
import com.ontimize.db.DBErrorMessages;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.gui.ServerLauncherServlet;
import com.utilmize.server.UOracleSQLStatementHandler;
import com.utilmize.server.tools.sqltemplate.AbstractJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.OracleParameterSetter;

public class OpentachServer extends ServerLauncherServlet {

	private static final Logger	logger	= LoggerFactory.getLogger(OpentachServer.class);
	private static final Logger	loggerTachoLibrary	= LoggerFactory.getLogger(TachoFile.class);

	public OpentachServer() throws Exception {}

	@Override
	public void contextInitialized(ServletContextEvent contextEvt) {
		// System.setProperty("oracle.jdbc.V8Compatible", "true");
		OpentachServer.setOntimizeParameters();
		super.contextInitialized(contextEvt);
	}

	private static void setOntimizeParameters() {
		OpentachServer.logger.info("TimeZone:     " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
		OpentachServer.logger.info("Version JAVA: " + System.getProperty("java.vm.version"));
		OpentachServer.logger.info("Version SO:   " + System.getProperty("sun.os.patch.level"));
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		OpentachServer.logger.info("Changing TimeZone to " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));

		// SQLStatementBuilder.DEBUG = true;

		try {
			DBErrorMessages.setSQLStateMessages("com/opentach/server/prop/dbmessages.properties");
		} catch (IOException e) {
			OpentachServer.logger.error(null, e);
		}
		ExtendedSQLConditionValuesProcessor exprProcessor = new ExtendedSQLConditionValuesProcessor();
		SQLStatementBuilder.registerSQLStatementHandler(SQLStatementBuilder.DEFAULT_HANDLER, new UOracleSQLStatementHandler());
		AbstractJdbcTemplate.setParameterSetter(new OracleParameterSetter());
		SQLStatementBuilder.setSQLConditionValuesProcessor(exprProcessor);
		Autonumerical.SYNCHRONIZE = true;
		com.imatia.tacho.tool.LoggerFactory.setLogger(new ILogger() {
			@Override
			public void log(String level, String s) {
				OpentachServer.loggerTachoLibrary.info("[{}] {}", level, s);
			}

			@Override
			public void error(Throwable o) {
				OpentachServer.loggerTachoLibrary.error(null, o);
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

	@Override
	public void contextDestroyed(ServletContextEvent contextEvt) {
		super.contextDestroyed(contextEvt);
		OpentachServer.logger.info("destroy");
		this.log(this.getClass().toString() + " -> destroy");
	}

	public static void main(String[] args) throws Exception {
		OpentachServer.setOntimizeParameters();
		if ((args == null) || (args.length == 0)) {
			args = new String[] { "com/opentach/server/prop/server.properties" };
			OpentachServer.logger.info("Server properties file not specified. Setting to " + args[0]);
		}
		try {
			ServerLauncherServlet.main(args);
		} catch (Exception e) {
			OpentachServer.logger.error(null, e);
			throw e;
		}
	}
}
