package com.opentach.messagequeue.api;

public interface IMessageQueueManager {
	/**
	 * Register listener.
	 *
	 * @param queueName
	 *            the queue name
	 * @param listener
	 *            the listener
	 */
	void registerListener(String queueName, IMessageQueueListener listener);

	/**
	 * Unregister listener.
	 *
	 * @param queueName
	 *            the queue name
	 * @param listener
	 *            the listener
	 */
	void unregisterListener(String queueName, IMessageQueueListener listener);

	/**
	 * Push event.
	 *
	 * @param queueName
	 *            the queue name
	 * @param event
	 *            the event
	 */
	void pushEvent(String queueName, IMessageQueueMessage message);

	/**
	 * Push event in background.
	 *
	 * @param queueName
	 *            the queue name
	 * @param event
	 *            the event
	 */
	void pushEventInBackground(String queueName, IMessageQueueMessage message);
}
