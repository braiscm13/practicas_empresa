package com.opentach.common.waybill;

import java.rmi.Remote;
import java.util.Hashtable;
import java.util.Locale;

import com.ontimize.util.remote.BytesBlock;

public interface IWaybillService extends Remote {

	String ID = "WaybillService";

	BytesBlock generateWaybill(WaybillType type, Hashtable<String, String> parameters, String cif, Locale locale, int sessionId) throws Exception;

//	BytesBlock getWaybill(Object wayId, String cif, int sessionId) throws Exception;
}
