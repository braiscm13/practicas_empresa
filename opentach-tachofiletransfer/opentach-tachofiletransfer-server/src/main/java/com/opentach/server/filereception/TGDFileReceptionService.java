package com.opentach.server.filereception;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.RandomStringGenerator;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.filereception.ITGDFileReceptionService;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.interfaces.AdvancedFileManagementEntity;
import com.opentach.common.util.ZipUtils;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.QueueNames;
import com.opentach.messagequeue.api.messages.PostFinishReceivingMessage;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.companies.ContractService;
import com.opentach.server.entities.EFicherosTGD;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.tachofiles.TachoFileTools;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class TGDFileReceptionService.
 */
public class TGDFileReceptionService extends UAbstractService implements ITGDFileReceptionService, AdvancedFileManagementEntity {

	/** The Constant logger. */
	private static final Logger					logger								= LoggerFactory.getLogger(TGDFileReceptionService.class);

	/** The Constant M_NO_SE_ENCUENTRA_CONTRATO. */
	public static final String					M_NO_SE_ENCUENTRA_CONTRATO			= "M_NO_SE_ENCUENTRA_CONTRATO";

	/** The Constant FLAG_INSERT_SOURCE. */
	public static final String					FLAG_INSERT_SOURCE					= "INSERT_ORIGEN";

	/** Indica que si no se encuentra un contrato para el conductor/vehiculo del fichero se asignará al contrato dummy definido en la aplicación. */
	public static final String					FLAG_INSERT_FILE_IN_DUMMY_CONTRACT	= "INSERT_FILE_IN_DUMMY_CONTRACT";
	public static final int						BLOCK_SIZE							= 60 * 1024;

	/** The thread pool executor. */
	private final ThreadPoolExecutor			threadPoolExecutor					= PoolExecutors.newPoolExecutor("UploadFiles", 50, 30, TimeUnit.SECONDS);

	/** The inoutfilelogs. */
	private final Map<String, InOutFileLog>		inoutfilelogs						= new HashMap<>();

	/** The helper. */
	private final TGDFileReceptionDelegate		helper;
	private final ScheduledThreadPoolExecutor	releaseStatusExecutor;

	/**
	 * Instantiates a new TGD file reception service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public TGDFileReceptionService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.helper = new TGDFileReceptionDelegate((IOpentachServerLocator) erl);
		this.releaseStatusExecutor = PoolExecutors.newScheduledFixedThreadPool("TGDFileReceptionService-releaseStatus", 1);
	}

	/**
	 * Gets the file entity.
	 *
	 * @return the file entity
	 */
	public EFicherosTGD getFileEntity() {
		return (EFicherosTGD) this.getEntity("EFicherosTGD");
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.FileManagementEntity#prepareToReceive(java.util.Hashtable, java.lang.String, java.lang.String, int)
	 */
	// TODOS LOS PREPARE FOR RECIVE SE CANALIZAN EN UNO
	@Override
	public String prepareToReceive(Hashtable cvA, String nombreFichero, String descripcionFichero, int sesionId) throws Exception {
		return this.prepareToReceive(cvA, sesionId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.FileManagementEntity#prepareToReceive(java.util.Hashtable, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public String prepareToReceive(Hashtable keysValues, String fileName, String originalFilePath, String fileDescription, int sessionId) throws Exception {
		return this.prepareToReceive(keysValues, sessionId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.FileManagementEntity#prepareToReceive(java.lang.String, java.util.Hashtable, int)
	 */
	@Override
	public String prepareToReceive(String fileCol, Hashtable cv, int sesionId) throws Exception {
		return this.prepareToReceive(cv, sesionId);
	}

	/**
	 * Prepare to receive.
	 *
	 * @param cvA
	 *            the cv a
	 * @param sesionId
	 *            the sesion id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	protected String prepareToReceive(Hashtable<Object, Object> cvA, int sesionId) throws Exception {
		try {
			Hashtable<Object, Object> cv = (Hashtable<Object, Object>) cvA.clone();
			Path fDestino = Files.createTempFile("EFicherosTGD", null);
			InOutFileLog ifl = this.generateInOutFileLog(cv, sesionId, fDestino);
			TGDFileReceptionService.logger.trace("Receiving file for id {}, tmp file {}", ifl.getId(), fDestino);
			return ifl.getId();
		} catch (Exception error) {
			TGDFileReceptionService.logger.error(null, error);
			throw error;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#putBytes(java.lang.String, com.ontimize.util.remote.BytesBlock, int)
	 */
	@Override
	public void putBytes(String receivingId, BytesBlock bytesBlock, int sesionId) throws Exception {
		InOutFileLog ifl = this.getInOutFileLog(receivingId);
		TGDFileReceptionService.logger.trace("putting {} bytes for id {} in file {}", bytesBlock.getBytes().length, receivingId, ifl.getFile());
		Path fTemporal = ifl.getFile();
		try (OutputStream fOut = Files.newOutputStream(fTemporal, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				BufferedOutputStream bOut = new BufferedOutputStream(fOut)) {
			bOut.write(bytesBlock.getBytes());
			bOut.flush();
		} catch (Exception error) {
			TGDFileReceptionService.logger.error("putBytes: Cancelando fichero adjunto por error: {}", error.getMessage(), error);
			this.cancelReceiving(receivingId, sesionId);
			throw error;
		}
	}

	/**
	 * Cancel receiving.
	 *
	 * @param receivingId
	 *            the receiving id
	 * @param sesionId
	 *            the sesion id
	 * @throws Exception
	 *             the exception
	 * @todo: No existe posibilidad de cancelar la transmision desde el cliente. Unicamente se puede cancelar no pidiendo más bytes al servidor, pero en el servidor el fichero
	 *        queda abierto.
	 */
	@Override
	public void cancelReceiving(String receivingId, int sesionId) throws Exception {
		try {
			TGDFileReceptionService.logger.trace("Canceling receiving of {}", receivingId);
			this.getFileEntity().checkPermissions(sesionId, TableEntity.UPDATE_ACTION);
			InOutFileLog ifl = this.getInOutFileLog(receivingId);
			boolean borrado = Files.deleteIfExists(ifl.getFile());
			if (borrado) {
				TGDFileReceptionService.logger.info("Borrado fichero adjunto por cancelación: {}", ifl.getFile());
			} else {
				TGDFileReceptionService.logger.info("NO SE PUDO BORRAR fichero adjunto por cancelación: {}", ifl.getFile());
			}
		} finally {
			this.releaseInOutFileLog(receivingId);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#finishReceiving(java.lang.String, int)
	 */
	@Override
	public boolean finishReceiving(final String receivingId, final int sesionId) throws Exception {
		this.getFileEntity().checkPermissions(sesionId, TableEntity.UPDATE_ACTION);
		try {
			return this.threadPoolExecutor.submit(new TaskFinishReceiving(receivingId, sesionId)).get();
		} catch (ExecutionException error) {
			throw (Exception) error.getCause();
		}
	}

	/**
	 * The Class TaskFinishReceiving.
	 */
	private class TaskFinishReceiving implements Callable<Boolean> {

		/** The receiving id. */
		private final String	receivingId;

		/** The sesion id. */
		private final int		sesionId;

		/**
		 * Instantiates a new task finish receiving.
		 *
		 * @param receivingId
		 *            the receiving id
		 * @param sesionId
		 *            the sesion id
		 */
		public TaskFinishReceiving(String receivingId, int sesionId) {
			super();
			this.receivingId = receivingId;
			this.sesionId = sesionId;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Boolean call() throws Exception {
			return new OntimizeConnectionTemplate<Boolean>() {
				@Override
				protected Boolean doTask(final Connection conn) throws UException {
					try {
						return TGDFileReceptionService.this.finishReceiving(TaskFinishReceiving.this.receivingId, TaskFinishReceiving.this.sesionId, conn);
					} catch (Exception err) {
						throw new UException(err);
					}
				}

				@Override
				protected void onErrorAfterRollback(Throwable error, Connection conn) throws UException {
					try {
						super.onErrorAfterRollback(error, conn);
						TGDFileReceptionService.logger.error("Cancelado fichero adjunto por error: " + error.getMessage(), error);
					} catch (Exception err) {
						throw new UException(err);
					}
				}
			}.execute(TGDFileReceptionService.this.getConnectionManager(), false);
		}

	}

	/**
	 * Finish receiving.
	 *
	 * @param receivingId
	 *            the receiving id
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	private boolean finishReceiving(final String receivingId, final int sesionId, Connection conn) throws Exception {
		try {
			TGDFileReceptionService.logger.trace("Finish receiving for {}", receivingId);
			InOutFileLog ifl = this.getInOutFileLog(receivingId);
			Hashtable<Object, Object> cv = ifl.getKeysValues();
			final String scif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
			if (scif != null) {
				String contratoVigente = this.getService(ContractService.class).getContratoVigente(scif, sesionId, conn);
				MapTools.safePut(cv, OpentachFieldNames.CG_CONTRATO_FIELD, contratoVigente);
			}

			TachoFile tgdf = this.helper.readTachoFile(ifl.getFile());
			cv.putAll(this.helper.extractTgdFileInfo(ifl, tgdf));

			EntityResult resI = this.getFileEntity().insert(cv, sesionId, conn);
			ifl.setIdFichero((Number) resI.get(OpentachFieldNames.IDFILE_FIELD));
			boolean insertSource = resI.containsKey(TGDFileReceptionService.FLAG_INSERT_SOURCE);
			if (resI.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
				ifl.setMessage(resI.getMessage());
			}

			this.helper.saveReceivedFile(ifl, sesionId, conn);

			// hay que ver si se hay que insertar el origen del fichero
			if (insertSource) {
				this.helper.insertSource(ifl, tgdf, sesionId, conn);
			} else if (cv.containsKey(TGDFileReceptionService.FLAG_INSERT_FILE_IN_DUMMY_CONTRACT)) {
				Object dummyContract = this.getService(ContractService.class).getDummyContract(conn);
				if (dummyContract == null) {
					throw new Exception(TGDFileReceptionService.M_NO_SE_ENCUENTRA_CONTRATO);
				}
				((Map<Object, Object>) ifl.getKeysValues()).put(OpentachFieldNames.CG_CONTRATO_FIELD, dummyContract);
				this.helper.insertSource(ifl, tgdf, sesionId, conn);
			}

			conn.commit();
			boolean assignedToContract = this.helper.assignContract(ifl, sesionId, conn);
			ifl.setAssignedToContract(assignedToContract);
			conn.commit();
			this.helper.saveRecord(ifl, sesionId, conn);
			conn.commit();
			((AbstractOpentachServerLocator) this.getLocator()).getBean(IMessageQueueManager.class).pushEvent(QueueNames.POST_FINISH_RECEIVING, new PostFinishReceivingMessage() {
				@Override
				public boolean isAssignedToContract() {
					return ifl.isAssignedToContract();
				}

				@Override
				public int getSessionId() {
					return sesionId;
				}

				@Override
				public Number getIdFichero() {
					return ifl.getIdFichero();
				}

				@Override
				public String getFileName() {
					return (String) ifl.getKeysValues().get(OpentachFieldNames.FILENAME_FIELD);
				}

				@Override
				public Object getCgContract() {
					return ifl.getKeysValues().get(OpentachFieldNames.CG_CONTRATO_FIELD);
				}
			});
			return true;
		} finally {
			this.releaseInOutFileLog(receivingId);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#prepareToTransfer(java.lang.String, java.util.Hashtable, int)
	 */
	@Override
	public synchronized String prepareToTransfer(String column, Hashtable cv, int sesionId) throws Exception {
		return this.prepareToTransfer(cv, sesionId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#prepareToTransfer(java.util.Hashtable, int)
	 */
	@Override
	public synchronized String prepareToTransfer(final Hashtable cv, final int sesionId) throws Exception {
		try {
			this.getFileEntity().checkPermissions(sesionId, TableEntity.QUERY_ACTION);
			InOutFileLog fileLog = this.createTransferFileLogToTransfer(cv, sesionId);
			if (fileLog != null) {
				return fileLog.getId();
			}
			return null;
		} catch (Exception ex) {
			TGDFileReceptionService.logger.error(null, ex);
			throw ex;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#getSize(java.lang.String)
	 */
	@Override
	public long getSize(String transferId) throws Exception {
		InOutFileLog ifl = this.getInOutFileLog(transferId);
		CheckingTools.failIf(ifl.getFile() == null, "No se encuentra el fichero de la transferencia");
		return Files.size(ifl.getFile());
	}

	/**
	 * Devuelve un BytesBlock con los bytes desde offset y longitud length. Devuelve null si se ha alcanzado el final de archivo. Debe llamarse antes a prepareForTransfer para
	 * obtener 'transferId'.
	 *
	 * @param transferId
	 *            the transfer id
	 * @param offset
	 *            the offset
	 * @param lenght
	 *            the lenght
	 * @param sesionId
	 *            the sesion id
	 * @return the bytes
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public BytesBlock getBytes(String transferId, int offset, int lenght, int sesionId) throws Exception {
		InOutFileLog ifl = this.getInOutFileLog(transferId);
		try (RandomAccessFile ra = new RandomAccessFile(ifl.getFile().toFile(), "r")) {
			long longitudArchivo = ra.length();
			if (offset >= longitudArchivo) {
				this.releaseInOutFileLog(transferId);
				return null;
			}
			ra.seek(offset);
			long bytesRestantes = longitudArchivo - offset;
			byte[] bytes = new byte[(int) Math.min(bytesRestantes, lenght)];
			ra.readFully(bytes);
			BytesBlock bytesBlock = new BytesBlock(bytes);
			return bytesBlock;
		}
	}

	/**
	 * Gets the transfer file log to transfer.
	 *
	 * @param cv
	 *            the cv
	 * @param sesionId
	 *            the sesion id
	 * @return the transfer file log to transfer
	 * @throws Exception
	 *             the exception
	 */
	public InOutFileLog createTransferFileLogToTransfer(final Hashtable<Object, Object> cv, final int sesionId) throws Exception {
		try {
			// Si el registro tiene valor en la columna NOMB_GUARDADO se intenta
			// primero por ahí
			Vector<String> atr = new Vector<String>();
			atr.addAll(this.getFileEntity().getKeys());
			atr.add(ITGDFileConstants.NOMB_GUARDADO);

			EntityResult res = this.getFileEntity().query(cv, atr, sesionId);
			CheckingTools.failIf(res.isEmpty(), "Registro con " + cv + " no encontrado!!");
			Hashtable<?, ?> datos = res.getRecordValues(0);
			String nombre = (String) datos.get(ITGDFileConstants.NOMB_GUARDADO);
			CheckingTools.failIf(nombre == null, "NO_HD_FILE");
			EPreferenciasServidor ePreferences = (EPreferenciasServidor) this.getEntity(EPreferenciasServidor.SERVER_PREFERENCES_ENTITY_NAME);
			String path = ePreferences.getValue(EPreferenciasServidor.FILESTORE_PATH, TableEntity.getEntityPrivilegedId(ePreferences));
			CheckingTools.failIf(path == null, "NO_PATH_FILE");
			Path file = Paths.get(path, nombre);
			CheckingTools.failIf(!Files.exists(file), "CAN_NOT_FIND_FILE_IN_HD");
			return this.generateInOutFileLog(cv, sesionId, file);
		} catch (Exception ex) {
			// Probamos a leer el fichero de la tabla de la base de datos
			return new OntimizeConnectionTemplate<InOutFileLog>() {

				@Override
				protected InOutFileLog doTask(Connection con) throws UException {
					try {
						Vector<String> atr = new Vector<String>();
						atr.add("FICHERO");
						Hashtable<?, ?> clavesValoresValidas = TGDFileReceptionService.this.getFileEntity().getValidQueryingKeysValues(cv);
						File file = TGDFileReceptionService.this.getFileEntity().readOracleBLOBStream(ITGDFileConstants.FILE_FIELD, atr, clavesValoresValidas, con);
						if (file != null) {
							if (file.length() > 0) {
								return TGDFileReceptionService.this.generateInOutFileLog(cv, sesionId, file.toPath());
							}
							TGDFileReceptionService.logger.error("Fichero {} vacio", cv);
						} else {
							TGDFileReceptionService.logger.error("Fichero {} no encontrado", cv);
						}
						return null;
					} catch (Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), true);
		}
	}

	/**
	 * Retorna el indentificador para la tranferencia del fichero.
	 *
	 * @param cv
	 *            the cv
	 * @param sesionId
	 *            the sesion id
	 * @param fDestino
	 *            the f destino
	 * @return the string
	 */
	private InOutFileLog generateInOutFileLog(Hashtable<Object, Object> cv, int sesionId, Path fDestino) {
		synchronized (this.inoutfilelogs) {
			String transferId = RandomStringGenerator.generate(10);
			while (this.inoutfilelogs.containsKey(transferId)) {
				transferId = RandomStringGenerator.generate(10);
			}

			InOutFileLog iofl = new InOutFileLog(transferId, cv, fDestino);
			this.inoutfilelogs.put(transferId, iofl);
			return iofl;
		}
	}

	/**
	 * Gets the in out file log.
	 *
	 * @param id
	 *            the id
	 * @return the in out file log
	 * @throws Exception
	 *             the exception
	 */
	private InOutFileLog getInOutFileLog(String id) throws Exception {
		synchronized (this.inoutfilelogs) {
			InOutFileLog inOutFileLog = this.inoutfilelogs.get(id);
			if (inOutFileLog == null) {
				TGDFileReceptionService.logger.error("Id no encontrado {}", id);
				throw new Exception("ReceivingId no encontrado");
			}
			return inOutFileLog;
		}
	}

	/**
	 * Release in out file log.
	 *
	 * @param id
	 *            the id
	 */
	private void releaseInOutFileLog(String id) {
		this.releaseStatusExecutor.schedule(() -> {
			synchronized (this.inoutfilelogs) {
				this.inoutfilelogs.remove(id);
			}
		}, 3000, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.interfaces.AdvancedFileManagementEntity#finishReceivingReturnKey(java.lang.String, int)
	 */
	@Override
	public Object finishReceivingReturnKey(String id, int sessionID) throws Exception {
		InOutFileLog inOutFileLog = this.getInOutFileLog(id);
		this.finishReceiving(id, sessionID);
		return inOutFileLog.getIdFichero();
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.interfaces.AdvancedFileManagementEntity#getReceiveRecordKey(java.lang.String, int)
	 */
	@Override
	public Object getReceiveRecordKey(String receivingId, int sessionID) throws Exception {
		throw new NotImplementedException("Not implementted");
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.FileManagementEntity#getExtension(java.lang.String)
	 */
	@Override
	public String getExtension(String transferId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.FileManagementEntity#deleteAttachmentFile(java.util.Hashtable, int)
	 */
	@Override
	public boolean deleteAttachmentFile(Hashtable keys, int sessionId) throws Exception {
		throw new NotImplementedException("Not implementted");
	}

	/**
	 * Read file data.
	 *
	 * @param savedName
	 *            the saved name
	 * @param sesionId
	 *            the sesion id
	 * @param con
	 *            the con
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	public BytesBlock readFileData(String savedName, int sesionId, Connection con) throws Exception {
		return this.helper.readFileData(savedName, sesionId, con);
	}

	/**
	 * Gets the ID file.
	 *
	 * @param name
	 *            the name
	 * @param fecini
	 *            the fecini
	 * @param fecfin
	 *            the fecfin
	 * @param conn
	 *            the conn
	 * @return the ID file
	 * @throws Exception
	 *             the exception
	 */
	public Number getIDFile(String name, Date fecini, Date fecfin, Connection conn) throws Exception {
		return this.helper.getIDFile(name, fecini, fecfin, conn);
	}

	/**
	 * Checks for contract flota auto.
	 *
	 * @param cgContrato
	 *            the cg contrato
	 * @param cif
	 *            the cif
	 * @param sessionId
	 *            the session id
	 * @param conn
	 *            the conn
	 * @return true, if successful
	 */
	public boolean hasContractFlotaAuto(String cgContrato, String cif, String sTipo, int sessionId, Connection conn) {
		return this.helper.hasContractFlotaAuto(cgContrato, cif, sTipo, sessionId, conn);
	}

	public void uploadFile(TachoFile tachofile, byte[] file, String cif, Date downloadDate, UploadSourceType sourceType, boolean insertFileInDummyContract, String filename,
			String user, int sessionId) throws Exception {
		this.uploadFile(tachofile, file, cif, downloadDate, sourceType, insertFileInDummyContract, filename, null, false, false, null, null, null, null, null, user, sessionId);
	}

	public void uploadFile(TachoFile tachofile, byte[] file, String cif, Date downloadDate, UploadSourceType sourceType, boolean insertFileInDummyContract, String filename,
			String reportMail, boolean isMobile, boolean analyze, String email, String notifField, Number latitude, Number longitude, Object blackberryid,
			String user, int sessionId) throws Exception {
		if (tachofile == null) {
			tachofile = TachoFileTools.getTachoFile(file);
		}
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		// 07/02/2001 el mail hay que sacarlo de la tabla de CDBLACKBERRY
		MapTools.safePut(av, ITGDFileConstants.EMAIL_FIELD, reportMail);
		MapTools.safePut(av, ITGDFileConstants.ANALIZAR_FIELD, analyze ? "S" : "N");
		MapTools.safePut(av, "EMAIL", email);
		MapTools.safePut(av, "IDBLACKBERRY", blackberryid);
		MapTools.safePut(av, "ISMOVIL", isMobile ? "S" : "N");
		MapTools.safePut(av, "NOTIF_FIELD", notifField);
		MapTools.safePut(av, "LATITUDE", latitude);
		MapTools.safePut(av, "LONGITUDE", longitude);
		MapTools.safePut(av, "SOURCE_TYPE", sourceType);
		MapTools.safePut(av, "CIF", cif);
		MapTools.safePut(av, "USUARIO_ALTA", user);
		String validName = filename;
		if (filename != null) {
			MapTools.safePut(av, OpentachFieldNames.FILENAME_FIELD, filename);
		} else {
			Calendar calendar = Calendar.getInstance();
			if (downloadDate != null) {
				calendar.setTime(downloadDate);
			}
			validName = tachofile.computeFileName(null, com.imatia.tacho.model.TachoFile.FILENAME_FORMAT_SPAIN, calendar);
			MapTools.safePut(av, OpentachFieldNames.FILENAME_FIELD, validName);
		}

		// Para que si no existe el conductor en ningún lado se asocie al contrato dummy
		if (insertFileInDummyContract) {
			MapTools.safePut(av, TGDFileReceptionService.FLAG_INSERT_FILE_IN_DUMMY_CONTRACT, Boolean.TRUE);
		}

		// comprimo y escribo en memoria para enviar posteriormente
		try (ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				ZipOutputStream zOut = new ZipOutputStream(bOut);
				ByteArrayInputStream is = new ByteArrayInputStream(file);) {
			ZipUtils.zip(is, validName, zOut);
			String rId = this.prepareToReceive(ITGDFileConstants.FILE_FIELD, av, sessionId);
			try (InputStream compressedIs = new ByteArrayInputStream(bOut.toByteArray())) {

				// Escribo
				byte[] buffer = new byte[TGDFileReceptionService.BLOCK_SIZE];
				int leido = 0;
				while ((leido = compressedIs.read(buffer)) != -1) {
					byte[] internalBuffer = new byte[leido];
					System.arraycopy(buffer, 0, internalBuffer, 0, leido);
					this.putBytes(rId, new BytesBlock(internalBuffer), sessionId);
				}
			}
			this.finishReceiving(rId, sessionId);
		}
	}

}
