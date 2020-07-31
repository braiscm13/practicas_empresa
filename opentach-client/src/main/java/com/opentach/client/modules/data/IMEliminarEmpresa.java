package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;

public class IMEliminarEmpresa extends IMRoot {

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButton("cancelar");
		this.managedForm.enableDataFields();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		CheckDataField grb = (CheckDataField) this.managedForm.getDataFieldReference("BORRAR_EMPRESA");
		if (grb != null) {
			grb.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent v) {
					CheckDataField grb = (CheckDataField) IMEliminarEmpresa.this.managedForm.getDataFieldReference("BORRAR_EMPRESA");
					if (grb != null) {
						if (grb.isSelected()) {
							IMEliminarEmpresa.this.managedForm.enableButton("aceptar");
						} else {
							IMEliminarEmpresa.this.managedForm.disableButton("aceptar");
						}
					}
				}
			});
		}
		Button btn = this.managedForm.getButton("aceptar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CheckDataField grb = (CheckDataField) IMEliminarEmpresa.this.managedForm.getDataFieldReference("BORRAR_EMPRESA");
					if (grb != null) {
						if (grb.isSelected()) {
							// int rtn =
							// JOptionPane.showConfirmDialog(formularioGestionado,
							// "Se dará de baja la empresa con todos sus datos ¿Desea continuar?",
							// "", JOptionPane.YES_NO_OPTION);
							// if (rtn == JOptionPane.YES_OPTION) {

							try {
								final String cif = (String) IMEliminarEmpresa.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);
								final Entity entidad = IMEliminarEmpresa.this.formManager.getReferenceLocator().getEntityReference(
										IMEliminarEmpresa.this.managedForm.getEntityName());
								OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Dando_de_baja_contrato")) {
									@Override
									public void run() {
										try {
											this.hasStarted = true;
											Hashtable atributosValores = new Hashtable(1);
											atributosValores.put(OpentachFieldNames.CIF_FIELD, cif);
											EntityResult resultado = entidad.delete(atributosValores, IMEliminarEmpresa.this.formManager
													.getReferenceLocator().getSessionId());
											if ((resultado != null) && (resultado.getCode() != EntityResult.OPERATION_SUCCESSFUL)) {
												throw new Exception(resultado.getMessage());
											} else {
												String msg = ApplicationManager.getTranslation("M_BAJA_EMPRESA",
														IMEliminarEmpresa.this.formManager.getResourceBundle());
												IMEliminarEmpresa.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
											}
											this.hasFinished = true;

										} catch (Exception ex) {
											IMEliminarEmpresa.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
										}
									}
								};
								ExtendedApplicationManager.proccessOperation(
										(JDialog) SwingUtilities.getWindowAncestor(IMEliminarEmpresa.this.managedForm), infoInforme, 50);
								IMEliminarEmpresa.this.managedForm.getJDialog().setVisible(false);
							} catch (Exception ex) {
								ex.printStackTrace();
								IMEliminarEmpresa.this.managedForm.message("M_ERROR_BAJA_EMPRESA", Form.ERROR_MESSAGE);
							}
							// }
						}
					}
				}
			});
		}
		btn = this.managedForm.getButton("cancelar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMEliminarEmpresa.this.managedForm.getJDialog().setVisible(false);
				}
			});
		}
	}

}