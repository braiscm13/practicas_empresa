package com.opentach.client.company.fim;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.Tab;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.client.company.listeners.RefreshTableCellEditorListener;
import com.opentach.client.company.tools.LicenseClientTools;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.tools.InsertTableAddButtonInstaller;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.company.service.ILicenseService;
import com.opentach.common.labor.ICalendarEventDays;
import com.opentach.common.labor.ICalendarEvents;
import com.opentach.common.labor.ICalendarHolidays;
import com.opentach.common.labor.ICalendarShifts;
import com.opentach.common.report.util.ReportSessionUtils;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.table.editor.UXmlBooleanCellEditor;

public class IMEmpresa extends IMRoot {

	private static final Logger		logger		= LoggerFactory.getLogger(IMEmpresa.class);

	@FormComponent(attr = ICalendarEvents.ATTR.ENTITY)
	private Table					calEventsTable;

	@FormComponent(attr = ICalendarShifts.ATTR.ENTITY)
	private Table					calShiftsTable;

	@FormComponent(attr = ICalendarEventDays.ATTR.ENTITY)
	private Table					calEventDays;

	@FormComponent(attr = ICalendarHolidays.ATTR.ENTITY)
	private Table					calHolidaysTable;

	@FormComponent(attr = "ojee.ManualJournalService.manualJournalDeleted")
	private Table				deletedManualJournalTable;

	@FormComponent(attr = "ECifEmpreReq")
	private Table				eCifEmpreReqTable;

	@FormComponent(attr = "Contratos")
	protected Tab				tabContratos;

	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private DataField				cmpCIF;

	@FormComponent(attr = "COMPANY.LICENSE")
	protected Tab					tabLicenses;

	public IMEmpresa() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add("CG_NACI");
		this.fieldsChain.add("CG_PROV");
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		InsertTableAddButtonInstaller.setListener(this.calEventsTable, ICalendarEvents.ATTR.DESCRIP);
		InsertTableAddButtonInstaller.setListener(this.calShiftsTable, ICalendarShifts.ATTR.START_HOUR);

		FIMUtils.setEventsOnFieldEnabled(this.managedForm, "SHOW_APPROVED", false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, "SHOW_DISMISSED", false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, "HOLIDAYS_TYPE",  false);

		// Disable license checks: un-modifiables
		form.getDataFieldReference(LicenseNaming.LIC_OPENTACH).setModifiable(false);
		form.getDataFieldReference(LicenseNaming.LIC_TACHOLAB).setModifiable(false);
		form.getDataFieldReference(LicenseNaming.LIC_TACHOLABPLUS).setModifiable(false);
		form.getDataFieldReference(LicenseNaming.LIC_OPENTACH_DEMO).setModifiable(false);
		form.getDataFieldReference(LicenseNaming.LIC_TACHOLAB_DEMO).setModifiable(false);
		form.getDataFieldReference(LicenseNaming.LIC_TACHOLABPLUS_DEMO).setModifiable(false);

		((UXmlBooleanCellEditor) this.calHolidaysTable.getEditorForColumn(ICalendarHolidays.ATTR.APPROVED))
		.addCellEditorListener(new RefreshTableCellEditorListener(this.calHolidaysTable));
		((UXmlBooleanCellEditor) this.calHolidaysTable.getEditorForColumn(ICalendarHolidays.ATTR.DISMISSED))
		.addCellEditorListener(new RefreshTableCellEditorListener(this.calHolidaysTable));
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.managedForm.setDataFieldValue("CG_NACI", "00042");
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.cmpCIF != null) {
			this.cmpCIF.setEnabled(false);
		}
		this.calEventDays.refreshInThread(0);
	}

	@Override
	public void dataChanged(DataNavigationEvent e) {
		super.dataChanged(e);
		this.deletedManualJournalTable.refreshInThread(0);

		this.decorateTabEmpreReqByCompanyAccess();
		this.decorateTabLicensesByCompanyAccess();
		this.decorateTacholabPlusTabs();
	}

	private void decorateTabEmpreReqByCompanyAccess() {
		if (this.eCifEmpreReqTable != null) {
			boolean hasValues = this.eCifEmpreReqTable.getJTable().getRowCount() > 0; // Granted by queryifvisible="no"
			JTabbedPane parent = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this.tabContratos);
			int idx = parent.indexOfComponent(this.tabContratos);
			parent.setIconAt(idx, hasValues ? null : ImageManager.getIcon("images-company/sign_warning16.png"));
		}
	}

	private void decorateTabLicensesByCompanyAccess() {
		// TODO do asynch, but carefull when navigating fast over records
		// Mark when company has no license to access to app
		boolean hasAccess = true;
		try {
			ILicenseService licenseService = BeansFactory.getBean(ILicenseService.class);
			String cif = (String) this.cmpCIF.getValue();
			if (ReportSessionUtils.isOpentach()) {
				hasAccess = licenseService.hasOpentachAccess(cif);
			} else if (ReportSessionUtils.isTacholab()) {
				hasAccess = licenseService.hasTacholabAccess(cif) || (LicenseClientTools.hasTacholabPlus(cif));
			}
		} catch (Exception err) {
			IMEmpresa.logger.error("E_CHECKING_COMPANY_ACCESS", err);
		}
		JTabbedPane parent = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this.tabLicenses);
		int idx = parent.indexOfComponent(this.tabLicenses);
		parent.setIconAt(idx, hasAccess ? null : ImageManager.getIcon("images-company/sign_warning16.png"));
	}

	private void decorateTacholabPlusTabs() {
		// TODO Auto-generated method stub

	}
}
