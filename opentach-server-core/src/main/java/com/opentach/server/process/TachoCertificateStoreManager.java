package com.opentach.server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.security.TachoCertificateStore;

public class TachoCertificateStoreManager {

	private static final Logger				logger	= LoggerFactory.getLogger(TachoCertificateStoreManager.class);

	private static TachoCertificateStore	certstore;
	static {
		TachoCertificateStoreManager.certstore = new TachoCertificateStore("TachoStore");
		try {
			TachoCertificateStoreManager.certstore.loadCertificates("certificate/cert.properties");
		} catch (Exception err) {
			TachoCertificateStoreManager.logger.error(null, err);
		}
	}

	public static TachoCertificateStore getStore() {
		return TachoCertificateStoreManager.certstore;
	}

}
