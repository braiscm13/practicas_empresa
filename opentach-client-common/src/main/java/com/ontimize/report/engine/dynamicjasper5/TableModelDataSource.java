/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.ontimize.report.engine.dynamicjasper5;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.TableModel;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class TableModelDataSource implements JRDataSource {

	private final TableModel model;
	private int index;
	private final int size;

	public TableModelDataSource(TableModel model) {
		this.model = model;
		this.size = model.getRowCount();
		this.index = -1;
	}

	public int findColumnIndex(String name) {
		for (int i = 0; i < this.model.getColumnCount(); i++) {
			if (this.model.getColumnName(i).equals(name)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		return this.model.getValueAt(this.index, this.findColumnIndex(jrField.getName()));
	}

	@Override
	public boolean next() throws JRException {
		this.index++;
		return this.index < this.size;
	}

	public static JRField[] getFields(TableModel model) {
		Vector tmp = new Vector();

		for (int i = 0; i < model.getColumnCount(); i++) {

			Class classClass = model.getColumnClass(i);
			String className = model.getColumnClass(i).getName();

			Hashtable m = new Hashtable();
			m.put(CustomField.NAME_KEY, model.getColumnName(i));
			m.put(CustomField.VALUE_CLASS_NAME_KEY, className);
			m.put(CustomField.VALUE_CLASS_KEY, classClass);

			tmp.add(new CustomField(m));
		}

		// To array
		int s = tmp.size();
		CustomField[] a = new CustomField[s];
		for (int i = 0; i < s; i++) {
			Object o = tmp.get(i);
			if ((o == null) || !(o instanceof CustomField)) {
				continue;
			}
			a[i] = (CustomField) o;
		}
		return a;
	}
}
