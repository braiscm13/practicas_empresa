package com.opentach.client.devicecontrol;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class CardWaitDialog extends JDialog {

	private final JLabel	jpb;

	public CardWaitDialog(Window parent) {
		super(parent);
		this.setModal(true);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.jpb = new JLabel();
		this.jpb.setBorder(BorderFactory.createRaisedBevelBorder());
		this.getContentPane().add(this.jpb, BorderLayout.CENTER);
	}

	public void setIcon(Icon icon) {
		this.jpb.setIcon(icon);
		Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		this.jpb.setPreferredSize(d);
		this.setSize(d);
	}

	@Override
	public void setVisible(boolean b) {
		this.setLocationRelativeTo(this.getParent());
		super.setVisible(b);
	}

}
