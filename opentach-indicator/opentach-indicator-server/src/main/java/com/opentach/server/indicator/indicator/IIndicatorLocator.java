package com.opentach.server.indicator.indicator;

import com.opentach.server.IOpentachServerLocator;

/**
 * This interface is implemented by indicators which wants access to the locator (with this one we can access locator, db, beans, ...)
 */
public interface IIndicatorLocator {

	void setLocator(IOpentachServerLocator locator);

	IOpentachServerLocator getLocator();
}
