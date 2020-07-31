package com.opentach.client.simpledownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.model.scard.SmartCardMonitor;

public class SimpleDownloaderUI {

	private static final Logger		logger	= LoggerFactory.getLogger(SimpleDownloaderUI.class);
	private final SmartCardMonitor	scmonitor;
	private final ResourceBundle	bundle;

	public SimpleDownloaderUI() {
		super();
		this.bundle = ResourceBundle.getBundle("i18n-simpledownloader.bundle", Locale.getDefault(), Thread.currentThread().getContextClassLoader());
		this.scmonitor = new SmartCardMonitor();
		this.scmonitor.setAutoDownload(true);
		try {
			this.scmonitor.setRepositoryDir(File.createTempFile("non", "none").getParentFile());
		} catch (Exception err) {
			SimpleDownloaderUI.logger.error(null, err);
		}
	}

	public void startup() {
		SwingUtilities.invokeLater(this::startupSwing);
	}

	public void startupSwing() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception err) {
			SimpleDownloaderUI.logger.error(null, err);
		}

		JLabel labelInstruction = new JLabel();
		labelInstruction.setText(this.bundle.getString("instruction"));
		labelInstruction.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(labelInstruction);
		panel.add(progressBar);
		panel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("images-simpledownloader/ico.png")) {
			frame.setIconImage(ImageIO.read(is));
		} catch (IOException err) {
			SimpleDownloaderUI.logger.error(null, err);
		}
		frame.setTitle("Opentach - simple downloader");
		frame.setSize(500, 500);
		// frame.getContentPane().setLayout(new GridLayout());
		frame.getContentPane().add(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		this.scmonitor.addCardListener(new SmartCardListener(this.bundle, progressBar));
	}

}
