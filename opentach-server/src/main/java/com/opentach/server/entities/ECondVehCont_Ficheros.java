package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.util.db.FileTableEntity;

public class ECondVehCont_Ficheros extends FileTableEntity implements ITGDFileConstants {

	private static final Logger		logger			= LoggerFactory.getLogger(ECondVehCont_Ficheros.class);

	public ECondVehCont_Ficheros(EntityReferenceLocator brefs, DatabaseConnectionManager gc, int puerto) throws Exception {
		super(brefs, gc, puerto);
	}


	@Override
	public EntityResult query(Hashtable cv, Vector atributosA, int sesionId, Connection con) throws Exception {
		Object oIDOrigen = cv.get(OpentachFieldNames.IDORIGEN_FIELD);
		if ((oIDOrigen != null) && (oIDOrigen instanceof String)) {
			String sIDOrigen = (String) oIDOrigen;
			sIDOrigen = "*" + sIDOrigen + "*";
			cv.put(OpentachFieldNames.IDORIGEN_FIELD, sIDOrigen);
		}
		return super.query(cv, atributosA, sesionId, con);
	}
}
