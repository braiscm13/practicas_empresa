/*
 *
 */
package com.opentach.client.comp;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JComponent;

import com.utilmize.client.gui.field.UListDataField;

/**
 * The Class UCustomListDataField.
 */
public class UCustomListDataField extends UListDataField {

	/**
	 * Instantiates a new u list data field.
	 *
	 * @param parameters
	 *            the parameters
	 */
	public UCustomListDataField(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);
		this.setCellRenderer(new CustomListCellRenderer());
	}

	/**
	 * Sets the opacity.
	 *
	 * @param c
	 *            the c
	 * @param opacity
	 *            the opacity
	 */
	@Override
	protected void setOpacity(JComponent c, boolean opacity) {
		c.setOpaque(false);
		Component[] components = c.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JComponent) {
				this.setOpacity((JComponent) components[i], opacity);
			}
		}
	}
}
