package com.opentach.server.dms.services;

import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.AbstractDelegate;

/**
 * Abstract class implemented bu DMS helpers (docFelper, fileHelper, versionHelper). This ensure to set a default configuration to columnHelper, that manages all relative about
 * columns naming.
 */
public abstract class AbstractDMSServiceHelper extends AbstractDelegate {

	private DMSColumnHelper columnHelper;

	public AbstractDMSServiceHelper(IOpentachServerLocator locator) {
		super(locator);
		this.columnHelper = new DMSColumnHelper();
	}


	protected DMSColumnHelper getColumnHelper() {
		return this.columnHelper;
	}

	public void setColumnHelper(DMSColumnHelper columnHelper) {
		this.columnHelper = columnHelper;
	}
}
