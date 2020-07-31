package com.opentach.adminclient.modules.surveys.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.gui.table.Table;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.buttons.UTableButton;
import com.utilmize.client.gui.buttons.UTableButton.TableButtonXmlListener;

public class TableButtonListener extends TableButtonXmlListener {

	private UTableButton	button;
	private Table			table;
	private Hashtable<String, Object>	data, dataCache;

	public TableButtonListener(UButton button, Hashtable<?, ?> params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.button = this.getTableButton();
		this.table = this.getTabla();
		int index = this.table.getSelectedRow();
		this.data = this.table.getRowData(this.table.getSelectedRow());
		if (this.button.getKey().equals("up")) {
			if (this.table.getSelectedRow() != 0) {
				this.dataCache = this.table.getRowData(this.table.getSelectedRow() - 1);
				this.moveUp(index);
			}
		} else if (this.button.getKey().equals("down")) {
			if (this.table.getSelectedRow() != (this.table.getJTable().getRowCount() - 2)) {
				this.dataCache = this.table.getRowData(this.table.getSelectedRow() + 1);
				this.moveDown(index);
			}
		}
	}

	private void moveUp(int index) {
		Object optionCache = this.dataCache.get("OPTION_TITLE");
		Object option = this.data.get("OPTION_TITLE");
		this.dataCache.put("OPTION_TITLE", option);
		this.data.put("OPTION_TITLE", optionCache);
		this.table.updateRowData(this.data);
		this.table.updateRowData(this.dataCache);
	}

	private void moveDown(int index) {
		Object optionCache = this.dataCache.get("OPTION_TITLE");
		Object option = this.data.get("OPTION_TITLE");
		this.data.put("OPTION_TITLE", optionCache);
		this.dataCache.put("OPTION_TITLE", option);
		this.table.updateRowData(this.dataCache);
		this.table.updateRowData(this.data);
	}
}
