package com.opentach.adminclient.modules.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.RadioButtonDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.BooleanCellEditor;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.CellEditor;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;

public class IMViewSolicitudCondVeh extends IMDataRoot {

	@FormComponent(attr = "SOLTODAS")
	private RadioButtonDataField	rbAll;
	@FormComponent(attr = "SOLPEND")
	private RadioButtonDataField	rbPend;
	@FormComponent(attr = "ESolicitudesCondVeh")
	private Table					tbData;

	public IMViewSolicitudCondVeh() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
	}

	@Override
	public void registerInteractionManager(Form form, final IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		ValueChangeListener vcl = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					IMViewSolicitudCondVeh.this.doOnQuery();
				}
			}
		};
		this.rbAll.addValueChangeListener(vcl);
		this.rbPend.addValueChangeListener(vcl);
		UReferenceDataField rcif = (UReferenceDataField) this.CIF;
		if (rcif != null) {
			rcif.addValueChangeListener(vcl);
		}

		Button btng = this.managedForm.getButton("mSuperado");
		if (btng != null) {
			this.managedForm.remove(btng);
			// add button to drivers table
			this.tbData.addComponentToControls(btng);
			btng.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					IMViewSolicitudCondVeh.this.updateResuelto(true);
				}
			});

		}

		Button btngQuitar = this.managedForm.getButton("mNoSuperado");
		if (btngQuitar != null) {
			this.managedForm.remove(btngQuitar);
			// add button to drivers table
			this.tbData.addComponentToControls(btngQuitar);
			btngQuitar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMViewSolicitudCondVeh.this.updateResuelto(false);
				}
			});
		}

		if (this.tbData != null) {
			Hashtable params = new Hashtable();
			params = new Hashtable();
			BooleanCellRenderer bcr = new BooleanCellRenderer();
			this.tbData.setRendererForColumn("RESUELTO", bcr);
			params = new Hashtable();
			params.put(CellEditor.COLUMN_PARAMETER, "RESUELTO");
			BooleanCellEditor bce = new BooleanCellEditor(params);
			this.tbData.setColumnEditor("RESUELTO", bce);
		}
	}

	private void updateResuelto(boolean resuelto) {

		int[] selectRow = IMViewSolicitudCondVeh.this.tbData.getSelectedRows();
		if (selectRow.length == 0) {
			IMViewSolicitudCondVeh.this.managedForm.message("SELECCIONE_FILA_ALUMNO", Form.ERROR_MESSAGE);
			return;
		}
		try {
			ReferenceLocator b = (ReferenceLocator) ((FormManager) this.formManager).getReferenceLocator();
			Entity entidad = b.getEntityReference("ESolicitudesCondVeh");
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("RESUELTO", resuelto ? 1 : 0);

			Hashtable<Object, Object> avPeriodo = new Hashtable<Object, Object>();

			for (int i = 0; i < selectRow.length; i++) {

				Hashtable<String, Object> avRow = IMViewSolicitudCondVeh.this.tbData.getRowData(selectRow[i]);
				avPeriodo.put("IDSOLICITUD", avRow.get("IDSOLICITUD"));
				entidad.update(av, avPeriodo, b.getSessionId());

			}
			IMViewSolicitudCondVeh.this.doOnQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doOnQuery() {
		if ((this.rbAll == null) || (this.rbPend == null)) {
			return;
		}
		this.refreshTable();
	}

	@SuppressWarnings("unchecked")
	protected void refreshTable() {
		final Table t = this.tbData;
		if (t == null) {
			return;
		}
		String cgContrato = (String) this.cgContract.getValue();
		Hashtable kv = new Hashtable();
		Vector vq = t.getVisibleColumns();
		if (cgContrato != null) {
			kv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
		}
		if ((this.rbPend.getValue() != null) && (((Number) this.rbPend.getValue()).intValue() == 1)) {
			kv.put("F_RESOLUCION", new SearchValue(SearchValue.NULL, null));
		}
		final EntityReferenceLocator loc = this.formManager.getReferenceLocator();
		Entity ent;
		try {
			ent = loc.getEntityReference(t.getEntityName());
			EntityResult er = ent.query(kv, vq, loc.getSessionId());
			if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				t.setValue(er);
			} else {
				t.deleteData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			t.deleteData();
		}

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.managedForm.getButton("mSuperado").setEnabled(true);
		this.managedForm.getButton("mNoSuperado").setEnabled(true);
		this.doOnQuery();
	}
}
