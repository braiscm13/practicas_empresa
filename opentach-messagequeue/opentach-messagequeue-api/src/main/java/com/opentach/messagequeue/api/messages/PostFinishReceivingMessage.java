package com.opentach.messagequeue.api.messages;

import com.opentach.messagequeue.api.IMessageQueueMessage;

public interface PostFinishReceivingMessage extends IMessageQueueMessage {

	boolean isAssignedToContract();

	Object getCgContract();

	String getFileName();

	Number getIdFichero();

	int getSessionId();

}
