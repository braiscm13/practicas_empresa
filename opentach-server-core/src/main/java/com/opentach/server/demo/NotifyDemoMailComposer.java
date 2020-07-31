package com.opentach.server.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.mail.MailComposer;

class NotifyDemoMailComposer extends MailComposer {
	protected Object				idUser;
	protected Object				cif;
	protected IOpentachServerLocator	locator;

	public NotifyDemoMailComposer(List<String> emails, Object idUser, Object cif, IOpentachServerLocator locator) {
		super(NotifyDemoMailComposer.composeMailTo(emails), null, null, null);
		this.idUser = idUser;
		this.cif = cif;
		this.locator = locator;
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		try {
			final EPreferenciasServidor preferences = (EPreferenciasServidor) this.locator.getEntityReferenceFromServer("EPreferenciasServidor");
			final Hashtable<String, String> replacement = new Hashtable<String, String>();
			replacement.put("%USERDATA%", this.queryUserInfo(this.idUser));
			replacement.put("%COMPANYDATA%", this.queryCompanyInfo(this.cif));
			replacement.put("%EXPDATE%", this.queryDemoFinish(this.idUser));
			String template = preferences.getValue("DemoNotif.Template", TableEntity.getEntityPrivilegedId(preferences));
			for (final Entry<String, String> entry : replacement.entrySet()) {
				template = template.replaceAll(entry.getKey(), entry.getValue());
			}
			return template;
		} catch (final Exception exception) {
			return "Debe configurar la plantilla DemoNotif.Template";
		}
	}

	@Override
	protected String getSubject() {
		try {
			final EPreferenciasServidor preferences = (EPreferenciasServidor) this.locator.getEntityReferenceFromServer("EPreferenciasServidor");
			return preferences.getValue("DemoNotif.Subject", TableEntity.getEntityPrivilegedId(preferences));
		} catch (final Exception exception) {
			return "Aviso de caducidad de Demo";
		}
	}

	private String queryDemoFinish(Object idUser) throws Exception {
		final Entity entity = this.locator.getEntityReferenceFromServer("Usuario");
		final Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put("USUARIO", idUser);
		final EntityResult res = entity.query(cv, EntityResultTools.attributes("DMAXLOGIN"), TableEntity.getEntityPrivilegedId(entity));
		final Hashtable<String, Object> data = res.getRecordValues(0);
		final Date maxDate = (Date) data.get("DMAXLOGIN");
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(maxDate);
	}

	private String queryCompanyInfo(Object cif) throws Exception {
		final Entity entity = this.locator.getEntityReferenceFromServer(CompanyNaming.ENTITY);
		final Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put("CIF", cif);
		final EntityResult res = entity.query(cv, EntityResultTools.attributes("NOMB"), TableEntity.getEntityPrivilegedId(entity));
		final Hashtable<String, Object> data = res.getRecordValues(0);
		final String nombre = (String) data.get("NOMB");
		return nombre == null ? "-" : nombre;
	}

	private String queryUserInfo(Object idUser) throws Exception {
		final Entity entity = this.locator.getEntityReferenceFromServer("Usuario");
		final Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put("USUARIO", idUser);
		final EntityResult res = entity.query(cv, EntityResultTools.attributes("NOMBRE_FIRMANTE", "APELLIDOS_FIRMANTE"), TableEntity.getEntityPrivilegedId(entity));
		final Hashtable<String, Object> data = res.getRecordValues(0);
		final String nombre = (String) data.get("NOMBRE_FIRMANTE");
		final String apell = (String) data.get("APELLIDOS_FIRMANTE");
		return (nombre == null ? "-" : nombre) + " " + (apell == null ? "-" : apell) + " (" + idUser + ")";
	}

	private static String composeMailTo(List<String> emails) {
		StringBuilder res = new StringBuilder();
		for (final String s : emails) {
			res = res.append(s).append(";");
		}
		res.delete(res.length() - 1, res.length());
		return res.toString();
	}

}