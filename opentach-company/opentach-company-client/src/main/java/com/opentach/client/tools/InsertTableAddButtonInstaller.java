package com.opentach.client.tools;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;
import com.utilmize.client.gui.field.table.editor.UXmlReferenceCellEditor;

/**
 * This class adds functionality to the {@link Table#BUTTON_PLUS} of a
 * {@link Table} so that when pressed it behaves as if the user had clicked on
 * the given column (and therefore started insertion of a new record).
 * <p>
 * Useful to avoid creating a single-field form to be invoked to insert a new
 * record (i.e., for people that find the "clicking in the last empty row to add
 * a new record" feature confusing).
 *
 */
public class InsertTableAddButtonInstaller {

	private static final Logger logger = LoggerFactory.getLogger(InsertTableAddButtonInstaller.class);

	private InsertTableAddButtonInstaller() {
		throw new IllegalStateException("Utility class");
	}


	/**
	 * Adds an {@link ActionListener } to the {@link Table#BUTTON_PLUS} of a
	 * {@link Table} so that when pressed it behaves as if the user had clicked on
	 * the given column (and therefore started insertion of a new record).
	 *
	 * @param table   {@link Table} that owns the "PLUS" button.
	 * @param colName Name of the column to edit.
	 */

	public static void setListener(Table table, String colName) {
		InsertTableAddButtonInstaller.logger.trace("ADDING LISTENER TO COLUMN {} OF TABLE {}", colName, table.getEntityName());

		if (!table.isInsertingEnabled()) {
			return;
		}

		TableButton button = (TableButton) table.getTableComponentReference(Table.BUTTON_PLUS);

		button.setEnabled(true);
		button.setVisible(true);

		button.addActionListener(event -> {
			InsertTableAddButtonInstaller.logger.debug("GOT ACTION FOR COLUMN {} OF TABLE {}", colName, table.getEntityName());
			JTable jTable = table.getJTable();

			int colId = -1;
			int rowId = jTable.getRowCount() - 1;

			for (int col = 0; col < jTable.getColumnCount(); col++) {
				if (colName.equals(jTable.getColumnName(col))) {
					colId = col;
				}
			}

			if ((colId < 0) || (rowId < 0)) {
				InsertTableAddButtonInstaller.logger.error("COULDN'T FIND INSERT ROW AND/OR COLUMN '{}' FOR TABLE {}", colName, table.getEntityName());
				return;
			}

			table.setSelectedRow(rowId);
			jTable.editCellAt(rowId, colId);

			// If the editor is a combo, the value is already loaded. We insert an
			// ENTER key press to accept the selected value and validate it. Gnapa:

			if (jTable.getCellEditor(rowId, colId) instanceof UXmlReferenceCellEditor) {
				try {
					Robot robot = new Robot();
					robot.keyPress(KeyEvent.VK_ENTER);
					robot.keyRelease(KeyEvent.VK_ENTER);
				} catch (AWTException exception) {
					InsertTableAddButtonInstaller.logger.warn("FAILED TO SIMULATE ENTER KEY PRESSED", exception);
				}
			}
		});
	}
}
