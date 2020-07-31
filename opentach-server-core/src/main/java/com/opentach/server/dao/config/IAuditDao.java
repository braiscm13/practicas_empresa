package com.opentach.server.dao.config;

/**
 * Daos which wants to audited all or some operations
 */
public interface IAuditDao {
	// TODO MOVE TO AUDIT module
	/**
	 * Return the query of this DAO that can be queried with KEYS of this entity to rescue PARENT AUD_ID
	 */
	String getAuditPKQuery();

	// TODO MORE
}
