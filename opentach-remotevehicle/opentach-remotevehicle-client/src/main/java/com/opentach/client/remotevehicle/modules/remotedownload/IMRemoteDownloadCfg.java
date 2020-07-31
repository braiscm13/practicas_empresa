package com.opentach.client.remotevehicle.modules.remotedownload;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.ITGDFileConstants;

public class IMRemoteDownloadCfg extends IMReportRoot implements ITGDFileConstants {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteDownloadCfg.class);

	@FormComponent(attr = "ojee.RemoteVehicleManagementService.downloadConfigAll")
	private Table				table;
	@FormComponent(attr = "SRCTYPE")
	private DataField			srcTypeField;
	@FormComponent(attr = "CIF")
	private DataField			cifField;

	public IMRemoteDownloadCfg() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		// Por ahora filtramos solo vehiculos
		this.srcTypeField.setValue("V");
	}

	@Override
	public void doOnQuery(final boolean alert) {
		try {
			this.ultimosDatos();
			if (this.checkRequiredVisibleDataFields(alert)) {
				this.table.refreshInThread(0);
			}
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMRemoteDownloadCfg.logger);
		}
	}

	@Override
	protected List<DataField> getFilterFields() {
		return Arrays.asList(new DataField[] { this.cifField, this.srcTypeField });
	}

}
