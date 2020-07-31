package com.opentach.client.comp.field;

import java.awt.Dialog;
import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.Form;
import com.ontimize.gui.table.CellRenderer;
import com.ontimize.gui.table.PrintingSetupWindow;
import com.ontimize.gui.table.Table;
import com.utilmize.client.gui.field.table.UTable;

public class OpentachTable extends UTable {

	@SuppressWarnings("unchecked")
	public OpentachTable(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	protected void installButtonsListener() {
		super.installButtonsListener();
		if ((this.controlsPanel != null) && (this.buttonPrint != null)) {
			this.controlsPanel.remove(this.buttonPrint);
		}
		this.buttonPrint = null;
	}

	private void setRenders(PrintingSetupWindow vc) {
		try {
			// Acccedo a la tabla de vConfigImpresion --> ta que es privado por
			// reflexion para modificar el render de minutos.
			Field[] fs = vc.getClass().getDeclaredFields();
			// Para todas las tablas estableco el render de los minutos.
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				f.setAccessible(true);
				if (f.get(vc) instanceof Table) {
					Table t = (Table) f.get(vc);
					if (t != this) {
						JTable jt = t.getJTable();
						int colcount = jt.getModel().getColumnCount();
						for (int c = 0; c < colcount; c++) {
							String colname = jt.getColumnName(c);
							if (OpentachTable.this.getRendererForColumn(colname) instanceof CellRenderer) {
								CellRenderer renderer = (CellRenderer) OpentachTable.this.getRendererForColumn(colname);
								if (renderer != null) {
									Object clon = null;
									try {
										Method method = ((Object) renderer).getClass().getMethod("clone", (Class<?>) null);
										clon = method.invoke(renderer, (Object[]) null);
									} catch (Exception e) {
									}
									if ((clon != null) && (clon instanceof TableCellRenderer)) {
										t.setRendererForColumnExp(colname, (TableCellRenderer) clon);
									}
								}
							}
						}
						t.setLineRemark(false);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void print() {
		this.checkRefreshThread();
		PrintingSetupWindow vConfigImpresion = null;
		try {
			Window w = SwingUtilities.getWindowAncestor(OpentachTable.this);
			if (w instanceof Dialog) {
				if (vConfigImpresion == null) {
					vConfigImpresion = new PrintingSetupWindow((Dialog) w, OpentachTable.this);
				}
				this.setRenders(vConfigImpresion);
				vConfigImpresion.setResourceBundle(this.resourcesFile);
				vConfigImpresion.setVisible(true);
			} else {
				if (vConfigImpresion == null) {
					vConfigImpresion = new PrintingSetupWindow(OpentachTable.this.parentFrame, OpentachTable.this);
				}
				this.setRenders(vConfigImpresion);
				vConfigImpresion.setResourceBundle(this.resourcesFile);
				vConfigImpresion.setVisible(true);
			}
		} catch (Exception e) {
			this.parentForm.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (OutOfMemoryError error) {
			System.out.println("Error memoria:");
			error.printStackTrace();
			if (vConfigImpresion != null) {
				vConfigImpresion.setVisible(false);
				vConfigImpresion.dispose();
			}
			vConfigImpresion = null;
			System.gc();
		}
	}

}
