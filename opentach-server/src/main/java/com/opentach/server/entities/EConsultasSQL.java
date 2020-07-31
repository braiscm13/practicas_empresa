package com.opentach.server.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLEntity;
import com.ontimize.locator.EntityReferenceLocator;

/**
 * <p>
 * Título:
 * </p>
 * <p>
 * Descripción:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Empresa:
 * </p>
 *
 * @author sin atribuir
 * @version 1.0
 */
public class EConsultasSQL extends SQLEntity {
	private final static Logger	logger	= LoggerFactory.getLogger(EConsultasSQL.class);

	public EConsultasSQL(EntityReferenceLocator bRefs, DatabaseConnectionManager gc, int puerto) throws Exception {
		super(bRefs, gc, puerto);
	}

	@Override
	public EntityResult execute(String sql, int sesionId) throws Exception {
		EConsultasSQL.logger.debug("EConsultasSQL.ejecutar sql: {}", sql);
		EntityResult res = super.execute(sql, sesionId);
		return res;
	}

}
