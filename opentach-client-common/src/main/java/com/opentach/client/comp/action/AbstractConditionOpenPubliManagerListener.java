package com.opentach.client.comp.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;

public abstract class AbstractConditionOpenPubliManagerListener extends OpenManagerMenuActionListener {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractConditionOpenPubliManagerListener.class);
	private String				publiRsc;

	public AbstractConditionOpenPubliManagerListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	protected void init(Hashtable params) throws Exception {
		super.init(params);
		this.publiRsc = ParseUtilsExtended.getString((String) params.get("publirsc"), null);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			if (this.hastToShowPubli(evt)) {
				this.showPubli();
			} else {
				super.actionPerformed(evt);
			}
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, AbstractConditionOpenPubliManagerListener.logger);
		}

	}

	protected void showPubli() {
		PublicityDialog dialog = new PublicityDialog(ApplicationManager.getApplication().getFrame());
		dialog.showDialog(this.publiRsc);
	}

	protected abstract boolean hastToShowPubli(ActionEvent evt) throws Exception;

	public static class PublicityDialog {

		private static final Logger	logger	= LoggerFactory.getLogger(PublicityDialog.class);

		protected final JDialog		dialog;
		protected final JLabel		jlContent;

		public PublicityDialog(Window parent) {
			this.dialog = new JDialog(parent);
			this.dialog.setTitle(ApplicationManager.getTranslation("INFO"));
			this.dialog.setModal(true);
			final JPanel jpMain = new JPanel(new BorderLayout());
			final JPanel jpSouth = new JPanel(new BorderLayout());
			jpMain.setFocusable(true);
			jpSouth.setFocusable(true);
			this.jlContent = new JLabel();
			this.dialog.getContentPane().setLayout(new BorderLayout());

			this.dialog.getContentPane().add(jpMain, BorderLayout.CENTER);
			this.dialog.getContentPane().add(jpSouth, BorderLayout.SOUTH);

			Color borderColor = new Color(0x515151);

			jpSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

			this.jlContent.setOpaque(true);
			this.jlContent.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));

			jpMain.setBorder(new LineBorder(borderColor, 5, true));
			jpMain.add(this.jlContent, BorderLayout.CENTER);
			jpMain.add(jpSouth, BorderLayout.SOUTH);

			this.dialog.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					PublicityDialog.this.dialog.setVisible(false);
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
						PublicityDialog.this.dialog.setVisible(false);
					}
				}
			});
		}

		public void showDialog(String contentRsc) {

			this.jlContent.setIcon(ImageManager.getIcon(contentRsc));
			this.dialog.pack();
			this.dialog.setLocationRelativeTo(this.dialog.getParent());
			this.dialog.setVisible(true);
		}

	}
}
