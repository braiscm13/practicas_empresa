package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.companies.IAssetGroupService;
import com.opentach.common.company.naming.CompanyNaming;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMAssetGroupAddAssetConfirmActionListener extends AbstractActionListenerButton {
	/** The Constant logger. */
	private static final Logger	logger	= LoggerFactory.getLogger(IMAssetGroupAddAssetConfirmActionListener.class);

	@FormComponent(attr = "ojee.AssetGroupService.availableVehicleGroup")
	private UTable				vehicleTable;
	@FormComponent(attr = "ojee.AssetGroupService.availableDriverGroup")
	private UTable				driverTable;

	public IMAssetGroupAddAssetConfirmActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMAssetGroupAddAssetConfirmActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		new USwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				Map<String, Object> av = new HashMap<>();
				av.put(CompanyNaming.COM_ID, IMAssetGroupAddAssetConfirmActionListener.this.getForm().getDataFieldValue(CompanyNaming.COM_ID));
				av.put(CompanyNaming.CAG_ID, IMAssetGroupAddAssetConfirmActionListener.this.getForm().getDataFieldValue(CompanyNaming.CAG_ID));

				if (IMAssetGroupAddAssetConfirmActionListener.this.vehicleTable.isVisible()) {
					List<String> selectedVehicles = IMAssetGroupAddAssetConfirmActionListener.this.getSelectedVehicles();
					if (selectedVehicles.isEmpty()) {
						return null;
					}
					av.put(CompanyNaming.VEH_ID, selectedVehicles);
					BeansFactory.getBean(IAssetGroupService.class).vehicleGroupInsert(av);
				} else {
					List<String> selectedDrivers = IMAssetGroupAddAssetConfirmActionListener.this.getSelectedDrivers();
					if (selectedDrivers.isEmpty()) {
						return null;
					}
					av.put(CompanyNaming.DRV_ID, selectedDrivers);
					BeansFactory.getBean(IAssetGroupService.class).driverGroupInsert(av);
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					this.uget();
					SwingUtilities.getWindowAncestor(IMAssetGroupAddAssetConfirmActionListener.this.getForm()).setVisible(false);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMAssetGroupAddAssetConfirmActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	protected List<String> getSelectedDrivers() {
		return (List<String>) this.driverTable.getSelectedRowData().get(CompanyNaming.DRV_ID);
	}

	private List<String> getSelectedVehicles() {
		return (List<String>) this.vehicleTable.getSelectedRowData().get(CompanyNaming.VEH_ID);
	}
}
