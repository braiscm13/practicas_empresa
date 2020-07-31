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

public class AltaBajaMailComposer extends MailComposer implements ITGDFileConstants {

	public static final String	TEXT	= "textContent";
	public static final String	TYPE	= "TypeAB";

	public AltaBajaMailComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		Template template = this.loadTemplate("mailTemplateAltaBaja.html");
		Map<String, String> replace = new HashMap<String, String>();
		replace.put("%EMPRESA%", (String) params.get(OpentachFieldNames.NAME_FIELD));
		replace.put("%FECHA%", sdf.format(new Date()));
		replace.put("%TYPE%", (String) params.get(AltaBajaMailComposer.TYPE));
		replace.put("%TEXT%", (String) params.get(AltaBajaMailComposer.TEXT));
		return template.fillTemplate(replace);
	}

	@Override
	protected String getSubject() {
		return "Alta/baja de conductor";
	}

}
