package com.opentach.client.util.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.ontimize.gui.ApplicationManager;

public final class JMessageDialog extends JDialog implements ActionListener {

	public static final int			ERROR_MESSAGE		= JOptionPane.ERROR_MESSAGE;
	public static final int			WARNING_MESSAGE		= JOptionPane.WARNING_MESSAGE;
	public static final int			INFORMATION_MESSAGE	= JOptionPane.INFORMATION_MESSAGE;

	private static final ImageIcon	ICABOUT				= new ImageIcon(JMessageDialog.class.getClassLoader().getResource("images/about32.png"));
	private static final ImageIcon	ICERROR				= new ImageIcon(JMessageDialog.class.getClassLoader().getResource("images/error32.png"));
	private static final ImageIcon	ICWARN				= new ImageIcon(JMessageDialog.class.getClassLoader().getResource("images/warning32.png"));

	private final Timer				timer;
	private int						timeout				= Integer.MAX_VALUE;
	private int						count				= 0;
	private final JLabel			jlMessage;
	private final JLabel			jlIcon;
	private final JButton			jbClose;
	private final String			title;

	private JMessageDialog(Window parent, String title) {
		super(parent);
		this.setModal(true);
		this.setResizable(false);
		this.setTitle(title);
		this.title = title;
		this.timer = new Timer(1000, this);
		this.jlMessage = new JLabel("");
		this.jlMessage.setHorizontalAlignment(SwingConstants.CENTER);
		this.jlIcon = new JLabel();
		this.jlIcon.setPreferredSize(new Dimension(48, 48));
		JLabel jlFill = new JLabel();
		jlFill.setPreferredSize(new Dimension(48, 48));
		this.jbClose = new JButton("<HTML> <B>" + ApplicationManager.getTranslation("ACEPTAR") + "</B></HTML>");
		this.jbClose.setPreferredSize(new Dimension(80, 30));
		this.jbClose.setSize(new Dimension(80, 30));
		this.jbClose.setMaximumSize(new Dimension(80, 30));
		this.jbClose.addActionListener(this);
		JPanel jpClose = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jpClose.add(this.jbClose);
		JPanel jpIcon = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jpIcon.add(this.jlIcon);
		JPanel jpFill = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jpFill.add(jlFill);
		Container cont = this.getContentPane();
		cont.setLayout(new BorderLayout());
		cont.add(this.jlMessage, BorderLayout.CENTER);
		cont.add(jpIcon, BorderLayout.WEST);
		cont.add(jpFill, BorderLayout.EAST);
		cont.add(jpClose, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.timer) {
			this.count += 1000;
			this.setTitle(this.title + " ( " + ((this.timeout - this.count) / 1000) + " s. )");
			if ((this.count == this.timeout) && this.isVisible()) {
				this.jbClose.doClick();
			}
		} else {
			this.setVisible(false);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			this.jbClose.requestFocus();
		} else {
			this.timer.stop();
		}
		super.setVisible(visible);
	}

	private Icon getIcon(int msgType) {
		switch (msgType) {
			case ERROR_MESSAGE:
				return JMessageDialog.ICERROR;
			case WARNING_MESSAGE:
				return JMessageDialog.ICWARN;
			case INFORMATION_MESSAGE:
				return JMessageDialog.ICABOUT;
			default:
				return null;
		}
	}

	public static void showMessageDialog(Window parent, String msg, String title, int msgType, int timeout) {
		JMessageDialog jmd = new JMessageDialog(parent, title);
		jmd.jlMessage.setText(msg);
		jmd.jlIcon.setIcon(jmd.getIcon(msgType));
		if (timeout > -1) {
			jmd.timeout = timeout;
			jmd.timer.start();
		}
		jmd.pack();
		jmd.setLocationRelativeTo(parent);
		jmd.setVisible(true);
	}

	public static void showMessageDialogWithTimeOut(Window parent, String msg, String title, int msgType) {
		JMessageDialog.showMessageDialog(parent, msg, title, msgType, 10000);
	}

	public static void showMessageDialogWithOutTimeOut(Window parent, String msg, String title, int msgType) {
		JMessageDialog.showMessageDialog(parent, msg, title, msgType, -1);
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame("Test");
		jf.setSize(800, 600);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		JMessageDialog.showMessageDialogWithTimeOut(jf, "asdasdasdasdasdasdasdasd", "joder!", JMessageDialog.ERROR_MESSAGE);
	}

}
