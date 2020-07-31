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
import com.opentach.common.company.exception.CompanyException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.service.ICompanyDelegationService;
import com.opentach.common.exception.OpentachRuntimeException;
import com.utilmize.client.gui.field.table.UTable;

public class AssetProviderDelegation extends AbstractAssetProvider {

	private static final Logger	logger	= LoggerFactory.getLogger(AssetProviderDelegation.class);

	private static final String	ID		= "optionDelegationMode";
	private UTable				table;

	public AssetProviderDelegation(AssetComboDataField assetComboDataField) {
		super(assetComboDataField);
	}

	@Override
	public String getId() {
		return AssetProviderDelegation.ID;
	}

	@Override
	public String getRadioButtonText() {
		return "assetgroup.optionDelegationMode";
	}

	@Override
	public void onProviderSelected() {
		this.establishTable(this.getTable(), this.getData(), this.getTableWindow());
	}

	private EntityResult getData() {
		try {
			Hashtable<String, Object> filter = new Hashtable<>();
			Hashtable<String, Object> parentKeyValues = this.getComboField().getParentKeyValues();
			filter.put(CompanyNaming.CIF, parentKeyValues.get(CompanyNaming.CIF));
			return BeansFactory.getBean(ICompanyDelegationService.class).companyDelegationQuery(filter,
					new Vector<>(Arrays.asList(CompanyNaming.NOMBRE_DEL, CompanyNaming.IDDELEGACION)));
		} catch (Exception err) {
			throw new OpentachRuntimeException(err);
		}
	}

	@Override
	protected Table getTable() {
		if (this.table == null) {
			Hashtable<String, Object> parameters = new Hashtable<>();
			parameters.put("cols", StringTools.concatWithSeparator(";", CompanyNaming.NOMBRE_DEL, CompanyNaming.IDDELEGACION));
			parameters.put("visiblecols", StringTools.concatWithSeparator(";", CompanyNaming.NOMBRE_DEL));

			parameters.put("parentkeys", CompanyNaming.CIF);
			parameters.put("entity", "ojee.CompanyDelegationService.companyDelegation");
			this.table = this.buildTable(parameters);
		}
		return this.table;
	}

	@Override
	public void onTableSelection() {
		try {
			int[] indices = this.getTable().getJTable().getSelectedRows();
			List<Object> vDelegationIds = new ArrayList<>();
			for (int idx : indices) {
				Object oValue = this.getTable().getRowData(idx).get(CompanyNaming.IDDELEGACION);
				if (oValue.equals(CustomComboBoxModel.NULL_SELECTION)) {
					continue;
				}
				vDelegationIds.add(oValue);
			}
			if (!vDelegationIds.isEmpty()) {
				List<Object> resourceList = this.queryResourceList(vDelegationIds);
				if (resourceList.size() == 1) {
					this.getComboField().setValue(resourceList.get(0), ValueEvent.USER_CHANGE);
				} else {
					this.getComboField().setQueryValues(resourceList);
				}
			}
			this.getComboField().hideTableWindow();
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, AssetProviderDelegation.logger);
		}
	}

	private List<Object> queryResourceList(List<Object> vDelegationIds) throws CompanyException {
		Map<String, Object> filter = new HashMap<>();
		Hashtable<String, Object> parentKeyValues = this.getComboField().getParentKeyValues();
		filter.put(CompanyNaming.CIF, parentKeyValues.get(CompanyNaming.CIF));
		filter.put(CompanyNaming.IDDELEGACION, new SearchValue(SearchValue.IN, new Vector<>(vDelegationIds)));
		List<Object> resourceList = null;
		switch (this.getComboField().getMode()) {
			case DRIVER:
				EntityResult driverGroupQuery = BeansFactory.getBean(ICompanyDelegationService.class).companyDelegationDriverQuery(filter,
						Arrays.asList(CompanyNaming.IDCONDUCTOR));
				resourceList = (List<Object>) driverGroupQuery.get(CompanyNaming.IDCONDUCTOR);
				break;
			case VEHICLE:
				EntityResult vehileGroupQuery = BeansFactory.getBean(ICompanyDelegationService.class).companyDelegationVehicleQuery(filter, Arrays.asList(CompanyNaming.MATRICULA));
				resourceList = (List<Object>) vehileGroupQuery.get(CompanyNaming.MATRICULA);
				break;
		}

		return resourceList == null ? Collections.emptyList() : resourceList;
	}

	@Override
	public void onRefreshRequested() {
		this.getTable().setValue(this.getData());
	}
}
