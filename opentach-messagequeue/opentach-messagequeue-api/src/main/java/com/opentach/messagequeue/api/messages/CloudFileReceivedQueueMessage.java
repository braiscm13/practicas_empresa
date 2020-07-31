package com.opentach.messagequeue.api.messages;

import java.io.InputStream;

import com.opentach.messagequeue.api.IMessageQueueMessage;

public interface CloudFileReceivedQueueMessage extends IMessageQueueMessage {
	String getDevicePin();

	String getFileName();

	String getNotes();

	InputStream getInputStream();
}

