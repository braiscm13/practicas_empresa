package com.opentach.server.filereception;

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

public class FileUploadComposer extends MailComposer implements ITGDFileConstants {

	public static final String	CDODSCR	= "CDODSCR";

	public FileUploadComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		Template template = this.loadTemplate("mailTemplateFileUpload.html");
		Map<String, String> replace = new HashMap<String, String>();
		replace.put("%NOMBRE%", (String) params.get(OpentachFieldNames.NAME_FIELD));
		replace.put("%FICHERO%", (String) params.get(OpentachFieldNames.FILENAME_FIELD));
		replace.put("%FECHA%", sdf.format(new Date()));
		replace.put("%CDODSCR%", (String) params.get(FileUploadComposer.CDODSCR));
		return template.fillTemplate(replace);
	}

	@Override
	protected String getSubject() {
		return "Subida de fichero";
	}

}
