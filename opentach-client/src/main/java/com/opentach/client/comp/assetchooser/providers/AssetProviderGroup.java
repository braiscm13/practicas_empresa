package com.opentach.client.comp.assetchooser.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CustomComboBoxModel;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.client.comp.assetchooser.AssetComboDataField;
import com.opentach.common.companies.IAssetGroupService;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.exception.OpentachRuntimeException;
import com.utilmize.client.gui.field.table.UTable;

public class AssetProviderGroup extends AbstractAssetProvider {

	private static final Logger	logger	= LoggerFactory.getLogger(AssetProviderGroup.class);

	private static final String	ID		= "optionGroupMode";
	private UTable				table;

	public AssetProviderGroup(AssetComboDataField assetComboDataField) {
		super(assetComboDataField);
	}

	@Override
	public String getId() {
		return AssetProviderGroup.ID;
	}

	@Override
	public String getRadioButtonText() {
		return "assetgroup.optionGroupMode";
	}

	@Override
	public void onProviderSelected() {
		this.establishTable(this.getTable(), this.getData(), this.getTableWindow());
	}

	private EntityResult getData() {
		try {
			Map<String, Object> filter = new HashMap<>();
			Hashtable<String, Object> parentKeyValues = this.getComboField().getParentKeyValues();
			filter.put(CompanyNaming.COM_ID, parentKeyValues.get(CompanyNaming.CIF));
			return BeansFactory.getBean(IAssetGroupService.class).assetGroupQuery(filter, Arrays.asList(CompanyNaming.CAG_NAME, CompanyNaming.CAG_ID));
		} catch (Exception err) {
			throw new OpentachRuntimeException(err);
		}
	}

	@Override
	protected Table getTable() {
		if (this.table == null) {
			Hashtable<String, Object> parameters = new Hashtable<>();
			parameters.put("cols", StringTools.concatWithSeparator(";", CompanyNaming.CAG_NAME, CompanyNaming.CAG_ID));
			parameters.put("visiblecols", StringTools.concatWithSeparator(";", CompanyNaming.CAG_NAME));

			parameters.put("parentkeys", "CIF:COM_ID");
			parameters.put("entity", "ojee.AssetGroupService.assetGroup");
			this.table = this.buildTable(parameters);
		}
		return this.table;
	}

	@Override
	public void onTableSelection() {
		try {
			int[] indices = this.getTable().getJTable().getSelectedRows();
			List<Object> vGroupIds = new ArrayList<>();
			for (int idx : indices) {
				Object oValue = this.getTable().getRowData(idx).get(CompanyNaming.CAG_ID);
				if (oValue.equals(CustomComboBoxModel.NULL_SELECTION)) {
					continue;
				}
				vGroupIds.add(oValue);
			}

			if (!vGroupIds.isEmpty()) {
				List<Object> resourceList = this.queryResourceList(vGroupIds);

				if (resourceList.size() == 1) {
					this.getComboField().setValue(resourceList.get(0), ValueEvent.USER_CHANGE);
				} else {
					this.getComboField().setQueryValues(resourceList);
				}
			}
			this.getComboField().hideTableWindow();
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, AssetProviderGroup.logger);
		}
	}

	private List<Object> queryResourceList(List<Object> vGroupIds) throws OpentachException {
		Map<String, Object> filter = new HashMap<>();
		Hashtable<String, Object> parentKeyValues = this.getComboField().getParentKeyValues();
		filter.put(CompanyNaming.COM_ID, parentKeyValues.get(CompanyNaming.CIF));
		filter.put(CompanyNaming.CAG_ID, new SearchValue(SearchValue.IN, new Vector<>(vGroupIds)));

		List<Object> resourceList = null;
		switch (this.getComboField().getMode()) {
			case DRIVER:
				EntityResult driverGroupQuery = BeansFactory.getBean(IAssetGroupService.class).driverGroupQuery(filter, Arrays.asList(CompanyNaming.DRV_ID));
				resourceList = (List<Object>) driverGroupQuery.get(CompanyNaming.DRV_ID);
				break;
			case VEHICLE:
				EntityResult vehicleGroupQuery = BeansFactory.getBean(IAssetGroupService.class).vehicleGroupQuery(filter, Arrays.asList(CompanyNaming.VEH_ID));
				resourceList = (List<Object>) vehicleGroupQuery.get(CompanyNaming.VEH_ID);
				break;
		}
		return resourceList == null ? Collections.emptyList() : resourceList;
	}

	@Override
	public void onRefreshRequested() {
		this.getTable().setValue(this.getData());
	}
}
