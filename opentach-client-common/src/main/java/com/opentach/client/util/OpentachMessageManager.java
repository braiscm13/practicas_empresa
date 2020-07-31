package com.opentach.client.util;

import java.util.concurrent.ExecutionException;

import com.ontimize.jee.desktopclient.components.messaging.MessageManager;

import net.sf.jasperreports.engine.fill.JRExpressionEvalException;

public class OpentachMessageManager extends MessageManager {

	public OpentachMessageManager() {
		super();
	}

	@Override
	public Throwable getCauseException(Throwable error) {
		if (error instanceof JRExpressionEvalException) {
			if (error.getCause() != null) {
				return super.getCauseException(error.getCause());
			}
		} else if ((error instanceof ExecutionException) && (error.getCause() != null) && (error.getCause() != error)) {
			return this.getCauseException(error.getCause());
		} else if ((error instanceof RuntimeException) && (error.getCause() != null) && (error.getCause() != error)) {
			return this.getCauseException(error.getCause());
		}
		return super.getCauseException(error);
	}

}
