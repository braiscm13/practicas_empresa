package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.button.QueryButton;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.document.CIFDocument;
import com.ontimize.gui.field.document.NIFDocument;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;
import com.ontimize.gui.tree.Tree;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.client.util.BIDocument;
import com.opentach.client.util.UserTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.user.IUserData;

public class IMContratoEmp extends OpentachBasicInteractionManager {
	// TODO intentar arreglar esta clase

	private static final Logger	logger				= LoggerFactory.getLogger(IMContratoEmp.class);

	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private final DataField		cmpCIF				= null;
	@FormComponent(attr = "NOMB")
	private final DataField		cmpNombre			= null;
	@FormComponent(attr = "buscarCIF")
	private final QueryButton	btncCIF				= null;
	@FormComponent(attr = "cifnif")
	private final Button		btnChkCIFNIF		= null;
	@FormComponent(attr = "EConductorCont")
	private final Table			tbConductores		= null;
	@FormComponent(attr = "EVehiculoCont")
	private final Table			tbVehiculos			= null;
	@FormComponent(attr = "CHACTIVO")
	private final Button		chActivo			= null;
	@FormComponent(attr = OpentachFieldNames.CG_CONTRATO_FIELD)
	private final DataField		cgContrato			= null;

	private ActionListener		alDeleteConductor	= null;
	private ActionListener		alDeleteVehiculo	= null;
	private Form				fInsertar			= null;
	private JDialog				jInsertar			= null;
	private Form				fInsertarV			= null;
	private JDialog				jInsertarV			= null;
	private Form				fEliminar			= null;
	private JDialog				jEliminar			= null;

	// al actualizar, eliminar, insertar, debería recargar la configuración de
	// DatosUsuario

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.stayInRecordAfterInsert = true;

		this.replaceInsertListener();
		this.replaceDeleteListener();
		this.replaceUpdateListener();
		this.addAddButtonToDriversTable();
		this.addAddButtonToVehiclesTable();
		this.installActiveContractSelectorListener();
		this.overrideDriversDeleteButton();
		this.installCifListeners();

		if (this.cmpNombre != null) {
			this.cmpNombre.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent valueEvent) {
					try {
						if (valueEvent.getType() == ValueEvent.USER_CHANGE) {
							String val = (String) valueEvent.getNewValue();
							if (((val == null) || (val.length() == 0)) && (IMContratoEmp.this.cmpCIF != null)) {
								// Borro nombre de la empresa.
								IMContratoEmp.this.cmpCIF.setValue(null);
							}
						}
						IMContratoEmp.this.activarBotonCIF();
					} catch (Exception ex) {
						System.err.println("" + ex.getMessage());
					}
				}
			});
		}
	}

	private void installCifListeners() {
		if (this.cmpCIF != null) {
			this.cmpCIF.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					try {
						String val = (String) IMContratoEmp.this.cmpCIF.getValue();
						// Realizo la consulta cuando se hallan insertado al
						// menos 4 digitos.
						if ((val != null) && (val.length() > 4) && (val.substring(0, 4).indexOf('*') == -1)) {
							if ((val.length() < 9) && (val.indexOf('*') == -1)) {
								val += "*";
							}
							ReferenceLocator b = (ReferenceLocator) IMContratoEmp.this.formManager.getReferenceLocator();
							Entity entidad = b.getEntityReference(CompanyNaming.ENTITY);
							if (entidad != null) {
								Hashtable<String, Object> cv = new Hashtable<String, Object>();
								cv.put(OpentachFieldNames.CIF_FIELD, val);
								EntityResult resultado = null;
								resultado = entidad.query(cv, new Vector(0), b.getSessionId());
								if (resultado.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
									if (resultado.calculateRecordNumber() == 1) {
										IMContratoEmp.this.managedForm.setDataFieldValue("NOMB", resultado.getRecordValues(0).get("NOMB"));
										IMContratoEmp.this.managedForm.setDataFieldValue(OpentachFieldNames.CIF_FIELD, resultado.getRecordValues(0)
												.get(OpentachFieldNames.CIF_FIELD));
									} else if ((IMContratoEmp.this.btncCIF != null) && (resultado.calculateRecordNumber() > 0)) {
										IMContratoEmp.this.btncCIF.doClick();
									}
								}
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			this.cmpCIF.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent valueEvent) {
					try {
						if (valueEvent.getType() == ValueEvent.USER_CHANGE) {
							String val = (String) valueEvent.getNewValue();

							if (((val == null) || (val.length() == 0)) && (IMContratoEmp.this.cmpNombre != null)) { // Borro
								// nombre de la empresa.
								IMContratoEmp.this.cmpNombre.setValue(null);
							}
						}
						IMContratoEmp.this.activarBotonCIF();
					} catch (Exception ex) {
						System.err.println("" + ex.getMessage());
					}
				}
			});
		}

		// Codigo para la verificacion del NIF-CIF
		if (this.btnChkCIFNIF != null) {
			this.btnChkCIFNIF.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (IMContratoEmp.this.cmpCIF != null) {
							String nifcif = (String) IMContratoEmp.this.cmpCIF.getValue();
							boolean nifcifCorrecto = NIFDocument.isNIEWellFormed(nifcif) || NIFDocument.isNIFWellFormed(nifcif) || CIFDocument
									.isCIFWellFormed(nifcif) || BIDocument.esBICorrecto(nifcif);
							if (nifcifCorrecto == false) {
								IMContratoEmp.this.managedForm.message(
										ApplicationManager.getTranslation("M_NIF_CIF_TITULAR_NO_ES_VALIDO",
												IMContratoEmp.this.managedForm.getResourceBundle()), Form.WARNING_MESSAGE);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}

	}

	private void replaceUpdateListener() {
		final UpdateListener lAnteriorActualizacion = this.updateListener;
		this.removeUpdateListener();
		this.updateListener = new UpdateListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				if (IMContratoEmp.this.checkUpdate()) {
					lAnteriorActualizacion.actionPerformed(evento);
					try {
						((OpentachClientLocator) IMContratoEmp.this.formManager.getReferenceLocator()).reloadUserData();
					} catch (Exception e) {
					}
				}
			}
		};
		this.managedForm.getButton(InteractionManager.UPDATE_KEY).addActionListener(this.updateListener);

	}

	private void replaceDeleteListener() {
		this.removeDeleteListener();
		this.deleteListener = new DeleteListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				if (IMContratoEmp.this.jEliminar == null) {
					IMContratoEmp.this.fEliminar = IMContratoEmp.this.formManager.getFormCopy("formEliminarContrato.xml");
					IMContratoEmp.this.fEliminar.getInteractionManager().setInitialState();
					IMContratoEmp.this.jEliminar = IMContratoEmp.this.fEliminar.putInModalDialog();
					// JEliminar = getFormDialog(FEliminar);
					// JEliminar.setSize(400, 300);
				}
				IMContratoEmp.this.fEliminar.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
						IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
				IMContratoEmp.this.fEliminar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
						IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
				IMContratoEmp.this.jEliminar.setVisible(true);
				IMContratoEmp.this.managedForm.refreshCurrentDataRecord();
			}
		};
		this.managedForm.getButton(InteractionManager.DELETE_KEY).addActionListener(this.deleteListener);

	}

	private void overrideDriversDeleteButton() {
		if ((this.tbConductores != null) && (this.tbVehiculos != null)) {
			TableButton b = (TableButton) this.tbConductores.getTableComponentReference("deletebutton");
			ActionListener[] alarray = b.getListeners(ActionListener.class);
			ActionListener alDelete = null;
			if (alarray.length > 0) {
				// solo tiene un listener
				this.alDeleteConductor = alarray[0];
				b.removeActionListener(alDelete);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IMContratoEmp.this.alDeleteConductor.actionPerformed(e);
						IMContratoEmp.this.tbConductores.refresh();
					}
				});
			}
			b = (TableButton) this.tbVehiculos.getTableComponentReference("deletebutton");
			alarray = b.getListeners(ActionListener.class);
			if (alarray.length > 0) {
				// solo tiene un listener
				this.alDeleteVehiculo = alarray[0];
				b.removeActionListener(alDelete);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IMContratoEmp.this.alDeleteVehiculo.actionPerformed(e);
						IMContratoEmp.this.tbVehiculos.refresh();
					}
				});
			}
		}

	}

	private void installActiveContractSelectorListener() {
		//
		if ((this.chActivo != null) && (this.cmpCIF != null) && (this.cgContrato != null)) {
			this.setValueChangedEventListener(this.chActivo.getAttribute(), false);
			this.managedForm.setCheckModifiedDataField((String) this.chActivo.getAttribute(), false);
			this.chActivo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						IUserData user = ((OpentachClientLocator) IMContratoEmp.this.formManager.getReferenceLocator()).getUserData();
						user.addActiveContract((String) IMContratoEmp.this.cmpCIF.getValue(), (String) IMContratoEmp.this.cgContrato.getValue());
						IMContratoEmp.this.chActivo.setEnabled(false);
						Tree arbol = ((FormManager) IMContratoEmp.this.managedForm.getFormManager()).getTree();
						if (arbol != null) {
							arbol.repaint();
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
	}

	private void addAddButtonToVehiclesTable() {
		Button btngV = this.managedForm.getButton("btnAnadirV");
		// add button to vehicles table
		if (btngV != null) {
			this.managedForm.remove(btngV);
			this.tbVehiculos.addComponentToControls(btngV);
			btngV.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (IMContratoEmp.this.jInsertarV == null) {
						IMContratoEmp.this.fInsertarV = IMContratoEmp.this.formManager.getFormCopy("formVehiculoCont.xml");
						IMContratoEmp.this.jInsertarV = IMContratoEmp.this.fInsertarV.putInModalDialog();
						((IMCondVehContrato) IMContratoEmp.this.fInsertarV.getInteractionManager()).tabla = IMContratoEmp.this.tbVehiculos;
					}
					IMContratoEmp.this.fInsertarV.getInteractionManager().setInsertMode();
					IMContratoEmp.this.fInsertarV.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
							IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
					IMContratoEmp.this.fInsertarV.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
							IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
					IMContratoEmp.this.fInsertarV.setDataFieldValue("TIPO", IMContratoEmp.this.managedForm.getDataFieldValue("TIPO"));
					IMContratoEmp.this.jInsertarV.setVisible(true);
					IMContratoEmp.this.tbVehiculos.refresh();
				}
			});
		}
	}

	private void addAddButtonToDriversTable() {
		//
		Button btng = this.managedForm.getButton("btnAnadir");
		if (btng != null) {
			this.managedForm.remove(btng);
			// add button to drivers table
			this.tbConductores.addComponentToControls(btng);
			btng.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (IMContratoEmp.this.jInsertar == null) {
						IMContratoEmp.this.fInsertar = IMContratoEmp.this.formManager.getFormCopy("formConductorCont.xml");
						IMContratoEmp.this.jInsertar = IMContratoEmp.this.fInsertar.putInModalDialog();
						((IMCondVehContrato) IMContratoEmp.this.fInsertar.getInteractionManager()).tabla = IMContratoEmp.this.tbConductores;
					}
					IMContratoEmp.this.fInsertar.getInteractionManager().setInsertMode();
					IMContratoEmp.this.fInsertar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
							IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
					IMContratoEmp.this.fInsertar.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
							IMContratoEmp.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
					IMContratoEmp.this.fInsertar.setDataFieldValue("TIPO", IMContratoEmp.this.managedForm.getDataFieldValue("TIPO"));
					IMContratoEmp.this.fInsertar.setDataFieldValue("USUARIO_ALTA", IMContratoEmp.this.managedForm.getDataFieldValue("USUARIO_ALTA"));
					IMContratoEmp.this.fInsertar.setDataFieldValue("F_ALTA", IMContratoEmp.this.managedForm.getDataFieldValue("F_ALTA"));
					IMContratoEmp.this.jInsertar.setVisible(true);
					IMContratoEmp.this.tbConductores.refresh();
				}
			});
		}

	}

	private void replaceInsertListener() {
		this.removeInsertListener();
		this.insertListener = new InsertListener() {

			String	cif	= "";

			@Override
			protected void postCorrectInsert(EntityResult result, Entity entity) throws Exception {
				super.postCorrectInsert(result, entity);
				IUserData user = ((OpentachClientLocator) IMContratoEmp.this.formManager.getReferenceLocator()).getUserData();
				user.addActiveContract(this.cif, (String) IMContratoEmp.this.managedForm.getDataFieldValue("CG_CONTRATO"));

			}

			@Override
			public void actionPerformed(ActionEvent event) {
				this.cif = (String) IMContratoEmp.this.managedForm.getDataFieldValue("CIF");
				super.actionPerformed(event);
			}

		};

		Button bInsert = this.managedForm.getButton(InteractionManager.INSERT_KEY);
		if (bInsert != null) {
			bInsert.addActionListener(this.insertListener);
		}
	}

	@Override
	public boolean checkInsert() {
		try {
			if (this.checkValidCIFNIF(true)) {
				return super.checkInsert();
			}
			String msg = ApplicationManager.getTranslation("VERIFIQUE_DATOS_EMPRESA", this.managedForm.getResourceBundle());
			this.managedForm.message(msg, Form.ERROR_MESSAGE);
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private void activarBotonCIF() {
		String val = null;
		if ((this.getCurrentMode() == InteractionManager.INSERT) || (this.getCurrentMode() == InteractionManager.UPDATE)) {
			if (this.cmpNombre != null) {
				val = (String) this.cmpNombre.getValue();
				if ((val != null) && (val.length() > 6) && (val.substring(0, 6).indexOf('*') == -1)) {
					this.managedForm.enableButton("buscarCIF");
					return;
				}
				if (this.cmpCIF != null) {
					val = (String) this.cmpCIF.getValue();
					if ((val != null) && (val.length() > 3) && (val.substring(0, 3).indexOf('*') == -1)) {
						this.managedForm.enableButton("buscarCIF");
						return;
					}
				}
			}
		}
		this.managedForm.disableButton("buscarCIF");
	}

	@Override
	public boolean checkUpdate() {
		return (super.checkUpdate() && this.checkValidCIFNIF(true));
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.managedForm.disableDataField(OpentachFieldNames.CG_CONTRATO_FIELD);
		// formularioGestionado.activarBoton( "cifnif" );
		this.managedForm.enableButton("letraNIF");
		this.managedForm.disableButton("buscarCIF");
		this.managedForm.disableDataField("CG_RENOVACION");
		this.managedForm.enableDataField(OpentachFieldNames.CIF_FIELD);
		this.chActivo.setEnabled(false);
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
		// formularioGestionado.activarBoton( "cifnif" );
		this.managedForm.enableButton("letraNIF");
		this.managedForm.disableButton("buscarCIF");
		this.managedForm.disableDataField(OpentachFieldNames.NUMREQ_FIELD);
		this.managedForm.disableDataField(OpentachFieldNames.CIF_FIELD);
		try {
			IUserData user = ((OpentachClientLocator) this.formManager.getReferenceLocator()).getUserData();
			String contrato = user.getActiveContract((String) this.cmpCIF.getValue());
			String vContrato = (String) this.cgContrato.getValue();
			if ((vContrato == null) || !vContrato.equals(contrato)) {
				this.chActivo.setEnabled(true);
			} else {
				this.chActivo.setEnabled(false);
			}
		} catch (Exception ex) {
		}
	}

	@Override
	public void setQueryMode() {
		super.setQueryMode();
		this.managedForm.enableButton("letraNIF");
		this.managedForm.disableButton("buscarCIF");
		this.managedForm.enableDataField(OpentachFieldNames.CIF_FIELD);
		this.chActivo.setEnabled(false);
	}

	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
		this.chActivo.setEnabled(false);
	}

}
