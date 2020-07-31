package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EUsuDfEmp extends FileTableEntity {

	public EUsuDfEmp(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		if (av.containsKey("INSERT_RELATION")) {
			String usuario = (String) av.get("USUARIO");
			String password = (String) av.get("PASSWORD");
			String telef = (String) av.get("TELEFONO");
			String email = (String) av.get("EMAIL");
			String nivel = (String) av.get("NIVEL_CD");
			Hashtable<String, Object> avUser = new Hashtable<String, Object>(4);
			avUser.put("USUARIO", usuario);
			avUser.put("PASSWORD", password);
			avUser.put("NIVEL_CD", nivel);
			if (telef != null) {
				avUser.put("TELEFONO", telef);
			}
			if (email != null) {
				avUser.put("EMAIL", email);
			}
			TableEntity eUsu = (TableEntity) ((OpentachServerLocator) this.locator).getEntityReferenceFromServer("EUsuariosTodos");
			eUsu.insert(avUser, sesionId, con);
		}
		return super.insert(av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId, Connection con) throws Exception {
		if (av.containsKey("UPDATE_RELATION")) {
			String password = (String) av.get("PASSWORD");
			String telef = (String) av.get("TELEFONO");
			String email = (String) av.get("EMAIL");
			String nivel = (String) av.get("NIVEL_CD");
			Hashtable<String, Object> avUser = new Hashtable<String, Object>(4);
			if (password != null) {
				avUser.put("PASSWORD", password);
			}
			if (nivel != null) {
				avUser.put("NIVEL_CD", nivel);
			}
			if (telef != null) {
				avUser.put("TELEFONO", telef);
			}
			if (email != null) {
				avUser.put("EMAIL", email);
			}
			if (avUser.size() > 0) {
				TableEntity eUsu = (TableEntity) ((OpentachServerLocator) this.locator).getEntityReferenceFromServer("EUsuariosTodos");
				eUsu.update(avUser, cv, sesionId, con);
			}
		}
		return super.update(av, cv, sesionId, con);
	}

}
