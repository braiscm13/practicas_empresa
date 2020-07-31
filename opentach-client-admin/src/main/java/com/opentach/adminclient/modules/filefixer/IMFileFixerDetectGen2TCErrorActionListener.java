package com.opentach.adminclient.modules.filefixer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
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
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.util.ZipUtils;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UFileDataField;

public class IMFileFixerDetectGen2TCErrorActionListener extends AbstractActionListenerButton {
	/** The CONSTANT logger */
	private static final Logger		logger		= LoggerFactory.getLogger(IMFileFixerDetectGen2TCErrorActionListener.class);

	private final ExecutorService	executor	= Executors.newFixedThreadPool(40);

	private JProgressBar			progressBar	= null;

	private int						totalParts	= -1;
	private final AtomicInteger		current		= new AtomicInteger(-1);
	private final AtomicInteger		gen2Errors	= new AtomicInteger(-1);
	private final AtomicInteger		gen2Oks		= new AtomicInteger(-1);

	public IMFileFixerDetectGen2TCErrorActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		this.getButton().setEnabled(false);
		IMFileFixerDetectGen2TCErrorActionListener.this.progressBar.setIndeterminate(true);
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				IMFileFixerDetectGen2TCErrorActionListener.this.doOperation();
				return null;
			}

			@Override
			protected void done() {
				try {
					this.get();
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMFileFixerDetectGen2TCErrorActionListener.logger);
					IMFileFixerDetectGen2TCErrorActionListener.this.reset();
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
		JComponent container = (JComponent) this.getForm().getElementReference("step1ProgresContainer");
		container.add(this.progressBar, new GridBagConstraints(-1, -1, 1, 0, 1.0f, 0.0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void doOperation() throws Exception {
		File folder = new File(((UFileDataField) this.getForm().getElementReference("step1Folder")).getText());
		Date from = (Date) this.getForm().getDataFieldValue("step1DateFrom");

		EntityResult er = this.getEntity("EFicherosTGD").query(//
				EntityResultTools.keysvalues(//
						"F_ALTA", new SearchValue(SearchValue.MORE_EQUAL, from), //
						"TIPO", "TC"//
						), EntityResultTools.attributes("IDFICHERO"), this.getSessionId());
		this.totalParts = er.calculateRecordNumber();
		this.progressBar.setMaximum(this.totalParts);
		this.progressBar.setIndeterminate(false);
		this.current.set(0);
		this.gen2Errors.set(0);
		this.gen2Oks.set(0);
		List<Object> vIds = (List<Object>) er.get("IDFICHERO");
		vIds.stream().forEach(id -> this.executor.submit(() -> {
			try {
				this.checkFile(id, folder, from);
			} catch (Exception e1) {
				IMFileFixerDetectGen2TCErrorActionListener.logger.error(null, e1);
			} finally {
				this.updateCount();
			}
		}));
	}

	public void checkFile(Object idFichero, File folder, Date from) throws Exception {
		EntityResult erF = this.getEntity("EFicherosTGD")
				.query(EntityResultTools.keysvalues(//
						"IDFICHERO", idFichero, //
						"F_ALTA", new SearchValue(SearchValue.MORE, from), //
						"TIPO", "TC"//
						), EntityResultTools.attributes("F_ALTA", "USUARIO_ALTA", "NOMB_PROCESADO", "IDORIGEN", "FICHERO"), this.getSessionId());
		int num = erF.calculateRecordNumber();
		if (num != 1) {
			IMFileFixerDetectGen2TCErrorActionListener.logger.error("Esto no deberia pasar con el fichero: {} se encuentra {} registros", idFichero, num);
			throw new OpentachException("esto no deberia pasar");
		}
		Hashtable recordValues = erF.getRecordValues(0);

		String fileName = idFichero + "#";
		fileName += recordValues.get("USUARIO_ALTA") + "#";
		fileName += recordValues.get("IDORIGEN") + "#";
		fileName += recordValues.get("NOMB_PROCESADO");
		BytesBlock zipContent = (BytesBlock) recordValues.get("FICHERO");
		List<ByteArrayInputStream> unzip = ZipUtils.unzip(new ByteArrayInputStream(zipContent.getBytes()));
		byte[] content = new byte[unzip.get(0).available()];
		unzip.get(0).read(content);
		com.imatia.tacho.model.tc.TCFile tc = (com.imatia.tacho.model.tc.TCFile) TachoFile.readTachoFile(content);
		if (GenerationEnum.SECOND.equals(tc.getGeneration())) {
			if (tc.getEfCardCertificate().getData(GenerationEnum.SECOND) == null) {
				File file = new File(folder, fileName);
				FileTools.copyFile(new ByteArrayInputStream(content), file);
				this.gen2Errors.incrementAndGet();
			} else {
				// File file = new File(new File(folder, "ok"), fileName);
				// FileTools.copyFile(new ByteArrayInputStream(content), file);
				this.gen2Oks.incrementAndGet();
			}
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
						"Finalizado. Encontrados " + this.gen2Errors.get() + " errores de gen2 y " + this.gen2Oks.get() + " correctos de gen 2 en " + this.totalParts + " archivos",
						MessageType.INFORMATION, true);
			}
		});
	}

}
