package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.company.naming.CompanyNaming;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMAssetGroupAddAssetActionListener extends AbstractActionListenerButton {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IMAssetGroupAddAssetActionListener.class);
	private boolean				isDriver;

	public IMAssetGroupAddAssetActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMAssetGroupAddAssetActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.isDriver = "driver".equals(params.get("assetType"));
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String detailFormName = "formAssetGroupAddAsset.form";
		String detailTableId = this.isDriver ? "ojee.AssetGroupService.availableDriverGroup" : "ojee.AssetGroupService.availableVehicleGroup";
		String detailTableHideId = !this.isDriver ? "ojee.AssetGroupService.availableDriverGroup" : "ojee.AssetGroupService.availableVehicleGroup";
		Table table = (Table) this.getForm().getElementReference(this.isDriver ? "ojee.AssetGroupService.driverGroup" : "ojee.AssetGroupService.vehicleGroup");
		new USwingWorker<Form, Void>() {

			@Override
			protected Form doInBackground() throws Exception {
				Form formReference = IMAssetGroupAddAssetActionListener.this.getFormManager().getFormCopy(detailFormName);
				Hashtable<String, Object> values = new Hashtable<>();
				values.put(CompanyNaming.CAG_ID, IMAssetGroupAddAssetActionListener.this.getForm().getDataFieldValue(CompanyNaming.CAG_ID));
				values.put(CompanyNaming.COM_ID, IMAssetGroupAddAssetActionListener.this.getForm().getDataFieldValue(CompanyNaming.COM_ID));
				formReference.setDataFieldValues(values);
				formReference.hideElement(detailTableHideId);
				UTable table = (UTable) formReference.getElementReference(detailTableId);
				table.setEnabled(true);
				table.refreshInThread(0);
				return formReference;
			}

			@Override
			protected void done() {
				try {
					Form detailForm = this.uget();
					JDialog dialog = detailForm.putInModalDialog(
							ApplicationManager.getTranslation(CompanyNaming.ASSET_GROUP_PREFIX + detailFormName,
									ApplicationManager.getApplicationBundle()),
							IMAssetGroupAddAssetActionListener.this.getForm());
					dialog.setVisible(true);
					dialog.dispose();
					SwingUtilities.invokeLater(() -> detailForm.free());
					table.refreshInThread(0);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMAssetGroupAddAssetActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	@Override
	protected void considerToEnableButton() {
		super.considerToEnableButton();
		this.getButton().setEnabled(this.getForm().getInteractionManager().getCurrentMode() == InteractionManager.UPDATE);
	}

}
