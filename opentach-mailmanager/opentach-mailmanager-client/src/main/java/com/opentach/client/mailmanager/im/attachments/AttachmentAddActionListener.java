package com.opentach.client.mailmanager.im.attachments;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.client.comp.NativeJFileChooser;
import com.opentach.client.util.ProgressInputStream;
import com.opentach.client.util.ProgressInputStream.IProgressListener;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.list.listeners.AbstractListComponentListener;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.gui.tasks.WorkerStatusInfo;
import com.utilmize.tools.thread.Paralelizer;

public class AttachmentAddActionListener extends AbstractListComponentListener {

	private static final Logger			logger	= LoggerFactory.getLogger(AttachmentAddActionListener.class);
	private JFileChooser				fileChooser;
	private final Map<Integer, Double>	progress;

	public AttachmentAddActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
		this.progress = new HashMap<>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.fileChooser == null) {
			this.fileChooser = new NativeJFileChooser();
		}
		this.fileChooser.setMultiSelectionEnabled(true);
		if (this.fileChooser.showOpenDialog(this.getForm()) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		new USwingWorker<EntityResult, Void>() {
			@Override
			protected EntityResult doInBackground() throws Exception {
				File[] selectedFiles = AttachmentAddActionListener.this.fileChooser.getSelectedFiles();
				List<Future<Pair<Object, File>>> res = new ArrayList<>();
				int i = 0;
				for (File file : selectedFiles) {
					Future<Pair<Object, File>> paralelizeInBackground = Paralelizer.paralelizeInBackground(
							new UploadAttachmentTask(file, AttachmentAddActionListener.this.getForm().getDataFieldValue(MailManagerNaming.MAI_ID), perone -> {
								AttachmentAddActionListener.this.progress.put(i, perone);
								this.fireStatusUpdate(new WorkerStatusInfo(MailManagerNaming.M_UPLOADING, null, AttachmentAddActionListener.this.computeProgress()));
							}));
					res.add(paralelizeInBackground);
				}
				EntityResult er = new EntityResult();
				for (Future<Pair<Object, File>> partial : res) {
					Pair<Object, File> pair = partial.get();
					er.addRecord(EntityResultTools.keysvalues( //
							MailManagerNaming.MAT_ID,pair.getFirst(), //
							MailManagerNaming.MAT_NAME, pair.getSecond().getName(), //
							MailManagerNaming.MAT_SIZE, pair.getSecond().length() //
							));
				}
				return er;
			}

			@Override
			protected void done() {
				try {
					AttachmentAddActionListener.this.getListComponent().parseAndAddComponent(this.uget());
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, AttachmentAddActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	protected int computeProgress() {
		int size = this.progress.size();
		double percent = 0;
		for (Entry<Integer, Double> entry : this.progress.entrySet()) {
			percent += entry.getValue() * 100;
		}
		return (int) (percent / size);

	}

	public static class UploadAttachmentTask implements Callable<Pair<Object, File>> {

		private final File				file;
		private final Object			malId;
		private final IProgressListener	listener;

		public UploadAttachmentTask(File file, Object malId, IProgressListener listener) {
			super();
			this.file = file;
			this.malId = malId;
			this.listener = listener;
		}

		@Override
		public Pair<Object, File> call() throws Exception {
			try (InputStream is = new ProgressInputStream(new FileInputStream(this.file), this.file.length(), this.listener)) {
				Object matId = BeansFactory.getBean(IMailManagerService.class).attachmentAdd(this.malId, this.file.getName(), is);
				return new Pair<>(matId, this.file);
			}
		}
	}

}
