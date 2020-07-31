package com.opentach.client.modules.version;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.utilmize.client.gui.field.UFxHtmlDataField;

public class VersionDialog {

	private static final Logger	logger	= LoggerFactory.getLogger(VersionDialog.class);

	protected final JDialog	dialog;
	// protected final JLabel jlContent;
	protected final UFxHtmlDataField	jlContent;
	protected final JLabel	jlTitle;

	public VersionDialog(Window parent) {
		this.dialog = new JDialog(parent);
		this.dialog.setModal(true);
		this.dialog.setUndecorated(true);
		final JPanel jpMain = new JPanel(new BorderLayout());
		final JPanel jpNorth = new JPanel(new BorderLayout());
		final JPanel jpSouth = new JPanel(new BorderLayout());
		jpMain.setFocusable(true);
		jpNorth.setFocusable(true);
		jpSouth.setFocusable(true);
		// this.jlContent = new JLabel();
		this.jlContent = new UFxHtmlDataField(EntityResultTools.keysvalues("dim", "text", "labelvisible", "no"));
		this.jlTitle = new JLabel();
		this.dialog.getContentPane().setLayout(new BorderLayout());

		this.dialog.getContentPane().add(jpNorth, BorderLayout.NORTH);
		this.dialog.getContentPane().add(jpMain, BorderLayout.CENTER);
		this.dialog.getContentPane().add(jpSouth, BorderLayout.SOUTH);

		Color borderColor = new Color(0x515151);
		this.jlTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.jlTitle.setHorizontalTextPosition(SwingConstants.TRAILING);
		this.jlTitle.setVerticalTextPosition(SwingConstants.CENTER);
		this.jlTitle.setPreferredSize(new Dimension(800, 40));
		this.jlTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

		JCheckBox checkb = new JCheckBox();
		checkb.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
		checkb.setText("No volver a mostrar este mensaje");
		jpSouth.add(checkb, BorderLayout.SOUTH);
		jpSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

		JButton boton = new JButton("");
		ImageIcon ima = ImageManager.getIcon("com/opentach/client/rsc/exit24.png");
		boton.setIcon(ima);
		boton.setToolTipText(ApplicationManager.getTranslation("exit"));
		boton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				VersionDialog.this.setVisible(false);
			}
		});
		jpNorth.add(boton, BorderLayout.EAST);
		jpNorth.add(this.jlTitle, BorderLayout.CENTER);
		jpNorth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

		this.jlContent.setOpaque(true);
		// this.jlContent.setVerticalAlignment(SwingConstants.TOP);
		// this.jlContent.setVerticalTextPosition(SwingConstants.TOP);
		this.jlContent.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));

		JScrollPane scroll = new JScrollPane(this.jlContent);
		scroll.setAutoscrolls(false);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		jpMain.setBorder(new LineBorder(borderColor, 5, true));
		jpMain.add(jpNorth, BorderLayout.NORTH);
		jpMain.add(scroll, BorderLayout.CENTER);
		jpMain.add(jpSouth, BorderLayout.SOUTH);
		this.dialog.setSize(830, 600);

		this.dialog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				VersionDialog.this.dialog.setVisible(false);
			}
		});
		this.dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				jpMain.requestFocus();
			}
		});
		jpMain.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					VersionDialog.this.dialog.setVisible(false);
				}
			}
		});
	}

	public void setVisible(boolean visible) {
		this.dialog.setLocationRelativeTo(this.dialog.getParent());
		this.dialog.setVisible(visible);
	}

	public void showNewsVersion() {
		this.jlTitle.setText(ApplicationManager.getTranslation("NOVEDADES_VERSION"));

		String html = "";
		try {
			html = this.queryText(ApplicationManager.getApplication().getLocale().toString());
		} catch (Exception ex) {
			VersionDialog.logger.error(null, ex);
		}

		this.jlContent.setValue(html);
		// this.jlContent.setText(html);
	}

	private String queryText(String localeValue) throws Exception {
		if (localeValue == null) {
			localeValue = "default";
		}

		EntityResult res = ApplicationManager
				.getApplication()
				.getReferenceLocator()
				.getEntityReference("EPreferenciasServidor")
				.query(EntityResultTools.keysvalues("NOMBRE", "Version.News." + localeValue), EntityResultTools.attributes("VALOR"),
						ApplicationManager.getApplication().getReferenceLocator().getSessionId());
		CheckingTools.checkValidEntityResult(res, "ERROR_GETTING_NEWS", true, true, new Object[] {});
		String html = (String) res.getRecordValues(0).get("VALOR");

		if ((html == null) && !"default".equals(localeValue)) {
			html = this.queryText("default");
		}
		return html;
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
