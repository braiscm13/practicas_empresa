package com.opentach.client.modules.social;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.WWWDataField;
import com.ontimize.gui.images.ImageManager;
import com.opentach.client.OpentachClientLocator;

public class SocialDialog {
	private static final Logger		LOGGER					= LoggerFactory.getLogger(SocialDialog.class);

	private static final Color		COLOR_LABEL_MOUSEOVER	= new Color(200, 200, 200);
	private static final Dimension	MINIMUM_SIZE			= new Dimension(215, 275);

	private static final String		FACEBOOKTURL			= "https://www.facebook.com/OPENTACH/";
	private static final String		TWITTERURL				= "https://twitter.com/Opentach";
	private static final String		YOUTUBEURL				= "https://www.youtube.com/user/OPENTACHSISTEMAS";
	private static final String		GOOGLEPLUS				= "https://plus.google.com/+Opentach";

	protected final JDialog			dialog;
	protected final JPanel			jlContent;
	protected final JLabel			jlTitle;

	protected Color					labelColor				= null;

	public SocialDialog(Window parent) {
		this.dialog = new JDialog(parent);
		this.dialog.setTitle(ApplicationManager.getTranslation("btn_social"));
		this.dialog.setModal(true);
		final JPanel jpMain = new JPanel(new BorderLayout());
		final JPanel jpNorth = new JPanel(new BorderLayout());
		final JPanel jpSouth = new JPanel(new BorderLayout());
		jpMain.setFocusable(true);
		jpNorth.setFocusable(true);
		jpSouth.setFocusable(true);
		this.jlContent = new JPanel();
		this.jlContent.setLayout(new GridLayout(4, 1));
		this.jlTitle = new JLabel();
		this.dialog.getContentPane().setLayout(new BorderLayout());

		this.dialog.getContentPane().add(jpNorth, BorderLayout.NORTH);
		this.dialog.getContentPane().add(jpMain, BorderLayout.CENTER);
		this.dialog.getContentPane().add(jpSouth, BorderLayout.SOUTH);

		this.dialog.setSize(SocialDialog.MINIMUM_SIZE);
		this.dialog.setMinimumSize(SocialDialog.MINIMUM_SIZE);
		this.dialog.setMaximumSize(SocialDialog.MINIMUM_SIZE);
		this.dialog.setResizable(false);

		this.dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				jpMain.requestFocus();
			}
		});

		Color borderColor = new Color(0x515151);
		this.jlTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.jlTitle.setHorizontalTextPosition(SwingConstants.TRAILING);
		this.jlTitle.setVerticalTextPosition(SwingConstants.CENTER);
		this.jlTitle.setPreferredSize(new Dimension(500, 40));
		this.jlTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

		jpSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

		jpNorth.add(this.jlTitle, BorderLayout.CENTER);
		jpNorth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

		JScrollPane scroll = new JScrollPane(this.jlContent);
		scroll.setAutoscrolls(false);
		scroll.setBorder(new EmptyBorder(5, 5, 5, 5));

		jpMain.setBorder(new LineBorder(borderColor, 5, true));
		jpMain.add(jpNorth, BorderLayout.NORTH);
		jpMain.add(scroll, BorderLayout.CENTER);
		jpMain.add(jpSouth, BorderLayout.SOUTH);
		jpMain.setOpaque(true);
		jpMain.setBackground(Color.white);

		jpMain.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					SocialDialog.this.dialog.setVisible(false);
				}
			}
		});

		this.jlTitle.setText(ApplicationManager.getTranslation("btn_social"));

		JLabel facebook = this.createLabel("com/opentach/client/rsc/facebook24.png", "Facebook", SocialDialog.FACEBOOKTURL);
		JLabel twitter = this.createLabel("com/opentach/client/rsc/twitter24.png", "Twitter", SocialDialog.TWITTERURL);
		JLabel youtube = this.createLabel("com/opentach/client/rsc/youtube24.png", "Youtube", SocialDialog.YOUTUBEURL);
		JLabel googlePlus = this.createLabel("com/opentach/client/rsc/googleplus24.png", "Google Plus", SocialDialog.GOOGLEPLUS);
		this.jlContent.add(facebook);
		this.jlContent.add(twitter);
		this.jlContent.add(youtube);
		this.jlContent.add(googlePlus);
	}

	public void setVisible(boolean visible) {
		this.dialog.setLocationRelativeTo(this.dialog.getParent());
		this.dialog.setVisible(visible);
	}

	public void showSocialNetworks() {
		this.setVisible(true);
	}

	private JLabel createLabel(String iconPath, String text, String url) {
		ImageIcon icon = ImageManager.getIcon(iconPath);
		JLabel label = new JLabel(text, icon, SwingConstants.LEFT);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				WWWDataField.processURL(url);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				label.setBackground(SocialDialog.COLOR_LABEL_MOUSEOVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				label.setBackground(SocialDialog.this.labelColor);
			}
		});
		label.setOpaque(true);
		label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		label.setIconTextGap(20);
		label.setFont(new Font("Verdana", Font.BOLD, 12));
		this.labelColor = label.getBackground();
		label.setForeground(Color.black);
		return label;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				SocialDialog s = new SocialDialog(null);
				s.showSocialNetworks();
			}
		});
	}

	public boolean isVisibleMessage() {
		JCheckBox jc = (JCheckBox) ((JPanel) ((JPanel) this.dialog.getContentPane().getComponent(0)).getComponent(2)).getComponent(0);
		Object[] ob = jc.getSelectedObjects();
		if ((ob != null) && (ob.length > 0)) {
			final OpentachClientLocator bref = (OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
			final String user = bref.getUser();
			try {
				ApplicationManager.getApplication().getPreferences().setPreference(user, "initial_information", "false");
				return false;
			} catch (Exception ex) {
			}
		}
		return true;
	}
}
