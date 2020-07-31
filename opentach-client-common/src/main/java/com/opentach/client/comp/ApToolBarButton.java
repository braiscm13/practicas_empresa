package com.opentach.client.comp;

import java.awt.Dimension;
import java.util.Hashtable;

public class ApToolBarButton extends com.ontimize.gui.ApToolBarButton {

	public ApToolBarButton(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(24, 24);
	}

}
