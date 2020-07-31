package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.infractionwarning.IInfractionWarningService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMCartaAdvComInfraccionesGenerateReportListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMCartaAdvComInfraccionesGenerateReportListener.class);

	public IMCartaAdvComInfraccionesGenerateReportListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (this.getForm().existEmptyRequiredDataField()) {
			this.getForm().message("", Form.INFORMATION_MESSAGE);
			return;
		}
		this.generateTemplate();
	}

	private void generateTemplate() {
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				String driverId = "";
				Object driverDataValue = IMCartaAdvComInfraccionesGenerateReportListener.this.getForm().getDataFieldValue("IDCONDUCTOR");
				if (driverDataValue instanceof SearchValue) {
					driverId = (String) (((Vector) (((SearchValue) driverDataValue).getValue())).get(0));
				} else {
					driverId = (String) driverDataValue;
				}

				String cif = (String) IMCartaAdvComInfraccionesGenerateReportListener.this.getForm().getDataFieldValue("CIF");
				Date from = (Date) IMCartaAdvComInfraccionesGenerateReportListener.this.getForm().getDataFieldValue("FILTERFECINI");
				Date to = (Date) IMCartaAdvComInfraccionesGenerateReportListener.this.getForm().getDataFieldValue("FILTERFECFIN");
				String cgContrato = (String) IMCartaAdvComInfraccionesGenerateReportListener.this.getForm().getDataFieldValue("CG_CONTRATO");


				BytesBlock bb = ((OpentachClientLocator) IMCartaAdvComInfraccionesGenerateReportListener.this.getReferenceLocator()).getRemoteService(IInfractionWarningService.class)
						.generateInfractionWarningReport(driverId, cif, cgContrato, from, to, IMCartaAdvComInfraccionesGenerateReportListener.this.getSessionId());
				File file = File.createTempFile("InformeInfracWarn", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(bb.getBytes()), file);
				return file;
			}

			@Override
			protected void done() {
				try {
					File file = this.uget();
					Desktop.getDesktop().open(file);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(new Exception("M_PROCESO_INCORRECTO_GENERANDO_INFORME", error),
							IMCartaAdvComInfraccionesGenerateReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

}
