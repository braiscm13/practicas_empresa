package com.opentach.client.comp.assetchooser.tablewindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.comp.assetchooser.AssetComboDataField;
import com.opentach.client.comp.assetchooser.providers.IAssetProvider;
import com.utilmize.client.gui.Column;
import com.utilmize.client.gui.field.URadioButtonDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.field.reference.window.TableWindow;
import com.utilmize.client.gui.field.table.UTable;

public class AssetTableWindow extends TableWindow {
	protected JPanel	tableContainer;

	private String		selectionMode	= null;

	public AssetTableWindow(UReferenceDataField parentDataField, Window f) {
		super(parentDataField, f);
	}

	protected AssetComboDataField getAssetField() {
		return (AssetComboDataField) this.parentDataField;
	}

	public IAssetProvider getSelectedProvider() {
		return this.getAssetField().getAssetProvider(this.selectionMode);
	}

	@Override
	protected Container buildCenterPanel(Hashtable parameters) throws Exception {
		this.tableContainer = new JPanel(new BorderLayout());
		this.table = this.buildTable(parameters); // dummy
		JPanel panelRadio = this.buildOptionsPanel();
		this.tableContainer.add(panelRadio, BorderLayout.NORTH);
		return this.tableContainer;
	}

	private JPanel buildOptionsPanel() {
		Column column = new Column(EntityResultTools.keysvalues( //
				"expand", "yes", //
				"title", ApplicationManager.getTranslation("assetgroup.selectionMode") //
				));
		ButtonGroup buttonGroup = new ButtonGroup();
		for (IAssetProvider assetProvider : this.getAssetField().getAssetProviders()) {
			URadioButtonDataField option = new URadioButtonDataField(EntityResultTools.keysvalues( //
					"attr", assetProvider.getId(), //
					"text", ApplicationManager.getTranslation(assetProvider.getRadioButtonText()), //
					"dim", "no", //
					"labelposition", "right", //
					"align", "left" //
					));
			option.addItemListener(new SelectionModeListener());
			buttonGroup.add((AbstractButton) option.getDataField());
			if (buttonGroup.getSelection() == null) {
				buttonGroup.setSelected(option.getAbstractButton().getModel(), true);
			}
			column.add(option, option.getConstraints(column.getLayout()));
		}
		return column;
	}

	private class SelectionModeListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent evt) {
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				IdentifiedElement formComponent = (IdentifiedElement) SwingUtilities.getAncestorOfClass(FormComponent.class, (Component) evt.getSource());
				String currentSelection = (String) formComponent.getAttribute();
				if ((AssetTableWindow.this.selectionMode == null) || !AssetTableWindow.this.selectionMode.equals(currentSelection)) {
					AssetTableWindow.this.selectionMode = currentSelection;
					AssetTableWindow.this.getAssetField().getAssetProvider(AssetTableWindow.this.selectionMode).onProviderSelected();
					System.out.println(AssetTableWindow.this.selectionMode);
				}
			}
		}
	}

	@Override
	protected Table buildTable(Hashtable parameters) throws Exception {
		return new UTable(parameters);// super.buildTable(parameters);// dummy
	}

	@Override
	protected JButton buildOKButton(Map parameters) {
		return this.buildButton(true, TableWindow.OK_BUTTON_TEXT, TableWindow.OK_ICON, new AssetOKListener(this, this.getAssetField()));
	}

	@Override
	protected JButton buildRefreshCacheButton(Map parameters) {
		return this.buildButton(false, TableWindow.REFRESH_BUTTON_TEXT, TableWindow.REFRESH_ICON, new AssetRefreshListener(this, this.getAssetField()));
	}

	@Override
	protected JButton buildCancelButton(Map parameters) {
		return super.buildCancelButton(parameters);
	}

	@Override
	protected void onPropertyChangeCache(Object value) {
		// do nothing
	}

	@Override
	protected void onPropertyChangeValue(Object value) {
		// do nothing
	}

	@Override
	public void configureAdvancedQueryMode(boolean enabled) {
		for (IAssetProvider assetProvider : this.getAssetField().getAssetProviders()) {
			assetProvider.configureAdvancedQueryMode();
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && (this.mode == TableWindowMode.DEFAULT) && this.isValidToInitCache()) {
			this.parentDataField.ensureInitCache();
		}
		this.setLocationRelativeTo(this.parentDataField.getParentForm());
		super.setVisible(visible);
	}

	public void setActiveTable(Table table) {
		Component oldTable = ((BorderLayout) this.tableContainer.getLayout()).getLayoutComponent(BorderLayout.CENTER);
		if (oldTable != null) {
			this.tableContainer.remove(oldTable);
		}
		this.tableContainer.add(table, BorderLayout.CENTER);
		this.invalidate();
		this.doLayout();
		this.revalidate();
	}

}
