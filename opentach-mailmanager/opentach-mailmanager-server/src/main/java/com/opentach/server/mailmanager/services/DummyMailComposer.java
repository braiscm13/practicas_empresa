package com.opentach.server.mailmanager.services;

import java.util.Map;

import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailInfo;

public class DummyMailComposer extends MailComposer {

	private final MailInfo mailInfo;

	public DummyMailComposer(MailInfo mailInfo) {
		super(null, null, null, null);
		this.mailInfo = mailInfo;
	}

	@Override
	protected MailInfo getMailInfo() {
		return this.mailInfo;
	}

	@Override
	protected String composeContent(Map<String, Object> params) throws Exception {
		return null;
	}

	@Override
	protected String getSubject() {
		return null;
	}

}
