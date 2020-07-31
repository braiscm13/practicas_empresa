package com.opentach.client.simpledownloader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.extractor.AbstractExtractor;
import com.imatia.tacho.model.TachoFile;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;
import com.opentach.model.scard.CardListener;

public class SmartCardListener implements CardListener {

	private static final Logger		logger	= LoggerFactory.getLogger(SmartCardListener.class);

	private final JProgressBar		progressBar;
	private final JFileChooser		fileChooser;
	private final ResourceBundle	bundle;

	public SmartCardListener(ResourceBundle bundle, JProgressBar progressBar) {
		super();
		this.progressBar = progressBar;
		this.fileChooser = new NativeJFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.bundle = bundle;

	}

	@Override
	public void cardStatusChange(CardEvent ce) {
		if (CardEventType.CARD_DOWNLOAD_START.equals(ce.getType())) {
			AbstractExtractor.DEBUG = true;
			this.progressBar.setVisible(true);
		} else if (CardEventType.CARD_DOWNLOAD_END.equals(ce.getType())) {
			this.progressBar.setVisible(false);
			File selectedFolder = null;
			while ((selectedFolder == null) || !selectedFolder.exists()) {
				if (JFileChooser.APPROVE_OPTION == this.fileChooser.showSaveDialog(this.progressBar)) {
					selectedFolder = this.fileChooser.getSelectedFile();
					if (selectedFolder.exists()) {
						try {
							String fileName = TachoFile.readTachoFile(ce.getFile()).computeFileName(null, TachoFile.FILENAME_FORMAT_SPAIN, null);
							Path selectedFile = selectedFolder.toPath().resolve(fileName);
							Files.copy(ce.getFile().toPath(), selectedFile);
							JOptionPane.showConfirmDialog(this.progressBar, this.bundle.getString("msg.downloadok"), this.bundle.getString("msg.title"), JOptionPane.DEFAULT_OPTION,
									JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception err) {
							SmartCardListener.logger.error(null, err);
							JOptionPane.showConfirmDialog(this.progressBar, err.getLocalizedMessage(), this.bundle.getString("err.title"), JOptionPane.DEFAULT_OPTION,
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showConfirmDialog(this.progressBar, this.bundle.getString("wrong_folder"), this.bundle.getString("err.title"), JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		} else if (CardEventType.CARD_ERROR.equals(ce.getType())) {
			this.progressBar.setVisible(false);
			SmartCardListener.logger.error(null, new Exception(ce.getMessage()));
			JOptionPane.showConfirmDialog(this.progressBar, this.bundle.getString("err.downloading") + ce.getMessage(), this.bundle.getString("err.title"),
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
	}
}