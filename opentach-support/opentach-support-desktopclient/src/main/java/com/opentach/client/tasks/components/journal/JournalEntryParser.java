package com.opentach.client.tasks.components.journal;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.ontimize.db.EntityResult;
import com.utilmize.client.gui.list.IListParser;
import com.utilmize.client.gui.list.ListComponent;

public class JournalEntryParser implements IListParser {

	protected Hashtable<Object, Object>	params;

	protected ListComponent			parentComp;

	public JournalEntryParser(ListComponent parentComp, Hashtable<Object, Object> params) {
		this.params = params;
		this.parentComp = parentComp;
	}

	@Override
	public List<Component> parseComponent(Object value) {
		if (value == null) {
			return new ArrayList<>();
		}
		if (value instanceof EntityResult) {
			List<Component> list = new ArrayList<>();
			int numRecords = ((EntityResult) value).calculateRecordNumber();
			for (int i = 0; i < numRecords; i++) {
				list.add(new JournalEntryComponent(((EntityResult) value).getRecordValues(i)));
			}
			return list;
		}
		return Arrays.asList(new JournalEntryComponent[] { new JournalEntryComponent(value) });
	}

}
