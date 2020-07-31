package com.opentach.client.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.PermissionReferenceLocator;
import com.opentach.client.util.usbkey.USBInfo;

public final class DBUtils {

	private static final Logger	logger	= LoggerFactory.getLogger(DBUtils.class);


	public static final List<USBInfo> getUSBInfo(PermissionReferenceLocator erl) {
		List<USBInfo> lInfo = null;
		try {
			int sessionID = erl.getSessionId();
			Entity eConf = erl.getEntityReference("EConfUSBKey");
			Vector<Object> vq = new Vector<Object>(0);
			Hashtable<String, Object> kv = new Hashtable<String, Object>(0);
			EntityResult res = eConf.query(kv, vq, sessionID);
			if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				final int regCount = res.calculateRecordNumber();
				lInfo = new ArrayList<USBInfo>();
				Hashtable<String, Object> rowht = null;
				USBInfo uinfo = null;
				for (int i = 0; i < regCount; i++) {
					rowht = res.getRecordValues(i);
					uinfo = new USBInfo((String) rowht.get("KEYNAME"), (String) rowht.get("KEYPATH"));
					lInfo.add(uinfo);
				}
			}
		} catch (Exception e) {
			DBUtils.logger.error(null, e);
		}
		return lInfo;
	}
}
