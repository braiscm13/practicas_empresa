package com.opentach.server.sessionstatus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.ontimize.jee.common.tools.Template;
import com.opentach.server.mail.MailComposer;

public class CDODownMailComposer extends MailComposer {

	public static final String	COMPANY		= "empresa";
	public static final String	DSCR		= "dscr";
	public static final String	INCIDATE	= "f_inci";
	public static final String	USER		= "user";
	public static final String	REPORTTITLE	= "title";

	public CDODownMailComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		Template template = this.loadTemplate("mailTemplateCDODown.html");
		String user = (String) params.get(CDODownMailComposer.USER);
		String dscr = (String) params.get(CDODownMailComposer.DSCR);
		Map<String, String> replace = new HashMap<String, String>();
		replace.put("%INCIDATE%", sdf.format(params.get(CDODownMailComposer.INCIDATE)));
		if (user != null) {
			replace.put("%USER%", user);
		}
		if (dscr != null) {
			replace.put("%DSCR%", dscr);
		} else {
			replace.put("%DSCR%", "");
		}
		return template.fillTemplate(replace);
	}

	@Override
	protected String getSubject() {
		return "Caída de Poste de Descarga";
	}

}
