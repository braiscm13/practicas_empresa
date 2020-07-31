package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class EUsuariosCDO.
 */
public class EUsuariosCDO extends FileTableEntity {

	/**
	 * Instantiates a new e usuarios cdo.
	 *
	 * @param b
	 *            the b
	 * @param g
	 *            the g
	 * @param p
	 *            the p
	 * @throws Exception
	 *             the exception
	 */
	public EUsuariosCDO(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.entities.Usuario#insert(java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection conn) throws Exception {
		TransactionalEntity eUsers = (TransactionalEntity) this.getEntityReference("EUsuariosTodos");
		eUsers.insert(av, sesionId, conn);
		return super.insert(av, sesionId, conn);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.entities.Usuario#update(java.util.Hashtable, java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult update(Hashtable av, Hashtable kv, int sessionId, Connection conn) throws Exception {
		TransactionalEntity eUsers = (TransactionalEntity) this.getEntityReference("EUsuariosTodos");
		eUsers.update(av, kv, sessionId, conn);
		return super.update(av, kv, sessionId, conn);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.entities.Usuario#delete(java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult delete(Hashtable kv, int sessionId, Connection conn) throws Exception {
		EntityResult delete = super.delete(kv, sessionId, conn);
		TransactionalEntity eUsers = (TransactionalEntity) this.getEntityReference("EUsuariosTodos");
		eUsers.delete(kv, sessionId, conn);
		return delete;
	}
}
