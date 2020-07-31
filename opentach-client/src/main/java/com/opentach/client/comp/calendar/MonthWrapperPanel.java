package com.opentach.client.comp.calendar;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.CellRendererPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sf.nachocalendar.components.MonthPanel;

public class MonthWrapperPanel extends JPanel {
	protected MonthPanel		monthPanel;
	// protected JPanel leftPanel;
	// protected JScrollPane rightPanel;
	protected CellRendererPane	rendererPane;

	// protected IPanelRenderer leftRenderer;
	// protected IPanelRenderer rightRenderer;

	public MonthWrapperPanel(boolean showWeekNumber) {
		super(new GridBagLayout());
		this.rendererPane = new CellRendererPane();
		//
		// JScrollPane scroll = new JScrollPane(this.rendererPane);
		// scroll.setAutoscrolls(true);
		// scroll.setWheelScrollingEnabled(true);
		// scroll.getVerticalScrollBar().setUnitIncrement(10);
		// view.setEnabled(true);
		// frame.getContentPane().add(scroll);
		// frame.setVisible(true);
		//

		// this.rightPanel = new JScrollPane() {
		// @Override
		// public void paint(Graphics g) {
		// if (MonthWrapperPanel.this.rightRenderer != null) {
		// JComponent c =
		// MonthWrapperPanel.this.rightRenderer.getRendererComponent(MonthWrapperPanel.this.monthPanel);
		// MonthWrapperPanel.this.rendererPane.paintComponent(g, c, this, 0, 0,
		// getWidth(), getHeight(), true);
		// }
		// }
		// };
		// this.rightPanel.setAutoscrolls(true);
		// this.rightPanel.setWheelScrollingEnabled(false);
		//
		// this.rightPanel.getVerticalScrollBar().setUnitIncrement(1);

		this.monthPanel = new MonthPanel(false);
		// this.add(
		// this.leftPanel,
		// new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		// GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.monthPanel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(0, 0, 0,
				0), 0, 0));
		// this.add(
		// this.rightPanel,
		// new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
		// GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new MonthPanel(), BorderLayout.CENTER);
		frame.setVisible(true);
	}

	public MonthPanel getMonthPanel() {
		return this.monthPanel;
	}

	// public void setLeftRenderer(IPanelRenderer leftRenderer) {
	// this.leftRenderer = leftRenderer;
	// }

	public void setRightRenderer(IPanelRenderer rightRenderer) {
		// this.rightRenderer = rightRenderer;
	}

}
