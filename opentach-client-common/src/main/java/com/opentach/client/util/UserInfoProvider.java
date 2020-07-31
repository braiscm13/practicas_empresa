package com.opentach.client.util;

import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.PermissionReferenceLocator;
import com.opentach.common.user.IUserData;

public interface UserInfoProvider {


	IUserData getUserData() throws Exception;

	String getUserDescription();

	<T> T getRemoteService(Class<T> clazz) throws Exception;

	<T> T getLocalService(Class<T> clazz);

	int getSessionId();

	public static String getUserDescription(PermissionReferenceLocator erl) {
		String dscr = null;
		try {
			int sessionID = erl.getSessionId();
			Entity eUser = erl.getEntityReference("EUsuariosTodos");
			Hashtable<String, Object> kv = new Hashtable<String, Object>();
			Vector<Object> vq = new Vector<Object>(1);
			vq.add("DSCR");
			kv.put("USUARIO", erl.getUser());
			EntityResult res = eUser.query(kv, vq, sessionID);
			if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (res.calculateRecordNumber() == 1)) {
				Hashtable<String, Object> resv = res.getRecordValues(0);
				dscr = (String) resv.get("DSCR");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(UserInfoProvider.class).error(null, e);
		}
		return dscr;
	}

}
