package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;

public class IMEliminarVehiculo extends IMRoot {

	private static final String	BORRAR_ASOCIACION	= "BORRAR_ASOCIACION";

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		this.managedForm.disableDataField(IMEliminarVehiculo.BORRAR_ASOCIACION);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		final ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();

		Button btn = this.managedForm.getButton("aceptar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// compruebo cual es el radiobutton que esta seleccionado
					RadioButtonDataField grb = (RadioButtonDataField) IMEliminarVehiculo.this.managedForm
							.getDataFieldReference("BAJA_VEHICULO_CONTRATO");
					if (grb != null) {
						if (grb.isSelected()) {
							int rtn = JOptionPane.showConfirmDialog(IMEliminarVehiculo.this.managedForm,
									ApplicationManager.getTranslation("M_SE_DARA_DE_BAJA_VEHICULO_DE_CONTRATO"), "", JOptionPane.YES_NO_OPTION);
							if (rtn == JOptionPane.YES_OPTION) {
								//
								try {
									final String cg_contrato = (String) IMEliminarVehiculo.this.managedForm.getDataFieldValue("CG_CONTRATO");
									if (cg_contrato == null) {
										IMEliminarVehiculo.this.managedForm.message("M_NO_EXISTE_CONTRATO", Form.ERROR_MESSAGE);
										return;
									}
									final String matricula = (String) IMEliminarVehiculo.this.managedForm.getDataFieldValue("MATRICULA");
									final String cif = (String) IMEliminarVehiculo.this.managedForm.getDataFieldValue("CIF");
									final Entity entidad = IMEliminarVehiculo.this.formManager.getReferenceLocator().getEntityReference(
											"EVehiculoCont");
									final Date d = new Date();
									OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("dando_de_baja_vehiculo")) {
										@Override
										public void run() {
											try {
												this.hasStarted = true;
												Hashtable atributosValores = new Hashtable();
												Hashtable clavesValores = new Hashtable();
												clavesValores.put("CG_CONTRATO", cg_contrato);
												clavesValores.put("MATRICULA", matricula);
												clavesValores.put("CIF", cif);
												atributosValores.put("F_BAJA", d);
												EntityResult resultado = entidad.update(atributosValores, clavesValores,
														IMEliminarVehiculo.this.formManager.getReferenceLocator().getSessionId());
												if (resultado.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
													throw new Exception();
												}
												String msg = ApplicationManager.getTranslation("M_BAJA_VEHICULO",
														IMEliminarVehiculo.this.formManager.getResourceBundle());
												IMEliminarVehiculo.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
											} catch (Exception ex) {
												IMEliminarVehiculo.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
											} finally {
												this.hasFinished = true;
											}
										}
									};
									ExtendedApplicationManager.proccessOperation(
											(JDialog) SwingUtilities.getWindowAncestor(IMEliminarVehiculo.this.managedForm), infoInforme, 50);
									IMEliminarVehiculo.this.managedForm.getJDialog().setVisible(false);
								} catch (Exception ex) {
									ex.printStackTrace();
									IMEliminarVehiculo.this.managedForm.message("M_ERROR_BAJA_VEHICULO", Form.ERROR_MESSAGE);

								}

							} else {
								IMEliminarVehiculo.this.managedForm.getJDialog().setVisible(false);
							}
						}
					}
					grb = (RadioButtonDataField) IMEliminarVehiculo.this.managedForm.getDataFieldReference("BORRAR_TODO_VEHICULO");
					if (grb != null) {
						if (grb.isSelected()) {
							// Entidad entidad = null;
							try {
								final String cg_contrato = (String) IMEliminarVehiculo.this.managedForm
										.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
								if (cg_contrato == null) {
									IMEliminarVehiculo.this.managedForm.message("M_NO_EXISTE_CONTRATO", Form.ERROR_MESSAGE);
									return;
								}
								final String matricula = (String) IMEliminarVehiculo.this.managedForm
										.getDataFieldValue(OpentachFieldNames.MATRICULA_FIELD);
								final String cif = (String) IMEliminarVehiculo.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);

								final Entity entidad = buscador.getEntityReference("EVehiculosEmp");
								int rtn = JOptionPane.showConfirmDialog(IMEliminarVehiculo.this.managedForm,
										ApplicationManager.getTranslation("M_SE_ELIMINARA_DATOS_VEHICULO"), "", JOptionPane.YES_NO_OPTION);
								if (rtn == JOptionPane.YES_OPTION) {
									OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Eliminando_vehiculo")) {
										@Override
										public void run() {
											try {
												this.hasStarted = true;
												Hashtable clavesValores = new Hashtable();
												clavesValores.put(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato);
												clavesValores.put(OpentachFieldNames.MATRICULA_FIELD, matricula);
												clavesValores.put(OpentachFieldNames.CIF_FIELD, cif);
												EntityResult resultado = entidad.delete(clavesValores, IMEliminarVehiculo.this.formManager
														.getReferenceLocator().getSessionId());
												if (resultado.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
													throw new Exception();
												}
												String msg = ApplicationManager.getTranslation("M_BORRAR_VEHICULO",
														IMEliminarVehiculo.this.formManager.getResourceBundle());
												IMEliminarVehiculo.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
											} catch (Exception ex) {
												IMEliminarVehiculo.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
											} finally {
												this.hasFinished = true;
											}
										}
									};
									ExtendedApplicationManager.proccessOperation(
											(JDialog) SwingUtilities.getWindowAncestor(IMEliminarVehiculo.this.managedForm), infoInforme, 50);
								}
								IMEliminarVehiculo.this.managedForm.getJDialog().setVisible(false);

							} catch (Exception ex) {
								ex.printStackTrace();
								IMEliminarVehiculo.this.managedForm.message("M_ERROR_BORRAR_CONDUCTOR", Form.ERROR_MESSAGE);
							}
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
					IMEliminarVehiculo.this.managedForm.getJDialog().setVisible(false);
				}
			});
		}
	}

}