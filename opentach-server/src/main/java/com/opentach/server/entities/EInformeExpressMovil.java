package com.opentach.server.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

/**
 * The Class EInformeExpressMovil.
 */
public class EInformeExpressMovil extends FileTableEntity {

	/** The Constant logger. */
	private static final Logger	logger	= LoggerFactory.getLogger(EInformeExpressMovil.class);

	/**
	 * Instantiates a new e informe express movil.
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
	public EInformeExpressMovil(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

}
