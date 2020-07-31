package com.opentach.client.comp;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.MapTools;
import com.utilmize.client.gui.buttons.UButton;

public class TabbedFormOpenButton extends UButton {

	private static final Logger	logger	= LoggerFactory.getLogger(TabbedFormOpenButton.class);

	public TabbedFormOpenButton(Hashtable parameters) throws Exception {
		super(parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.UButton#init(java.util.Hashtable)
	 */
	@Override
	public void init(Hashtable params) {
		super.init(this.completeParameters(params));
	}

	/**
	 * Complete parameters.
	 *
	 * @param params
	 *            the params
	 * @return the hashtable
	 */
	private Hashtable<?, ?> completeParameters(Hashtable<?, ?> params) {
		MapTools.safePut((Hashtable<Object, Object>) params, "key", "opentab", true);
		MapTools.safePut((Hashtable<Object, Object>) params, "text", "", true);
		MapTools.safePut((Hashtable<Object, Object>) params, "tip", "open_new_tab", true);
		MapTools.safePut((Hashtable<Object, Object>) params, "icon", "com/opentach/client/rsc/elements116.png", true);
		MapTools.safePut((Hashtable<Object, Object>) params, "listener", TabbedFormOpenListener.class.getName(), true);
		return params;
	}
}
