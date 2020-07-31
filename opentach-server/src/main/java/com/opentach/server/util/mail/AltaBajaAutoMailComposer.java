package com.opentach.server.util.mail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.ontimize.jee.common.tools.Template;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.mail.MailComposer;

public class AltaBajaAutoMailComposer extends MailComposer implements ITGDFileConstants {

	public enum Type {
		ALTA, BAJA
	}

	public static final String	TYPE		= "TYPE";
	public static final String	USER		= "USER";
	public static final String	CONDUCTOR	= "NAME_CONDUCTOR";

	public AltaBajaAutoMailComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		DateFormat sdf = new SimpleDateFormat("kk:mm:ss dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		Template template = this.loadTemplate("mailTemplateAltaBajaAuto.html");
		Map<String, String> replace = new HashMap<String, String>();
		Type type = (Type) params.get(AltaBajaAutoMailComposer.TYPE);
		String sType = type.equals(Type.ALTA) ? "Alta" : "Baja";
		replace.put("%TYPE%", sType);
		replace.put("%USER%", (String) params.get(AltaBajaAutoMailComposer.USER));
		replace.put("%CIF%", (String) params.get(OpentachFieldNames.CIF_FIELD));
		replace.put("%EMPRESA%", (String) params.get(OpentachFieldNames.NAME_FIELD));
		replace.put("%FECHA%", sdf.format(new Date()));
		replace.put("%CONTRATO%", (String) params.get(OpentachFieldNames.CG_CONTRATO_FIELD));
		String matricula = (String) params.get(OpentachFieldNames.MATRICULA_FIELD);
		if (matricula != null) {
			replace.put("%VEHICULO%", "Vehiculo: " + (String) params.get(OpentachFieldNames.MATRICULA_FIELD));
		} else {
			replace.put("%VEHICULO%", "");
		}
		String conductor = (String) params.get(AltaBajaAutoMailComposer.CONDUCTOR);
		if (conductor != null) {
			replace.put("%CONDUCTOR%", "Conductor: " + (String) params.get(AltaBajaAutoMailComposer.CONDUCTOR));
		} else {
			replace.put("%CONDUCTOR%", "");
		}
		return template.fillTemplate(replace);
	}

	@Override
	protected String getSubject() {
		return "Auditoria: Alta/baja de conductor";
	}

}
