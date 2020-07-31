package com.opentach.client.comp;

import java.util.Hashtable;

import javax.swing.BorderFactory;

import com.ontimize.gui.field.Label;

public class TransparentLabel extends Label {

	public TransparentLabel(Hashtable params) {
		super(params);
		this.setOpaque(false);
		this.setBorder(BorderFactory.createEmptyBorder());
	}

}