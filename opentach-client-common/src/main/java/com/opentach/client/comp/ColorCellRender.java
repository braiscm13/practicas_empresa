package com.opentach.client.comp;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.table.TableColumn;

import com.ontimize.gui.table.CellRenderer;

public class ColorCellRender extends CellRenderer {

	private  Color CFROJO = new Color(250,50,50);

	private String modifyColumn = null;
	private Object compareValue = null;
	private String compareOperation = null;

	public ColorCellRender(String column, Object value, String operation){
		this.modifyColumn  = column;
		this.compareValue= value;
		this.compareOperation = operation;
	}



	@Override
	public Component getTableCellRendererComponent(javax.swing.JTable jt, java.lang.Object valor, boolean seleccionado, boolean tieneFoco,
			int row, int columna) {
		Component comPadre = super.getTableCellRendererComponent(jt, valor, seleccionado, tieneFoco, row, columna);
		
		JLabel labelTemp = (JLabel) comPadre;
		TableColumn tcLastActivity = jt.getColumn(this.modifyColumn);
		if (tcLastActivity!=null){
			
			int imDP 		= tcLastActivity.getModelIndex();
			Object actualValue  = jt.getValueAt(row, imDP);
			System.out.println(this.modifyColumn +" columna "+ row +" fila, con valor: "+actualValue);
			labelTemp.setText((String)valor);
			if (actualValue!=null && !(actualValue.equals("-"))) {
				if (this.compareOperation.equals("<")) {
					if (actualValue instanceof Date) {
						if (((Date)actualValue).before((Date)this.compareValue)) {
							labelTemp.setBackground(CFROJO);
						}
					}
					if  (actualValue instanceof Number) {
						if (((Number)actualValue).intValue() < ((Number)this.compareValue).intValue()) {
							labelTemp.setBackground(CFROJO);
						}
					}
				}else if (this.compareOperation.equals(">")) {
					if (actualValue instanceof Date) {
						if (((Date)actualValue).after((Date)this.compareValue)) {
							labelTemp.setBackground(CFROJO);
						}
					}else if  (actualValue instanceof Number) {
						if (((Number)actualValue).intValue() > ((Number)this.compareValue).intValue()) {
							labelTemp.setBackground(CFROJO);
						}
					}else if (actualValue instanceof String) {
						int value = (new Integer((String)actualValue)).intValue();
						if (value > ((Number)this.compareValue).intValue()) {
							labelTemp.setBackground(CFROJO);
						}
					}

				}else if (this.compareOperation.equals("=")) {
					if (actualValue.equals(this.compareValue)) {
						labelTemp.setBackground(CFROJO);
					}
				}
			}
		}
		return comPadre;
	}
}
