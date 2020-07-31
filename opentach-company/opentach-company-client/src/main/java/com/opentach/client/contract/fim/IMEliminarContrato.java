package com.opentach.client.contract.fim;

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
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.companies.IContractService;

public class IMEliminarContrato extends IMRoot {

	private static final String		BORRAR_ASOCIACION		= "BORRAR_ASOCIACION";
	private RadioButtonDataField	crBorrarFicheros		= null;
	private RadioButtonDataField	crBajaContrato			= null;
	private RadioButtonDataField	crBorrarTodo			= null;
	private RadioButtonDataField	crReprocesarContrato	= null;
	private ReferenceLocator		buscador				= null;

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		this.managedForm.disableDataField(IMEliminarContrato.BORRAR_ASOCIACION);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.crBorrarFicheros = (RadioButtonDataField) this.managedForm.getDataFieldReference("BORRAR_FICHEROS");
		this.crBajaContrato = (RadioButtonDataField) this.managedForm.getDataFieldReference("BAJA_CONTRATO");
		this.crBorrarTodo = (RadioButtonDataField) this.managedForm.getDataFieldReference("BORRAR_TODO");
		this.crReprocesarContrato = (RadioButtonDataField) this.managedForm.getDataFieldReference("REPROCESAR_CONTRATO");
		this.buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
		// activar-desactivar el campo check
		if (this.crBorrarFicheros != null) {
			this.crBorrarFicheros.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if (IMEliminarContrato.this.crBorrarFicheros.isSelected()) {
						IMEliminarContrato.this.managedForm.enableDataField(IMEliminarContrato.BORRAR_ASOCIACION);
					} else {
						IMEliminarContrato.this.managedForm.disableDataField(IMEliminarContrato.BORRAR_ASOCIACION);
					}
				}
			});
		}
		Button btn = this.managedForm.getButton("aceptar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (IMEliminarContrato.this.crBajaContrato != null) {
						if (IMEliminarContrato.this.crBajaContrato.isSelected()) {
							int rtn = JOptionPane.showConfirmDialog(IMEliminarContrato.this.managedForm,
									ApplicationManager.getTranslation("se_limpiara_contrato_seleccionado_mantener"), "", JOptionPane.YES_NO_OPTION);
							if (rtn == JOptionPane.YES_OPTION) {
								try {
									final String cg_contrato = (String) IMEliminarContrato.this.cgContract.getValue();
									final Entity entidad = IMEliminarContrato.this.formManager.getReferenceLocator().getEntityReference(
											IMEliminarContrato.this.managedForm.getEntityName());
									final Date d = new Date();
									OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Dando_de_baja_contrato")) {
										@Override
										public void run() {
											try {
												this.hasStarted = true;
												Hashtable atributosValores = new Hashtable();
												Hashtable clavesValores = new Hashtable();
												atributosValores.put("F_BAJA", d);
												clavesValores.put(OpentachFieldNames.NUMREQ_FIELD, cg_contrato);
												EntityResult resultado = entidad.update(atributosValores, clavesValores,
														IMEliminarContrato.this.formManager.getReferenceLocator().getSessionId());
												if (resultado.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
													throw new Exception();
												} else {
													String msg = ApplicationManager.getTranslation("M_BAJA_CONTRATO",
															IMEliminarContrato.this.formManager.getResourceBundle());
													IMEliminarContrato.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
												}
												this.hasFinished = true;

											} catch (Exception ex) {
												IMEliminarContrato.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
											}
										}
									};
									ExtendedApplicationManager.proccessOperation(
											(JDialog) SwingUtilities.getWindowAncestor(IMEliminarContrato.this.managedForm), infoInforme, 50);
									IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
								} catch (Exception ex) {
									ex.printStackTrace();
									IMEliminarContrato.this.managedForm.message("M_ERROR_BAJA_CONTRATO", Form.ERROR_MESSAGE);
								}
							} else {
								IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
							}
						}
					}
					if (IMEliminarContrato.this.crBorrarTodo != null) {
						if (IMEliminarContrato.this.crBorrarTodo.isSelected()) {
							try {
								final String cg_contrato = (String) IMEliminarContrato.this.cgContract.getValue();
								int rtn = JOptionPane.showConfirmDialog(IMEliminarContrato.this.managedForm,
										ApplicationManager.getTranslation("se_limpiara_contrato_seleccionado_datos"), "", JOptionPane.YES_NO_OPTION);
								if (rtn == JOptionPane.YES_OPTION) {
									OperationThread infoInforme = new OperationThread("Eliminando_contrato") {
										@Override
										public void run() {
											try {
												this.hasStarted = true;
												IContractService contractService = ((AbstractOpentachClientLocator) IMEliminarContrato.this
														.getReferenceLocator()).getRemoteService(IContractService.class);
												contractService.eliminarContrato(cg_contrato,
														IMEliminarContrato.this.buscador.getSessionId());
												this.hasFinished = true;
												String msg = ApplicationManager.getTranslation("M_ELIMINAR_CONTRATO",
														IMEliminarContrato.this.formManager.getResourceBundle());
												IMEliminarContrato.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);

											} catch (Exception ex) {
												if (ex.getMessage()==null){
													IMEliminarContrato.this.managedForm.message("ERROR_BORRANDO_CONTRATO", Form.ERROR_MESSAGE);
												} else {
													IMEliminarContrato.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
												}
											}
										}
									};
									ExtendedApplicationManager.proccessOperation(
											(JDialog) SwingUtilities.getWindowAncestor(IMEliminarContrato.this.managedForm), infoInforme, 50);
								}
								IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
							} catch (Exception ex) {
								ex.printStackTrace();
								IMEliminarContrato.this.managedForm.message("M_ERROR_BORRAR_CONTRATO", Form.ERROR_MESSAGE);
							}
						}
					}
					if (IMEliminarContrato.this.crBorrarFicheros != null) {
						if (IMEliminarContrato.this.crBorrarFicheros.isSelected()) {
							CheckDataField c = (CheckDataField) IMEliminarContrato.this.managedForm
									.getDataFieldReference(IMEliminarContrato.BORRAR_ASOCIACION);
							if ((c == null) || c.isSelected()) {
								// if delete match file-contract
								try {
									int rtn = JOptionPane.showConfirmDialog(IMEliminarContrato.this.managedForm,
											ApplicationManager.getTranslation("se_limpiara_contrato_seleccionado_fich"), "",
											JOptionPane.YES_NO_OPTION);
									if (rtn == JOptionPane.YES_OPTION) {
										final String cg_contrato = (String) IMEliminarContrato.this.cgContract.getValue();
										OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Limpiando_contrato")) {
											@Override
											public void run() {
												try {
													this.hasStarted = true;
													IContractService contractService = ((AbstractOpentachClientLocator) IMEliminarContrato.this
															.getReferenceLocator()).getRemoteService(IContractService.class);
													contractService.limpiarContrato(cg_contrato,
															IMEliminarContrato.this.buscador.getSessionId());
													this.hasFinished = true;
													String msg = ApplicationManager.getTranslation("M_LIMPIAR_CONTRATO",
															IMEliminarContrato.this.formManager.getResourceBundle());
													IMEliminarContrato.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
												} catch (Exception ex) {
													IMEliminarContrato.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
												}
											}
										};
										ExtendedApplicationManager.proccessOperation(
												(JDialog) SwingUtilities.getWindowAncestor(IMEliminarContrato.this.managedForm), infoInforme, 50);
										// infoInforme.run();

									}
									IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
								} catch (Exception ex) {
									ex.printStackTrace();
									IMEliminarContrato.this.managedForm.message("M_ERROR_LIMPIAR_CONTRATO", Form.ERROR_MESSAGE);
								}
							} else {
								try {
									int rtn = JOptionPane.showConfirmDialog(IMEliminarContrato.this.managedForm,
											ApplicationManager.getTranslation("se_limpiara_contrato_seleccionado"), "", JOptionPane.YES_NO_OPTION);
									if (rtn == JOptionPane.YES_OPTION) {
										final String cg_contrato = (String) IMEliminarContrato.this.cgContract.getValue();
										OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Limpiando_contrato")) {
											@Override
											public void run() {
												try {
													this.hasStarted = true;
													IContractService contractService = ((AbstractOpentachClientLocator) IMEliminarContrato.this
															.getReferenceLocator()).getRemoteService(IContractService.class);
													contractService.limpiarDatos(cg_contrato,
															IMEliminarContrato.this.buscador.getSessionId());
													this.hasFinished = true;
													String msg = ApplicationManager.getTranslation("M_LIMPIAR_CONTRATO",
															IMEliminarContrato.this.formManager.getResourceBundle());
													IMEliminarContrato.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
												} catch (Exception ex) {
													IMEliminarContrato.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
												}
											}
										};
										ExtendedApplicationManager.proccessOperation(
												(JDialog) SwingUtilities.getWindowAncestor(IMEliminarContrato.this.managedForm), infoInforme, 50);
										// infoInforme.run();
									}
									IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
								} catch (Exception ex) {
									ex.printStackTrace();
									IMEliminarContrato.this.managedForm.message("M_ERROR_LIMPIAR_CONTRATO", Form.ERROR_MESSAGE);
								}
							}
						}
					}
					if ((IMEliminarContrato.this.crReprocesarContrato != null) && IMEliminarContrato.this.crReprocesarContrato.isSelected()) {
						try {
							int rtn = JOptionPane.showConfirmDialog(IMEliminarContrato.this.managedForm,
									ApplicationManager.getTranslation("Se_limpiaran_los_datos_fichero_y_se_reprocesara_ficheros"), "",
									JOptionPane.YES_NO_OPTION);
							if (rtn == JOptionPane.YES_OPTION) {
								OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Reprocesando_contrato")) {
									@Override
									public void run() {
										try {
											this.hasStarted = true;
											String cg_contrato = (String) IMEliminarContrato.this.cgContract.getValue();
											if (cg_contrato == null) {
												IMEliminarContrato.this.managedForm.message("M_NO_EXISTE_CONTRATO_ACTIVO", Form.ERROR_MESSAGE);
												this.hasFinished = true;
												return;
											}
											IContractService contractService = ((AbstractOpentachClientLocator) IMEliminarContrato.this.getReferenceLocator())
													.getRemoteService(IContractService.class);
											contractService.reprocesarContrato(cg_contrato,
													IMEliminarContrato.this.buscador.getSessionId());
											this.hasFinished = true;
											String msg = ApplicationManager.getTranslation("M_REPROCESAR_CONTRATO",
													IMEliminarContrato.this.formManager.getResourceBundle());
											IMEliminarContrato.this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
										} catch (Exception ex) {
											IMEliminarContrato.this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
										}
									}
								};
								ExtendedApplicationManager.proccessOperation(
										(JDialog) SwingUtilities.getWindowAncestor(IMEliminarContrato.this.managedForm), infoInforme, 50);
								// infoInforme.run();
							}
							IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
						} catch (Exception ex) {
							ex.printStackTrace();
							IMEliminarContrato.this.managedForm.message("M_ERROR_REPROCESAR_CONTRATO", Form.ERROR_MESSAGE);
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
					IMEliminarContrato.this.managedForm.getJDialog().setVisible(false);
				}
			});
		}
	}

}
