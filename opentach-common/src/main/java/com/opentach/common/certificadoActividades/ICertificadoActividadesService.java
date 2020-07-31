package com.opentach.common.certificadoActividades;

import java.rmi.Remote;
import java.util.Hashtable;
import java.util.Locale;

import com.ontimize.util.remote.BytesBlock;

public interface ICertificadoActividadesService extends Remote {


	static final String ID = "CertificadoActividadesService";

	BytesBlock generateCertificadoActividadesServiceReport(Hashtable<String,String> parameters, Locale locale,  int sessionId) throws Exception;
}
