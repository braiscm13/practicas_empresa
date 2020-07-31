package com.opentach.server.report;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.ontimize.jee.common.tools.Template;
import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailInfo;

public class IGMailComposer extends MailComposer {

	public static final String	COMPANY		= "empresa";
	public static final String	REPORTDATE	= "f_informe";
	public static final String	REPORTTITLE	= "title";

	public IGMailComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		Template template = this.loadTemplate("mailTemplateInformeGestor.html");
		Map<String, String> replace = new HashMap<String, String>();
		replace.put("%TITLE%", (String) params.get(IGMailComposer.REPORTTITLE));// "Informe resumen");
		replace.put("%EMPRESA%", (String) params.get(IGMailComposer.COMPANY));
		replace.put("%FECHAINFORME%", sdf.format(params.get(IGMailComposer.REPORTDATE)));

		return template.fillTemplate(replace);
	}

	@Override
	protected MailInfo getMailInfo() {
		final MailInfo mi = super.getMailInfo();
		return mi;
	}

	@Override
	protected String getSubject() {
		return "Entrega del Informe Resumen";
	}

}
