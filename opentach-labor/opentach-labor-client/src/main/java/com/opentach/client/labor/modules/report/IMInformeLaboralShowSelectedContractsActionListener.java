package com.opentach.client.labor.modules.report;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.field.CampoComboReferenciaDespl;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMInformeLaboralShowSelectedContractsActionListener extends AbstractActionListenerButton {

	private static final Logger			logger	= LoggerFactory.getLogger(IMInformeLaboralShowSelectedContractsActionListener.class);

	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	private CampoComboReferenciaDespl	idConductorField;
	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private UReferenceDataField		cifField;
	private Form						detailForm;

	public IMInformeLaboralShowSelectedContractsActionListener() throws Exception {
		super();
	}

	public IMInformeLaboralShowSelectedContractsActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMInformeLaboralShowSelectedContractsActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMInformeLaboralShowSelectedContractsActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	private void ensureForm() {
		if (this.detailForm == null) {
			this.detailForm = this.getFormManager().getFormCopy("formInformeLaboralContracts.xml");
			this.detailForm.putInModalDialog(ApplicationManager.getTranslation("EDriverContracts"), this.getForm());
			this.detailForm.getInteractionManager().setQueryMode();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.idConductorField.isEmpty()) {
			MessageManager.getMessageManager().showMessage(this.getForm(), ApplicationManager.getTranslation("CHOOSE_DRIVER"), MessageType.INFORMATION, false);
			return;
		}

		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				IMInformeLaboralShowSelectedContractsActionListener.this.ensureForm();
				IMInformeLaboralShowSelectedContractsActionListener.this.detailForm.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD,
						IMInformeLaboralShowSelectedContractsActionListener.this.idConductorField.getValue());
				IMInformeLaboralShowSelectedContractsActionListener.this.detailForm.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
						IMInformeLaboralShowSelectedContractsActionListener.this.cifField.getValue());
				((Table) IMInformeLaboralShowSelectedContractsActionListener.this.detailForm.getElementReference("EDriverContracts")).refreshInThread(0);
				IMInformeLaboralShowSelectedContractsActionListener.this.detailForm.enableDataFields(false);
				return null;
			}

			@Override
			protected void done() {
				IMInformeLaboralShowSelectedContractsActionListener.this.detailForm.getJDialog().setVisible(true);
			}
		}.executeOperation(this.getForm());

	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		this.getButton().setEnabled(true);
	}
}
