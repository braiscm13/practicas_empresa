package com.opentach.client.employee.listeners;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ParameterException;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UFormHeaderButton;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.gui.tasks.WorkerStatusInfo;

public class IMEmpleadoDeleteConfirmActionListener extends AbstractActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger		logger	= LoggerFactory.getLogger(IMEmpleadoDeleteConfirmActionListener.class);

	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private DataField				cifField;
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	private DataField				idConductorField;
	@FormComponent(attr = OpentachFieldNames.CG_CONTRATO_FIELD)
	private DataField				cgContratoField;

	@FormComponent(attr = "BAJA_CONDUCTOR_CONTRATO")
	protected RadioButtonDataField	opcionBajaContrato;
	@FormComponent(attr = "BORRAR_TODO_CONDUCTOR")
	protected RadioButtonDataField	opcionBorrarEmpleado;

	public IMEmpleadoDeleteConfirmActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmpleadoDeleteConfirmActionListener(UFormHeaderButton button, Hashtable params) throws Exception {
		super(button, button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object cg_contrato = this.cgContratoField.getValue();
		if (cg_contrato == null) {
			this.getForm().message("M_NO_EXISTE_CONTRATO", Form.ERROR_MESSAGE);
			return;
		}

		Hashtable<String, Object> keys = new Hashtable<String, Object>();
		MapTools.safePut(keys, OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato);
		MapTools.safePut(keys, OpentachFieldNames.CIF_FIELD, this.cifField.getValue());
		MapTools.safePut(keys, OpentachFieldNames.IDCONDUCTOR_FIELD, this.idConductorField.getValue());

		if ((this.opcionBajaContrato != null) && this.opcionBajaContrato.isSelected()) {
			if (this.askCofirm("M_SE_DARA_DE_BAJA_CONDUCTOR_DE_CONTRATO")) {
				this.doBajaContrato(keys);
			}
		} else if ((this.opcionBorrarEmpleado != null) && this.opcionBorrarEmpleado.isSelected()) {
			if (this.askCofirm("M_SE_DARA_DE_BAJA_DATOS_CONDUCTOR")) {
				this.doBajaEmpleadoCompleto(keys);
			}
		} else {
			return;
		}
	}

	protected boolean askCofirm(String question) {
		int rtn = JOptionPane.showConfirmDialog(this.getForm(), ApplicationManager.getTranslation(question), "", JOptionPane.YES_NO_OPTION);
		return (rtn == JOptionPane.YES_OPTION);
	}

	protected void doBajaContrato(Hashtable<String, Object> keys) {
		new DeleteEmployeeUSwingWorker(this.getForm(), "dando_de_baja_conductor", "M_BAJA_CONDUCTOR", "M_ERROR_BAJA_CONDUCTOR") {
			@Override
			protected EntityResult doWork() throws Exception {
				Entity entidad = IMEmpleadoDeleteConfirmActionListener.this.getEntity("EConductorCont");
				Hashtable<Object, Object> atributosValores = EntityResultTools.keysvalues("F_BAJA", new Date());
				return entidad.update(atributosValores, keys, IMEmpleadoDeleteConfirmActionListener.this.getSessionId());
			}
		}.executeOperation(this.getForm(), false);
	}

	protected void doBajaEmpleadoCompleto(Hashtable<String, Object> keys) {
		new DeleteEmployeeUSwingWorker(this.getForm(), "Eliminando_conductor", "M_BORRAR_CONDUCTOR", "M_ERROR_BORRAR_CONDUCTOR") {
			@Override
			protected EntityResult doWork() throws Exception {
				Entity entidad = IMEmpleadoDeleteConfirmActionListener.this.getEntity("EConductoresEmp");
				return entidad.delete(keys, IMEmpleadoDeleteConfirmActionListener.this.getSessionId());
			}
		}.executeOperation(this.getForm(), false);
	}

	@Override
	protected boolean getEnableValueToSet() {
		return true;
	}

	private static abstract class DeleteEmployeeUSwingWorker extends USwingWorker<EntityResult, Void> {

		protected Form		form;
		protected String	title;
		protected String	okMessage;
		protected String	koMessage;

		public DeleteEmployeeUSwingWorker(Form parent, String title, String okMessage, String koMessage) {
			this.form = parent;
			this.title = title;
			this.okMessage = okMessage;
			this.koMessage = koMessage;
		}

		@Override
		protected EntityResult doInBackground() throws Exception {
			this.fireStatusUpdate(new WorkerStatusInfo(this.title, null, null));
			return this.doWork();
		}

		protected abstract EntityResult doWork() throws Exception;

		@Override
		protected void done() {
			super.done();
			try {
				EntityResult res = this.uget();
				if (res.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
					throw new ParameterException(res.getMessage(), res.getMessageParameter());
				} else {
					String msg = ApplicationManager.getTranslation(this.okMessage);
					MessageManager.getMessageManager().showMessage(this.form, msg, MessageType.INFORMATION, false);
				}
			} catch (Exception err) {
				MessageManager.getMessageManager().showExceptionMessage(err, this.form, IMEmpleadoDeleteConfirmActionListener.logger, this.koMessage);
			} finally {
				// Finally close dialog
				this.form.getJDialog().setVisible(false);
			}
		}
	}
}
