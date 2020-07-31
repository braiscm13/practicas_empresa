package com.opentach.server.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.Template;

public abstract class MailComposer {

	private static final Logger			logger		= LoggerFactory.getLogger(MailComposer.class);
	protected final String				mailto;
	protected final String				mailack;
	protected final Locale				locale;
	protected final Map<String, Object>	params;

	public static final String			FILENAMES	= "filename";
	public static final String			STREAMS		= "stream";

	public MailComposer(String mailto, String mailack, Locale locale, Map<String, Object> params) {
		this.mailto = mailto;
		this.mailack = mailack;
		this.locale = locale;
		this.params = params;
	}

	protected abstract String composeContent(Map<String, Object> params) throws Exception;

	protected abstract String getSubject();

	protected MailInfo getMailInfo() {
		try {
			final MailInfo mi = new MailInfo();
			mi.setTo(this.extractTokens(this.mailto, ";"));
			mi.setCc(this.extractTokens(null, ";"));
			mi.setBcc(this.extractTokens(null, ";"));
			mi.setSubject(this.getSubject());
			mi.setAckAddress(this.mailack);
			mi.setContent(this.composeContent(this.params));
			mi.setContentType("text/html");

			int numMas = 0;


			if (this.params != null) {

				if (!"Informe Express".equals(this.params.get("title"))) {
					numMas = 3;
				}


				String[] fileNames;
				File[] attchs;
				InputStream[] streams = (InputStream[]) this.params.get(MailComposer.STREAMS);
				if (this.params.get(MailComposer.FILENAMES) != null) {
					fileNames = new String[((String[]) this.params.get(MailComposer.FILENAMES)).length + numMas];
					attchs = new File[streams.length + numMas];
				} else {
					fileNames = new String[numMas];
					attchs = new File[numMas];
				}
				int i = 0;
				if ((streams != null) && (fileNames != null)) {

					for (i = 0; i < (attchs.length - numMas); i++) {
						fileNames[i] = ((String[]) this.params.get(MailComposer.FILENAMES))[i];
						attchs[i] = this.createFile(streams[i]);
					}
				}

				if (numMas != 0) {
					int j = 0;

					InputStream is = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("com/opentach/server/mail/templates/es_ES/conduccion.png");
					attchs[i + j] = this.createFile(is);
					fileNames[i + j] = "conduccion.png";
					j++;

					InputStream is2 = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("com/opentach/server/mail/templates/es_ES/descanso.png");
					attchs[i + j] = this.createFile(is2);
					fileNames[i + j] = "descanso.png";
					j++;

					InputStream is3 = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("com/opentach/server/mail/templates/es_ES/logo_info_express.png");
					attchs[i + j] = this.createFile(is3);
					fileNames[i + j] = "logo_info_express.png";
				}

				mi.setAttchNames(fileNames);
				mi.setAttchFiles(attchs);
			}

			return mi;
		} catch (Exception e) {
			MailComposer.logger.error(null, e);
			return null;
		}
	}

	protected final String[] extractTokens(String s, String separator) {
		if (s == null) {
			return new String[0];
		} else if (separator == null) {
			return new String[] { s };
		}
		return s.split(separator);
	}

	protected File createFile(InputStream is) throws Exception {
		File f = File.createTempFile("toSend", "data");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			byte[] buffer = new byte[10240];
			int readed = 0;
			while ((readed = is.read(buffer)) != -1) {
				fos.write(buffer, 0, readed);
			}
			return f;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	protected Template loadTemplate(String name) throws Exception {
		MailComposer.logger.debug("LOCALE {}, NAME {}", this.locale, name);
		String path = "templates/";
		if (this.locale != null) {
			path = path.concat(this.locale.toString() + "/");
		}
		path = path.concat(name);
		MailComposer.logger.debug("path de  la plantilla : {}", path);
		Template template = new Template(MailComposer.class.getResourceAsStream(path));
		return template;
	}

	public final static String joinMailAddresses(List<String> list) {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			for (String mail : list) {
				if (mail != null) {
					sb.append(mail.trim()).append(";");
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		return sb.toString();
	}
}
