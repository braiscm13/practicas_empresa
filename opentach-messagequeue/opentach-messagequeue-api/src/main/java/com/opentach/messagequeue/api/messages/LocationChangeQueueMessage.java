package com.opentach.messagequeue.api.messages;

import java.util.Date;

import com.opentach.messagequeue.api.IMessageQueueMessage;

public interface LocationChangeQueueMessage extends IMessageQueueMessage {
	String getSmartphoneId();

	double getLatitude();

	double getLongitude();

	double getAccuracy();

	Date getDate();
}
