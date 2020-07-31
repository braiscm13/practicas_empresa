package com.opentach.client.util.upload;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.zip.ZipOutputStream;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.TCFile;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.PermissionReferenceLocator;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.TGDFileInfo.TGDFileType;
import com.opentach.client.util.upload.UploadEvent.UploadEventType;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.ZipUtils;

public class TGDFileSendThread extends ExtendedOperationThread implements ITGDFileConstants {

	private static final Logger			logger		= LoggerFactory.getLogger(TGDFileSendThread.class);

	private final Map<String, Object>	cv;
	private final TGDFileInfo			fileInfo;
	private final Form					form;
	private final String				uriSonido;
	private final UploadNotifier		uploadNotifier;

	public static final int				BLOCK_SIZE	= 360 * 1024;

	public TGDFileSendThread(Form form, Map<String, Object> cv, TGDFileInfo fileInfo, String endsound, UploadNotifier un) {
		super(">> " + fileInfo.getOrigFile().getName());
		this.cv = cv;
		this.fileInfo = fileInfo;
		this.form = form;
		this.uriSonido = endsound;
		this.uploadNotifier = un;
	}

	private TachoFile getTachoFile(File file) throws Exception {
		try {
			return TachoFile.readTachoFile(file);
		} catch (Exception err) {
			TGDFileSendThread.logger.error("Invalid card driver or vehicle unit file format: {}", file.getName(), err);
			throw new Exception("Formato de fichero de tacografo o tarjeta de conductor no válido");
		}
	}

	private byte[] compressToByteArray(File f) throws Exception {
		FileInputStream fi = null;
		BufferedInputStream bi = null;
		ByteArrayOutputStream baos = null;
		ZipOutputStream zos = null;
		try {
			fi = new FileInputStream(this.fileInfo.getOrigFile());
			bi = new BufferedInputStream(fi);
			baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(new BufferedOutputStream(baos));
			ZipUtils.zip(bi, this.fileInfo.getOrigFile().getName(), zos);
		} finally {
			if (zos != null) {
				zos.close();
			}
			if (bi != null) {
				bi.close();
			}
		}
		return baos.toByteArray();
	}

	private void extractInfoFromFile(TGDFileInfo pi, Hashtable<String, Object> cv) throws Exception {
		TachoFile tgdf = this.getTachoFile(this.fileInfo.getOrigFile());

		Date dExtract = tgdf.getExtractTime(null);
		pi.setDate(dExtract);
		String validName = tgdf.computeFileName(null, com.imatia.tacho.model.TachoFile.FILENAME_FORMAT_SPAIN, null);
		String ownerName = tgdf.getOwnerName();
		String ownerId = tgdf.getOwnerId();

		pi.setUploadedFile(new File(this.fileInfo.getOrigFile().getParent(), validName));
		pi.setOwnerName(ownerName);
		pi.setOwnerId(ownerId);
		pi.setFileType(tgdf instanceof TCFile ? TGDFileType.TC : TGDFileType.VU);
	}

	private void updateStatus(String string) {
		if (string == null) {
			this.status = "";
			return;
		}
		this.status = ApplicationManager.getTranslation(string, this.form != null ? this.form.getResourceBundle() : ApplicationManager.getApplicationBundle());
	}

	private final void notifyUploadStatusChange(TGDFileInfo file, UploadEventType type, String msg) {
		if (this.uploadNotifier != null) {
			this.uploadNotifier.notifyUploadEvent(file, type, msg);
		}
	}

	@Override
	public void run() {
		this.hasStarted = true;
		this.updateStatus("InicializandoTransferencia");
		this.setPriority(Thread.MIN_PRIORITY);
		final long totalSize = this.fileInfo.getOrigFile().length();
		final PermissionReferenceLocator erl = (PermissionReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
		try {
			Hashtable<String, Object> cv = new Hashtable<>();
			if (cv != null) {
				cv.putAll(this.cv);
			}
			this.updateStatus("LeyendoFicheroEntrada");
			// Antes de enviar el fichero compruebo si es un fichero TGD Valido y extraigo su informacion.
			this.extractInfoFromFile(this.fileInfo, cv);

			byte[] fileData = FileTools.getBytesFromFile(this.fileInfo.getOrigFile());
			String filename = this.fileInfo.getOrigFile().getName();
			this.progressDivisions = (int) totalSize;
			// Escribo
			this.uploadFile(erl.getSessionId(), filename, cv, fileData);
			if (this.isCancelled()) {
				this.updateStatus("Cancelado");
				return;
			}

			this.updateStatus("Finalizado");
			this.currentPosition = this.progressDivisions;
			if (this.uriSonido != null) {
				try {
					ApplicationManager.playSound(this.uriSonido);
				} catch (Exception ex) {
					TGDFileSendThread.logger.error("Error playing sound {}", this.uriSonido);
				}
			}
			this.fileInfo.setSuccess(true);
			this.notifyUploadStatusChange(this.fileInfo, UploadEventType.FILE_UPLOAD_END, this.status);
			this.onSuccess();
		} catch (CancellationException ex) {
			return;
		} catch (Throwable error) {
			this.onError(error);
		} finally {
			this.hasFinished = true;
			this.onFinished();
		}
	}

	protected void onFinished() {
		// for superclasses
	}

	protected void onSuccess() {
		// for superclasses
	}

	protected void onError(Throwable error) {
		TGDFileSendThread.logger.error(null, error);
		EntityResult eRes = new EntityResult();
		eRes.setCode(EntityResult.OPERATION_WRONG);
		eRes.setMessage(error.getMessage());
		this.res = eRes;
		this.updateStatus(MessageManager.getMessageManager().getCauseException(error).getMessage());

		this.fileInfo.setSuccess(false);

		// TODO: ver si aqui se pueden meter mas excepciones
		if (error instanceof UnknownHostException) {
			this.notifyUploadStatusChange(this.fileInfo, UploadEventType.FILE_UPLOAD_ERROR, this.status);
		} else if (error instanceof Exception) {
			this.notifyUploadStatusChange(this.fileInfo, UploadEventType.DRIVER_VEH_ERROR, this.status);
		} else {
			this.notifyUploadStatusChange(this.fileInfo, UploadEventType.FILE_UPLOAD_ERROR, this.status);
		}
	}

	protected void showErrorMessage(String message) {
		MainApplication opc = (MainApplication) ApplicationManager.getApplication();
		// opc.getActiveForm().message(message, Form.ERROR_MESSAGE);

		final JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(opc.getActiveForm()), ApplicationManager.getTranslation("Informacion"));
		JLabel infos = new JLabel(this.status);
		infos.setPreferredSize(new Dimension(350, 80));
		dlg.getContentPane().add(infos, BorderLayout.CENTER);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.pack();
		dlg.setLocationRelativeTo(opc.getActiveForm());

		final Timer timer = new Timer(5000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (dlg.isVisible()) {
					dlg.setVisible(false);
				}
				dlg.dispose();
			}
		});
		timer.start();
		dlg.setVisible(true);
	}

	private void uploadFile(int sesId, String filename, Hashtable<String, Object> cv, byte[] fileData) throws Exception {
		// new UploadFileStrategyEFicherosTGD().upload(sesId, filename, cv, fileData, new ProgressNotifier());
		new UploadFileStrategyRestClient().upload(sesId, filename, (String) cv.get(OpentachFieldNames.CIF_FIELD), fileData, new ProgressNotifier());
	}

	public class ProgressNotifier {
		public boolean isCancelled() {
			return TGDFileSendThread.this.cancelled;
		}

		public void setEstimatedTimeLeft(int ms) {
			TGDFileSendThread.this.estimatedTimeLeft = ms;
		}

		public void setCurrentPosition(int pos) {
			TGDFileSendThread.this.currentPosition = pos;
		}

		public void updateStatus(String st) {
			TGDFileSendThread.this.status = st;
		}
	}
}
