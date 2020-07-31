package com.opentach.client.company.listeners;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.listeners.USaveListener;

public class SaveCompanyListener extends USaveListener {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(SaveCompanyListener.class);

	public SaveCompanyListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}


	@Override
	protected EntityResult insertToServerInternal(Entity entity, Map<?, ?> attributesValuesToInsert) throws Exception {
		boolean contratoCoopChangedToYes = this.checkContratToYes(attributesValuesToInsert);

		EntityResult insertToServerInternal = super.insertToServerInternal(entity, attributesValuesToInsert);

		if (contratoCoopChangedToYes) {
			this.checkToSetActiveContract((String) attributesValuesToInsert.get("CIF"));
		}

		return insertToServerInternal;
	}



	@Override
	protected EntityResult updateToServerInternal(Entity entity, Hashtable<?, ?> keysValues, Map<?, ?> toUpdate) throws Exception {
		boolean contratoCoopChangedToYes = this.checkContratToYes(toUpdate);

		EntityResult updateToServerInternal = super.updateToServerInternal(entity, keysValues, toUpdate);

		if (contratoCoopChangedToYes) {
			this.checkToSetActiveContract((String) keysValues.get("CIF"));
		}

		return updateToServerInternal;
	}

	private boolean checkContratToYes(Map<?, ?> toUpdate) {
		if (!toUpdate.containsKey("CONTRATO_COOP")) {
			return false;
		}
		Object updatedContractCoop = toUpdate.get("CONTRATO_COOP");
		return ParseUtilsExtended.getBoolean(updatedContractCoop, false);
	}

	private void checkToSetActiveContract(String cif) throws Exception {
		EntityResult res = this.getEntity("EEmpreReq").query(EntityResultTools.keysvalues("CIF", cif), EntityResultTools.attributes("NUMREQ"), this.getSessionId());
		if ((res != null) && (res.calculateRecordNumber() > 0)) {
			this.setActiveContract(cif, (String) res.getRecordValues(0).get("NUMREQ"));
		}

	}

	public void setActiveContract(String cif, String cgcontrato) {
		try {
			IUserData user = ((UserInfoProvider) this.getReferenceLocator()).getUserData();
			user.addActiveContract(cif, cgcontrato);
		} catch (Exception e) {
			SaveCompanyListener.logger.trace(null, e);
		}
	}
}
