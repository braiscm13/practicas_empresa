package com.opentach.adminclient.modules.filefixer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.DecimalFormat;
import com.imatia.tacho.model.GenerationEnum;
import com.imatia.tacho.model.TachoFile;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UFileDataField;

public class IMFileFixerFixGen2TCErrorActionListener extends AbstractActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger		logger		= LoggerFactory.getLogger(IMFileFixerFixGen2TCErrorActionListener.class);

	private final ExecutorService	executor	= Executors.newFixedThreadPool(40);

	private JProgressBar			progressBar	= null;
	private int						totalParts	= -1;
	private final AtomicInteger		current		= new AtomicInteger(-1);
	private final AtomicInteger		errors		= new AtomicInteger(-1);
	private final AtomicInteger		oks			= new AtomicInteger(-1);

	public IMFileFixerFixGen2TCErrorActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		this.getButton().setEnabled(false);
		this.progressBar.setIndeterminate(true);
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				IMFileFixerFixGen2TCErrorActionListener.this.doOperation();
				return null;
			}

			@Override
			protected void done() {
				try {
					this.get();
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMFileFixerFixGen2TCErrorActionListener.logger);
					IMFileFixerFixGen2TCErrorActionListener.this.reset();
				}
			}
		}.execute();
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		this.ensureProgressBar();
	}

	private void ensureProgressBar() {
		if (this.progressBar != null) {
			return;
		}
		this.progressBar = new JProgressBar();
		this.progressBar.setStringPainted(true);
		JComponent container = (JComponent) this.getForm().getElementReference("step2ProgresContainer");
		container.add(this.progressBar, new GridBagConstraints(-1, -1, 1, 0, 1.0f, 0.0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void doOperation() {
		File inputFolder = new File(((UFileDataField) this.getForm().getElementReference("step2FolderIn")).getText());
		File outputFolder = new File(((UFileDataField) this.getForm().getElementReference("step2FolderOut")).getText());
		File[] inputFiles = inputFolder.listFiles();
		this.totalParts = inputFiles.length;
		this.progressBar.setMaximum(this.totalParts);
		this.progressBar.setIndeterminate(false);
		this.current.set(0);
		this.errors.set(0);
		this.oks.set(0);
		for (File inputFile : inputFiles) {
			this.executor.submit(() -> {
				try {
					this.fixFile(inputFile, new File(outputFolder, inputFile.getName()));
				} catch (Exception e1) {
					IMFileFixerFixGen2TCErrorActionListener.logger.error(null, e1);
				} finally {
					this.updateCount();
				}
			});
		}
	}

	private void fixFile(File inputFile, File outputFile) throws Exception {
		com.imatia.tacho.model.tc.TCFile tc = (com.imatia.tacho.model.tc.TCFile) TachoFile.readTachoFile(inputFile);
		if (GenerationEnum.SECOND.equals(tc.getGeneration())) {
			if (tc.getEfCardCertificate().getData(GenerationEnum.SECOND) == null) {
				byte[] errBytes = FileTools.getBytesFromFile(inputFile);
				FileTools.copyFile(new ByteArrayInputStream(errBytes, 0, tc.getGen2Offset()), outputFile);
				this.oks.incrementAndGet();
			} else {
				IMFileFixerFixGen2TCErrorActionListener.logger.error("Error fixing file {}", inputFile.getAbsoluteFile());
				this.errors.incrementAndGet();
			}
		} else {
			IMFileFixerFixGen2TCErrorActionListener.logger.error("File is not second generation {}", inputFile.getAbsoluteFile());
			this.errors.incrementAndGet();
		}
	}

	private void reset() {
		this.getButton().setEnabled(true);
		this.progressBar.setValue(0);
		this.progressBar.setString("");
	}

	private void updateCount() {
		SwingUtilities.invokeLater(() -> {
			int value = this.current.incrementAndGet();
			this.progressBar.setValue(value);
			this.progressBar.setString(value + "/" + this.totalParts + " -  " + new DecimalFormat("#0.00%").format(this.progressBar.getPercentComplete()));
			if ((this.totalParts - value) == 0) {
				this.reset();
				MessageManager.getMessageManager().showMessage(this.getForm(),
						"Finalizado. Corregidos " + this.oks.get() + ", fallos " + this.errors.get() + " de " + this.totalParts + " archivos",
						MessageType.INFORMATION, true);
			}
		});
	}

}
