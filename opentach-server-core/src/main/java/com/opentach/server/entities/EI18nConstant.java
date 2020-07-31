package com.opentach.server.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;

/**
 * The Class EPreferenciasServidor.
 */
public class EI18nConstant extends TableEntity {

	/** The Constant logger. */
	private static final Logger						logger							= LoggerFactory.getLogger(EI18nConstant.class);

	/**
	 * Instantiates a new e preferencias servidor.
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
	public EI18nConstant(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

}
