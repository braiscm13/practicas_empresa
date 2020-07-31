package com.opentach.client.company.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.labor.ICalendarHolidays;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.table.editor.UXMLButtonCellEditor;

public class ResetHolidayDataActionListener extends AbstractActionListenerButton implements CellEditorListener {
	private static final Logger	logger	= LoggerFactory.getLogger(ResetHolidayDataActionListener.class);
	private static boolean		locked	= false;

	public ResetHolidayDataActionListener() throws Exception {
		super();
	}

	public ResetHolidayDataActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public ResetHolidayDataActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public ResetHolidayDataActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	public ResetHolidayDataActionListener(UXMLButtonCellEditor editor, Hashtable params) throws Exception {
		super(editor, editor, params);
	}

	@Override
	public void editingStopped(final ChangeEvent event) {
		synchronized (this) {
			if (!ResetHolidayDataActionListener.locked) {
				ResetHolidayDataActionListener.locked = true;
				this.resetRecordAndRefresh();
				ResetHolidayDataActionListener.locked = false;
			}
		}
	}

	public void resetRecordAndRefresh() {
		Table table = (Table) this.getForm().getElementReference(ICalendarHolidays.ATTR.ENTITY);

		if (table == null) {
			ResetHolidayDataActionListener.logger.error("HOLIDAYS TABLE: NOT FOUND");
			return;
		}

		if (table.getSelectedRowsNumber() != 1) {
			ResetHolidayDataActionListener.logger.error("HOLIDAYS TABLE: NOT EXACTLY ONE ROW SELECTED");
			return;
		}

		Hashtable<String, Vector<Number>> tableRowData = table.getSelectedRowData();
		Number calendarId = tableRowData.get(ICalendarHolidays.ATTR.CALENDAR).get(0);
		Number dayId = tableRowData.get(ICalendarHolidays.ATTR.DAY_ID).get(0);
		boolean dismissed = tableRowData.get(ICalendarHolidays.ATTR.DISMISSED).get(0).intValue() != 0;
		boolean approved = tableRowData.get(ICalendarHolidays.ATTR.APPROVED).get(0).intValue() != 0;

		if (dismissed || approved) {
			EntityReferenceLocator locator = this.getReferenceLocator();

			try {
				Entity calendarHolidays = locator.getEntityReference(ICalendarHolidays.ATTR.ENTITY);
				calendarHolidays.update( //
						EntityResultTools.keysvalues( //
								"RESET", 1), //
						EntityResultTools.keysvalues( //
								ICalendarHolidays.ATTR.CALENDAR, calendarId, //
								ICalendarHolidays.ATTR.DAY_ID, dayId, //
								ICalendarHolidays.ATTR.DISMISSED, dismissed ? 1 : 0), //
						locator.getSessionId());

				table.refresh();
			} catch (Exception exception) {
				ResetHolidayDataActionListener.logger.error( //
						String.format("COULD NOT GET %s ENTITY", ICalendarHolidays.ATTR.ENTITY), //
						exception);
				this.getForm().message(exception.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void editingCanceled(ChangeEvent evt) {
		// NOTHING to do.
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		// NOTHING to do.
	}
}
