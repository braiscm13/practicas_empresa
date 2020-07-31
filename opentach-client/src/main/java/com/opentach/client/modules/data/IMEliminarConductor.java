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

public class IMEliminarConductor extends IMRoot {

	private static final String	BORRAR_ASOCIACION	= "BORRAR_ASOCIACION";

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		this.managedForm.disableDataField(IMEliminarConductor.BORRAR_ASOCIACION);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		final ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
		Button btn = f.getButton("aceptar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// compruebo cual es el radiobutton que esta seleccionado
					RadioButtonDataField grb = (RadioButtonDataField) IMEliminarConductor.this.managedForm
							.getDataFieldReference("BAJA_CONDUCTOR_CONTRATO");
					if (grb != null) {
						if (grb.isSelected()) {
							int rtn = JOptionPane.showConfirmDialog(IMEliminarConductor.this.managedForm,
									ApplicationManager.getTranslation("M_SE_DARA_DE_BAJA_CONDUCTOR_DE_CONTRATO"), "", JOptionPane.YES_NO_OPTION);
							if (rtn == JOptionPane.YES_OPTION) {
								try {
									final String cg_contrato = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("CG_CONTRATO");
									if (cg_contrato == null) {
										IMEliminarConductor.this.managedForm.message("M_NO_EXISTE_CONTRATO", Form.ERROR_MESSAGE);
										return;
									}

									/*TODO: IDCONDUCTOR-DNI */
									final String dni = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("IDCONDUCTOR");


									//									final String dni = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("DNI");
									final String cif = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("CIF");
									final Entity entidad = IMEliminarConductor.this.formManager.getReferenceLocator().getEntityReference(
											"EConductorCont");
									final Date d = new Date();
									OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("dando_de_baja_conductor")) {
										@Override
										public void run() {
											try {
												this.hasStarted = true;
												Hashtable<String, Object> atributosValores = new Hashtable<String, Object>();
												Hashtable<String, Object> clavesValores = new Hashtable<String, Object>();
												clavesValores.put(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato);

												/*TODO: IDCONDUCTOR-DNI */
												clavesValores.put(OpentachFieldNames.IDCONDUCTOR_FIELD, dni);

												//												clavesValores.put(OpentachFieldNames.DNI_FIELD, dni);
												clavesValores.put(OpentachFieldNames.CIF_FIELD, cif);
												atributosValores.put("F_BAJA", d);
												EntityResult resultado = entidad.update(atributosValores, clavesValores,
														IMEliminarConductor.this.formManager.getReferenceLocator().getSessionId());
												if (resultado.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
													throw new Exception();
												} else {
													String msg = ApplicationManager.getTranslation("M_BAJA_CONDUCTOR",
															IMEliminarConductor.this.formManager.getResourceBundle());
													IMEliminarConductor.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
												}
												this.hasFinished = true;
											} catch (Exception ex) {
												IMEliminarConductor.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
											}
										}
									};
									ExtendedApplicationManager.proccessOperation(
											(JDialog) SwingUtilities.getWindowAncestor(IMEliminarConductor.this.managedForm), infoInforme, 50);
									IMEliminarConductor.this.managedForm.getJDialog().setVisible(false);
								} catch (Exception ex) {
									ex.printStackTrace();
									IMEliminarConductor.this.managedForm.message("M_ERROR_BAJA_CONDUCTOR", Form.ERROR_MESSAGE);
								}
							} else {
								IMEliminarConductor.this.managedForm.getJDialog().setVisible(false);
							}
						}
					}
					grb = (RadioButtonDataField) IMEliminarConductor.this.managedForm.getDataFieldReference("BORRAR_TODO_CONDUCTOR");
					if (grb != null) {
						if (grb.isSelected()) {
							// Entidad entidad = null;
							try {
								final String cg_contrato = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("CG_CONTRATO");
								if (cg_contrato == null) {
									IMEliminarConductor.this.managedForm.message("M_NO_EXISTE_CONTRATO", Form.ERROR_MESSAGE);
									return;
								}

								/*TODO: IDCONDUCTOR-DNI */
								final String dni = (String) IMEliminarConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
								//								final String dni = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("DNI");
								final String cif = (String) IMEliminarConductor.this.managedForm.getDataFieldValue("CIF");
								final Entity entidad = buscador.getEntityReference("EConductoresEmp");
								int rtn = JOptionPane.showConfirmDialog(IMEliminarConductor.this.managedForm,
										ApplicationManager.getTranslation("M_SE_DARA_DE_BAJA_DATOS_CONDUCTOR"), "", JOptionPane.YES_NO_OPTION);
								if (rtn == JOptionPane.YES_OPTION) {
									OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Eliminando_conductor")) {
										@Override
										public void run() {
											try {
												this.hasStarted = true;
												Hashtable<String,Object> clavesValores = new Hashtable<String,Object>();
												clavesValores.put(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato);


												/*TODO: IDCONDUCTOR-DNI */
												clavesValores.put(OpentachFieldNames.IDCONDUCTOR_FIELD, dni);

												//												clavesValores.put(OpentachFieldNames.DNI_FIELD, dni);
												clavesValores.put(OpentachFieldNames.CIF_FIELD, cif);
												EntityResult resultado = entidad.delete(clavesValores, IMEliminarConductor.this.formManager
														.getReferenceLocator().getSessionId());
												if (resultado.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
													throw new Exception();
												} else {
													String msg = ApplicationManager.getTranslation("M_BORRAR_CONDUCTOR",
															IMEliminarConductor.this.formManager.getResourceBundle());
													IMEliminarConductor.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
												}
											} catch (Exception ex) {
												IMEliminarConductor.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
											} finally {
												this.hasFinished = true;
											}
										}
									};
									ExtendedApplicationManager.proccessOperation(
											(JDialog) SwingUtilities.getWindowAncestor(IMEliminarConductor.this.managedForm), infoInforme, 50);
								}
								IMEliminarConductor.this.managedForm.getJDialog().setVisible(false);
							} catch (Exception ex) {
								ex.printStackTrace();
								IMEliminarConductor.this.managedForm.message("M_ERROR_BORRAR_CONDUCTOR", Form.ERROR_MESSAGE);
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
					IMEliminarConductor.this.managedForm.getJDialog().setVisible(false);
				}
			});
		}
	}

}