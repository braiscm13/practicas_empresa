package com.opentach.client.labor.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.labor.laboral.ILaborService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMInformeDriverStatusTimeReportActionListener extends AbstractActionListenerButton {

	private static final Logger		logger				= LoggerFactory.getLogger(IMInformeDriverStatusTimeReportActionListener.class);


	public IMInformeDriverStatusTimeReportActionListener() throws Exception {
		super();
	}

	public IMInformeDriverStatusTimeReportActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMInformeDriverStatusTimeReportActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMInformeDriverStatusTimeReportActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getInteractionManager(IMRoot.class).checkRequiredVisibleDataFields(true)) {
			return;
		}

		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				final AbstractOpentachClientLocator ocl = (AbstractOpentachClientLocator) IMInformeDriverStatusTimeReportActionListener.this.getReferenceLocator();
				Pair<Date, Date> computeFilterDates = IMInformeDriverStatusTimeReportActionListener.this.getInteractionManager(IMInformeDriverStatusTime.class)
						.computeFilterDates();
				String companyCif = (String) IMInformeDriverStatusTimeReportActionListener.this.getForm().getDataFieldValue(OpentachFieldNames.CIF_FIELD);
				Object driverId = IMInformeDriverStatusTimeReportActionListener.this.getForm().getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
				String reportType = (String) IMInformeDriverStatusTimeReportActionListener.this.getForm().getDataFieldValue("REPORT_TYPE");
				List<String> activityTypes = this.composeActivityTypes();
				if (driverId instanceof SearchValue) {
					driverId = ((SearchValue) driverId).getValue();
				} else {
					driverId = Arrays.asList(driverId);
				}
				BytesBlock pdfBytes = null;
				pdfBytes = ocl.getRemoteService(ILaborService.class).createDriverStatusTimeReport((List<Object>) driverId, companyCif, computeFilterDates.getFirst(),
						computeFilterDates.getSecond(), reportType, activityTypes, ocl.getSessionId());

				File tmpFile = File.createTempFile("driverstatustime", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(pdfBytes.getBytes()), tmpFile);
				return tmpFile;
			}

			private List<String> composeActivityTypes() {
				String[] attrs = new String[] { "DRIVING", "REST", "WORK", "AVAILABLE", "INDETERMINATE" };
				List<String> res = new ArrayList<String>();
				for (String attr : attrs) {
					if (((CheckDataField) IMInformeDriverStatusTimeReportActionListener.this.getForm().getElementReference(attr)).isSelected()) {
						res.add(attr);
					}
				}
				return res;
			}

			@Override
			protected void done() {
				try {
					File res = this.uget();
					Desktop.getDesktop().open(res);
				} catch (Throwable ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeDriverStatusTimeReportActionListener.logger);
				}
			}

		}.executeOperation(this.getForm());

	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		this.getButton().setEnabled(true);
	}
}
