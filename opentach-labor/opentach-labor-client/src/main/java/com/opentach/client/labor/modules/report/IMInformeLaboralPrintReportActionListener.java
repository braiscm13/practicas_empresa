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

import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.SearchValue;
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

public class IMInformeLaboralPrintReportActionListener extends AbstractActionListenerButton {

	private static final Logger		logger				= LoggerFactory.getLogger(IMInformeLaboralPrintReportActionListener.class);

	protected static final String	REPORT_DAILY_RECORD	= "daily";

	protected static final String	REPORT_LABOR		= "labor";

	private String					reportName;

	public IMInformeLaboralPrintReportActionListener() throws Exception {
		super();
	}

	public IMInformeLaboralPrintReportActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMInformeLaboralPrintReportActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMInformeLaboralPrintReportActionListener(UButton button, Hashtable params) throws Exception {
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
				final AbstractOpentachClientLocator ocl = (AbstractOpentachClientLocator) IMInformeLaboralPrintReportActionListener.this.getReferenceLocator();
				Pair<Date, Date> computeFilterDates = IMInformeLaboralPrintReportActionListener.this.getInteractionManager(IMInformeLaboral.class).computeFilterDates();
				String companyCif = (String) IMInformeLaboralPrintReportActionListener.this.getForm().getDataFieldValue(OpentachFieldNames.CIF_FIELD);
				Object driverId = IMInformeLaboralPrintReportActionListener.this.getForm().getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
				if (driverId instanceof SearchValue) {
					driverId = ((SearchValue) driverId).getValue();
				} else {
					driverId = Arrays.asList(driverId);
				}
				BytesBlock pdfBytes = null;
				if (IMInformeLaboralPrintReportActionListener.REPORT_DAILY_RECORD.equals(IMInformeLaboralPrintReportActionListener.this.reportName)) {
					pdfBytes = ocl.getRemoteService(ILaborService.class).createDailyRecordReport((List<Object>) driverId, companyCif, computeFilterDates.getFirst(),
							computeFilterDates.getSecond(), ocl.getSessionId());
				} else if (IMInformeLaboralPrintReportActionListener.REPORT_LABOR.equals(IMInformeLaboralPrintReportActionListener.this.reportName)) {
					pdfBytes = ocl.getRemoteService(ILaborService.class).createLaborReport((List<Object>) driverId, companyCif, computeFilterDates.getFirst(),
							computeFilterDates.getSecond(),
							ocl.getSessionId());
				}

				File tmpFile = File.createTempFile("dailyreportrecord", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(pdfBytes.getBytes()), tmpFile);
				return tmpFile;
			}

			@Override
			protected void done() {
				try {
					File res = this.uget();
					Desktop.getDesktop().open(res);
				} catch (Throwable ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeLaboralPrintReportActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());

	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		this.getButton().setEnabled(true);
	}
}
