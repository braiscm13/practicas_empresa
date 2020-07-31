package com.opentach.client.remotevehicle.modules.remotevehicle;

import java.util.Hashtable;
import java.util.Map;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.listeners.USaveListener;

public class IMRemoteVehicleCompanySetupSaveActionListener extends USaveListener {

	public IMRemoteVehicleCompanySetupSaveActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected EntityResult insertToServerInternal(Entity entity, Map<?, ?> attributesValuesToInsert) throws Exception {

		try {
			this.notifyPreInsert(attributesValuesToInsert);
			String companyId = (String) ((Map<String, Object>) attributesValuesToInsert).get("COM_ID");
			IRemoteVehicleService vehicleService = BeansFactory.getBean(IRemoteVehicleService.class);
			EntityResult resInsert = vehicleService.createCompany((Map<String, Object>) attributesValuesToInsert, companyId);
			CheckingTools.checkValidEntityResult(resInsert);
			this.notifyPostInsert(attributesValuesToInsert, resInsert);
			return resInsert;
		} catch (Exception ex) {
			this.notifyIncorrectInsert(attributesValuesToInsert, ex);
			throw ex;
		}
	}

	@Override
	protected EntityResult updateToServerInternal(Entity entity, Hashtable<?, ?> keysValues, Map<?, ?> toUpdate) throws Exception {
		try {
			this.notifyPreUpdate(keysValues, toUpdate);
			IRemoteVehicleService vehicleService = BeansFactory.getBean(IRemoteVehicleService.class);
			Object ccfId = ((Map<String, Object>) keysValues).get("CCF_ID");
			EntityResult resUpdate = vehicleService.updateCompany((Map<String, Object>) toUpdate, ccfId);

			CheckingTools.checkValidEntityResult(resUpdate);
			this.notifyPostUpdate(keysValues, toUpdate, resUpdate);
			return resUpdate;
		} catch (Exception ex) {
			this.notifyIncorrectUpdate(keysValues, toUpdate, ex);
			throw ex;
		}
	}

}
