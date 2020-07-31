package com.opentach.client.util.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.tc.TCFile;
import com.ontimize.db.Entity;
import com.ontimize.db.FileManagementEntity;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.util.HideThreadMonitor;
import com.opentach.client.util.StoreLogParser;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.operationmonitor.OperationMonitor;
import com.opentach.client.util.upload.TGDFileSendThread;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.tachofiles.ITachoFileRecordService;
import com.opentach.common.util.ZipUtils;

public final class DownloadMonitor implements ITGDFileConstants {

	private static final Logger	logger			= LoggerFactory.getLogger(DownloadMonitor.class);

	private final String		uriSonido;
	private final Object		lock			= new Object();
	private HideThreadMonitor	htm				= null;
	// para sincronizacion interna
	private final Semaphore		sem				= new Semaphore(2);

	public final static String	RUNNING_TAG		= "uploadmonitor.send2store";
	public final static String	INTERVAL_TAG	= "uploadmonitor.timeout";

	public DownloadMonitor(HideThreadMonitor htm, String uriSonido) {
		this.htm = htm;
		this.uriSonido = uriSonido;
	}

	public Object getLock() {
		return this.lock;
	}

	public void descargarFichero(final Object idFile, String tipo, final String cgContrato, final File fSelec, Form form) {
		this.descargarFichero(idFile, tipo, cgContrato, fSelec, form, null);
	}

	public void descargarFichero(final Object idFile, String tipo, final String cgContrato, final File destFile, Form form, IFileRenamer renamer) {
		final Form f = form;
		try {
			final EntityReferenceLocator brefs = f.getFormManager().getReferenceLocator();
			Entity ent = brefs.getEntityReference(ITGDFileConstants.FILE_ENTITY);
			if (!(ent instanceof FileManagementEntity)) {
				throw new Exception("La entidad " + ITGDFileConstants.FILE_ENTITY + " no implementa EntidadGestionArchivos");
			}
			final Hashtable<String, Object> cv = new Hashtable<String, Object>(1);
			cv.put(OpentachFieldNames.IDFILE_FIELD, idFile);
			final FileManagementEntity entF = (FileManagementEntity) ent;
			final String store = destFile.getParentFile().getAbsolutePath();
			DownloadMonitor.logger.info("DescargarFicheroAdjunto " + destFile.getCanonicalPath());
			ExtendedOperationThread eop = new ExtendedOperationThread(ApplicationManager.getTranslation("<< ", f.getResourceBundle()) + " " + destFile.getName()) {
				@Override
				public void run() {
					this.hasStarted = true;
					File fSelec = destFile;
					DownloadMonitor.this.sem.acquireUninterruptibly();
					final int blockSize = TGDFileSendThread.BLOCK_SIZE;
					this.setPriority(Thread.MIN_PRIORITY);
					BufferedOutputStream bOut = null;
					BufferedInputStream bi = null;
					try {
						this.status = ApplicationManager.getTranslation("INICIANDO_TRANSFERENCIA", f.getResourceBundle());
						String rId = entF.prepareToTransfer(cv, brefs.getSessionId());
						long totalSize = entF.getSize(rId);
						this.status = ApplicationManager.getTranslation("ARCHIVO_DESCARGADO", f.getResourceBundle());
						this.progressDivisions = (int) totalSize;
						ByteArrayOutputStream baout = new ByteArrayOutputStream();
						bOut = new BufferedOutputStream(baout);
						BytesBlock by = null;
						int leidosTotales = 0;
						long tIni = System.currentTimeMillis();
						long tiempoTranscurrido = 0;
						while ((by = entF.getBytes(rId, leidosTotales, blockSize, brefs.getSessionId())) != null) {
							Thread.yield(); // Permite que la barra de progreso
							// se refresque más dinamicamente.
							bOut.write(by.getBytes());
							leidosTotales = leidosTotales + by.getBytes().length;
							DownloadMonitor.logger.info("Descargados {} bytes", leidosTotales);
							this.currentPosition = leidosTotales;
							if (this.isCancelled()) {
								this.hasFinished = true;
								this.status = ApplicationManager.getTranslation("OPERACION_CANCELADA", f.getResourceBundle());
								bOut.close();
								return;
							}
							try {
								Thread.sleep(20);
							} catch (Exception ex) {
							}

							tiempoTranscurrido = System.currentTimeMillis() - tIni;
							this.estimatedTimeLeft = (int) (((totalSize - leidosTotales) * tiempoTranscurrido) / (float) leidosTotales);
						}
						// Es necesario cerrar el buffered o hacer un flush para
						// que los datos leidos esten disponibles.
						bOut.close();

						ByteArrayInputStream bai = new ByteArrayInputStream(baout.toByteArray());
						bi = new BufferedInputStream(bai);
						// Los ficheros se almacenan comprimidos.
						String name = ZipUtils.unzip(bi, store);
						if (name != null) {
							File file = new File(store + File.separator + name);
							DownloadMonitor.fixFile(file);
							if (renamer != null) {
								String newName = renamer.renameFile(file, name, fSelec.getName());
								if (newName != null) {
									fSelec = new File(fSelec.getParentFile(), newName);
								}
							}
							boolean resul = file.renameTo(fSelec);
							DownloadMonitor.logger.info("Renaming file = {}", resul);

						}
						this.currentPosition = this.progressDivisions;
						this.status = ApplicationManager.getTranslation("Finalizado", f.getResourceBundle());
						if (DownloadMonitor.this.uriSonido != null) {
							ApplicationManager.playSound(DownloadMonitor.this.uriSonido);
						}
						// Actualizo los campos de descarga.
						if (cgContrato != null) {
							DownloadMonitor.this.updateTrace((UserInfoProvider) brefs, idFile, cgContrato, fSelec.getName());
						}
					} catch (Exception e) {
						DownloadMonitor.logger.error(null, e);
						this.res = e.getMessage();
						this.status = ApplicationManager.getTranslation("Error", f.getResourceBundle()) + " : " + e.getMessage();
					} catch (Error e) {
						DownloadMonitor.logger.error(null, e);
						this.res = e.getMessage();
						this.status = ApplicationManager.getTranslation("Error", f.getResourceBundle()) + " : " + e.getMessage();
					} finally {
						try {
							if (bi != null) {
								bi.close();
							}
						} catch (Exception e) {
						}
						try {
							if (bOut != null) {
								bOut.close();
							}
						} catch (Exception e) {
						}
						DownloadMonitor.this.sem.release();
						synchronized (DownloadMonitor.this.lock) {
							DownloadMonitor.this.lock.notifyAll();
						}
						this.hasFinished = true;
					}
				}
			};
			// Escribo el el registro del almacen.
			String dir = destFile.getParent();
			String name = destFile.getName();
			StoreLogParser.registerDownloadFile(new File(dir + File.separator + name));
			OperationMonitor.ExtOpThreadsMonitor threadsMonitor = OperationMonitor.getExtOpThreadsMonitor();
			threadsMonitor.addExtOpThread(eop);
			this.htm.add(threadsMonitor);
			threadsMonitor.setVisible(true);
		} catch (Exception ex) {
			DownloadMonitor.logger.error(null, ex);
			f.message(ex.getMessage(), Form.ERROR_MESSAGE);
		}
	}

	/**
	 * Fix signature problems
	 *
	 * @param file
	 */
	private static void fixFile(File file) {
		try {
			byte[] fileByte = DownloadMonitor.readFile(file);
			ByteArrayInputStream fixedFileIs = DownloadMonitor.fixFile(fileByte);
			if (fixedFileIs != null) {
				file.delete();
				FileTools.copyFile(fixedFileIs, file);
			}
		} catch (Exception ex) {
			DownloadMonitor.logger.error(null, ex);
		}
	}

	private static byte[] readFile(File file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			FileTools.copyFile(fis, baos);
			fis.close();
			return baos.toByteArray();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
					DownloadMonitor.logger.error(null, ex);
				}
			}
		}
	}

	private final static int	SIGNATURE_LENGHT	= 128 + DownloadMonitor.TLV_LENGTH;
	private final static int	ICC_LENGHT			= 25 + DownloadMonitor.TLV_LENGTH;
	private final static int	IC_LENGHT			= 8 + DownloadMonitor.TLV_LENGTH;
	private final static int	APP_ID_LENGHT		= 10 + DownloadMonitor.TLV_LENGTH + DownloadMonitor.SIGNATURE_LENGHT;
	private final static int	CARD_CERT_LENGHT	= 194 + DownloadMonitor.TLV_LENGTH;
	private final static int	CA_CERT_LENGHT		= 194 + DownloadMonitor.TLV_LENGTH;
	private final static int	TLV_LENGTH			= 5;
	private final static int	PREVIOUS_LENGHT		= DownloadMonitor.ICC_LENGHT + DownloadMonitor.IC_LENGHT + DownloadMonitor.APP_ID_LENGHT + DownloadMonitor.CARD_CERT_LENGHT;

	private static ByteArrayInputStream fixFile(byte[] bais) {
		try {
			byte[] res = new byte[bais.length];
			System.arraycopy(bais, 0, res, 0, bais.length);
			TCFile tcFile = new TCFile();
			tcFile.setValue(bais);
			boolean needFix = false;
			if (tcFile.getEfCardCertificate().getJoinedData().hasSignature()) {
				needFix = true;
				byte[] tmp = new byte[res.length - DownloadMonitor.SIGNATURE_LENGHT];
				System.arraycopy(res, 0, tmp, 0, DownloadMonitor.PREVIOUS_LENGHT);
				System.arraycopy(res, DownloadMonitor.PREVIOUS_LENGHT + DownloadMonitor.SIGNATURE_LENGHT, tmp, DownloadMonitor.PREVIOUS_LENGHT,
						tmp.length - DownloadMonitor.PREVIOUS_LENGHT);
				res = tmp;
			}
			if (tcFile.getEfCACertificate().getJoinedData().hasSignature()) {
				needFix = true;
				byte[] tmp = new byte[res.length - DownloadMonitor.SIGNATURE_LENGHT];
				System.arraycopy(res, 0, tmp, 0, DownloadMonitor.PREVIOUS_LENGHT + DownloadMonitor.CA_CERT_LENGHT);
				System.arraycopy(res, DownloadMonitor.PREVIOUS_LENGHT + DownloadMonitor.CA_CERT_LENGHT + DownloadMonitor.SIGNATURE_LENGHT, tmp,
						DownloadMonitor.PREVIOUS_LENGHT + DownloadMonitor.CA_CERT_LENGHT, tmp.length - DownloadMonitor.PREVIOUS_LENGHT - DownloadMonitor.CA_CERT_LENGHT);
				res = tmp;
			}
			if (needFix) {
				return new ByteArrayInputStream(res);
			}
		} catch (Exception ex) {
			// do nothing
		}
		return null;
	}

	private void updateTrace(final UserInfoProvider brefs, final Object idFile, final String cgContrato, final String name) {
		try {
			brefs.getRemoteService(ITachoFileRecordService.class).saveFileRecord(idFile, cgContrato, ITachoFileRecordService.DOWNLOAD, name, null, null, null, false, null, null,
					null, null, "N", brefs.getSessionId());
		} catch (Exception e) {
			DownloadMonitor.logger.error(null, e);
		}
	}

}
