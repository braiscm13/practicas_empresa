package com.opentach.client.modules.stats;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.cardboard.CardBoard;
import com.utilmize.client.listeners.UForceQueryWithFiltersListener;

public class SearchListener extends UForceQueryWithFiltersListener {

	@FormComponent(attr = "CARD_BOARD")
	protected CardBoard	cardBoard;

	public SearchListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void launchSearch() {
		this.cardBoard.refresh();
	}

	@Override
	protected boolean getEnableValueToSet() {
		return true;
	}
}
