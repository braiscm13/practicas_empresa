package com.opentach.client.comp;

import java.awt.BorderLayout;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.ontimize.gui.container.Column;

public class HTMLColumn extends Column {

	private JLabel	jl;

	public HTMLColumn(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);
		this.jl = new JLabel();
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(this.jl));
	}

	public void setText(String text) {
		this.jl.setText(text);
	}

}
