package com.opentach.messagequeue.api.messages;

import com.opentach.messagequeue.api.IMessageQueueMessage;

public interface LocationIdentifyQueueMessage extends IMessageQueueMessage {
	String getSmartphoneId();

	String getTokenId();
}
