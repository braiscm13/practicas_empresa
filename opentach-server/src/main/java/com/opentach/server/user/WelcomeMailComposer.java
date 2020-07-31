package com.opentach.server.user;

import java.util.HashMap;
import java.util.Map;

import com.ontimize.jee.common.tools.Template;
import com.opentach.server.mail.MailComposer;

/**
 * The Class WelcomeMailComposer.
 */
public class WelcomeMailComposer extends MailComposer {

	/** The login. */
	private final String	login;

	/** The pass. */
	private final String	pass;

	/**
	 * Instantiates a new welcome mail composer.
	 *
	 * @param login
	 *            the login
	 * @param pass
	 *            the pass
	 * @param mailto
	 *            the mailto
	 */
	public WelcomeMailComposer(String login, String pass, String mailto) {
		super(mailto, null, null, null);
		this.login = login;
		this.pass = pass;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.mail.MailComposer#composeContent(java.util.Map)
	 */
	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		Template template = this.loadTemplate("mailTemplateWelcome.html");
		Map<String, String> replace = new HashMap<String, String>();
		replace.put("%USER%", this.login);
		replace.put("%PASS%", this.pass);
		return template.fillTemplate(replace);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.mail.MailComposer#getSubject()
	 */
	@Override
	protected String getSubject() {
		return "Bienvenido a Opentach";
	}

}
