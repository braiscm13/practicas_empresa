package com.opentach.client.comp;

import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.BorderFactory;

import com.ontimize.gui.button.Button;

public class TransparentButton extends Button {

	public TransparentButton(Hashtable params) {
		super(params);
		this.setOpaque(false);
		this.setContentAreaFilled(false);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setMargin(new Insets(0, 0, 0, 0));
	}
}