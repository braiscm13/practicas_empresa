package com.opentach.server.entities;

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.interfaces.ICartasPorte;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class ECartasPorteADR extends FileTableEntity implements ICartasPorte {

	public ECartasPorteADR(EntityReferenceLocator locator, DatabaseConnectionManager cm, int puerto) throws Exception {
		super(locator, cm, puerto);

	}

	@Override
	public EntityResult insert(Hashtable av, int sessionId, Connection conn) throws Exception {
		EntityResult er = super.insert(av, sessionId, conn);
		return er;
	}

	@Override
	public void saveCartasPorte(final File f, final String nombre, final String tipo, final Date dInforme, final int sessionID) throws Exception {

		new OntimizeConnectionTemplate<Void>() {

			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					ECartasPorteADR.this.saveCartasPorte(f, nombre, tipo, dInforme, sessionID, con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}

			}
		}.execute(this.manager, true);
	}

	public void saveCartasPorte(final File f, final String nombre, final String tipo, final Date dInforme, final int sessionID, Connection conn) throws Exception {
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put("NOMBRE", nombre);
		av.put("F_ALTA", dInforme);
		av.put("USER_ALTA", this.getUser(sessionID));
		av.put("TIPO", tipo);
		EntityResult res = this.insert(av, sessionID, conn);
		this.writeOracleBLOBStream(f, "CARTA", res, conn, true);
	}

}
