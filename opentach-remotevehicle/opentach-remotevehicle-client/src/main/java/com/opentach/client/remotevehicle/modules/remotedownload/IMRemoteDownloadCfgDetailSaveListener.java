package com.opentach.client.remotevehicle.modules.remotedownload;

import java.util.Hashtable;
import java.util.Map;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService.RemoteDownloadPeriod;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.listeners.USaveListener;

public class IMRemoteDownloadCfgDetailSaveListener extends USaveListener {
	public IMRemoteDownloadCfgDetailSaveListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@FormComponent(attr = "SRCTYPE")
	private DataField	srcTypeField;
	@FormComponent(attr = "CIF")
	private DataField	cifField;
	@FormComponent(attr = "RDP_ID")
	private DataField	rdpField;
	@FormComponent(attr = "RDW_ID")
	private DataField	idField;
	@FormComponent(attr = "SRC_ID")
	private DataField	srcIdField;
	@FormComponent(attr = "RDW_ACTIVE")
	private DataField	activeField;
	@FormComponent(attr = "RDW_PERIOD")
	private DataField	periodField;
	@FormComponent(attr = "RDW_HOUR")
	private DataField	hourField;
	@FormComponent(attr = "RDV_UNIT_SN")
	private DataField	rdvUnitField;

	@Override
	protected EntityResult updateToServerInternal(Entity entity, Hashtable<?, ?> keysValues, Map<?, ?> toUpdate) throws Exception {
		try {
			this.notifyPreUpdate(keysValues, toUpdate);
			boolean active = "S".equals(this.activeField.getValue());
			Object providerId = this.rdpField.getValue();
			RemoteDownloadPeriod period = RemoteDownloadPeriod.valueOf((String) this.periodField.getValue());
			Long hour = this.hourField.getValue() == null ? null : ((Number) this.hourField.getValue()).longValue();
			EntityResult res = null;
			if ("V".equals(this.srcTypeField.getValue())) {
				res = BeansFactory.getBean(IRemoteVehicleService.class).configureVehicle(active, providerId, period, hour, (String) this.cifField.getValue(),
						(String) this.srcIdField.getValue(), (String) this.rdvUnitField.getValue());
			} else {
				res = BeansFactory.getBean(IRemoteVehicleService.class).configureDriver(active, providerId, period, hour, (String) this.cifField.getValue(),
						(String) this.srcIdField.getValue());
			}

			this.notifyPostUpdate(keysValues, toUpdate, res);
			if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
				MessageManager.getMessageManager().showMessage(this.getForm(), res.getMessage(), MessageType.WARNING, new Object[] {});
			}
			return res;
		} catch (Exception ex) {
			this.notifyIncorrectUpdate(keysValues, toUpdate, ex);
			throw ex;
		}
	}

}
