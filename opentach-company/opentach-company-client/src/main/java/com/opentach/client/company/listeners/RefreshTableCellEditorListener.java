package com.opentach.client.company.listeners;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.ontimize.gui.table.Table;

public class RefreshTableCellEditorListener implements CellEditorListener {
	private Table			table;
	private static boolean	locked	= false;

	/**
	 * This listener refreshes the table passed in its constructor whenever a {@link CellEditorListener#editingStopped(ChangeEvent)} is received.
	 *
	 */
	public RefreshTableCellEditorListener(Table table) {
		super();
		this.table = table;
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		// Probably not necessary - this should always be EDT.
		synchronized (this) {
			// Avoid recursion:
			if (!RefreshTableCellEditorListener.locked) {
				RefreshTableCellEditorListener.locked = true;
				this.table.refresh();
				RefreshTableCellEditorListener.locked = false;
			}
		}
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
		// NOTHING to do.
	}
}
