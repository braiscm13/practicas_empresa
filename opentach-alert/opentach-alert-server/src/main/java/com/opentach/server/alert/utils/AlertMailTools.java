package com.opentach.server.alert.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;

public class AlertMailTools {

	private AlertMailTools() {
		// Nothing
	}

	public static List<String> parseEmailsTo(Properties properties, Map<?, ?> data) {
		return AlertMailTools.parseEmails(properties, data, "EMAIL", "EMAIL_TO", "EMAIL.TO", "MAIL", "MAIL_TO", "MAIL.TO");
	}

	public static List<String> parseEmailsCC(Properties properties, Map<?, ?> data) {
		return AlertMailTools.parseEmails(properties, data, "EMAIL_CC", "EMAIL.CC", "MAIL_CC", "MAIL.CC");
	}

	public static List<String> parseEmailsBCC(Properties properties, Map<?, ?> data) {
		return AlertMailTools.parseEmails(properties, data, "EMAIL_BCC", "EMAIL.BCC", "MAIL_BCC", "MAIL.BCC");
	}

	public static List<String> parseEmails(Properties properties, Map<?, ?> data, String... candidateFields) {
		List<String> emails = new ArrayList<String>();
		if (candidateFields == null) {
			return emails;
		}
		for (String field : candidateFields) {
			for (int i = 0; i < 10; i++) {
				// Look for into properties (fixed emails)
				String key = field + (i == 0 ? "" : i);
				if (properties != null) {
					String candidates = (String) properties.get(key);
					ListTools.ensureAllValues(emails, ParseUtilsExtended.getStringList(candidates, " ,;\t\r\n\f/", new ArrayList<String>()));
				}
				// Look for into data (dynamic emails)
				if (data != null) {
					String candidates = (String) data.get(key);
					ListTools.ensureAllValues(emails, ParseUtilsExtended.getStringList(candidates, " ,;\t\r\n\f/", new ArrayList<String>()));
				}
			}
		}
		return emails.stream().map((s) -> s.trim()).collect(Collectors.toList());
	}
}
