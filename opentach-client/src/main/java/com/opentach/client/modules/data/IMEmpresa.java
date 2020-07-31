package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;
import com.ontimize.gui.tree.OTreeNode;
import com.ontimize.gui.tree.Tree;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.render.CompanyTreeCellRenderer;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.util.UserTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMEmpresa extends IMRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMEmpresa.class);

	private Form				FEliminar	= null;
	private JDialog				JEliminar	= null;
	private DataField			cmpCIF		= null;
	private final Button		bConfRep	= null;

	public IMEmpresa() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add("CG_NACI");
		this.fieldsChain.add("CG_PROV");
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.overrideInsertListener();
		this.overrideUpdateListener();
		this.overrideDeleteListener();

		if (((FormManager) formManager).getTree() != null) {
			((FormManager) formManager).getTree().setCellRenderer(new CompanyTreeCellRenderer());
		}

		String level = UserTools.getUserLevel();
		// usuarios empresa, EMPRESA GESTION FLOTAS o EMPRESA GESTION TARJETAS
		if (level.equals(IUserData.NIVEL_EMPRESA) || level.equals(IUserData.NIVEL_EMPRESAGF) || level.equals(IUserData.NIVEL_EMPRESAGTARJETAS)) {
			this.updateTree();
		}

		this.installCooperativaStuff();
		this.installCifLetterListener();


		Table tbConductores = (Table) this.managedForm.getDataFieldReference("EConductoresEmp");
		Table tbVehiculos = (Table) this.managedForm.getDataFieldReference("EVehiculosEmp");

		this.createDeleteCondButton(tbConductores, "EConductorCont", "IDCONDUCTOR");
		this.createDeleteCondButton(tbVehiculos, "EVehiculoCont", "MATRICULA");

	}

	private void overrideDeleteListener() {
		this.removeDeleteListener();
		this.deleteListener = new BasicInteractionManager.DeleteListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				if (IMEmpresa.this.JEliminar == null) {
					IMEmpresa.this.FEliminar = IMEmpresa.this.formManager.getFormCopy("formEliminarEmpresa.xml");
					IMEmpresa.this.FEliminar.getInteractionManager().setInitialState();
					IMEmpresa.this.JEliminar = IMEmpresa.this.FEliminar.putInModalDialog();
				}
				IMEmpresa.this.FEliminar
				.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD, IMEmpresa.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
				IMEmpresa.this.FEliminar.setDataFieldValue(OpentachFieldNames.CIF_FIELD, IMEmpresa.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
				IMEmpresa.this.JEliminar.setVisible(true);
				((FormManager) IMEmpresa.this.formManager).getTree().updatePath(IMEmpresa.this.managedForm.getAssociatedTreePath().getParentPath());
				IMEmpresa.this.managedForm.refreshCurrentDataRecord();
			}
		};
		if (this.managedForm.getButton(InteractionManager.DELETE_KEY) != null) {
			this.managedForm.getButton(InteractionManager.DELETE_KEY).addActionListener(this.deleteListener);
		}
	}

	private void overrideUpdateListener() {
		this.removeUpdateListener();
		this.updateListener = new UpdateListener() {

			String	contrato	= "N";

			@Override
			protected void postCorrectUpdate(EntityResult result, Entity entity) throws Exception {
				super.postCorrectUpdate(result, entity);
				if (this.contrato.equals("S")) {
					// setActiveContract(String cif, String cgcontrato) ;
					Entity ent = IMEmpresa.this.formManager.getReferenceLocator().getEntityReference("EEmpreReq");
					Hashtable<String, Object> av = new Hashtable<String, Object>();
					av.put("CIF", IMEmpresa.this.managedForm.getDataFieldValue("CIF"));
					EntityResult res = ent.query(av, new Vector<Object>(), IMEmpresa.this.formManager.getReferenceLocator().getSessionId());
					if ((res != null) && (res.calculateRecordNumber() > 0)) {
						IMEmpresa.this.setActiveContract((String) av.get("CIF"), (String) res.getRecordValues(0).get("NUMREQ"));
					}
				}
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				if (IMEmpresa.this.modifiedFieldAttributes.contains("CONTRATO_COOP")) {
					String contratoCoop = (String) IMEmpresa.this.managedForm.getDataFieldValue("CONTRATO_COOP");
					if ("S".equals(contratoCoop)) {
						this.contrato = "S";
					}
				}
				super.actionPerformed(event);
			}

		};

		Button bUpdate = this.managedForm.getButton(InteractionManager.UPDATE_KEY);
		if (bUpdate != null) {
			bUpdate.addActionListener(this.updateListener);
		}

	}

	private void overrideInsertListener() {
		this.removeInsertListener();
		this.insertListener = new InsertListener() {

			String	contrato	= "N";
			String	cif			= "";

			@Override
			protected void postCorrectInsert(EntityResult result, Entity entity) throws Exception {
				super.postCorrectInsert(result, entity);
				if (this.contrato.equals("S")) {
					// setActiveContract(String cif, String cgcontrato) ;
					Entity ent = IMEmpresa.this.formManager.getReferenceLocator().getEntityReference("EEmpreReq");
					Hashtable<String, Object> av = new Hashtable<String, Object>();
					av.put("CIF", this.cif);
					EntityResult res = ent.query(av, new Vector<Object>(), IMEmpresa.this.formManager.getReferenceLocator().getSessionId());
					if ((res != null) && (res.calculateRecordNumber() > 0)) {
						IUserData user = ((OpentachClientLocator) IMEmpresa.this.formManager.getReferenceLocator()).getUserData();
						user.addActiveContract(this.cif, (String) res.getRecordValues(0).get("NUMREQ"));
					}
				}
			}

			@Override
			public void actionPerformed(ActionEvent event) {

				String contratoCoop = (String) IMEmpresa.this.managedForm.getDataFieldValue("CONTRATO_COOP");
				if ("S".equals(contratoCoop)) {
					this.cif = (String) IMEmpresa.this.managedForm.getDataFieldValue("CIF");
					this.contrato = "S";
				}

				super.actionPerformed(event);
			}

		};

		Button bInsert = this.managedForm.getButton(InteractionManager.INSERT_KEY);
		if (bInsert != null) {
			bInsert.addActionListener(this.insertListener);
		}
	}

	private void installCifLetterListener() {
		this.cmpCIF = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.CIF_FIELD);
		this.cmpCIF.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent valueEvent) {
				try {
					String nv = (String) valueEvent.getNewValue();
					if ((valueEvent.getType() == ValueEvent.USER_CHANGE) && (nv != null) && (nv.length() > 0)) {
						IMEmpresa.this.managedForm.enableButton("letraNIF");
					} else {
						IMEmpresa.this.managedForm.disableButton("letraNIF");
					}
				} catch (Exception ex) {
				}
			}
		});
	}

	private void installCooperativaStuff() {

		CheckDataField cdf = (CheckDataField) this.managedForm.getDataFieldReference("IS_COOPERATIVA");
		if (cdf != null) {
			cdf.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if (e.getOldValue() != e.getNewValue()) {
						if (e.getNewValue().equals("S")) {
							IMEmpresa.this.managedForm.getDataFieldReference("CIF_COOPERATIVA").setEnabled(false);
						} else {
							IMEmpresa.this.managedForm.getDataFieldReference("CIF_COOPERATIVA").setEnabled(true);
						}
					}

				}
			});
		}

		UReferenceDataField cCif = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF_COOPERATIVA");
		if (cCif != null) {
			cCif.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChanged(ValueEvent e) {
					if (e.getOldValue() != e.getNewValue()) {
						if ((IMEmpresa.this.managedForm.getDataFieldValue("IS_COOPERATIVA") != null) && "S".equals(IMEmpresa.this.managedForm.getDataFieldValue("IS_COOPERATIVA"))) {
							IMEmpresa.this.managedForm.getDataFieldReference("CIF_COOPERATIVA").setEnabled(false);
						} else {
							IMEmpresa.this.managedForm.getDataFieldReference("CIF_COOPERATIVA").setEnabled(true);
						}
					}
				}
			});
		}

	}

	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
		this.setDataFieldsVisible(true, "CIF_COOPERATIVA", "CONTRATO_COOP");
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.managedForm.setDataFieldValue("CG_NACI", "00042");

	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.cmpCIF != null) {
			this.cmpCIF.setEnabled(false);
		}
		if (this.bConfRep != null) {
			this.bConfRep.setEnabled(true);
		}
		this.updateCooperativaFields();

	}

	@Override
	public void dataChanged(DataNavigationEvent event) {
		super.dataChanged(event);
		this.updateCooperativaFields();
	}

	private void updateCooperativaFields() {
		if ("S".equals(IMEmpresa.this.managedForm.getDataFieldValue("IS_COOPERATIVA"))) {
			this.setDataFieldsVisible(false, "CIF_COOPERATIVA", "CONTRATO_COOP");
		} else if ("S".equals(IMEmpresa.this.managedForm.getDataFieldValue("CONTRATO_COOP"))) {
			this.setDataFieldsVisible(false, "IS_COOPERATIVA", "CONTRATO_COOP");
		} else {
			this.setDataFieldsVisible(true, "CIF_COOPERATIVA", "CONTRATO_COOP", "IS_COOPERATIVA");
			this.setDataFieldsEnable(true, "CONTRATO_COOP");
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

	private void updateTree() {

		Tree tree = ((FormManager) this.managedForm.getFormManager()).getTree();
		if (tree != null) {
			tree.collapseRow(0);
			TreePath path = tree.getPathForRow(0);
			tree.expandPath(path);
			// int nodos = ((OTreeNode) path.getPathComponent(0)).getCount();
			// if (nodos < 11){
			Enumeration<TreeNode> enumFactories = ((OTreeNode) path.getPathComponent(0)).children();

			while (enumFactories.hasMoreElements()) {
				OTreeNode o = (OTreeNode) enumFactories.nextElement();
				TreePath expand = new TreePath(o.getPath());
				tree.expandPath(expand);
				// // Enumeration<Object> enumVehCond = ((OTreeNode) expand.getLastPathComponent()).children();
				// // while (enumVehCond.hasMoreElements()) {
				// // OTreeNode organizeInstsNode = (OTreeNode) enumVehCond.nextElement();
				// // TreePath instPathOrganizative = new TreePath(organizeInstsNode.getPath());
				// // tree.expandPath(instPathOrganizative);
				// // }
			}
			// }
			tree.setSelectionPath(path);

		}
	}


	private void createDeleteCondButton(Table tb, String deleteEntity, String key){

		TableButton tbIcon = (TableButton)tb.getTableComponentReference(Table.DELETE_BUTTON);
		for (ActionListener listener :  tbIcon.getActionListeners()) {
			if (listener instanceof ActionListener) {
				tbIcon.removeActionListener(listener);
			}
		}

		tbIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					EntityReferenceLocator erl = IMEmpresa.this.formManager.getReferenceLocator();
					Entity eCont = erl.getEntityReference(deleteEntity);
					Hashtable<String, Object> kv = new Hashtable<String, Object>();

					int[] conds = tb.getSelectedRows();
					for (int i=0; i<conds.length;i++){
						Hashtable<String, Object> hregistro = tb.getRowData(conds[i]);
						kv.put(OpentachFieldNames.CIF_FIELD, hregistro.get(OpentachFieldNames.CIF_FIELD));
						kv.put(key, hregistro.get(key));

						EntityResult resCont = eCont.query(kv, new Vector(), erl.getSessionId());
						if ((resCont.getCode()==EntityResult.OPERATION_SUCCESSFUL) && (resCont.calculateRecordNumber()>0)){
							String cg_contrato = (String)resCont.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD);
							kv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato);
							kv.put(key, hregistro.get(key));
							eCont.delete(kv, erl.getSessionId());
							tb.refresh();
						}
					}
				}
				catch (Exception ex) {
				}
			}
		});

	}
}
