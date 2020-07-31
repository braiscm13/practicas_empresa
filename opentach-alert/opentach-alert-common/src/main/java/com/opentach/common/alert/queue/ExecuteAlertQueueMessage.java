package com.opentach.common.alert.queue;

import com.opentach.messagequeue.api.IMessageQueueMessage;

public class ExecuteAlertQueueMessage implements IMessageQueueMessage {

	protected Object alrId;

	public ExecuteAlertQueueMessage(Object alrId) {
		this.alrId = alrId;
	}

	public Object getAlrId() {
		return this.alrId;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(" + this.getAlrId() + ")";
	}

}
