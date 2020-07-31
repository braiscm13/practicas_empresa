package com.gvenzl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailService;
import com.opentach.server.report.IGMailComposerMovil;

public class TestMail {

	public TestMail() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			TestMail.sendMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendMail() throws Exception {
		MailService ms = TestMail.createMailSender();

		String opentachMail = "sistemas@fortysoft.es";

		List<String> emailAddresses = new ArrayList<String>();
		emailAddresses.add("joaquin.romero@imatia.com");

		String mailto = MailComposer.joinMailAddresses(emailAddresses);
		ms.sendMail(new IGMailComposerMovil(mailto, opentachMail, new Locale("es", "ES"), new Hashtable<>()));
	}

	protected static MailService createMailSender() throws Exception {
		return new MailService(80, null, null) {
			@Override
			protected void updateMailConfiguration() {
				this.mailServer = "smtp.fortysoft.es";
				this.mailUser = "sistemas@fortysoft.es";
				this.mailPassword = "xser78A11";
				this.mailAuthenticationRequired = true;
				this.mailStarttlsEnabled = true;
				this.mailReqAddress = "sistemas@fortysoft.es";
				this.mailSuppaddress = "sistemas@fortysoft.es";
				this.mailAuditaddress = "sistemas@fortysoft.es";
			}
		};
	}

}
