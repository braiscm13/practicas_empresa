package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.interfaces.AdvancedFileManagementEntity;
import com.opentach.server.contract.TachoFileContractService;
import com.opentach.server.filereception.InOutFileLog;
import com.opentach.server.filereception.TGDFileReceptionService;
import com.opentach.server.util.db.FileTableEntity;

public class EFicherosTGD extends FileTableEntity implements AdvancedFileManagementEntity {
	private final static Logger							logger								= LoggerFactory.getLogger(EFicherosTGD.class);

	public EFicherosTGD(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.util.db.FileTableEntity#query(java.util.Hashtable, java.util.Vector, int, java.sql.Connection)
	 */
	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		// Si se pide la columna FICHERO y están en disco, hay que leerlo
		boolean queryFile = av.contains(ITGDFileConstants.FILE_FIELD);
		if (queryFile && !av.contains(ITGDFileConstants.NOMB_GUARDADO)) {
			av.add(ITGDFileConstants.NOMB_GUARDADO);
		}
		EntityResult res = super.query(cv, av, sesionId, con);

		if (queryFile) {
			Vector<Object> files = ((Vector<Object>) res.get(ITGDFileConstants.FILE_FIELD));
			Vector<Object> names = ((Vector<Object>) res.get(ITGDFileConstants.NOMB_GUARDADO));
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					Object ob = files.get(i);
					if (ob == null) {
						BytesBlock bb = this.getService(TGDFileReceptionService.class).readFileData((String) names.get(i), sesionId, con);
						files.set(i, bb);
					}
				}
			}
		}
		return res;
	}


	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.util.db.FileTableEntity#insert(java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection conn) throws Exception {
		EntityResult res = null;
		final String nomb = (String) av.get(OpentachFieldNames.FILENAME_FIELD);
		final String nombProcess = (String) av.get(OpentachFieldNames.FILENAME_PROCESSED_FIELD);

		final String cgContrato = (String) av.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		final String scif = (String) av.get(OpentachFieldNames.CIF_FIELD);

		final String sIdSource = (String) av.get(OpentachFieldNames.IDORIGEN_FIELD);
		final String sTipo = (String) av.get(ITGDFileConstants.FILEKIND_FIELD);

		final Date fecini = (Date) av.get(OpentachFieldNames.FECINI_FIELD);
		final Date fecfin = (Date) av.get(OpentachFieldNames.FECFIN_FIELD);

		if ((nomb == null) || (nombProcess == null) || (sTipo == null) || (!"DA".equals(sTipo) && (sIdSource == null))) {
			throw new Exception("M_FILENAME_IS_NULL");
		}
		final String upNombProcess = nombProcess.toUpperCase();

		// SE COMPRUEBA SI YA EXISTE EL FICHERO EN LA APLICACION
		Number idFile = this.getService(TGDFileReceptionService.class).getIDFile(upNombProcess, fecini, fecfin, conn);

		// inserto fichero
		if (idFile == null) {
			av.remove(ITGDFileConstants.OBSR_FIELD);
			res = super.insert(av, sesionId, conn);
			if ((res == null) || (res.getCode() != EntityResult.OPERATION_SUCCESSFUL)) {
				throw new Exception("M_ERROR_INSERTING_FILE");
			}
			idFile = (Number) res.get(OpentachFieldNames.IDFILE_FIELD);
		} else {
			res = new EntityResult(EntityResult.OPERATION_SUCCESSFUL, EntityResult.NODATA_RESULT);
			res.put(OpentachFieldNames.IDFILE_FIELD, idFile);
		}

		TachoFileContractService contractService = this.getService(TachoFileContractService.class);
		if (cgContrato == null) {
			// No viene el contrato
			// lo intento asociar a un contrato si no está asociado
			List<String> contratos = contractService.getContractToAssign(scif == null ? null : Arrays.asList(new String[] { scif }), sIdSource, sTipo, conn);
			if (contratos.isEmpty() && !av.containsKey(TGDFileReceptionService.FLAG_INSERT_FILE_IN_DUMMY_CONTRACT)) {
				if ("TC".equals(sTipo)) {
					EFicherosTGD.logger.warn("No se encuentra conductor asociado a contrato {} de empresa {}", sIdSource, scif);
					throw new Exception("M_NO_SE_ENCUENTRA_CONDUCTOR_ASOCIADO_CONTRATO");
				} else if ("VU".equals(sTipo)) {
					EFicherosTGD.logger.warn("No se encuentra vehiculo asociado a contrato {} de empresa {}", sIdSource, scif);
					throw new Exception("M_NO_SE_ENCUENTRA_VEHICULO_ASOCIADO_CONTRATO");
				}
			} else if (!contratos.isEmpty()) {
				av.remove(TGDFileReceptionService.FLAG_INSERT_FILE_IN_DUMMY_CONTRACT);
			} else {
				EFicherosTGD.logger.warn("No se encuentra contrato asociado a {}, se intentara asociar al dummy contract", sIdSource);
			}
		} else {
			if (contractService.isFileAssignedToContract(idFile, cgContrato, conn)) {
				av.remove(TGDFileReceptionService.FLAG_INSERT_FILE_IN_DUMMY_CONTRACT);
			} else {
				// Viene el contrato en la solicitud de inserción.
				if (contractService.isSourceAssignedToContract(sIdSource, cgContrato, sTipo, conn)) {
					av.remove(TGDFileReceptionService.FLAG_INSERT_FILE_IN_DUMMY_CONTRACT);
				} else {
					EFicherosTGD.logger.warn("Source {} is not assigned to contract {} {}", sIdSource, cgContrato, sTipo);
					// lo intento asociar a una empresa si tiene el check de
					// insercion automática activado
					boolean hasFlotaAuto = this.getService(TGDFileReceptionService.class).hasContractFlotaAuto(cgContrato, scif, sTipo, sesionId, conn);
					if (hasFlotaAuto) {
						res.put(TGDFileReceptionService.FLAG_INSERT_SOURCE, Boolean.TRUE);
					}
					if (!hasFlotaAuto) {
						if ("TC".equals(sTipo)) {
							throw new Exception("M_NO_SE_ENCUENTRA_CONDUCTOR_ASOCIADO_CONTRATO");
						} else if ("VU".equals(sTipo)) {
							throw new Exception("M_NO_SE_ENCUENTRA_VEHICULO_ASOCIADO_CONTRATO");
						}
					}
				}
			}
		}
		return res;
	}


	@Override
	public String getAttachmentFileNameForKeys(String fileName, Hashtable keysValues) throws Exception {
		return super.getAttachmentFileNameForKeys(fileName, keysValues);
	}

	public InOutFileLog createTransferFileLogToTransfer(final Hashtable<Object, Object> cv, final int sesionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).createTransferFileLogToTransfer(cv, sesionId);
	}
	/*************************************************************
	 * MODIFICACIÓN DE LA UTILIDAD DE RECEPCIÓN DE FICHEROS PARA PODER GUARDAR EN DISCO Y EN BASE DE DATOS
	 *************************************************************/

	@Override
	public long getSize(String transferId) throws Exception {
		return this.getService(TGDFileReceptionService.class).getSize(transferId);
	}

	@Override
	public String getExtension(String transferId) throws Exception {
		return this.getService(TGDFileReceptionService.class).getExtension(transferId);
	}

	@Override
	public synchronized String prepareToTransfer(String column, Hashtable keys, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).prepareToTransfer(column, keys, sessionId);
	}

	@Override
	public String prepareToReceive(String column, Hashtable keys, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).prepareToReceive(column, keys, sessionId);
	}

	@Override
	public String prepareToTransfer(Hashtable cv, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).prepareToTransfer(cv, sessionId);
	}

	@Override
	public String prepareToReceive(Hashtable keys, String fileName, String fileDescription, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).prepareToReceive(keys, fileName, fileDescription, sessionId);
	}

	@Override
	public String prepareToReceive(Hashtable keys, String fileName, String originalFilePath, String fileDescription, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).prepareToReceive(keys, fileName, originalFilePath, fileDescription, sessionId);
	}

	@Override
	public BytesBlock getBytes(String transferId, int offset, int lenght, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).getBytes(transferId, offset, lenght, sessionId);
	}

	@Override
	public void putBytes(String transferId, BytesBlock bytesBlock, int sessionId) throws Exception {
		this.getService(TGDFileReceptionService.class).putBytes(transferId, bytesBlock, sessionId);

	}

	@Override
	public boolean finishReceiving(String transferId, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).finishReceiving(transferId, sessionId);
	}

	@Override
	public void cancelReceiving(String transferId, int sessionId) throws Exception {
		this.getService(TGDFileReceptionService.class).cancelReceiving(transferId, sessionId);
	}

	@Override
	public boolean deleteAttachmentFile(Hashtable keys, int sessionId) throws Exception {
		return this.getService(TGDFileReceptionService.class).deleteAttachmentFile(keys, sessionId);
	}

	@Override
	public Object getReceiveRecordKey(String receivingId, int sessionID) throws Exception {
		return this.getService(TGDFileReceptionService.class).getReceiveRecordKey(receivingId, sessionID);
	}

	@Override
	public Object finishReceivingReturnKey(String rId, int sessionID) throws Exception {
		return this.getService(TGDFileReceptionService.class).finishReceivingReturnKey(rId, sessionID);
	}
}
