package com.opentach.downclient;

import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.Label;
import com.opentach.client.MonitorProvider;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.TGDFileInfo.TGDFileType;
import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.DefaultStateFactory;
import com.opentach.client.util.devicecontrol.IDownCenterMonitorProvider;
import com.opentach.client.util.devicecontrol.StateFactory;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.devicecontrol.states.GeneratingReportState;
import com.opentach.client.util.devicecontrol.states.IState;
import com.opentach.client.util.devicecontrol.states.PrintState;
import com.opentach.client.util.printer.PrintEvent;
import com.opentach.client.util.printer.PrinterListener;
import com.opentach.client.util.printer.TicketPrinter;
import com.opentach.client.util.screenreport.ReportEvent;
import com.opentach.client.util.screenreport.ReportListener;
import com.opentach.client.util.usbkey.USBEvent;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.IDownCenterReportService;
import com.opentach.common.downcenterreport.IDownCenterReportService.DownCenterReportType;
import com.opentach.common.downcenterreport.TcReportDto;
import com.opentach.common.downcenterreport.VuReportDto;
import com.opentach.common.user.ICDOUserData;
import com.opentach.downclient.component.UProgressBar;

public class AnonymousDeviceController extends AbstractDeviceController implements PrinterListener, ReportListener {

	private static final Logger	logger					= LoggerFactory.getLogger(AnonymousDeviceController.class);
	private final IMMainWizard	im;

	// reporting variables
	/** Determine if the action of generating report is done in SYNCHRONOUS or ASYNCHRONOS mode. */
	private final boolean		REPORT_ASYNCHRONOUS		= false;
	private boolean				reportGenerated			= false;
	private boolean				reportNotified			= false;
	private final Object		reportGenerateSemaphore	= new Object();
	private boolean				canNotifyReport			= false;
	private AbstractReportDto	reportInfo				= null;
	protected CancelableThread	reportThread;

	public AnonymousDeviceController(IMMainWizard im, IDownCenterMonitorProvider uinfo, String uriSonido) {
		super(uinfo, uriSonido, true);
		this.im = im;
	}

	public IDownCenterMonitorProvider getDownCenterMonitorProvider() {
		return (IDownCenterMonitorProvider) this.monitorProvider;
	}

	@Override
	public void init() {
		UProgressBar progressBar = (UProgressBar) this.im.managedForm.getElementReference("DownloadingProgressBar");
		Label progressLabel = (Label) this.im.managedForm.getElementReference("DownloadingProgressText");
		progressBar.setMaximum(1);
		progressBar.setValue(0);
		progressLabel.setText(ApplicationManager.getTranslation("Extracting", this.im.managedForm.getResourceBundle(), new Object[] { "-" }));
		AnonymousDeviceController.logger.info("init");
		this.im.lFileInfo = null;
		this.im.setCurrentStep(IMMainWizard.SCREEN_HOME);
	}

	@Override
	protected StateFactory createStateFactory() {
		StateFactory sf = new DefaultStateFactory(this);
		sf.registerState(StateFactoryType.PRINT, new PrintState(this));
		sf.registerState(StateFactoryType.REPORTGENERATING, new GeneratingReportState(this));
		return sf;
	}

	@Override
	public void startCardDownload() {
		AnonymousDeviceController.logger.info("startCardDownload");
		this.im.setCurrentStep(IMMainWizard.SCREEN_DOWNLOADING_CARD);
	}

	@Override
	public void cardError(String msg) {
		AnonymousDeviceController.logger.info("msg");
		this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_INVALID_CARD);
	}

	@Override
	public void endCardDownload() {
		AnonymousDeviceController.logger.info("endCardDownload");
	}

	@Override
	public void showErrorMessage(String message) {
		AnonymousDeviceController.logger.info("showErrorMessage");
	}

	@Override
	public void uploadError(boolean isDown, List<TGDFileInfo> lFileInfo) {
		AnonymousDeviceController.logger.info("uploadError");
		this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_SOURCE_UNKNOW);
	}

	@Override
	public void uploadFinished(List<TGDFileInfo> lFileInfo) {
		AnonymousDeviceController.logger.info("uploadFinished");
		this.im.lFileInfo = lFileInfo;
		this.im.setCurrentStep(IMMainWizard.SCREEN_FINISH_OK);
		this.beginGeneratingReport();
	}

	@Override
	public void printAction(PrintEvent e) {
		IState s = this.state.execute(e);
		this.handle(s);
	}

	@Override
	public void reportAction(ReportEvent e) {
		IState s = this.state.execute(e);
		this.handle(s);
	}

	public void printTicket(PrintEvent pe) {
		TicketPrinter tp = this.getDownCenterMonitorProvider().getTicketPrinter();
		if (tp != null) {
			int copies = pe.getNumCopies();
			if (copies == 1) {
				tp.print(pe.getDscr(), pe.getLPrintInfo());
			} else if (copies == 2) {
				tp.print(pe.getDscr(), pe.getLPrintInfo());
				tp.print(pe.getDscr(), pe.getLPrintInfo());
			} else {
				AnonymousDeviceController.logger.info("No printing");
			}
		}
	}

	/**
	 * When REPORT_ASYNCHRONOUS activated the report begins while other tasks (print, ...) are done. Note: This method sometimes blocks the app, due to timer cancel.
	 */
	private void beginGeneratingReport() {
		AnonymousDeviceController.logger.info("beginGeneratingReport ({})", this.REPORT_ASYNCHRONOUS ? "ASYNCH" : "SYNCH");
		this.reportGenerated = false;
		this.canNotifyReport = false;
		this.reportNotified = false;
		this.reportInfo = null;
		if (this.REPORT_ASYNCHRONOUS) {
			if (this.reportThread != null) {
				this.reportThread.cancel();
			}
			this.reportThread = new CancelableThread("ReportGenerationAsynch_" + System.currentTimeMillis()) {
				@Override
				public void doTask() {
					AnonymousDeviceController.this.buildReport(false);
				}

				@Override
				public void postTask() throws Exception {
					synchronized (AnonymousDeviceController.this.reportGenerateSemaphore) {
						AnonymousDeviceController.this.reportGenerated = true;
						if (AnonymousDeviceController.this.canNotifyReport) {
							AnonymousDeviceController.this.notifyReportGenerated();
						}
					}
				}
			};
			this.reportThread.start();
		}
	}

	@Override
	public void generateReport(boolean ignoreInfractions) {
		AnonymousDeviceController.logger.info("generateReport ({})", this.REPORT_ASYNCHRONOUS ? "ASYNCH" : "SYNCH");
		this.im.setCurrentStep(IMMainWizard.SCREEN_GENERATING_REPORT);
		if (this.REPORT_ASYNCHRONOUS) {
			synchronized (this.reportGenerateSemaphore) {
				AnonymousDeviceController.logger.info("   *reportGenerated={}", this.reportGenerated);
				if (this.reportGenerated) {
					this.notifyReportGenerated();
				} else {
					this.canNotifyReport = true;
				}
			}
		} else {
			// Another thread, becuase when callen form Swing thread, is not shown the "Generating report" screen
			if (this.reportThread != null) {
				this.reportThread.cancel();
			}
			this.reportThread = new CancelableThread("ReportGenerationSynch_" + System.currentTimeMillis()) {
				@Override
				public void doTask() {
					AnonymousDeviceController.this.buildReport(ignoreInfractions);
				}

				@Override
				public void postTask() throws Exception {
					AnonymousDeviceController.this.notifyReportGenerated();
				}
			};
			this.reportThread.start();
		}
	}

	/**
	 * Implementation to analize file and compose report.
	 */
	private void buildReport(boolean limitedInfo) {
		List<TGDFileInfo> lFileInfo = AnonymousDeviceController.this.im.lFileInfo;
		try {
			AnonymousDeviceController.this.reportInfo = AnonymousDeviceController.this.getReportInfo(lFileInfo, limitedInfo);
		} catch (Exception ex) {
			AnonymousDeviceController.logger.error(null, ex);
		}
	}

	private void notifyReportGenerated() {
		if (this.reportNotified) {
			AnonymousDeviceController.logger.info("notifyReportGenerated aborted, already notified!!!!!");
			return;
		}
		AnonymousDeviceController.logger.info("notifyReportGenerated");
		final ReportEvent re = new ReportEvent(this, this.reportInfo);
		this.reportAction(re);
		this.reportNotified = true;
	}

	@Override
	public void print() {
		AnonymousDeviceController.logger.info("print");
		String printConfig;
		try {
			printConfig = ((ICDOUserData) this.getDownCenterMonitorProvider().getUserData()).getPrintConfig();
		} catch (Exception error) {
			throw new RuntimeException(error);
		}
		AnonymousDeviceController.logger.info("  *printconfig \"{}\"", printConfig);
		if ("0".equals(printConfig)) {
			this.setTimerToValue(0);
			this.im.setCurrentStep(IMMainWizard.SCREEN_QUESTION_COPIES);
		} else if ("1".equals(printConfig)) {
			final PrintEvent pe = new PrintEvent(this, 1, this.getDownCenterMonitorProvider().getUserDescription(), this.im.getFiles());
			this.printAction(pe);
		} else if ("2".equals(printConfig)) {
			final PrintEvent pe = new PrintEvent(this, 2, this.getDownCenterMonitorProvider().getUserDescription(), this.im.getFiles());
			this.printAction(pe);
		} else {
			// no imprimir
			final PrintEvent pe = new PrintEvent(this, 0, this.getDownCenterMonitorProvider().getUserDescription(), this.im.getFiles());
			this.printAction(pe);
		}
	}

	@Override
	public void showReport(Object reportInfo, boolean limitedInfo) {
		AnonymousDeviceController.logger.info("showReport");
		if (reportInfo instanceof VuReportDto) {
			this.im.fillReport((VuReportDto) reportInfo);
			this.im.setCurrentStep(IMMainWizard.SCREEN_REPORT_VU);
		} else if (reportInfo instanceof TcReportDto) {
			this.im.fillReport((TcReportDto) reportInfo, limitedInfo);
			this.im.setCurrentStep(IMMainWizard.SCREEN_REPORT_TC);
		} else {
			this.handle(this.getStateFactory().getState(StateFactoryType.THANKS));
		}
	}

	@Override
	public void thanks() {
		AnonymousDeviceController.logger.info("thanks");
		this.im.setCurrentStep(IMMainWizard.SCREEN_THANKS);
	}

	@Override
	public void processUSBError(String msg) {
		AnonymousDeviceController.logger.info("processUsbError");
		if (USBEvent.M_ERROR_COPYING_DATA.equals(msg)) {
			this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_USB_GENERAL);
		} else if (USBEvent.M_ERROR_INTERNAL.equals(msg)) {
			this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_USB_GENERAL);
		} else if (USBEvent.M_KEY_EMPTY.equals(msg)) {
			this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_USB_EMPTY);
		} else {
			this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_USB_GENERAL);
		}
	}

	@Override
	public void endUSBDownload() {
		AnonymousDeviceController.logger.info("endUSBDownload");
	}

	@Override
	public void downloadProgress(final String message, final Object[] msgParameters, final int currentPart, final int totalParts) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UProgressBar progressBar = (UProgressBar) AnonymousDeviceController.this.im.managedForm.getElementReference("DownloadingProgressBar");
				Label progressLabel = (Label) AnonymousDeviceController.this.im.managedForm.getElementReference("DownloadingProgressText");
				progressBar.setMaximum(totalParts + 1);
				progressBar.setValue(currentPart);
				progressLabel.setText(ApplicationManager.getTranslation("Extracting", AnonymousDeviceController.this.im.managedForm.getResourceBundle(),
						new Object[] { currentPart + "/" + (totalParts + 1) }));
			}
		});
	}

	@Override
	public void onUsbNotFound() {
		this.setTimerToValue(3000);
		this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_NO_USB_DETECTED);
	}

	@Override
	public void startUSBDownload() {
		AnonymousDeviceController.logger.info("startUSBDownload");
		this.im.setCurrentStep(IMMainWizard.SCREEN_DOWNLOADING_USB);
	}

	@Override
	public void unavailableService() {
		AnonymousDeviceController.logger.info("unavailableService");
		this.im.setCurrentStep(IMMainWizard.SCREEN_ERROR_GENERAL);
	}

	public AbstractReportDto getReportInfo(List<TGDFileInfo> lFileInfo, boolean limitedInfo) {
		if ((lFileInfo == null) || lFileInfo.isEmpty()) {
			return null;
		}
		Collections.sort(lFileInfo, new TGDFileInfo.TGDFileInfoDateComparator());
		TGDFileInfo tfi = lFileInfo.get(lFileInfo.size() - 1);
		return this.getReportInfo(tfi, limitedInfo);
	}

	public AbstractReportDto getReportInfo(TGDFileInfo fileInfo, boolean limitedInfo) {
		try {
			DownCenterReportType type = TGDFileType.TC.equals(fileInfo.getFileType()) ? DownCenterReportType.TCType : DownCenterReportType.VUType;
			AnonymousDeviceController.logger.info("getTcReportInfo  Type={} Source={}", type, fileInfo.getOwnerId());
			int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
			return this.getReporter().queryReportData(type, fileInfo.getOwnerId(), limitedInfo, sessionId);
		} catch (Exception error) {
			AnonymousDeviceController.logger.error(null, error);
			return null;
		}
	}


	private IDownCenterReportService getReporter() throws Exception {
		MonitorProvider locator = (MonitorProvider) ApplicationManager.getApplication().getReferenceLocator();
		return locator.getRemoteService(IDownCenterReportService.class);
	}

	protected abstract class CancelableThread extends Thread {
		protected boolean canceled;

		public CancelableThread(String name) {
			super(name);
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return this.canceled;
		}

		@Override
		public void run() {
			AnonymousDeviceController.logger.trace("CancellableThread {} just starts...", this.getName());
			try {
				this.doTask();
				if (!this.isCanceled()) {
					AnonymousDeviceController.logger.warn("CancellableThread {} task done !!!!", this.getName());
					this.postTask();
				} else {
					AnonymousDeviceController.logger.warn("CancellableThread {} task done but cancelled -> Ignored!!!", this.getName());
				}
			} catch (Exception ex) {
				AnonymousDeviceController.logger.warn("CancellableThread " + this.getName() + " BREAKS !!!", ex);
			} finally {
				AnonymousDeviceController.logger.warn("CancellableThread {} just finish now !!!!", this.getName());
			}
		}

		public abstract void doTask() throws Exception;

		public abstract void postTask() throws Exception;
	}
}