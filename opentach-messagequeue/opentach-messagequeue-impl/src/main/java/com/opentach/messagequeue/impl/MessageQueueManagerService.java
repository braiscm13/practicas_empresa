package com.opentach.messagequeue.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.opentach.common.util.concurrent.NamedThreadFactory;
import com.opentach.messagequeue.api.IMessageQueueListener;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.IMessageQueueMessage;

@Service("QueueManagerService")
public class MessageQueueManagerService implements IMessageQueueManager {

	/** The CONSTANT logger */
	private static final Logger								logger	= LoggerFactory.getLogger(MessageQueueManagerService.class);

	private final Map<String, List<IMessageQueueListener>>	listeners;
	private final ExecutorService					executor;

	public MessageQueueManagerService() {
		super();
		this.listeners = new HashMap<>();
		this.executor = Executors.newFixedThreadPool(5, new NamedThreadFactory("QueueManagerService"));
	}

	@Override
	public void registerListener(String queueName, IMessageQueueListener listener) {
		MessageQueueManagerService.logger.info("Listener '{}' registered to listen to queue '{}'", listener, queueName);
		List<IMessageQueueListener> listenerList = this.listeners.computeIfAbsent(queueName, k -> new ArrayList<IMessageQueueListener>());
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	@Override
	public void unregisterListener(String queueName, IMessageQueueListener listener) {
		MessageQueueManagerService.logger.info("Listener '{}' un-registered to listen to queue '{}'", listener, queueName);
		List<IMessageQueueListener> listenerList = this.listeners.computeIfAbsent(queueName, k -> new ArrayList<IMessageQueueListener>());
		listenerList.remove(listener);
	}

	@Override
	public void pushEvent(String queueName, IMessageQueueMessage event) {
		MessageQueueManagerService.logger.debug("PushEvent on queue:'{}'   message:'{}'", queueName, event);
		List<IMessageQueueListener> listenerList = this.listeners.computeIfAbsent(queueName, k -> new ArrayList<IMessageQueueListener>());
		for (IMessageQueueListener listener : listenerList) {
			MessageQueueManagerService.logger.trace("PushEvent on queue:'{}'   message:'{}'  notifies:'{}'", queueName, event, listener);
			listener.onQueueEvent(queueName, event);
		}
	}

	@Override
	public void pushEventInBackground(String queueName, IMessageQueueMessage message) {
		MessageQueueManagerService.logger.debug("pushEventInBackground on queue:'{}'   message:'{}'", queueName, message);
		List<IMessageQueueListener> listenerList = this.listeners.computeIfAbsent(queueName, k -> new ArrayList<IMessageQueueListener>());
		for (IMessageQueueListener listener : listenerList) {
			MessageQueueManagerService.logger.trace("pushEventInBackground on queue:'{}'   message:'{}'  notifies:'{}'", queueName, message, listener);
			this.executor.submit(() -> listener.onQueueEvent(queueName, message));
		}
	}

}
