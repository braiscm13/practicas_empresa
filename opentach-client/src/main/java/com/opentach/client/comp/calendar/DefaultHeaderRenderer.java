package com.opentach.client.comp.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

import net.sf.nachocalendar.components.HeaderPanel;
import net.sf.nachocalendar.components.HeaderRenderer;

public class DefaultHeaderRenderer extends JLabel implements HeaderRenderer {

	protected Color	weekendbg	= new Color(0xd5d9a1);
	protected Color	stdbg		= new Color(0xe3e6b8);

	public DefaultHeaderRenderer() {
		super();
		this.setOpaque(true);
		Font font = this.getFont();
		Font font1 = new Font(font.getName(), Font.BOLD, font.getSize());
		this.setFont(font1);

		this.setVerticalAlignment(0);
		this.setHorizontalAlignment(0);
	}

	@Override
	public Component getHeaderRenderer(HeaderPanel headerpanel, Object obj, boolean isheader, boolean isworking) {
		this.setText(obj.toString());
		try {
			Integer.parseInt((String) obj);
		} catch (Exception e) {
		}

		this.setBackground(("Dom".equals(obj) || "Sáb".equals(obj)) ? this.weekendbg : this.stdbg);
		return this;
	}
}