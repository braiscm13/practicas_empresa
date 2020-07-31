package com.opentach.downclient.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.imlabs.client.laf.common.UOntimizeLookAndFeel;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.plaf.utils.StyleUtil;
import com.opentach.downclient.DownCenterLAF;
import com.opentach.downclient.component.RoundPanel;
import com.utilmize.client.gui.Column;
import com.utilmize.client.gui.field.ULabel;

public class RoundPanelTest {

	public RoundPanelTest() {}

	public static void main(String[] args) {
		try {
			System.setProperty(StyleUtil.STYLE_PROPERTY, "downcenter.css");
			UOntimizeLookAndFeel uOntimizeLookAndFeel = new DownCenterLAF();
			// uOntimizeLookAndFeel.defineCustomColumn("\"BackgroundColumn\"");
			UIManager.setLookAndFeel(uOntimizeLookAndFeel);
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 600);
			frame.getContentPane().setLayout(new BorderLayout());
			Hashtable<Object, Object> keysvalues = EntityResultTools.keysvalues(//
					"uiname", "BackgroundColumn",//
					"expand", "yes"//
					);
			Column col = new Column(keysvalues);
			RoundPanel comp = new RoundPanel(new Hashtable<>()) {
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(500, 500);
				}
			};
			Hashtable parameters = EntityResultTools.keysvalues(//
					"text", "hola"//
					);
			comp.add(new ULabel(parameters), new GridBagConstraints());

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			panel.setBorder(new EmptyBorder(0, 0, 0, 0));
			panel.add(new JLabel("adios"));
			panel.setOpaque(false);

			comp.add(panel, new GridBagConstraints());
			col.add(comp, new GridBagConstraints());

			frame.getContentPane().add(col, BorderLayout.CENTER);
			frame.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
