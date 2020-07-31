package com.opentach.model.scard;

import java.util.EventListener;

/**
 * To notify application that a card is inserted
 * 
 * @author rafael.lopez
 */
public interface CardListener extends EventListener {

	public void cardStatusChange(CardEvent ce);

}
