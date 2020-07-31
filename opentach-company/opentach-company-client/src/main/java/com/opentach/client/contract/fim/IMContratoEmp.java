package com.opentach.client.contract.fim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;
import com.ontimize.jee.common.tools.DateTools;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.util.UserTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.tools.exception.UException;

public class IMContratoEmp extends UBasicFIM {
	// TODO intentar arreglar esta clase

	private static final Logger		logger				= LoggerFactory.getLogger(IMContratoEmp.class);

	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private final DataField			cmpCIF				= null;
	@FormComponent(attr = "EConductorCont")
	private final Table				tbConductores		= null;
	@FormComponent(attr = OpentachFieldNames.CG_CONTRATO_FIELD)
	private final DataField			cgContrato			= null;

	private ActionListener			alDeleteConductor	= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.setStayInRecordAfterInsert(true);

		this.replaceInsertListener();
		this.replaceDeleteListener();
		this.replaceUpdateListener();

		this.overrideDriversDeleteButton();
	}

	private void replaceInsertListener() {
		this.removeInsertListener();
		this.insertListener = new UInsertListener() {
			@Override
			protected void postCorrectInsert(EntityResult result, Entity entity) throws UException {
				super.postCorrectInsert(result, entity);
				try {
					IUserData user = ((AbstractOpentachClientLocator) IMContratoEmp.this.formManager.getReferenceLocator()).getUserData();
					user.addActiveContract((String) IMContratoEmp.this.cmpCIF.getValue(), (String) IMContratoEmp.this.managedForm.getDataFieldValue("CG_CONTRATO"));
				} catch (Exception err) {
					throw new UException(err);
				}
			}

		};
		Button bInsert = this.managedForm.getButton(InteractionManager.INSERT_KEY);
		if (bInsert != null) {
			bInsert.addActionListener(this.insertListener);
		}
	}

	private void replaceUpdateListener() {
		this.removeUpdateListener();
		this.updateListener = new UpdateListener() {
			@Override
			protected void postCorrectUpdate(EntityResult result, Entity entity) throws Exception {
				super.postCorrectUpdate(result, entity);
				try {
					((AbstractOpentachClientLocator) IMContratoEmp.this.formManager.getReferenceLocator()).reloadUserData();
				} catch (Exception ex) {
					IMContratoEmp.logger.error(null, ex);
				}
			}
		};
		Button updateButton = this.managedForm.getButton(InteractionManager.UPDATE_KEY);
		if (updateButton != null) {
			updateButton.addActionListener(this.updateListener);
		}

	}

	private void replaceDeleteListener() {
		this.removeDeleteListener();
		this.deleteListener = new DeleteListener() {
			private Form	fEliminar	= null;
			private JDialog	jEliminar	= null;

			@Override
			public void actionPerformed(ActionEvent evento) {
				if (this.jEliminar == null) {
					this.fEliminar = IMContratoEmp.this.formManager.getFormCopy("formEliminarContrato.xml");
					this.fEliminar.getInteractionManager().setInitialState();
					this.jEliminar = this.fEliminar.putInModalDialog(ApplicationManager.getTranslation("ELIMINAR_CONTRATO"), IMContratoEmp.this.managedForm);
					this.jEliminar.setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
				}
				this.fEliminar.setDataFieldValue(OpentachFieldNames.CIF_FIELD, IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
				this.fEliminar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
						IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
				this.jEliminar.setVisible(true);
				IMContratoEmp.this.managedForm.refreshCurrentDataRecord();
			}
		};
		this.managedForm.getButton(InteractionManager.DELETE_KEY).addActionListener(this.deleteListener);

	}

	private void overrideDriversDeleteButton() {
		if ((this.tbConductores != null)) {
			TableButton b = (TableButton) this.tbConductores.getTableComponentReference("deletebutton");
			ActionListener[] alarray = b.getListeners(ActionListener.class);
			ActionListener alDelete = null;
			if (alarray.length > 0) {
				this.alDeleteConductor = alarray[0]; // solo tiene un listener
				b.removeActionListener(alDelete);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IMContratoEmp.this.alDeleteConductor.actionPerformed(e);
						IMContratoEmp.this.tbConductores.refresh();
					}
				});
			}
		}

	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();

		this.cmpCIF.setEnabled(true);
		if (UserTools.isAgente()) {
			this.managedForm.setDataFieldValue("F_CONTRATO", new Date());
			this.managedForm.setDataFieldValue("FECINI", DateTools.addDays(new Date(), -365 * 2));
			this.managedForm.setDataFieldValue("FECFIN", DateTools.addDays(new Date(), 7));
			this.setDataFieldsEnable(false, "F_CONTRATO", "FECINI", "FECFIN");
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.disableDataField(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.managedForm.disableDataField(OpentachFieldNames.NUMREQ_FIELD);
		this.cmpCIF.setEnabled(false);
	}
}
