package com.opentach.client.comp;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.table.ButtonCellEditor;

public abstract class ButtonTableCell extends ButtonCellEditor implements TableCellRenderer {
	BasicInteractionManager	gif;

	public ButtonTableCell(Icon icon) {
		super(icon);

		ImageIcon picon = new ImageIcon(((ImageIcon) icon).getImage()) {
			@Override
			public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
				Composite old = g2.getComposite();
				g2.setComposite(alpha);
				super.paintIcon(c, g2, x, y);
				g2.setComposite(old);
			}
		};

		this.setPressedIcon(picon);

		this.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonTableCell.this.lastValue = Boolean.TRUE;
				ButtonTableCell.this.stopCellEditing();
				ButtonTableCell.this.doOnClick();
			}
		});
	}

	abstract public void doOnClick();

	public void setGestorInteraccion(BasicInteractionManager gif) {
		this.gif = gif;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this;
	}
}