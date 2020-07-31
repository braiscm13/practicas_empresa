package com.opentach.common.alert.queue;

import java.util.List;
import java.util.Map;

import com.opentach.messagequeue.api.IMessageQueueMessage;

/**
 * This message represents an event about some fact that affects to labor recompute task of an employee of a company, dated on specific time (latest)
 */
public class SendMailQueueMessage implements IMessageQueueMessage {

	protected List<String>			to;
	protected List<String>			cc;
	protected List<String>			bcc;
	protected String				templateSubject;
	protected String				templateBody;
	protected Map<String, Object>	data;

	public SendMailQueueMessage(List<String> to, List<String> cc, List<String> bcc, String templateSubject, String templateBody, Map<String, Object> data) {
		super();
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.templateSubject = templateSubject;
		this.templateBody = templateBody;
		this.data = data;
	}

	public List<String> getTo() {
		return this.to;
	}

	public List<String> getCc() {
		return this.cc;
	}

	public List<String> getBcc() {
		return this.bcc;
	}

	public String getTemplateSubject() {
		return this.templateSubject;
	}

	public String getTemplateBody() {
		return this.templateBody;
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(TO:'" + this.getTo() + "', CC:'" + this.getCc() + "', BCC:'" + this.getBcc() + "', TEMPLATE_SUBJECT:'" + this
				.getTemplateSubject() + "', TEMPLATE_BODY:'" + this.getTemplateBody() + "', DATA:'" + this.getData() + "')";
	}

}
