package com.opentach.client.comp.assetchooser.providers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.ListSelectionModel;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.client.comp.assetchooser.AssetComboDataField;
import com.opentach.client.comp.assetchooser.tablewindow.AssetTableWindow;
import com.opentach.common.exception.OpentachRuntimeException;
import com.utilmize.client.gui.field.table.UTable;

public abstract class AbstractAssetProvider implements IAssetProvider {

	private final AssetComboDataField assetComboDataField;

	public AbstractAssetProvider(AssetComboDataField assetComboDataField) {
		super();
		this.assetComboDataField = assetComboDataField;
	}

	public AssetComboDataField getComboField() {
		return this.assetComboDataField;
	}

	public AssetTableWindow getTableWindow() {
		return (AssetTableWindow) this.getComboField().getTableWindow();
	}

	public EntityReferenceLocator getLocator() {
		return this.getComboField().getLocator();
	}

	public int getSessionId() {
		try {
			return this.getLocator().getSessionId();
		} catch (Exception err) {
			throw new OpentachRuntimeException(err);
		}
	}

	protected UTable buildTable(Hashtable parameters) {
		try {
			Hashtable tableParameters = DefaultXMLParametersManager.getParameters(Table.class.getName());
			tableParameters.putAll(ParseUtilsExtended.getParametersPreffixed(parameters, "table.", true));
			// tableParameters.put("cols", StringTools.concatWithSeparator(";", true, this.parentDataField.getAttributes()));
			// tableParameters.put("visiblecols", StringTools.concatWithSeparator(";", true, this.parentDataField.getVisibleCols()));

			// 5.2067EN - Parameter onsetvalueset cannot be shared by field and table, it is configured only for field
			tableParameters.remove(Table.ONSETVALUESET);
			tableParameters.remove(UTable.PARENTKEY_LISTENER);
			tableParameters.remove("listener");
			MapTools.safePut(tableParameters, Table.CONTROLS_VISIBLE, "no", true);
			MapTools.safePut(tableParameters, Table.NUM_ROWS_COLUMN, "no", true);
			final UTable table = new UTable(tableParameters);
			table.setEnabledDetail(false);
			// parentDataField.getDetailHelper().configureTableDetail(table);
			table.setMinRowHeight(Form.defaultTableViewMinRowHeight);
			table.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getJTable().addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						e.consume();
						if (table.getSelectedRow() >= 0) {
							AbstractAssetProvider.this.onTableSelection();
						}
					}
				}
			});
			table.getJTable().addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					if ((e.getKeyCode() == KeyEvent.VK_ENTER)) {
						AbstractAssetProvider.this.onTableSelection();
					}
				}
			});
			return table;
		} catch (Exception err) {
			throw new OpentachRuntimeException(err);
		}
	}

	protected void establishTable(Table table, EntityResult tableValue, AssetTableWindow assetTableWindow) {
		// table = this.buildTable(parameters);
		table.setResourceBundle(this.getComboField().getResourceBundle());
		table.setValue(tableValue);
		table.setParentForm(this.getComboField().getParentForm());
		assetTableWindow.setActiveTable(table);
	}

	protected abstract Table getTable();

	@Override
	public void configureAdvancedQueryMode() {
		if (this.getComboField().isAdvancedQueryMode()) {
			this.getTable().getJTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			this.getTable().getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}
}
