package com.opentach.server.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.mail.MailComposer;

public class PDAMailNotificationComposer extends MailComposer implements ITGDFileConstants {
	protected SimpleDateFormat	sdf;

	public PDAMailNotificationComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		super(mailto, mailack, locale, params);
		this.sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		this.sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		return "El fichero " + params.get(OpentachFieldNames.FILENAME_PROCESSED_FIELD) + " enviado el "
				+ this.sdf.format(params.get(ITGDFileConstants.EXTRACT_DATE_FIELD)) + " ha sido procesado correctamente el "
				+ this.sdf.format(new Date()) + ".";
	}

	@Override
	protected String getSubject() {
		return "INFORME DE FICHERO PROCESADO";
	}
}
