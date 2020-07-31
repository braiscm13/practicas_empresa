package com.opentach.server;

import java.util.Locale;

import org.springframework.context.ApplicationContext;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.gui.ClientWatch;
import com.opentach.common.user.IUserData;
import com.utilmize.server.services.UAbstractService;

public interface IOpentachServerLocator {

	<T extends UAbstractService> T getService(Class<T> clazz);

	DatabaseConnectionManager getConnectionManager();

	boolean hasSession(int sessionId);

	IUserData getUserData(int sesionId) throws Exception;

	IUserData getUserData(String userLogin) throws Exception;

	String getUser(int id) throws Exception;

	Locale getUserLocale(int sessionID) throws Exception;

	Entity getEntityReferenceFromServer(String entityName);

	void endSession(int sessionID) throws Exception;

	int startSession(String user, String password, ClientWatch cw) throws Exception;

	String getLoginEntityName(int sessionID) throws Exception;

	<T> T getBean(Class<T> clazz);

	void setApplicationContext(ApplicationContext context);

	int getSessionIdForLogin(String userLogin);
}
