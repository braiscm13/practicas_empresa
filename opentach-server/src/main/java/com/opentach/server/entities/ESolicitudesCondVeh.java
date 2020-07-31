package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.mail.MailService;
import com.opentach.server.util.db.FileTableEntity;
import com.opentach.server.util.mail.AltaBajaMailComposer;

public class ESolicitudesCondVeh extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(ESolicitudesCondVeh.class);

	public ESolicitudesCondVeh(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		EntityResult er = super.insert(av, sesionId, con);
		if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			this.sendMail(av, sesionId, con);
		}
		return er;
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable kv, int sesionId, Connection con) throws Exception {
		Object resuelto = av.get("RESUELTO");
		if (resuelto instanceof Number) {
			Number nResuelto = (Number) resuelto;
			if (nResuelto.intValue() == 1) {
				av.put("F_RESOLUCION", new Date());
			} else {
				av.put("F_RESOLUCION", new NullValue(Types.DATE));
			}
			av.remove("RESUELTO");
		}
		EntityResult er = super.update(av, kv, sesionId, con);
		return er;
	}

	private void sendMail(Hashtable av, int sesionId, Connection con) {
		try {
			MailService ms = this.getService(MailService.class);
			final String mailto = ms.getMailReqAddress();
			if (mailto != null) {
				IUserData ud = ((OpentachServerLocator) this.locator).getUserData(sesionId);
				final String cif = ud.getCIF();
				final String cName = ud.getCompanyName();
				final String text = (String) av.get("SOLICITUD");
				final String type = (String) av.get("TIPOSOLICITUD");
				final Map<String, Object> mResult = new HashMap<String, Object>();
				mResult.put(AltaBajaMailComposer.TEXT, text);
				mResult.put(AltaBajaMailComposer.TYPE, type);
				mResult.put(OpentachFieldNames.NAME_FIELD, cif + " - " + cName);
				ms.sendMail(new AltaBajaMailComposer(mailto, null, null, mResult));
			}
		} catch (Exception e) {
			ESolicitudesCondVeh.logger.error(null, e);
		}
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		List<Object> l = new ArrayList<Object>();
		for (int i = 0; i < av.size(); i++) {
			l.add(av.get(i));
		}
		EntityResult res = new EntityResult(l);
		res = super.query(cv, av, sesionId, con);
		return res;
	}
}
