package com.opentach.client.modules.stats.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.utilmize.client.gui.Row;

public class DummyCard extends AbstractStatisticsCard<Row> {

	private static final Logger	logger	= LoggerFactory.getLogger(DummyCard.class);

	public DummyCard(OpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	protected Row buildViewComponent() {

		Hashtable<Object, Object> parametersRow = EntityResultTools.keysvalues(//
				"expand", "yes"//
				);
		try {
			Row row = new Row(parametersRow);
			row.setResourceBundle(ApplicationManager.getApplicationBundle());
			return row;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void refresh() {
	}

}
