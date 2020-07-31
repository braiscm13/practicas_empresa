package com.opentach.client.mailmanager.im;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.listeners.UCancelListener;

/**
 * The listener interface for receiving save events. The class that is interested in processing a save event implements this interface, and the object created with that class is
 * registered with a component using the component's <code>addSaveListener<code> method. When the save event occurs, that object's appropriate method is invoked.
 *
 * @see SaveEvent
 */
public class IMMailSendCancelListener extends UCancelListener {

	/** The Constant logger. */
	protected static final Logger						logger							= LoggerFactory.getLogger(IMMailSendCancelListener.class);

	/**
	 * Instantiates a new save listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMMailSendCancelListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#init(java.util.Map)
	 */
	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}


	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		super.actionPerformed(event);
	}

	@Override
	protected boolean getEnableValueToSet() {
		return true;
	}

}
