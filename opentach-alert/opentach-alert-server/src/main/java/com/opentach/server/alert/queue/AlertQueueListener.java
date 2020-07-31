package com.opentach.server.alert.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ontimize.jee.server.spring.SpringTools;
import com.opentach.common.alert.queue.AlertQueueNames;
import com.opentach.common.alert.queue.ExecuteAlertQueueMessage;
import com.opentach.common.alert.queue.SendMailQueueMessage;
import com.opentach.common.alert.services.IAlertService;
import com.opentach.messagequeue.api.IMessageQueueListener;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.IMessageQueueMessage;

/**
 * This class will receive and manage all events in message queue related with alerts (emails, executions ,...).
 * The execution will be delegated to IAlertService.
 */
@Component
public class AlertQueueListener implements InitializingBean, IMessageQueueListener {

	/** The CONSTANT logger */
	private static final Logger		logger	= LoggerFactory.getLogger(AlertQueueListener.class);

	@Autowired
	protected IMessageQueueManager	queueService;

	@Autowired
	protected IAlertService			alertService;

	public AlertQueueListener() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		this.queueService.registerListener(AlertQueueNames.SEND_MAIL, this);
		this.queueService.registerListener(AlertQueueNames.EXECUTE_ALERT, this);
	}

	@Override
	public void onQueueEvent(String queueName, IMessageQueueMessage message) {
		AlertQueueListener.logger.debug("New event QUEUE:{}__MESSAGE:{}", queueName, message);
		try {
			if (AlertQueueNames.SEND_MAIL.equals(queueName) && (message instanceof SendMailQueueMessage)) {
				SendMailQueueMessage typedMessage = (SendMailQueueMessage) message;
				this.getAlertService().sendEmailCompose(typedMessage.getTo(), typedMessage.getCc(), typedMessage.getBcc(), typedMessage.getTemplateSubject(),
						typedMessage.getTemplateBody(), typedMessage.getData());
			} else if (AlertQueueNames.EXECUTE_ALERT.equals(queueName) && (message instanceof ExecuteAlertQueueMessage)) {
				this.getAlertService().executeAlertAsynch(((ExecuteAlertQueueMessage) message).getAlrId());
			} else {
				AlertQueueListener.logger.warn("UNSPECTED_MESSAGE__QUEUE:{}__MESSAGE:{}", queueName, message);
			}
		} catch (Exception err) {
			AlertQueueListener.logger.error("E_PROCESSING_MESSAGE", err);
		}
	}

	protected IAlertService getAlertService() {
		return SpringTools.getTargetObject(this.alertService, IAlertService.class);
	}
}
