package com.opentach.client.comp;

import java.util.Hashtable;

import com.utilmize.client.gui.field.table.UInsertTable;

public class OpentachInsertTable extends UInsertTable {

	public OpentachInsertTable(Hashtable<String, Object> parameters) throws Exception {
		super(parameters);
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put("refreshicon", "com/opentach/client/rsc/refresh24.png");
		av.put("defaultcharticon", "com/opentach/client/rsc/chart24.png");
		av.put("printicon", "com/opentach/client/rsc/printer24.png");
		this.configureButtons(av);
	}
}
