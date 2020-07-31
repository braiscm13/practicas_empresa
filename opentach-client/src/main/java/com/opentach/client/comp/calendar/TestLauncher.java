package com.opentach.client.comp.calendar;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sf.nachocalendar.components.MonthPanel;

public class TestLauncher {
	public static void main(String[] args) {
		JFrame frame = new JFrame("hola");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		CustomCalendarPanel view = new CustomCalendarPanel(12, 0);
		// CalendarPanel view = new CalendarPanel(3);
		// view.setMonthLeftPanelRenderer(new CustomLeftRenderer());
		view.setMonthRightPanelRenderer(new CustomLeftRenderer());
		JScrollPane scroll = new JScrollPane(view);
		scroll.setAutoscrolls(true);
		scroll.setWheelScrollingEnabled(true);
		scroll.getVerticalScrollBar().setUnitIncrement(10);
		view.setEnabled(true);
		frame.getContentPane().add(scroll);
		frame.setVisible(true);
	}

	public static class CustomLeftRenderer extends JLabel implements IPanelRenderer {
		@Override
		public JComponent getRendererComponent(MonthPanel month) {
			this.setVerticalAlignment(SwingConstants.TOP);
			this.setText("<html><FONT COLOR=RED>Red</FONT> and " + "<FONT COLOR=BLUE>Blue</FONT> Text</html>");
			return this;
		}
	}
}
