package com.opentach.server.remotevehicle.provider.opentach;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CompanyCardRegister.
 */
public class CompanyCardRegister {

	/** The Constant logger. */
	private static final Logger							logger	= LoggerFactory.getLogger(CompanyCardRegister.class);

	/** The register. */
	private final Map<String, RemoteDownloadEndPoint>	register;

	/**
	 * The Constructor.
	 */
	public CompanyCardRegister() {
		this.register = new HashMap<String, RemoteDownloadEndPoint>();
	}

	/**
	 * Register.
	 *
	 * @param companyId
	 *            the company id
	 * @param session
	 *            the session
	 */
	public void register(String companyId, RemoteDownloadEndPoint session) {
		this.unregister(companyId);
		this.register.put(companyId, session);
	}

	/**
	 * Close session.
	 *
	 * @param oldSession
	 *            the old session
	 */
	private void closeEndPoint(RemoteDownloadEndPoint endPoint) {
		if (endPoint != null) {
			try {
				endPoint.close();
			} catch (Throwable e) {
				CompanyCardRegister.logger.error(null, e);
			}
		}
	}

	/**
	 * Unregister.
	 *
	 * @param companyId
	 *            the company id
	 */
	public void unregister(String companyId) {
		RemoteDownloadEndPoint oldEndPoint = this.register.remove(companyId);
		this.closeEndPoint(oldEndPoint);
	}

	/**
	 * Gets the end point.
	 *
	 * @param companyId
	 *            the company id
	 * @return the end point
	 */
	public RemoteDownloadEndPoint getEndPoint(String companyId) {
		return this.register.get(companyId);
	}

}
