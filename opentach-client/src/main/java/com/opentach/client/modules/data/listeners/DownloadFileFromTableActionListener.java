package com.opentach.client.modules.data.listeners;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.util.HessianFactory;
import com.opentach.client.util.ProgressInputStream;
import com.opentach.client.util.ProgressInputStream.IProgressListener;
import com.opentach.common.cloudfiles.ICloudFilesHessianService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.gui.tasks.WorkerStatusInfo;

public class DownloadFileFromTableActionListener extends AbstractActionListenerButton {

	private static final String	M_DONWLOADINGFILE	= "M_DONWLOADINGFILE";
	private static final Logger	logger	= LoggerFactory.getLogger(DownloadFileFromTableActionListener.class);
	private String				keyName;
	private String				tableName;
	private JFileChooser		chooser;

	public DownloadFileFromTableActionListener() throws Exception {
		super();
	}

	public DownloadFileFromTableActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public DownloadFileFromTableActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public DownloadFileFromTableActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.tableName = (String) params.get("table");
		this.keyName = (String) params.get("fileid");
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		final Table table = (Table) this.getForm().getElementReference(this.tableName);
		table.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				DownloadFileFromTableActionListener.this.getButton().setEnabled(table.getSelectedRows().length > 0);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new USwingWorker<Object, Void>() {

			@Override
			protected Object doInBackground() throws Exception {
				Table table = (Table) DownloadFileFromTableActionListener.this.getForm().getElementReference(
						DownloadFileFromTableActionListener.this.tableName);
				CheckingTools.failIf(table.getSelectedRows().length == 0, "E_SELECT_ONE_RECORD");
				Map<?, ?> rowData = table.getSelectedRowData();
				Object fileId = ((List) rowData.get(DownloadFileFromTableActionListener.this.keyName)).get(0);
				String fileName = (String) ((List) rowData.get("NAME")).get(0);
				Number fileSize = (Number) ((List) rowData.get("FILESIZE")).get(0);
				File folder = DownloadFileFromTableActionListener.this.chooseFolder();
				if (folder == null) {
					return null;
				}
				this.fireStatusUpdate(new WorkerStatusInfo(DownloadFileFromTableActionListener.M_DONWLOADINGFILE, null, 0));
				InputStream downloadFile = HessianFactory.getFactory().getService(ICloudFilesHessianService.class, "/cloud")
						.downloadFile(fileId, DownloadFileFromTableActionListener.this.getSessionId());
				ProgressInputStream pis = new ProgressInputStream(downloadFile, fileSize.longValue(), new IProgressListener() {
					@Override
					public void setProgress(double perone) {
						fireStatusUpdate(new WorkerStatusInfo(DownloadFileFromTableActionListener.M_DONWLOADINGFILE, null, (int) (perone * 100)));
					}
				});
				File file = new File(folder, fileName);
				try (FileOutputStream os = new FileOutputStream(file)) {
					IOUtils.copy(pis, os);
				}
				return file;
			}

			@Override
			protected void done() {
				try {
					super.done();
					Object uget = this.uget();
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, DownloadFileFromTableActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	private File chooseFolder() {
		if (this.chooser == null) {
			this.chooser = new JFileChooser();
			this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			this.chooser.setAcceptAllFileFilterUsed(false);
		}

		if (this.chooser.showSaveDialog(this.getForm()) == JFileChooser.APPROVE_OPTION) {
			return this.chooser.getSelectedFile();
		}
		return null;
	}

}
