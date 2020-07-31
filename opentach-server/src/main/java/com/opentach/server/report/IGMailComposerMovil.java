package com.opentach.server.report;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.ontimize.jee.common.tools.Template;
import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailInfo;

public class IGMailComposerMovil extends MailComposer {

	public static final String	COMPANY		= "empresa";
	public static final String	REPORTDATE	= "f_informe";
	public static final String	REPORTTITLE	= "title";

	public IGMailComposerMovil(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		Template template = this.loadTemplate("plantilla_informe_movil.html");

		Map<String, String> replace = new HashMap<String, String>();
		for (Entry<String, Object> entry : params.entrySet()) {
			replace.put("%" + entry.getKey() + "%", this.replaceNull(entry.getValue()));
		}

		return template.fillTemplate(replace);
	}

	protected String replaceNull(Object oparam) {
		String param;
		if (oparam instanceof Number) {
			param = new String(((Number) oparam).intValue() + "");
		} else if (oparam instanceof Boolean) {
			param = new String(((Boolean) oparam).booleanValue() + "");
		} else {
			param = (String) oparam;
		}
		if ((param == null) || ("".equals(param))) {
			return " ";
		}
		return param;
	}

	@Override
	protected MailInfo getMailInfo() {
		final MailInfo mi = super.getMailInfo();
		return mi;
	}

	@Override
	protected String getSubject() {
		return "Informe express móvil";
	}

}
