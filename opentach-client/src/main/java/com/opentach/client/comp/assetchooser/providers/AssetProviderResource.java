package com.opentach.client.comp.assetchooser.providers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CustomComboBoxModel;
import com.ontimize.gui.table.Table;
import com.opentach.client.comp.assetchooser.AssetComboDataField;
import com.opentach.client.comp.assetchooser.AssetComboDataField.AssetComboMode;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.field.reference.interfaces.IReferencePropertyChangeListener;
import com.utilmize.client.gui.field.table.UTable;

public class AssetProviderResource extends AbstractAssetProvider implements IReferencePropertyChangeListener {

	private static final Logger	logger	= LoggerFactory.getLogger(AssetProviderResource.class);

	private static final String	ID		= "optionResourceMode";
	private UTable				table;

	public AssetProviderResource(AssetComboDataField assetComboDataField) {
		super(assetComboDataField);
		assetComboDataField.addReferencePropertyChangeListener(this);
	}

	@Override
	public String getId() {
		return AssetProviderResource.ID;
	}

	@Override
	public String getRadioButtonText() {
		return AssetComboMode.DRIVER.equals(this.getComboField().getMode()) ? "assetgroup.optionDriverMode" : "assetgroup.optionVehicleMode";
	}

	@Override
	public void onProviderSelected() {
		this.getComboField().getCacheHelper().ensureInitCache();
		EntityResult dataCache = this.getComboField().getDataCache();
		this.establishTable(this.getTable(), dataCache, this.getTableWindow());
	}

	@Override
	protected Table getTable() {
		if (this.table == null) {
			Hashtable<String, Object> parameters = new Hashtable<>();
			parameters.put("parentkeys", "CG_CONTRATO;CIF");
			switch (this.getComboField().getMode()) {
				case DRIVER:
					parameters.put("cols", "IDCONDUCTOR;DNI;APELLIDOS;NOMBRE");
					parameters.put("visiblecols", "DNI;APELLIDOS;NOMBRE");
					parameters.put("entity", "EConductorContFicticio");
					break;
				case VEHICLE:
					parameters.put("cols", "MATRICULA");
					parameters.put("visiblecols", "MATRICULA");
					parameters.put("entity", "EVehiculoContFicticio");
					break;
			}
			this.table = this.buildTable(parameters);
		}
		return this.table;
	}

	@Override
	public void onTableSelection() {
		if (this.getTable().getSelectedRowsNumber() > 1) {
			// Support to advanced query mode
			int[] indices = this.getTable().getJTable().getSelectedRows();
			Vector vValues = new Vector();
			for (int i = 0; i < indices.length; i++) {
				Object oValue = this.getTable().getRowData(indices[i]).get(this.getComboField().getCode());
				if (oValue.equals(CustomComboBoxModel.NULL_SELECTION)) {
					continue;
				}
				vValues.add(oValue);
			}
			this.getComboField().setQueryValues(vValues);
		} else if (this.getTable().getSelectedRowsNumber() > 0) {
			// Change value of field
			Hashtable hFieldValue = this.getTable().getRowData(this.getTable().getSelectedRow());
			Object cod = hFieldValue.get(this.getComboField().getCode());
			this.getComboField().setValue(cod, ValueEvent.USER_CHANGE);
		}
		this.getComboField().hideTableWindow();
	}

	@Override
	public void onPropertyChange(String property, Object value) {
		if (UReferenceDataField.PROPERTY_CHANGE_BUNDLE.equals(property)) {
			this.onPropertyChangeBundle((ResourceBundle) value);
		} else if (UReferenceDataField.PROPERTY_CHANGE_CACHE.equals(property)) {
			this.onPropertyChangeCache(value);
			this.onPropertyChangeValue(this.getComboField().getValue());
		} else if (UReferenceDataField.PROPERTY_CHANGE_VALUE.equals(property)) {
			this.onPropertyChangeValue(value);
		}

	}

	private void onPropertyChangeBundle(ResourceBundle resources) {
		this.getTable().setResourceBundle(resources);
	}

	private void onPropertyChangeCache(Object value) {
		this.getTable().setValue(value);
	}

	protected void onPropertyChangeValue(Object value) {
		if (value == null) {
			this.getTable().setSelectedRow(-1);
		} else {
			Object oValue = this.getComboField().getValue();
			if (oValue instanceof SearchValue) {
				List<Object> values = (List<Object>) ((SearchValue) oValue).getValue();
				List<Integer> rowsToSelect = new ArrayList<>();
				for (Object o : values) {
					Hashtable hKeysValues = new Hashtable();
					hKeysValues.put(this.getComboField().getCode(), o);
					int row = this.getTable().getRowForKeys(hKeysValues);
					if (row >= 0) {
						rowsToSelect.add(row);
					}
				}
				int[] toSelect = new int[rowsToSelect.size()];
				for (int i = 0; i < rowsToSelect.size(); i++) {
					toSelect[i] = rowsToSelect.get(i);
				}
				this.getTable().setSelectedRows(toSelect);

			} else if (oValue != null) {
				Hashtable hKeysValues = new Hashtable();
				hKeysValues.put(this.getComboField().getCode(), oValue);
				int row = this.getTable().getRowForKeys(hKeysValues);
				if (row >= 0) {
					this.getTable().setSelectedRow(row);
				}
			}
		}
	}

	@Override
	public void onRefreshRequested() throws Exception {
		this.getComboField().refreshCache();
	}

}
