package com.opentach.client.company.listeners;

import java.awt.Component;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;
import com.opentach.client.util.TabVisibleUtil;
import com.opentach.client.util.UserTools;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.report.util.ReportSessionUtils;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class CompanyLicenseValueChangeListener extends AbstractValueChangeListener {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(CompanyLicenseValueChangeListener.class);

	@FormComponent(attr = LicenseNaming.LIC_OPENTACH)
	protected CheckDataField	licOpentach;
	@FormComponent(attr = LicenseNaming.LIC_OPENTACH_DEMO)
	protected CheckDataField	licOpentachDemo;

	@FormComponent(attr = LicenseNaming.LIC_TACHOLAB)
	protected CheckDataField	licTacholab;
	@FormComponent(attr = LicenseNaming.LIC_TACHOLAB_DEMO)
	protected CheckDataField	licTacholabDemo;

	@FormComponent(attr = LicenseNaming.LIC_TACHOLABPLUS)
	protected CheckDataField	licTacholabPlus;
	@FormComponent(attr = LicenseNaming.LIC_TACHOLABPLUS_DEMO)
	protected CheckDataField	licTacholabPlusDemo;

	public CompanyLicenseValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
		super.interactionManagerModeChanged(e);
		this.addaptLicenseFields();
	}

	@Override
	public void valueChanged(ValueEvent valueEvent) {
		this.addaptLicenseFields();
	}

	/**
	 * Control about license fields (in company form), to determine when enabled/visible
	 */
	private void addaptLicenseFields() {
		boolean tacholabApp = ReportSessionUtils.isTacholab();
		boolean opentachByLicense = (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE) && this.licOpentach.isSelected();
		boolean opentachByDemo = (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE) && this.licOpentachDemo.isSelected();
		boolean tacholabByLicense = (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE) && this.licTacholab.isSelected();
		boolean tacholabByDemo = (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE) && this.licTacholabDemo.isSelected();
		boolean tacholabPlusByLicense = (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE) && this.licTacholabPlus.isSelected();
		boolean tacholabPlusByDemo = (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE) && this.licTacholabPlusDemo.isSelected();
		boolean tacholabPlusModuleEnabled = LicenseNaming.TACHOLABPLUS_ENABLED;
		boolean adminUsers = UserTools.isSupervisor() || UserTools.isOperadorAvanzado() || UserTools.isDistribuidor();
		boolean tacholabPlusVisibility = tacholabPlusModuleEnabled && tacholabApp && (tacholabPlusByLicense || adminUsers);

		// TacholabPlus tabs visibility
		TabVisibleUtil.setTabVisible(this.getForm(), tacholabPlusVisibility, "CAL_CALENDARS");
		TabVisibleUtil.setTabVisible(this.getForm(), tacholabPlusVisibility, "CAL_SHIFTS");
		TabVisibleUtil.setTabVisible(this.getForm(), tacholabPlusVisibility, "CAL_EVENTS");
		TabVisibleUtil.setTabVisible(this.getForm(), tacholabPlusVisibility, "EVENTS_LIST");

		// TacholabPlus license fields visibility
		this.setElementsVisible(tacholabPlusVisibility, "tacholabplus_row");

		// Buttons: only for admin users
		this.setElementsVisible(adminUsers, //
				"start.1", "end.1", "start.demo.1", "end.demo.1", //
				"start.2", "end.2", "start.demo.2", "end.demo.2", //
				"start.3", "end.3", "start.demo.3", "end.demo.3", //
				"start.4", "end.4", "start.demo.4", "end.demo.4");
		this.setElementsVisible(adminUsers, //
				"LIC_OPENTACH_START", "LIC_OPENTACH_END", "LIC_OPENTACH_DEMO_START", "LIC_OPENTACH_DEMO_END", //
				"LIC_TACHOLAB_START", "LIC_TACHOLAB_END", "LIC_TACHOLAB_DEMO_START", "LIC_TACHOLAB_DEMO_END", //
				"LIC_TACHOLABPLUS_START", "LIC_TACHOLABPLUS_END", "LIC_TACHOLABPLUS_DEMO_START", "LIC_TACHOLABPLUS_DEMO_END"//
				);
		// Buttons for company
		this.setElementsVisible(UserTools.isEmpresa(), "info.1", "info.2", "info.3", "info.4");

		// License dates enable conditions
		this.setElementsEnabled(opentachByLicense && adminUsers, LicenseNaming.LIC_OPENTACH_FROM, LicenseNaming.LIC_OPENTACH_TO);
		this.setElementsEnabled(opentachByDemo && adminUsers, LicenseNaming.LIC_OPENTACH_DEMO_FROM, LicenseNaming.LIC_OPENTACH_DEMO_TO);
		this.setElementsEnabled(tacholabByLicense && adminUsers, LicenseNaming.LIC_TACHOLAB_FROM, LicenseNaming.LIC_TACHOLAB_TO);
		this.setElementsEnabled(tacholabByDemo && adminUsers, LicenseNaming.LIC_TACHOLAB_DEMO_FROM, LicenseNaming.LIC_TACHOLAB_DEMO_TO);
		this.setElementsEnabled(tacholabPlusByLicense && adminUsers, LicenseNaming.LIC_TACHOLABPLUS_FROM, LicenseNaming.LIC_TACHOLABPLUS_TO);
		this.setElementsEnabled(tacholabPlusByDemo && adminUsers, LicenseNaming.LIC_TACHOLABPLUS_DEMO_FROM, LicenseNaming.LIC_TACHOLABPLUS_DEMO_TO);
	}

	private void setElementsVisible(boolean visibility, String... elements) {
		if (elements == null) {
			return;
		}
		for (String element : elements) {
			Component component = this.getComponent(element);
			if (component != null) {
				component.setVisible(visibility);
			}
		}
	}

	private void setElementsEnabled(boolean enabled, String... elements) {
		if (elements == null) {
			return;
		}
		for (String element : elements) {
			Component component = this.getComponent(element);
			if (component != null) {
				component.setEnabled(enabled);
			}
		}
	}

	private Component getComponent(String element) {
		com.ontimize.gui.field.FormComponent ele = this.getForm().getElementReference(element);
		if (ele == null) {
			ele = this.getForm().getButton(element);
		}
		return (Component) ele;
	}


}