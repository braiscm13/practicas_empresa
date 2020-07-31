package com.ontimize.util.alerts;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.util.notice.INoticeSystem;
import com.ontimize.util.notice.SendAutomaticNotice;

public class MySendAutomaticNotice extends SendAutomaticNotice {

	private static final Logger	logger	= LoggerFactory.getLogger(MySendAutomaticNotice.class);

	@Override
	protected Hashtable getMessageData() {

		Hashtable messageData = super.getMessageData();
		try {
			Hashtable taskConfiguration = DefaultAlertManager.getTableTaskConfiguration(this.taskName, this.taskGroup);

			Object value = taskConfiguration.get(IAlertSystem.NOTICE_TO_PARAMETER);
			if (value != null) {
				Vector toParams = new Vector();
				StringTokenizer tkn = new StringTokenizer((String) value, ";");
				while (tkn.hasMoreElements()) {
					toParams.add(tkn.nextToken());
				}
				messageData.put(IAlertSystem.NOTICE_TO_PARAMETER, toParams);
				messageData.put(INoticeSystem.NOTICE_TO_PARAMETER, toParams);
			} else {
				messageData.remove(IAlertSystem.NOTICE_TO_PARAMETER);
				messageData.remove(INoticeSystem.NOTICE_TO_PARAMETER);
			}

			value = taskConfiguration.get(IAlertSystem.NOTICE_MAILTO_PARAMETER);
			if (value != null) {
				Vector toParams = new Vector();
				StringTokenizer tkn = new StringTokenizer((String) value, ";");
				while (tkn.hasMoreElements()) {
					toParams.add(tkn.nextToken());
				}
				messageData.put(IAlertSystem.NOTICE_MAILTO_PARAMETER, toParams);
				messageData.put(INoticeSystem.NOTICE_MAILTO_PARAMETER, toParams);
			} else {
				messageData.remove(IAlertSystem.NOTICE_MAILTO_PARAMETER);
				messageData.remove(INoticeSystem.NOTICE_MAILTO_PARAMETER);
			}

			value = taskConfiguration.get(IAlertSystem.NOTICE_FORCE_READ);
			if (value != null) {
				messageData.put(IAlertSystem.NOTICE_FORCE_READ, value);
				messageData.put(INoticeSystem.NOTICE_FORCE_READ, value);
			}

			value = taskConfiguration.get(IAlertSystem.NOTICE_RESPONSE_REQUEST);
			if (value != null) {
				messageData.put(IAlertSystem.NOTICE_RESPONSE_REQUEST, value);
				messageData.put(INoticeSystem.NOTICE_RESPONSE_REQUEST, value);
			}

			value = taskConfiguration.get(IAlertSystem.NOTICE_SEND_MAIL);
			if (value != null) {
				messageData.put(IAlertSystem.NOTICE_SEND_MAIL, value);
				messageData.put(INoticeSystem.NOTICE_SEND_MAIL, value);
			}
		} catch (Exception e) {
			MySendAutomaticNotice.logger.error(null, e);
		}

		return messageData;
	}
}
