package com.opentach.client.employee.fim;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.util.Calendar;
import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.container.Row;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.field.WWWDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.company.tools.LicenseClientTools;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.client.util.DriverUtil;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.labor.ICalendarSummary;
import com.opentach.common.labor.IManagedStaff;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.tools.VersionUtils;

public class IMEmpleado extends UBasicFIM /* extends IMDataRoot */ {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(IMEmpleado.class);

	@FormComponent(attr = OpentachFieldNames.DNI_FIELD)
	protected DataField				dniField;
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	protected DataField				idConductorField;
	@FormComponent(attr = CompanyNaming.CIF)
	protected DataField				cifField;

	@FormComponent(attr = IMDataRoot.ULTIMOS_DIAS)
	protected RadioButtonDataField	lastXDaysRadio;
	@FormComponent(attr = IMDataRoot.RANGO_FECHA)
	protected RadioButtonDataField	rangoRadio;
	@FormComponent(attr = OpentachFieldNames.FILTERFECINI)
	protected DateDataField			filterIniField;
	@FormComponent(attr = OpentachFieldNames.FILTERFECFIN)
	protected DateDataField			filterFinField;

	@FormComponent(attr = OpentachFieldNames.CG_CONTRATO_FIELD)
	protected DataField				cgContratoField;

	@FormComponent(attr = "EMAIL")
	protected WWWDataField			email;
	@FormComponent(attr = "OTHER_DATA_COLUMN")
	protected Column				otherDataColumn;
	@FormComponent(attr = "WEB_USER")
	protected TextDataField			webUsr;
	@FormComponent(attr = "WEB_PASSWORD")
	protected PasswordDataField		webPass;
	@FormComponent(attr = "EMPLOYEEDATA.RESEND_WEB_USER_PASSWORD")
	protected Button				resendWebUsrPass;

	@FormComponent(attr = "WEB_PORTAL_SECTION")
	protected Row					webPortalSection;
	@FormComponent(attr = "EXPIRATION_COLUMN")
	protected Column				expirationColumn;

	@FormComponent(attr = ICalendarSummary.ATTR.ENTITY)
	Table							calendarSummary;
	@FormComponent(attr = IManagedStaff.ATTR.ENTITY)
	Table							managedStaff;

	@FormComponent(attr = "FAKE_DATA_COLUMN")
	protected Column				fakeDataColumn;
	@FormComponent(attr = "FEC_FILTER")
	protected DataField				fecFilterField;
	@FormComponent(attr = "FAKEDATAFECINI")
	protected DateDataField			fakeFecIni;
	@FormComponent(attr = "FAKEDATAFECFIN")
	protected DateDataField			fakeFecFin;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.setupFilters();
		this.setupWebAuthReSend();
	}

	private void setupWebAuthReSend() {
		ValueChangeListener valueChangeListener = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent event) {
				if (event.getType() == ValueEvent.USER_CHANGE) {
					IMEmpleado.this.resendWebUsrPass.setEnabled(false);
				}
			}
		};

		this.webUsr.addValueChangeListener(valueChangeListener);
		this.webPass.addValueChangeListener(valueChangeListener);
		this.email.addValueChangeListener(valueChangeListener);
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();

		// Disable report filters
		this.lastXDaysRadio.setEnabled(false);
		this.rangoRadio.setEnabled(false);
		this.filterIniField.setEnabled(false);
		this.filterFinField.setEnabled(false);

		// Disable re-send user & password for web access
		this.resendWebUsrPass.setEnabled(false);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.idConductorField.setEnabled(false);
	}

	@Override
	public void dataChanged(DataNavigationEvent event) {
		super.dataChanged(event);
		this.checkRefreshTables(false);

		String cif = (String) this.cifField.getValue();
		final boolean hasTachoLabPlus = LicenseClientTools.hasTacholabPlus(cif);
		this.calendarSummary.setVisible(hasTachoLabPlus);
		this.managedStaff.setVisible(hasTachoLabPlus);
		this.otherDataColumn.setVisible(hasTachoLabPlus);
		this.webPortalSection.setVisible(hasTachoLabPlus);
		this.expirationColumn.setVisible(hasTachoLabPlus);

		final boolean isDevel = this.isDevel();
		this.fakeDataColumn.setVisible(isDevel);
	}

	@Override
	public boolean checkUpdate() {
		return this.checkValidCIFNIF() && this.checkValidWebUser() && super.checkUpdate();
	}

	@Override
	public boolean checkInsert() {
		return this.checkValidCIFNIF() && this.checkValidWebUser() && super.checkInsert();
	}

	protected boolean checkValidWebUser() {
		String webUserString = (String) this.webUsr.getValue();
		String webPassString = (String) this.webPass.getValue();
		String emailString = (String) this.email.getValue();

		if (Stream.of(webUserString, webPassString, emailString).anyMatch(string -> (string == null) || string.trim().isEmpty())) {
			int option = MessageManager.getMessageManager().showMessage( //
					this.managedForm.getParent(), //
					"EMPLOYEEDATA.ASK_NO_WEB_USER_PASS_EMAIL_DEFINED", //
					MessageType.QUESTION);

			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		return true;
	}

	protected boolean checkValidCIFNIF() {
		if (!DriverUtil.checkValidCIFNIF(this.managedForm, (String) this.dniField.getValue(), false)) {
			String msg = ApplicationManager.getTranslation("VERIFIQUE_DATOS_CONDUCTOR", this.managedForm.getResourceBundle());
			int rtn = JOptionPane.showConfirmDialog(this.managedForm.getParent(), msg);
			if (rtn != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Install logic to filters section.
	 */
	protected void setupFilters() {
		this.filterIniField.setModifiable(false);
		this.filterFinField.setModifiable(false);

		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.fecFilterField.getAttribute(), false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.lastXDaysRadio.getAttribute(), false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.rangoRadio.getAttribute(), false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.filterIniField.getAttribute(), false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.filterFinField.getAttribute(), false);

		this.lastXDaysRadio.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (ObjectTools.safeIsEquals(e.getOldValue(), e.getNewValue()) || !ParseUtilsExtended.getBoolean(e.getNewValue(), true)) {
					return;
				}
				IMEmpleado.this.filterIniField.setValue(DateTools.add(new Date(), Calendar.DAY_OF_YEAR, -28));
				IMEmpleado.this.filterFinField.setValue(new Date());
				IMEmpleado.this.filterIniField.setEnabled(false);
				IMEmpleado.this.filterFinField.setEnabled(false);

				if (e.getType() == ValueEvent.USER_CHANGE) {
					IMEmpleado.this.checkRefreshTables(true);
				}
			}
		});
		this.rangoRadio.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (ObjectTools.safeIsEquals(e.getOldValue(), e.getNewValue()) || !ParseUtilsExtended.getBoolean(e.getNewValue(), true)) {
					return;
				}
				IMEmpleado.this.filterIniField.setEnabled(true);
				IMEmpleado.this.filterFinField.setEnabled(true);

				if (e.getType() == ValueEvent.USER_CHANGE) {
					IMEmpleado.this.checkRefreshTables(true);
				}
			}
		});

		this.filterIniField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					IMEmpleado.this.checkRefreshTables(true);
				}
			}
		});

		this.filterFinField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					IMEmpleado.this.checkRefreshTables(true);
				}
			}
		});

		this.lastXDaysRadio.setValue(Boolean.TRUE);// By default
	}

	/**
	 * Check date filters, and ensure to set it property .
	 * Tables are listening about this target filter and autorefresh (if all another parentkeys are available)
	 * @param showalert
	 * @return
	 */
	protected boolean checkRefreshTables(boolean showalert) {
		Date ini = (Date) this.filterIniField.getDateValue();
		Date fin = (Date) this.filterFinField.getDateValue();
		if (fin != null) {
			fin = DateTools.lastMilisecond(fin);
		}
		if ((ini == null) || (fin == null)) {
			if (showalert) {
				this.managedForm.message(ApplicationManager.getTranslation("M_INSERTE_FECHAS_FILTRO_BUSQUEDA"), Form.WARNING_MESSAGE);
			}
			return false;
		} else if (ini.after(fin)) {
			if (showalert) {
				this.managedForm.message(ApplicationManager.getTranslation("M_FECHAS_FILTRO_BUSQUEDA_INCORRECTAS"), Form.WARNING_MESSAGE);
			}
			return false;
		}

		if ((ini != null) && (fin != null) && !this.isEstablishingFormValues()) {
			this.fecFilterField.setValue(new SearchValue(SearchValue.BETWEEN, new Vector(Arrays.asList(new Date[] { ini, fin }))));
		}

		return true;
	}

	/**
	 * Check if the current contract of the company has TACHOLABPLUS activated.
	 *
	 * @return
	 */
	private boolean getHasTacholabPlus() {
		// TODO senen change that one
		boolean ret = this.isDevel();
		final ReferenceLocator referenceLocator = (ReferenceLocator) IMEmpleado.this.formManager.getReferenceLocator();
		try {
			final Entity entity = referenceLocator.getEntityReference("EContratoEmp");
			final EntityResult entityResult = entity.query( //
					EntityResultTools.keysvalues( //
							OpentachFieldNames.CG_CONTRATO_FIELD, this.cgContratoField.getValue()), //
					EntityResultTools.attributes( //
							"HAS_TACHOLABPLUS" //
							), //
					referenceLocator.getSessionId());

			if (entityResult.calculateRecordNumber() != 0) {

				ret = "S".equals(((Vector<String>) entityResult.get("HAS_TACHOLABPLUS")).get(0));
			}

		} catch (final Exception exception) {
			// We assign the default value since we couldn't determine if the
			// company has TACHOLABPLUS activated (no contract or error condition):
			IMEmpleado.logger.warn("NO 'HAS_TACHOLABPLUS' COLUMN FOUND: ASSUMING {}", ret, exception);
		}

		return ret;
	}

	private boolean isDevel() {
		boolean ret = false;
		String version = VersionUtils.getVersion(IMEmpleado.class).toLowerCase();

		if (version != null) {
			ret = "dev".equals(version);
		}

		return ret;
	}
}
