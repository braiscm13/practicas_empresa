package com.opentach.client.labor.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
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

public class IMInformeDriverJournalReportActionListener extends AbstractActionListenerButton {

	private static final Logger		logger				= LoggerFactory.getLogger(IMInformeDriverJournalReportActionListener.class);

	protected static final String	REPORT_DAILY_RECORD	= "daily";

	protected static final String	REPORT_LABOR		= "labor";

	private String					reportName;
	@FormComponent(attr = "NO_COMPUTE_EXTRA_TIME")
	private CheckDataField			noComputeExtraTime;

	public IMInformeDriverJournalReportActionListener() throws Exception {
		super();
	}

	public IMInformeDriverJournalReportActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMInformeDriverJournalReportActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMInformeDriverJournalReportActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.reportName = ParseUtilsExtended.getString((String) params.get("report"), null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getInteractionManager(IMRoot.class).checkRequiredVisibleDataFields(true)) {
			return;
		}

		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				final AbstractOpentachClientLocator ocl = (AbstractOpentachClientLocator) IMInformeDriverJournalReportActionListener.this.getReferenceLocator();
				Pair<Date, Date> computeFilterDates = IMInformeDriverJournalReportActionListener.this.getInteractionManager(IMInformeDriverJournal.class).computeFilterDates();
				String companyCif = (String) IMInformeDriverJournalReportActionListener.this.getForm().getDataFieldValue(OpentachFieldNames.CIF_FIELD);
				Object driverId = IMInformeDriverJournalReportActionListener.this.getForm().getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
				String reportType = (String) IMInformeDriverJournalReportActionListener.this.getForm().getDataFieldValue("REPORT_TYPE");
				Boolean avoidExtraTime = IMInformeDriverJournalReportActionListener.this.noComputeExtraTime.isSelected();
				if (driverId instanceof SearchValue) {
					driverId = ((SearchValue) driverId).getValue();
				} else {
					driverId = Arrays.asList(driverId);
				}
				BytesBlock pdfBytes = null;
				pdfBytes = ocl.getRemoteService(ILaborService.class).createDriverJournalReport((List<Object>) driverId, companyCif, computeFilterDates.getFirst(),
						computeFilterDates.getSecond(), reportType, avoidExtraTime, ocl.getSessionId());

				File tmpFile = File.createTempFile("driverreportrecord", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(pdfBytes.getBytes()), tmpFile);
				return tmpFile;
			}

			@Override
			protected void done() {
				try {
					File res = this.uget();
					Desktop.getDesktop().open(res);
				} catch (Throwable ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeDriverJournalReportActionListener.logger);
				}
			}

		}.executeOperation(this.getForm());

	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		this.getButton().setEnabled(true);
		this.noComputeExtraTime.setEnabled(true);
	}
}
