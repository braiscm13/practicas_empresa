package com.opentach.client.remotevehicle.modules.remotedownload;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMRemoteDownloadCfgRequestDownloadListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMRemoteDownloadCfgRequestDownloadListener.class);

	public IMRemoteDownloadCfgRequestDownloadListener() throws Exception {
		super();
	}

	public IMRemoteDownloadCfgRequestDownloadListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMRemoteDownloadCfgRequestDownloadListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMRemoteDownloadCfgRequestDownloadListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Se llama desde el boton de la tabla
		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				String companyId = (String) IMRemoteDownloadCfgRequestDownloadListener.this.getForm().getDataFieldValue("CIF");
				String srcId = (String) IMRemoteDownloadCfgRequestDownloadListener.this.getForm().getDataFieldValue("SRC_ID");
				String srcType = (String) IMRemoteDownloadCfgRequestDownloadListener.this.getForm().getDataFieldValue("SRCTYPE");
				Date from = (Date) IMRemoteDownloadCfgRequestDownloadListener.this.getForm().getDataFieldValue("FILTERFECINI");
				Date to = (Date) IMRemoteDownloadCfgRequestDownloadListener.this.getForm().getDataFieldValue("FILTERFECFIN");
				if ("V".equals(srcType)) {
					BeansFactory.getBean(IRemoteVehicleService.class).requestForceVehicleDownload(companyId, Arrays.asList(srcId), from, to);
				} else {
					BeansFactory.getBean(IRemoteVehicleService.class).requestForceDriverDownload(companyId, Arrays.asList(srcId));
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					this.uget();
					MessageManager.getMessageManager().showMessage(IMRemoteDownloadCfgRequestDownloadListener.this.getForm(), "remotevehicle.DOWNLOAD_REQUESTED",
							MessageType.INFORMATION, new Object[] {}, false);
					IMRemoteDownloadCfgRequestDownloadListener.this.getForm().setVisible(false);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMRemoteDownloadCfgRequestDownloadListener.this.getButton(),
							IMRemoteDownloadCfgRequestDownloadListener.logger);
				}
			}

		}.executeOperation(this.getForm());
	}

	@Override
	protected void considerToEnableButton() {
		this.getButton().setEnabled(true);
	}

}
