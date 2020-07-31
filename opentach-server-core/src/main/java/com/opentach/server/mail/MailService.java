package com.opentach.server.mail;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.entities.EPreferenciasServidor.IPreferenceChangeListener;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class MailService.
 */
public class MailService extends UAbstractService implements IPreferenceChangeListener {

	/** The Constant logger. */
	private static final Logger	logger	= LoggerFactory.getLogger(MailService.class);
	/** The Constant ID. */
	final static String			ID		= "MailService";

	/** The mail server. */
	protected String			mailServer;

	/** The mail user. */
	protected String			mailUser;

	/** The mail authentication required. */
	protected boolean			mailAuthenticationRequired;

	protected boolean			mailStarttlsEnabled;

	/** The mail password. */
	protected String			mailPassword;

	/** The mail req address. */
	protected String			mailReqAddress;

	/** The mail suppaddress. */
	protected String			mailSuppaddress;

	/** The mail auditaddress. */
	protected String			mailAuditaddress;

	/**
	 * Instantiates a new mail service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public MailService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		// Creo el MailSender
		this.updateMailConfiguration();
		((EPreferenciasServidor) MailService.this.getEntity("EPreferenciasServidor")).addPropertyChangeListener(this);
	}

	/**
	 * Update mail configuration.
	 */
	protected void updateMailConfiguration() {
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						EPreferenciasServidor ePref = (EPreferenciasServidor) MailService.this.getEntity("EPreferenciasServidor");
						int sessionId = MailService.this.getSessionId(-1, ePref);
						MailService.this.mailServer = ePref.getValue(MailFields.MAIL_SERVER, sessionId, con);
						MailService.this.mailUser = ePref.getValue(MailFields.MAIL_USER, sessionId, con);
						MailService.this.mailPassword = ePref.getValue(MailFields.MAIL_PASS, sessionId, con);

						if (Stream.of(MailService.this.mailServer, MailService.this.mailUser, MailService.this.mailPassword)
								.anyMatch(Objects::isNull)) {
							throw new UException("WRONG MAIL SERVER CONFIGURATION");
						}

						String mailPropertiesList = ePref.getValue(MailFields.MAIL_PROPERTIES, sessionId, con);
						if (mailPropertiesList != null) {
							String[] mailProperties = mailPropertiesList.split(";");
							for (String mailProperty : mailProperties) {
								String[] keyValue = mailProperty.split(":");
								switch (keyValue[0]) {
									case MailFields.MAIL_AUTH:
										MailService.this.mailAuthenticationRequired = Boolean.valueOf(keyValue[1]);
										break;
									case MailFields.MAIL_STARTTLS:
										MailService.this.mailStarttlsEnabled = Boolean.valueOf(keyValue[1]);
										break;
									default:
										MailService.logger.error("UNKNOWN key:value {}:{} found in {}", keyValue[0], keyValue[1], MailFields.MAIL_PROPERTIES);
								}
							}
						}
						MailService.this.mailReqAddress = ePref.getValue(MailFields.MAIL_REQADDRESS, sessionId, con);
						MailService.this.mailAuditaddress = ePref.getValue(MailFields.MAIL_SUPPADDRESS, sessionId, con);
						MailService.this.mailSuppaddress = ePref.getValue(MailFields.MAIL_AUDITADDRESS, sessionId, con);
						return null;
					} catch (Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getLocator(), true);
		} catch (Exception error) {
			MailService.logger.error(null, error);
		}
	}

	/**
	 * Gets the mail req address.
	 *
	 * @return the mail req address
	 */
	public String getMailReqAddress() {
		return this.mailReqAddress;
	}

	/**
	 * Gets the mail audit address.
	 *
	 * @return the mail audit address
	 */
	public String getMailAuditAddress() {
		return this.mailAuditaddress;
	}

	/**
	 * Gets the mail supp address.
	 *
	 * @return the mail supp address
	 */
	public String getMailSuppAddress() {
		return this.mailSuppaddress;
	}

	/**
	 * Send mail.
	 *
	 * @param composer
	 *            the composer
	 * @throws Exception
	 *             the exception
	 */
	public void sendMail(final MailComposer composer) throws Exception {
		MailInfo mi = composer.getMailInfo();
		if (mi == null) {
			MailService.logger.error("Cannot send mail, no content");
			return;
		}
		mi.setMailServer(this.mailServer);
		mi.setUser(this.mailUser);
		mi.setPass(this.mailPassword);
		mi.setAuthenticationRequired(this.mailAuthenticationRequired);
		mi.setStarttlsEnabled(this.mailStarttlsEnabled);
		try {
			this.sendMail(mi);
		} finally {
			this.deleteTemporaryFiles(mi.getAttchFiles());
		}
	}

	/**
	 * Delete temporary files.
	 *
	 * @param attchs
	 *            the attchs
	 */
	protected void deleteTemporaryFiles(File[] attchs) {
		if (attchs != null) {
			for (File attch : attchs) {
				attch.delete();
			}
		}
	}

	/**
	 * Envía el mail.
	 *
	 * @param info
	 *            the info
	 * @throws Exception
	 *             the exception
	 */
	private void sendMail(final MailInfo info) throws Exception {
		Properties p = new Properties();
		p.put("mail.smtp.host", info.getMailServer());
		p.put("user.name", info.getUser());
		p.put("mail.smtp.auth", String.valueOf(info.isAuthenticationRequired()));
		p.put("mail.smtp.starttls.enable",String.valueOf(info.isStarttlsEnabled()));
		// prop.put("mail.smtp.timeout", Integer.toString(timeoutSMTP));

		Authenticator authenticator = new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				if ((info.getPass() != null) && info.isAuthenticationRequired()) {
					return new PasswordAuthentication(info.getUser(), info.getPass());
				}
				return null;
			}
		};
		Session session = Session.getInstance(p, authenticator);
		// session.setDebug(true);

		// Crea el mensaje
		MimeMessage message = new MimeMessage(session);
		// Cubre los destinatarios.
		message.setFrom(new InternetAddress(info.getUser()));
		message.setRecipients(Message.RecipientType.TO, this.getInternetAddresses(info.getTo()));
		message.setRecipients(Message.RecipientType.CC, this.getInternetAddresses(info.getCc()));
		message.setRecipients(Message.RecipientType.BCC, this.getInternetAddresses(info.getBcc()));
		message.setSubject(info.getSubject());
		if (info.getAckAddress() != null) {
			message.setHeader("Disposition-Notification-To", info.getAckAddress());
		}
		// Cubre el cuerpo del mensaje
		if ((info.getAttchNames() == null) || (info.getAttchNames().length == 0)) {
			this.composeSimpleMail(message, info.getContent(), info.getContentType());
		} else {
			this.composeAttachmentMail(message, info.getContent(), info.getContentType(), info.getAttchNames(), info.getAttchFiles());
		}
		// pone la fecha: header
		message.setSentDate(new Date());
		// envía el mensaje
		Transport.send(message);
	}

	/**
	 * Devuelve un array de InternetAddress a partir de un array de Strings.
	 *
	 * @param addresses
	 *            the addresses
	 * @return the internet addresses
	 * @throws AddressException
	 *             the address exception
	 */
	protected InternetAddress[] getInternetAddresses(String[] addresses) throws AddressException {
		if (addresses == null) {
			return null;
		}
		InternetAddress[] inets = new InternetAddress[addresses.length];
		for (int i = 0; i < inets.length; i++) {
			inets[i] = new InternetAddress(addresses[i]);
		}
		return inets;
	}

	/**
	 * Compone un email simple (sólo texto sin adjuntos).
	 *
	 * @param message
	 *            the message
	 * @param content
	 *            the content
	 * @param contentType
	 *            the content type
	 * @throws Exception
	 *             the exception
	 */
	protected void composeSimpleMail(MimeMessage message, String content, String contentType) throws Exception {
		message.setContent(content.toString(), contentType);
	}

	/**
	 * Compone un email con adjuntos (MultiPart).
	 *
	 * @param message
	 *            the message
	 * @param content
	 *            the content
	 * @param contentType
	 *            the content type
	 * @param attchNames
	 *            the attch names
	 * @param attchs
	 *            the attchs
	 * @throws MessagingException
	 *             the messaging exception
	 */
	protected void composeAttachmentMail(MimeMessage message, String content, String contentType, String[] attchNames, File[] attchs) throws MessagingException {
		Multipart mp = new MimeMultipart("related");
		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setContent(content, contentType);
		mp.addBodyPart(mbp1);

		for (int i = 0; i < attchNames.length; i++) {
			// create the second message part
			MimeBodyPart mbp = new MimeBodyPart();
			// attach the file to the message
			// mbp.setDataHandler(new DataHandler(new
			// InputStreamDataSource(attchs[i], attchNames[i])) {
			mbp.setDataHandler(new DataHandler(new FileDataSource(attchs[i])) {
				@Override
				public void writeTo(OutputStream os) throws IOException {
					super.writeTo(os);
				}
			});
			mbp.setFileName(attchNames[i]);

			// nuevo Lorena
			mbp.setHeader("Content-ID", "<" + attchNames[i] + ">");

			// create the Multipart and its parts to it
			mp.addBodyPart(mbp);
			// add the Multipart to the message
			message.setContent(mp);
		}
	}

	/**
	 * Property changed.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	@Override
	public void propertyChanged(String key, String value) {
		if (MailFields.MAIL_SERVER.equalsIgnoreCase(key)) {
			this.mailServer = value;
		} else if (MailFields.MAIL_USER.equalsIgnoreCase(key)) {
			this.mailUser = value;
		} else if (MailFields.MAIL_PASS.equalsIgnoreCase(key)) {
			this.mailPassword = value;
		} else if (MailFields.MAIL_PROPERTIES.equalsIgnoreCase(key)) {
			String[] mailProperties = value.split(";");
			for (String mailProperty : mailProperties) {
				String[] keyValue = mailProperty.split(":");
				switch (keyValue[0]) {
					case MailFields.MAIL_AUTH:
						MailService.this.mailAuthenticationRequired = Boolean.valueOf(keyValue[1]);
						break;
					case MailFields.MAIL_STARTTLS:
						MailService.this.mailStarttlsEnabled = Boolean.valueOf(keyValue[1]);
						break;
					default:
						MailService.logger.error("UNKNOWN key:value {}:{} found in {}", keyValue[0], keyValue[1], MailFields.MAIL_PROPERTIES);
				}
			}
		} else if (MailFields.MAIL_REQADDRESS.equalsIgnoreCase(key)) {
			this.mailReqAddress = value;
		} else if (MailFields.MAIL_SUPPADDRESS.equalsIgnoreCase(key)) {
			this.mailSuppaddress = value;
		} else if (MailFields.MAIL_AUDITADDRESS.equalsIgnoreCase(key)) {
			this.mailAuditaddress = value;
		}
	}

	/**
	 * Checks if the given string matches the regular expression of an email address.
	 *
	 * @param email
	 *            the email
	 * @return true, if the string looks like a valid email address.
	 */
	public static boolean isValidEmail(String email) {
		// Improved pattern. Allow more characters and avoid spaces
		String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z](?:[a-z]*[a-z])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		// Match the given string with the pattern
		Matcher m = p.matcher(email);

		return m.matches();
	}
}
