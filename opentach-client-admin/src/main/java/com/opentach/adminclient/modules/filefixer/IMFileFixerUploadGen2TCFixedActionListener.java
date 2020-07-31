package com.opentach.adminclient.modules.filefixer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.DecimalFormat;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.util.ZipUtils;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UFileDataField;

public class IMFileFixerUploadGen2TCFixedActionListener extends AbstractActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger		logger		= LoggerFactory.getLogger(IMFileFixerUploadGen2TCFixedActionListener.class);

	private final ExecutorService	executor	= Executors.newFixedThreadPool(40);

	private JProgressBar			progressBar	= null;
	private int						totalParts	= -1;
	private final AtomicInteger		current		= new AtomicInteger(-1);
	private final AtomicInteger		errors		= new AtomicInteger(-1);
	private final AtomicInteger		oks			= new AtomicInteger(-1);

	public IMFileFixerUploadGen2TCFixedActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		this.getButton().setEnabled(false);
		this.progressBar.setIndeterminate(true);
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				IMFileFixerUploadGen2TCFixedActionListener.this.doOperation();
				return null;
			}

			@Override
			protected void done() {
				try {
					this.get();
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMFileFixerUploadGen2TCFixedActionListener.logger);
					IMFileFixerUploadGen2TCFixedActionListener.this.reset();
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
		JComponent container = (JComponent) this.getForm().getElementReference("step3ProgresContainer");
		container.add(this.progressBar, new GridBagConstraints(-1, -1, 1, 0, 1.0f, 0.0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void doOperation() {
		File inputFolder = new File(((UFileDataField) this.getForm().getElementReference("step3Folder")).getText());
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
					this.uploadFile(inputFile);
					this.oks.incrementAndGet();
				} catch (Exception e1) {
					IMFileFixerUploadGen2TCFixedActionListener.logger.error(null, e1);
					this.errors.incrementAndGet();
				} finally {
					this.updateCount();
				}
			});
		}
	}

	private void uploadFile(File file) throws Exception {
		String name = file.getName();
		String[] split = name.split("#");

		String idFichero = split[0];
		String creationUser = split[1];
		String idOrigen = split[2];
		String nombProc = split[3];

		// comprimo y escribo en memoria para enviar posteriormente
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (ZipOutputStream zOut = new ZipOutputStream(bOut); InputStream is = new FileInputStream(file);) {
			ZipUtils.zip(is, nombProc, zOut);
		}

		BytesBlock zipContent = new BytesBlock(bOut.toByteArray());

		EntityResult er = this.getEntity("EFicherosTGD").update(//
				EntityResultTools.keysvalues(//
						"FICHERO", zipContent, //
						"TIPO", "TC"//
						), //
				EntityResultTools.keysvalues("IDFICHERO", idFichero), //
				this.getSessionId());
		CheckingTools.checkValidEntityResult(er);
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
						"Finalizado. OKs " + this.oks.get() + ", errores " + this.errors.get() + " de " + this.totalParts + " archivos",
						MessageType.INFORMATION, true);
			}
		});
	}

}
