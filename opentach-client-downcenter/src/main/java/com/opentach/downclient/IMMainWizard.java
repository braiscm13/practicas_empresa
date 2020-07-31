package com.opentach.downclient;

import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.ScrollPaneConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.gif.BasicInteractionManagerWizard;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.net.NetworkMonitor;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.AbstractReportDto.Infraction;
import com.opentach.common.downcenterreport.TcReportDto;
import com.opentach.common.downcenterreport.VuReportDto;
import com.opentach.common.downcenterreport.VuReportDto.Calibrado;
import com.opentach.common.downcenterreport.VuReportDto.Control;
import com.opentach.common.downcenterreport.VuReportDto.Driver;
import com.opentach.common.downcenterreport.VuReportDto.Incidence;
import com.opentach.common.user.ICDOUserData;
import com.opentach.common.user.IUserData;
import com.opentach.model.scard.SmartCardMonitor;
import com.utilmize.client.gui.field.table.UTable;

public class IMMainWizard extends BasicInteractionManagerWizard {

	private static final Logger				logger							= LoggerFactory.getLogger(IMMainWizard.class);

	public final static int					SCREEN_HOME						= 1;
	public final static int					SCREEN_DOWNLOADING_CARD			= 2;
	public final static int					SCREEN_DOWNLOADING_USB			= 3;
	public final static int					SCREEN_GENERATING_REPORT		= 4;
	public final static int					SCREEN_FINISH_OK				= 5;
	public final static int					SCREEN_QUESTION_COPIES			= 6;
	public final static int					SCREEN_REPORT_TC				= 7;
	public final static int					SCREEN_REPORT_VU				= 72;
	public final static int					SCREEN_THANKS					= 8;

	public final static int					SCREEN_ERROR_GENERAL			= 500;
	public final static int					SCREEN_ERROR_SOURCE_UNKNOW		= 501;
	public final static int					SCREEN_ERROR_INVALID_CARD		= 502;
	public final static int					SCREEN_ERROR_NO_USB_DETECTED	= 503;
	public final static int					SCREEN_ERROR_USB_EMPTY			= 504;
	public final static int					SCREEN_ERROR_USB_GENERAL		= 505;

	protected AbstractDeviceController		devc;
	protected List<TGDFileInfo>				lFileInfo;
	protected IMMainWizardNewsController	newsController;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		try {
			super.registerInteractionManager(f, gf);

			final DownCenterClientLocator locator = (DownCenterClientLocator) gf.getReferenceLocator();
			// create device controller
			this.devc = this.createDeviceController(locator);
			this.setUserPreferences(locator);
			this.newsController = new IMMainWizardNewsController(f, gf);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// Show/Hide assistance column depending locale
		boolean assistanceVisible = !"pt".equals(ApplicationManager.getApplication().getLocale().getLanguage());
		((Column) this.managedForm.getElementReference("AssistanceColumn")).setVisible(assistanceVisible);
	}

	private AbstractDeviceController createDeviceController(final DownCenterClientLocator locator) throws Exception {
		IUserData ud = locator.getUserData();

		AbstractDeviceController devc = new AnonymousDeviceController(this, locator, SoundManager.uriSonido);
		SmartCardMonitor dcm = locator.getLocalService(SmartCardMonitor.class);
		USBKeyMonitor usbkm = locator.getUSBKeyMonitor();
		UploadMonitor upm = locator.getUploadMonitor();
		NetworkMonitor nm = locator.getNetworkMonitor();
		if (dcm != null) {
			dcm.addCardListener(devc);
		}
		if (usbkm != null) {
			if (((ICDOUserData) ud).isUSBDownloadUser()) {
				usbkm.setEnabled(true);
			}
			usbkm.setAutoDownload(true);
			usbkm.addUSBListener(devc);
		}
		if (nm != null) {
			nm.addNetworkMonitorListener(devc);
		}
		upm.addUploadListener(devc);
		return devc;
	}

	@Override
	protected void updateButtonsState() {}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.newsController.start();
	}

	@Override
	public boolean setCurrentStep(final int step) {
		return super.setCurrentStep(step);
	}

	@Override
	protected void cancel() {
		this.setCurrentStep(IMMainWizard.SCREEN_HOME);
	}

	@Override
	protected void end() {
		this.setCurrentStep(IMMainWizard.SCREEN_HOME);
	}

	@Override
	protected String getAttrCardPanel() {
		return "panel";
	}

	@Override
	protected String getComponentIdToShowInStep(int i) {
		// i = IMMainWizard.SCREEN_REPORT_VU; // TODO TEMP
		this.setElementsVisible(true, "NewsColumn");
		switch (i) {
			case SCREEN_HOME:
				return "Step1";
			case SCREEN_DOWNLOADING_CARD:
				this.setElementsVisible(true, "IconCard");
				this.setElementsVisible(false, "IconUsb");
				this.setLabelText("DownloadingTypeTitle", "DownloadingCard");
				return "Step2";
			case SCREEN_DOWNLOADING_USB:
				this.setElementsVisible(false, "IconCard");
				this.setElementsVisible(true, "IconUsb");
				this.setLabelText("DownloadingTypeTitle", "DownloadingUsb");
				return "Step2";
			case SCREEN_GENERATING_REPORT:
				return "StepGeneratingReport";
			case SCREEN_FINISH_OK:
				return "StepFinished";
			case SCREEN_QUESTION_COPIES:
				return "StepQuestionPrint";
			case SCREEN_REPORT_TC:
				this.setElementsVisible(false, "NewsColumn");
				// this.fillReport(this.buildDummyTcReportDto()); // TODO temp
				return "StepSummaryTC";
			case SCREEN_REPORT_VU:
				this.setElementsVisible(false, "NewsColumn");
				// this.fillReport(this.buildDummyVuReportDto()); // TODO temp
				return "StepSummaryVU";
			case SCREEN_THANKS:
				return "StepThanks";
			case SCREEN_ERROR_GENERAL:
				this.setElementsVisible(false, "NewsColumn");
				return "StepTerminalOutOfService";
			case SCREEN_ERROR_SOURCE_UNKNOW:
				return "StepWarning";
			case SCREEN_ERROR_INVALID_CARD:
				return "StepErrorCard";
			case SCREEN_ERROR_NO_USB_DETECTED:
				return "StepErrorNoUsbDetected";
			case SCREEN_ERROR_USB_EMPTY:
				return "StepErrorUsbEmpty";
			case SCREEN_ERROR_USB_GENERAL:
				return "StepErrorUsbGeneral";
			default:
				throw new RuntimeException("Unknow step index");
		}
	}

	private void setLabelText(String attr, String text) {
		Label label = (Label) this.managedForm.getElementReference(attr);
		label.setText(text == null ? "" : text);
	}

	private void setLabelText(String attr, Format format, Object value) {
		if ((value != null) && (format != null)) {
			value = format.format(value);
		}
		this.setLabelText(attr, value == null ? null : String.valueOf(value));

	}

	private void setElementsVisible(boolean visible, String... attrs) {
		for (String attr : attrs) {
			JComponent elementReference = (JComponent) this.managedForm.getElementReference(attr);
			if (elementReference == null) {
				elementReference = this.managedForm.getButton(attr);
			}
			if (elementReference != null) {
				elementReference.setVisible(visible);
			} else {
				IMMainWizard.logger.warn("{} not found for visible {}", attrs, visible);
			}
		}
	}

	@Override
	public int getStepsNumber() {
		return 10;
	}

	@Override
	protected boolean validStepChange(int arg0, int arg1) {
		return true;
	}

	@Override
	protected boolean validateFinish() {
		return true;
	}

	private void setUserPreferences(UserInfoProvider uinfo) throws Exception {
		uinfo.getUserData();
		// do nothing
	}

	public List<TGDFileInfo> getFiles() {
		return this.lFileInfo;
	}

	public AbstractDeviceController getDeviceController() {
		return this.devc;
	}

	public void fillReport(VuReportDto reportInfo) {
		// SimpleDateFormat formatShortDate = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat formatShortDate = DateFormat.getDateInstance(DateFormat.SHORT, ApplicationManager.getApplication().getLocale());
		DateFormat formatLongDate = DateFormat.getDateInstance(DateFormat.LONG, ApplicationManager.getApplication().getLocale());
		DecimalFormat formatKm = new DecimalFormat("#0,000", new DecimalFormatSymbols(ApplicationManager.getApplication().getLocale()));
		formatKm.setGroupingUsed(true);

		this.setLabelText("companyVU", reportInfo.getCompanyName());
		this.setLabelText("vehicleVU", reportInfo.getVehicle());
		this.setLabelText("vehicleKmsInitVU", formatKm, reportInfo.getKmIni());
		this.setLabelText("vehicleKmsEndVU", formatKm, reportInfo.getKmEnd());
		this.setLabelText("vehicleKmsTotalVU", formatKm, reportInfo.getKmTot());
		this.setLabelText("beginPeriodVU", formatShortDate, reportInfo.getInitDate());
		this.setLabelText("endPeriodVU", formatShortDate, reportInfo.getEndDate());
		this.setLabelText("nextDownloadVU", formatLongDate, reportInfo.getNextDownloadDate());

		UTable tDrivers = (UTable) this.managedForm.getElementReference("driverSummaryVU");
		this.customizeTable(tDrivers);
		tDrivers.setValue(this.getTableValueDrivers(reportInfo.getDriverList()));

		UTable tControls = (UTable) this.managedForm.getElementReference("controlSummaryVU");
		this.customizeTable(tControls);
		tControls.setValue(this.getTableValueControls(reportInfo.getControlList()));

		UTable tCalibrations = (UTable) this.managedForm.getElementReference("calibrationSummaryVU");
		this.customizeTable(tCalibrations);
		tCalibrations.setValue(this.getTableValueCalibrations(reportInfo.getCalibradosList()));

		// UTable tInfractions = (UTable) this.managedForm.getElementReference("infractionSummaryVU");
		// this.customizeTable(tInfractions);
		// tInfractions.setValue(this.getTableValueInfractions(reportInfo.getInfractionList()));

		UTable tIncidences = (UTable) this.managedForm.getElementReference("incidenceSummaryVU");
		this.customizeTable(tIncidences);
		tIncidences.setValue(this.getTableValueIncidences(reportInfo.getIncidentesList()));
	}

	public void fillReport(TcReportDto reportInfo, boolean limitedInfo) {
		// SimpleDateFormat formatShortDate = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat formatShortDate = DateFormat.getDateInstance(DateFormat.SHORT, ApplicationManager.getApplication().getLocale());
		DateFormat formatLongDate = DateFormat.getDateInstance(DateFormat.LONG, ApplicationManager.getApplication().getLocale());
		TimeFormatter formatTime = new TimeFormatter();
		DecimalFormat formatKm = new DecimalFormat("#0,000.00", new DecimalFormatSymbols(ApplicationManager.getApplication().getLocale()));
		formatKm.setGroupingUsed(true);

		this.setLabelText("company", reportInfo.getCompanyName());
		this.setLabelText("driverName", reportInfo.getDriverName() + " " + reportInfo.getDriverSurname());
		this.setLabelText("driverDNI", reportInfo.getDriverId());
		this.setLabelText("beginPeriod", formatShortDate, reportInfo.getInitDate());
		this.setLabelText("endPeriod", formatShortDate, reportInfo.getEndDate());
		this.setLabelText("nextDownload", formatLongDate, reportInfo.getNextDownloadDate());

		this.setLabelText("drivingTime", formatTime, reportInfo.getActivitySummaryDrivingTime());
		this.setLabelText("availableTime", formatTime, reportInfo.getActivitySummaryAvailableTime());
		this.setLabelText("pausingTime", formatTime, reportInfo.getActivitySummaryPauseTime());
		this.setLabelText("otherWorksTime", formatTime, reportInfo.getActivitySummaryWorkingTime());
		this.setLabelText("kilometersDriving", formatKm, reportInfo.getActivitySummaryKilometers());

		this.setLabelText("beginEndSessions", reportInfo.isHasIncidences() ? "M_HAS_INCIDENCES" : (reportInfo.isHasErros() ? "M_HAS_ERRORS" : "M_HAS_NOT_INCIDENCES"));

		// ((UList) this.managedForm.getElementReference("usedVehicles")).setValue(reportInfo.getVehicleList());
		Table usedVehiclesTable = (Table) this.managedForm.getElementReference("usedVehicles");
		this.customizeTable(usedVehiclesTable);
		usedVehiclesTable.setValue(this.getTableValueUsedVehicle(reportInfo.getVehicleList()));

		Table tInfractions = (Table) this.managedForm.getElementReference("infractionSummary");
		if (limitedInfo) {
			tInfractions.showInformationPanel("W_NOT_AVAILABLE_DATA");
		} else {
			this.customizeTable(tInfractions);
			tInfractions.setValue(this.getTableValueInfractions(reportInfo.getInfractionList()));
		}
	}

	private void customizeTable(Table tInfractions) {
		// Customize infractions table -----------
		tInfractions.setEnabled(true);
		tInfractions.setLineRemark(false);
		tInfractions.enableSort(false);
		tInfractions.enableFiltering(false);
		MouseListener popupListener = (MouseListener) ReflectionTools.getFieldValue(tInfractions, "popupListener");
		tInfractions.getJScrollPane().removeMouseListener(popupListener);
		tInfractions.getJTable().removeMouseListener(popupListener);
		tInfractions.getJScrollPane().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		tInfractions.getJTable().setRowSelectionAllowed(false);
	}

	private Object getTableValueDrivers(List<Driver> driverList) {
		if ((driverList != null) && !driverList.isEmpty()) {
			EntityResult res = new EntityResult(Arrays.asList(new String[] { "dni", "name", "surnames" }));
			for (Driver driver : driverList) {
				Hashtable<String, Object> record = new Hashtable<String, Object>();
				MapTools.safePut(record, "dni", driver.getDriverDNI());
				MapTools.safePut(record, "name", driver.getDriverName());
				MapTools.safePut(record, "surnames", driver.getDriverSurname());
				res.addRecord(record, res.calculateRecordNumber());
			}
			return res;
		}
		return null;
	}

	private Object getTableValueControls(List<Control> controlList) {
		if ((controlList != null) && !controlList.isEmpty()) {
			EntityResult res = new EntityResult(Arrays.asList(new String[] { "date", "controlcard", "description" }));
			for (Control control : controlList) {
				Hashtable<String, Object> record = new Hashtable<String, Object>();
				MapTools.safePut(record, "date", control.getDate());
				MapTools.safePut(record, "controlcard", control.getControlCardNumber());
				MapTools.safePut(record, "description", control.getType());
				res.addRecord(record, res.calculateRecordNumber());
			}
			return res;
		}
		return null;
	}

	private Object getTableValueCalibrations(List<Calibrado> calibradosList) {
		if ((calibradosList != null) && !calibradosList.isEmpty()) {
			EntityResult res = new EntityResult(Arrays.asList(new String[] { "description", "workshopcard", "nextdate" }));
			for (Calibrado calibrado : calibradosList) {
				Hashtable<String, Object> record = new Hashtable<String, Object>();
				MapTools.safePut(record, "description", calibrado.getType());
				MapTools.safePut(record, "workshopcard", calibrado.getWorkshopCardNumber());
				MapTools.safePut(record, "nextdate", calibrado.getNextDate());
				res.addRecord(record, res.calculateRecordNumber());
			}
			return res;
		}
		return null;
	}

	private Object getTableValueUsedVehicle(List<String> usedVehicleList) {
		EntityResult er = new EntityResult();
		er.put("vehicle", new Vector<String>(usedVehicleList));
		return er;
	}

	private Object getTableValueInfractions(List<Infraction> infractionList) {
		if ((infractionList != null) && !infractionList.isEmpty()) {
			EntityResult res = new EntityResult(Arrays.asList(new String[] { "type", "severity" }));
			for (AbstractReportDto.Infraction infract : infractionList) {
				Hashtable<String, Object> record = new Hashtable<String, Object>();
				MapTools.safePut(record, "type", infract.getType());
				MapTools.safePut(record, "severity", infract.getNature());
				res.addRecord(record, res.calculateRecordNumber());
			}
			return res;
		}
		return null;
	}

	private Object getTableValueIncidences(List<Incidence> incidentesList) {
		if ((incidentesList != null) && !incidentesList.isEmpty()) {
			EntityResult res = new EntityResult(Arrays.asList(new String[] { "type"/* , "date" */ }));
			for (Incidence incidence : incidentesList) {
				Hashtable<String, Object> record = new Hashtable<String, Object>();
				MapTools.safePut(record, "type", incidence.getType());
				// MapTools.safePut(record, "date", incidence.getDate());
				res.addRecord(record, res.calculateRecordNumber());
			}
			return res;
		}
		return null;
	}

	private static class TimeFormatter extends Format {

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
			if (obj instanceof Number) {
				int value = ((Number) obj).intValue();

				value = Math.abs(value);
				int h, m, sec;
				// Value is the number of seconds.
				h = value / (60 * 60);
				value = value % (60 * 60);
				m = value / 60;
				sec = value % 60;

				DecimalFormat df = new DecimalFormat("#00");
				toAppendTo.append(df.format(h));
				toAppendTo.append(":");
				toAppendTo.append(df.format(m));
			}
			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return null;
		}
	}

	private TcReportDto buildDummyTcReportDto() {
		TcReportDto res = new TcReportDto();

		res.setCompanyCif("ASD1234579879");
		res.setCompanyName("Empresa de prueba 1 S.L.");

		res.setDriverId("Id1");
		res.setDriverName("Jose Antonio");
		res.setDriverSurname("de la Fuente y García");
		res.setDriverDni("11.111.111-X");

		res.setInitDate(new Date());
		res.setEndDate(new Date());

		res.setNextDownloadDate(new Date());

		res.setActivitySummary((122 * 60 * 60) + (23 * 60) + 32, (0 * 60 * 60) + (21 * 60) + 17, (1 * 60 * 60) + (2 * 60) + 10, (1 * 60 * 60) + (8 * 60) + 55, 2512);

		res.setHasIncidences(true);

		res.addVehicle("3224 HBZ");
		res.addVehicle("3776 HGX");
		res.addVehicle("7569 GFT");

		res.addInfraction("FDD", "MG", new Date(), new Date());
		res.addInfraction("ECI", "L", new Date(), new Date());
		res.addInfraction("FDS", "G", new Date(), new Date());
		res.addInfraction("ECI", "L", new Date(), new Date());

		return res;
	}

	private VuReportDto buildDummyVuReportDto() {
		VuReportDto res = new VuReportDto();

		res.setCompanyCif("ASD1234579879");
		res.setCompanyName("Empresa de prueba 1 S.L.");

		res.setInitDate(new Date());
		res.setEndDate(new Date());

		res.setNextDownloadDate(new Date());

		res.setVehicle("3224 HBZ");
		res.setKms(112450, 113863, 11413);

		res.addDriver("12121212A", "Jose Antonio", "de la Fuente y García", "12121212A");
		res.addDriver("E123456789Z0000", "Juan", "García Olivares", "E123456789Z0000");

		res.addCalibrado("Activación", "EB27213727000", new Date());
		res.addCalibrado("Primera instalación (primer calibrado de la VU después de su activación)", "EB27213727000", new Date());
		res.addCalibrado("Instalación (primer calibrado de la VU en el vehículo actual)", "EB27213727000", new Date());

		res.addControl(new Date(), "type1", "numTrjControl1");
		res.addControl(new Date(), "type2", "numTrjControl2");

		res.addInfraction("FDD", "MG", new Date(), new Date());
		res.addInfraction("ECI", "L", new Date(), new Date());
		res.addInfraction("FDS", "G", new Date(), new Date());
		res.addInfraction("ECI", "L", new Date(), new Date());
		res.addInfraction("AAA", "L", new Date(), new Date());
		res.addInfraction("BBB", "L", new Date(), new Date());

		res.addIncideteFallo("M_EXCESOS_VELOCIDAD", new Date());
		res.addIncideteFallo("M_CONDUCCION_SIN_TARJETA", new Date());
		res.addIncideteFallo("M_INTERRUPCION_CORRIENTE", new Date());
		res.addIncideteFallo("M_FALLO_CONTROL", new Date());
		res.addIncideteFallo("M_FALLO_OTHERS", new Date());
		return res;
	}
}
