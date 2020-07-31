package com.opentach.adminclient.modules.admin;

import java.util.Hashtable;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.CellEditor;
import com.ontimize.gui.table.EditingVetoException;
import com.ontimize.gui.table.IntegerCellEditor;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableEditionEvent;
import com.ontimize.gui.table.TableEditorListener;
import com.utilmize.client.fim.UBasicFIM;

public class IMGenInformeGestor extends UBasicFIM {

	private Table	tbGen;

	@Override
	@SuppressWarnings("unchecked")
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.tbGen = (Table) f.getElementReference("EConfInformeGestor");
		if (this.tbGen != null) {
			Hashtable params = new Hashtable();
			params.put(CellEditor.COLUMN_PARAMETER, "DEND");
			IntegerCellEditor ce = new IntegerCellEditor(params);
			this.tbGen.setColumnEditor("DEND", ce);
			params = new Hashtable();
			params.put(CellEditor.COLUMN_PARAMETER, "DGEN");
			ce = new IntegerCellEditor(params);
			this.tbGen.setColumnEditor("DGEN", ce);
			this.tbGen.addTableEditorListener(new TableEditorListener() {
				@Override
				public void editingCanceled(TableEditionEvent e) {}

				@Override
				public void editingStopped(TableEditionEvent e) {}

				@Override
				public void editingWillStop(TableEditionEvent e) throws EditingVetoException {
					Number num = (Number) e.getValue();
					if ((num != null) && ((num.intValue() > 31) || (num.intValue() < 1))) {
						throw new EditingVetoException("M_MESES_1_31");
					}
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.tbGen != null) {
			this.tbGen.refresh();
		}
	}

}
