package com.opentach.client.report.components;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.DataField;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;

public class JRComboReport extends DataField {

	private class JRComboReportCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel jl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			jl.setText(ApplicationManager.getTranslation(jl.getText()));
			return jl;
		}
	}

	private String	url;

	public JRComboReport(Hashtable<String, Object> params) throws Exception {
		super();
		this.init(params);
	}

	@Override
	public void init(Hashtable params) {
		this.url = (String) params.get("url");
		Vector<JRReportDescriptor> v = null;
		if (this.url != null) {
			JRPropertyManager jpm = new JRPropertyManager(this.url);
			v = jpm.getDataVector();
		} else {
			v = new Vector<JRReportDescriptor>();
		}
		JComboBox<JRReportDescriptor> jcb = new JComboBox<JRReportDescriptor>(v);
		jcb.setRenderer(new JRComboReportCellRenderer());
		this.dataField = jcb;
		super.init(params);
	}

	@Override
	public void deleteData() {}

	@Override
	public int getSQLDataType() {
		return 0;
	}

	@Override
	public Object getValue() {
		return ((JComboBox<JRReportDescriptor>) this.dataField).getSelectedItem();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void setValue(Object value) {
	}

}
