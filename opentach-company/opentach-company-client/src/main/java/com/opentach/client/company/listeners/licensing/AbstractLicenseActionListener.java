package com.opentach.client.company.listeners.licensing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.IDetailForm;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.company.exception.CompanyException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.service.ICompanyService;
import com.opentach.common.company.service.ILicenseService;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UEmailDataField;

public abstract class AbstractLicenseActionListener extends AbstractUpdateModeActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(AbstractLicenseActionListener.class);

	@FormComponent(attr = CompanyNaming.CIF)
	protected DataField	cifField;

	@FormComponent(attr = CompanyNaming.EMAIL)
	protected DataField	emailField;

	protected String	licenseAttr;
	protected String	licenseFromAttr;
	protected String	licenseToAttr;
	protected String	demoAttr;
	protected String	demoFromAttr;
	protected String	demoToAttr;

	private Form		datesForm;
	private JDialog		datesDialog;

	public AbstractLicenseActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.licenseAttr = (String) params.get("licensefield");
		this.licenseFromAttr = (String) params.get("licensefromfield");
		this.licenseToAttr = (String) params.get("licensetofield");
		this.demoAttr = (String) params.get("demofield");
		this.demoFromAttr = (String) params.get("demofromfield");
		this.demoToAttr = (String) params.get("demotofield");
	}

	protected ILicenseService getLicenseService() {
		return BeansFactory.getBean(ILicenseService.class);
	}

	protected String getCIF() {
		return (String) this.cifField.getValue();
	}

	protected boolean isLicensed() {
		return ParseUtilsExtended.getBoolean(this.getForm().getDataFieldValue(this.licenseAttr));
	}

	protected boolean isDemoActive() {
		return ParseUtilsExtended.getBoolean(this.getForm().getDataFieldValue(this.demoAttr));
	}

	public String getLicenseAttr() {
		return this.licenseAttr;
	}

	protected boolean checkCompanyContract() {
		String contract = null;
		try {
			contract = this.getCompanyContract();
		} catch (Exception err) {
			AbstractLicenseActionListener.logger.error("E_GETTING_COMPANY_CONTRACT", err);
		}
		if ((contract == null) && !this.confirmAction("Q_NOT_CONTRACT_AVAILABLE__CONTINUE")) {
			return false;
		}
		return true;
	}

	protected String getCompanyContract() throws CompanyException {
		ICompanyService companyService = BeansFactory.getBean(ICompanyService.class);
		return companyService.getCompanyContract(this.getCIF());
	}

	protected boolean checkCompanyEmail() {
		// email changed without save
		if (this.getForm().getInteractionManager().getModifiedFieldAttributes().contains(CompanyNaming.EMAIL)) {
			MessageManager.getMessageManager().showMessage(this.getForm(), "W_UNSAVED_EMAIL_MANDATORY", MessageType.WARNING, new Object[] {});
			return false;
		}

		String emailValue = (String) this.emailField.getValue();
		if (StringTools.isEmpty(emailValue) || !UEmailDataField.isValidEmail(emailValue)) {
			MessageManager.getMessageManager().showMessage(this.getForm(), "W_INVALID_EMAIL_MANDATORY", MessageType.WARNING, new Object[] {});
			return false;
		}
		return true;
	}

	protected boolean confirmAction(String message) {
		return MessageManager.getMessageManager().showMessage(this.getForm(), message, MessageType.QUESTION, new Object[] {}) == JOptionPane.OK_OPTION;
	}

	protected Pair<Date, Date> askForDates(boolean onlyEnd, int numDaysAsDefault) {
		// Ask for with 2 dates fields (default values) + Accept button + cancel button
		this.checkForDatesForm(onlyEnd, numDaysAsDefault);
		this.datesDialog.setVisible(Boolean.TRUE);

		// Result?
		Object result = this.datesForm.getDataFieldValue("RESULT");
		if (!ParseUtilsExtended.getBoolean(result, false)) {
			return null;
		}

		Date from = (Date) this.datesForm.getDataFieldValue("INIT_DATE");
		Date to = (Date) this.datesForm.getDataFieldValue("END_DATE");

		// Validate dates
		if ((from != null) && (to != null) && to.before(from)) {
			MessageManager.getMessageManager().showMessage(this.getForm(), "W_INVALID_DATES", MessageType.WARNING, new Object[] {});
			return null;
		}
		// Past dates (ends before now) -> the check must not be activated -> Considered in service implementation
		// Future dates (starts after than now)? -> not problem? -> Must not be a problem, access will be restricted by check and dates

		return new Pair(from, to);
	}

	protected void checkForDatesForm(boolean onlyEnd, int numDaysAsDefault) {
		this.datesForm = null;
		if ((this.datesForm == null) || (this.datesDialog == null)) {
			this.datesForm = this.getForm().getFormManager().getFormCopy("formAskLicenseDates.form");
			this.datesForm.getInteractionManager().setInitialState();
			this.datesDialog = this.datesForm.putInModalDialog("ASK_FOR_DATES", this.getForm());
			this.datesDialog.setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
			this.datesDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							AbstractLicenseActionListener.this.datesForm.getButton("ACEPTAR").requestFocus();
						}
					});
				}
			});
		}

		Dimension dimension = new Dimension(300, 160);
		this.datesDialog.setSize(dimension);
		this.datesDialog.setPreferredSize(dimension);
		this.datesDialog.setLocationRelativeTo(this.getForm());

		// Clean and set initial state
		this.datesForm.deleteDataFields();
		this.datesForm.getInteractionManager().setUpdateMode();
		this.datesForm.getElementReference("INIT_DATE").setVisible(!onlyEnd);
		if (!onlyEnd) {
			this.datesForm.setDataFieldValue("INIT_DATE", new Date());
		}
		this.datesForm.setDataFieldValue("END_DATE", DateUtil.addDays(new Date(), numDaysAsDefault));
	}

	protected void reloadRecordData() {
		IDetailForm detailForm = this.getForm().getDetailComponent();
		if (detailForm != null) {
			Hashtable tableKeys = (Hashtable) ReflectionTools.getFieldValue(detailForm, "tableKeys");
			int vectorIndex = (int) ReflectionTools.getFieldValue(detailForm, "vectorIndex");
			detailForm.setKeys(tableKeys, vectorIndex);
		} else {
			Hashtable tableKeys = (Hashtable) ReflectionTools.getFieldValue(this.getForm(), "totalDataList");
			int currentIndex = (int) ReflectionTools.getFieldValue(this.getForm(), "currentIndex");
			this.getForm().updateDataFields(tableKeys, currentIndex);
		}
	}

}
