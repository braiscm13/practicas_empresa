package com.opentach.messagequeue.api;

public interface IMessageQueueListener {

	void onQueueEvent(String queueName, IMessageQueueMessage message);
}
