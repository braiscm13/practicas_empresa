/*
 *
 */
package com.opentach.server.filereception;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.DriverCardHolderIdentification;
import com.imatia.tacho.model.tc.TCFile;
import com.imatia.tacho.model.vu.VUFile;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.SQLStatement;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.tachofiles.ITachoFileRecordService;
import com.opentach.common.util.ZipUtils;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.contract.TachoFileContractService;
import com.opentach.server.entities.EFicherosTGD;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.tachofiles.TachoFileRecordService;
import com.opentach.server.tachofiles.TachoFileTools;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.ContractUtils;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class TGDFileReceptionDelegate extends AbstractDelegate {

	private static final Logger logger = LoggerFactory.getLogger(TGDFileReceptionDelegate.class);

	public TGDFileReceptionDelegate(IOpentachServerLocator locator) {
		super(locator);
	}

	public EFicherosTGD getFileEntity() {
		return (EFicherosTGD) this.getEntity("EFicherosTGD");
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
	Number getIDFile(String name, Date fecini, Date fecfin, Connection conn) throws Exception {
		if ((fecini == null) || (fecfin == null)) {
			return null;
		}
		String sql = new Template("tachofiletransfer-sql/getIdFileWithDates.sql").getTemplate();
		Object[] parameters = new Object[] { name, new Timestamp(fecini.getTime()), new Timestamp(fecfin.getTime()) };

		return new QueryJdbcTemplate<Number>() {
			@Override
			protected Number parseResponse(ResultSet rset) throws UException {
				try {
					if (rset.next()) {
						BigDecimal res = rset.getBigDecimal(1);
						TGDFileReceptionDelegate.logger.info("El fichero es un duplicado de {}", res);
						return res;
					}
					return null;
				} catch (Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, parameters);
	}

	/**
	 * Checks for contract flota auto.
	 *
	 * @param cgcontrato
	 *            the cgcontrato
	 * @param sessionID
	 *            the session id
	 * @param conn
	 *            the conn
	 * @return true, if successful
	 */
	boolean hasContractFlotaAuto(String cgcontrato, String cif, String sTipo, int sessionID, Connection conn) {

		try {
			TransactionalEntity eEmpReq = this.getEntity("EEmpreReq");
			TransactionalEntity eDfEmp = this.getEntity(CompanyNaming.ENTITY);

			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("NUMREQ", cgcontrato);
			if (cif != null) {
				av.put("CIF", cif);
			}
			EntityResult resEmpReq = eEmpReq.query(av, new Vector<String>(), this.getSessionId(sessionID, eEmpReq), conn);
			if (resEmpReq.calculateRecordNumber() > 0) {
				EntityResult res = eDfEmp.query(EntityResultTools.keysvalues(OpentachFieldNames.CIF_FIELD, resEmpReq.getRecordValues(0).get(OpentachFieldNames.CIF_FIELD)),
						EntityResultTools.attributes("COND_AUTOM", "VEH_AUTOM"), this.getSessionId(sessionID, eDfEmp), conn);
				CheckingTools.checkValidEntityResult(res, "E_COMPANY_CIF_NOT_FOUND", true, true, new Object[] {});

				if ("TC".equals(sTipo)) {
					return "S".equals(res.getRecordValues(0).get("COND_AUTOM"));
				} else {
					return "S".equals(res.getRecordValues(0).get("VEH_AUTOM"));
				}

			}
		} catch (Exception e) {
			TGDFileReceptionDelegate.logger.error(null, e);
		}
		return false;
	}

	/**
	 * Save received file.
	 *
	 * @param ifl
	 *            the ifl
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 * @throws Exception
	 *             the exception
	 */
	void saveReceivedFile(InOutFileLog ifl, int sesionId, Connection conn) throws Exception {

		EFicherosTGD eFicherosTGD = this.getFileEntity();
		Hashtable<?, ?> clavesValores = eFicherosTGD.replaceAliasByColumn(ifl.getKeysValues());
		eFicherosTGD.checkUpdateKeys(clavesValores);
		Hashtable<?, ?> clavesValoresValidas = eFicherosTGD.getValidUpdatingKeysValues(clavesValores);
		Hashtable<String, Object> av = new Hashtable<String, Object>();

		// Obtenemos el tipo de guardado BD/HD/BDHD
		EPreferenciasServidor ePreferences = (EPreferenciasServidor) this.getEntity(EPreferenciasServidor.SERVER_PREFERENCES_ENTITY_NAME);
		String mode = ePreferences.getValue(EPreferenciasServidor.FILESTORE_WHERE, TableEntity.getEntityPrivilegedId(ePreferences), conn);
		boolean insertInHd = ObjectTools.isIn(mode, InOutFileLog.INSERT_FILE_INTO_BDHD, InOutFileLog.INSERT_FILE_INTO_HD);
		boolean insertInBd = ObjectTools.isIn(mode, InOutFileLog.INSERT_FILE_INTO_BDHD, InOutFileLog.INSERT_FILE_INTO_BD);

		// Si no es BD, obtenemos la ruta de guardado de ficheros
		if (insertInBd) {
			eFicherosTGD.writeOracleBLOBStream(ifl.getFile().toFile(), "FICHERO", clavesValoresValidas, conn);
		}
		if (insertInHd) {
			Path fDestino = null;
			String path = ePreferences.getValue(EPreferenciasServidor.FILESTORE_PATH, TableEntity.getEntityPrivilegedId(ePreferences), conn);
			// Si no está configurada-->Petada
			if (path == null) {
				throw new Exception("La ruta de almacén de ficheros no está configurada.");
			}
			fDestino = Paths.get(path);
			Files.createDirectories(fDestino);
			String nombreFichero = (String) ifl.getKeysValues().get(OpentachFieldNames.FILENAME_PROCESSED_FIELD);
			fDestino = fDestino.resolve(eFicherosTGD.getAttachmentFileNameForKeys(nombreFichero, ifl.getKeysValues()));
			ifl.getKeysValues().put(ITGDFileConstants.NOMB_GUARDADO, fDestino.toFile().getName());
			av.put(ITGDFileConstants.NOMB_GUARDADO, fDestino.toFile().getName());
			Files.move(ifl.getFile(), fDestino, StandardCopyOption.REPLACE_EXISTING);

			Hashtable<?, ?> atributosValores = eFicherosTGD.replaceAliasByColumn(av);
			Hashtable<?, ?> atributosValoresValidos = eFicherosTGD.getValidUpdatingAttributesValues(atributosValores);
			CheckingTools.failIf(atributosValoresValidos.isEmpty() || clavesValoresValidas.isEmpty(), "La columna o las claves valores no son validos. No se ha actualizado.");

			// Consultamos si hay un adjunto anterior
			this.deletePreviousFile(ifl.getFile(), clavesValores, sesionId, conn);

			SQLStatement stSQL = SQLStatementBuilder.createUpdateQuery(eFicherosTGD.getTable(), atributosValoresValidos, clavesValoresValidas);
			String consultaSQL = stSQL.getSQLStatement();
			Vector<?> valores = stSQL.getValues();
			if (eFicherosTGD.executePreparedStatement(consultaSQL, valores, conn, sesionId).getCode() != EntityResult.OPERATION_SUCCESSFUL) {
				throw new Exception(
						"La columna o las claves valores no son validos. No se ha actualizado ningun registro con el nombre del fichero. " + consultaSQL + " -> " + valores);
			}
		}
		TGDFileReceptionDelegate.logger.debug("INSERTADO finishReceiving ");
		// Guardamos el archivo sin comprimir en el otro directorio
		try {
			Boolean storeunzip = "S".equals(ePreferences.getValue(EPreferenciasServidor.FILESTORE_UNZIP, TableEntity.getEntityPrivilegedId(ePreferences), conn));
			if (storeunzip) {
				String unzipPath = ePreferences.getValue(EPreferenciasServidor.FILESTORE_UNZIP_PATH, TableEntity.getEntityPrivilegedId(ePreferences), conn);
				Path funzip = Paths.get(unzipPath);
				Files.createDirectories(funzip);
				ZipUtils.unzip(ifl.getFile().toFile(), funzip.toFile().getAbsolutePath());
			}
		} catch (Exception error) {
			TGDFileReceptionDelegate.logger.error(null, error);
		}
	}

	/**
	 * Read tacho file.
	 *
	 * @param file
	 *            the file
	 * @return the tacho file
	 * @throws Exception
	 *             the exception
	 */
	public TachoFile readTachoFile(Path file) throws Exception {
		File funzip = new File(System.getProperty("java.io.tmpdir"));
		funzip.mkdirs();
		String name = ZipUtils.unzip(file.toFile(), funzip.getAbsolutePath());
		File ftemp = new File(funzip, name);
		try {
			return TachoFile.readTachoFile(FileTools.getBytesFromFile(ftemp));
		} finally {
			try {
				ftemp.delete();
			} catch (Exception error) {
				TGDFileReceptionDelegate.logger.error(null, error);
			}
		}
	}

	/**
	 * Insert source.
	 *
	 * @param ifl
	 *            the ifl
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 * @throws Exception
	 */
	void insertSource(InOutFileLog ifl, TachoFile tgdfile, int sesionId, Connection conn) throws Exception {
		if (tgdfile instanceof TCFile) {
			// conductor
			this.insertSourceTC((TCFile) tgdfile, ifl, sesionId, conn);
		} else {
			// vehículo
			this.insertSourceVU((VUFile) tgdfile, ifl, sesionId, conn);
		}
	}

	/**
	 * Insert source vu.
	 *
	 * @param vuFile
	 *            the vu file
	 * @param ifl
	 *            the ifl
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 * @throws Exception
	 *             the exception
	 */
	private void insertSourceVU(VUFile vuFile, InOutFileLog ifl, int sesionId, Connection conn) throws Exception {
		String cgcontrato = (String) ifl.getKeysValues().get("CG_CONTRATO");
		Object companyCif = this.getCompanyCif(cgcontrato, sesionId, conn);
		if (companyCif == null) {
			return;
		}
		String matricula = vuFile.getOwnerId();

		TransactionalEntity eVehCont = this.getEntity("EVehiculoCont");
		EntityResult res = eVehCont.query(//
				EntityResultTools.keysvalues("CG_CONTRATO", cgcontrato, "MATRICULA", matricula, "F_BAJA", new SearchValue(SearchValue.NOT_NULL, null)), //
				EntityResultTools.attributes("CG_CONTRATO"), //
				this.getSessionId(sesionId, eVehCont), conn);
		// TODO arriba se esta filtrando por f_baja not null... no va a entrar por este if, parece que todo se hace en el EVehiculosEmp
		if (res.calculateRecordNumber() > 0) {
			// si existe y esta de baja hay que darlo de alta
			eVehCont.update(EntityResultTools.keysvalues("F_BAJA", new NullValue()), //
					EntityResultTools.keysvalues("CG_CONTRATO", cgcontrato, "MATRICULA", matricula), //
					this.getSessionId(sesionId, eVehCont), conn);
		} else {
			Hashtable<String, Object> avVehicle = new Hashtable<String, Object>();
			avVehicle.put("MATRICULA", matricula);
			avVehicle.put("DSCR", "");
			avVehicle.put("CG_CONTRATO", cgcontrato);
			avVehicle.put("CIF", companyCif);
			TransactionalEntity eVehEmp = this.getEntity("EVehiculosEmp");
			// hay que dar de alta el vehículo en la
			// empresa y asociarlo al contrato
			eVehEmp.insert(avVehicle, this.getSessionId(sesionId, eVehEmp), conn);
		}
	}

	/**
	 * Insert source tc.
	 *
	 * @param tcFile
	 *            the tc file
	 * @param ifl
	 *            the ifl
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 * @throws Exception
	 *             the exception
	 */
	private void insertSourceTC(TCFile tcFile, InOutFileLog ifl, int sesionId, Connection conn) throws Exception {
		String cgcontrato = (String) ifl.getKeysValues().get("CG_CONTRATO");
		Object companyCif = this.getCompanyCif(cgcontrato, sesionId, conn);
		if (companyCif == null) {
			return;
		}

		// conductor
		DriverCardHolderIdentification driverCardHolderIdentification = tcFile.getEfIdentification().getJoinedData().getDriverCardHolderIdentification();
		String identification = tcFile.getEfIdentification().getJoinedData().getCardIdentification().getCardNumber().getIdentification();

		TransactionalEntity eCondCont = this.getEntity("EConductorCont");
		EntityResult res = eCondCont.query(//
				EntityResultTools.keysvalues("CG_CONTRATO", cgcontrato, "IDCONDUCTOR", identification, "F_BAJA", new SearchValue(SearchValue.NOT_NULL, null)), //
				EntityResultTools.attributes("CG_CONTRATO"), //
				this.getSessionId(sesionId, eCondCont), conn);
		// TODO arriba se esta filtrando por f_baja not null... no va a entrar por este if, parece que esta todo hecho en el EConductoresEmp
		if (res.calculateRecordNumber() > 0) {
			// si existe y esta de baja hay que darlo de alta
			eCondCont.update(EntityResultTools.keysvalues("F_BAJA", new NullValue()), //
					EntityResultTools.keysvalues("CG_CONTRATO", cgcontrato, "IDCONDUCTOR", identification), //
					this.getSessionId(sesionId, eCondCont), conn);
		} else {
			// hay que dar de alta el conductor en la
			// empresa y asociarlo al contrato
			String driverDni = TachoFileTools.extractDriverDni(identification,
					tcFile.getEfIdentification().getJoinedData().getCardIdentification().getCardIssuingMemberState().getNationAlphaEquivalent());
			String driverName = driverCardHolderIdentification.getCardHolderName().getHolderFirstNames().getValue();
			String driverSurname = driverCardHolderIdentification.getCardHolderName().getHolderSurname().getValue();
			Hashtable<String, Object> avConductor = new Hashtable<>();
			avConductor.put("CIF", companyCif);
			avConductor.put("IDCONDUCTOR", identification);
			avConductor.put("DNI", driverDni);
			avConductor.put("NOMBRE", driverName.trim());
			avConductor.put("APELLIDOS", driverSurname.trim());
			avConductor.put("CG_CONTRATO", cgcontrato);
			TransactionalEntity entCondEmp = this.getEntity("EConductoresEmp");
			entCondEmp.insert(avConductor, this.getSessionId(sesionId, entCondEmp), conn);
		}
	}

	private Object getCompanyCif(String cgcontrato, int sesionId, Connection conn) throws Exception {
		TransactionalEntity ent = this.getEntity("EEmpreReq");
		EntityResult res = ent.query(//
				EntityResultTools.keysvalues("NUMREQ", cgcontrato), //
				EntityResultTools.attributes("CIF"), //
				this.getSessionId(sesionId, ent), conn);
		if (res.calculateRecordNumber() > 0) {
			return res.getRecordValues(0).get("CIF");
		}
		return null;
	}

	/**
	 * Lee un fichero del disco duro
	 *
	 * @param savedName
	 * @param sesionId
	 * @param con
	 * @return
	 * @throws Exception
	 */
	BytesBlock readFileData(String savedName, int sesionId, Connection con) throws Exception {
		if (savedName == null) {
			return null;
		}
		EPreferenciasServidor ePreferences = (EPreferenciasServidor) this.getEntity(EPreferenciasServidor.SERVER_PREFERENCES_ENTITY_NAME);
		String path = ePreferences.getValue(EPreferenciasServidor.FILESTORE_PATH, TableEntity.getEntityPrivilegedId(ePreferences), con);
		if (path == null) {
			return null;
		}
		File f = new File(path, savedName);
		if (!f.exists()) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(f);
		try {
			byte[] buffer = new byte[1024];
			int readed = 0;
			while ((readed = fis.read(buffer)) >= 0) {
				baos.write(buffer, 0, readed);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return new BytesBlock(baos.toByteArray());
	}

	/**
	 * Delete previous file.
	 *
	 * @param newFile
	 *            the new file
	 * @param clavesValores
	 *            the claves valores
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 */
	private void deletePreviousFile(Path newFile, Hashtable<?, ?> clavesValores, int sesionId, Connection conn) {
		try {
			Vector<String> at = new Vector<String>();
			at.add(ITGDFileConstants.NOMB_GUARDADO);
			EntityResult res = this.getFileEntity().query(clavesValores, at, sesionId, conn);
			if (!res.isEmpty()) {
				Hashtable<?, ?> v = res.getRecordValues(0);
				if (v.containsKey(ITGDFileConstants.NOMB_GUARDADO)) {
					String nombre = (String) v.get(ITGDFileConstants.NOMB_GUARDADO);
					if ((nombre != null) && !nombre.equalsIgnoreCase(newFile.toFile().getName())) {
						Path f = newFile.getParent().resolve(nombre);
						Files.deleteIfExists(f);
					}
				}
			}
		} catch (Exception e) {
			TGDFileReceptionDelegate.logger.error("No se pudo borrar fichero adjunto anterior", e);
		}
	}

	public Map<Object, Object> extractTgdFileInfo(InOutFileLog ifl, TachoFile tgdf) throws Exception {
		Map<Object, Object> cv = new HashMap<>();

		String nombGuardado = (String) ObjectTools.coalesce(ifl.getKeysValues().get(ITGDFileConstants.NOMB_GUARDADO), ifl.getKeysValues().get(OpentachFieldNames.FILENAME_FIELD),
				ifl.getKeysValues().get(OpentachFieldNames.FILENAME_FIELD));
		String validName = tgdf.computeFileName(nombGuardado, com.imatia.tacho.model.TachoFile.FILENAME_FORMAT_SPAIN, null);
		Date extractTime = tgdf.getExtractTime(validName);

		MapTools.safePut(cv, OpentachFieldNames.FILENAME_FIELD, nombGuardado, true);
		MapTools.safePut(cv, OpentachFieldNames.FILENAME_PROCESSED_FIELD, validName);
		MapTools.safePut(cv, ITGDFileConstants.FILEKIND_FIELD, (tgdf instanceof com.imatia.tacho.model.tc.TCFile) ? "TC" : "VU");
		MapTools.safePut(cv, ITGDFileConstants.EXTRACT_DATE_FIELD, extractTime == null ? new Date() : extractTime, true);
		MapTools.safePut(cv, ITGDFileConstants.OBSR_FIELD, "Fichero sin procesar");
		MapTools.safePut(cv, OpentachFieldNames.IDORIGEN_FIELD, tgdf.getOwnerId());
		Date[] dateRange = tgdf.getDateRange();
		if (dateRange != null) {
			MapTools.safePut(cv, OpentachFieldNames.FECINI_FIELD, dateRange[0]);
			MapTools.safePut(cv, OpentachFieldNames.FECFIN_FIELD, dateRange[1]);
		}

		return cv;
	}

	/**
	 * Returns if file is assigned to contract
	 *
	 * @param filelog
	 * @param sesionId
	 * @param conn
	 * @return
	 */
	public boolean assignContract(InOutFileLog filelog, int sesionId, Connection conn) {
		Hashtable<Object, Object> cv = filelog.getKeysValues();
		// Al finalizar la recepción del fichero es cuando lo asigno al
		// contrato para que el procesado pueda entrar recuperar el fichero.
		Object cgContrato = cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		String scif = (String) cv.get(OpentachFieldNames.CIF_FIELD);

		final String sIdSource = (String) cv.get(OpentachFieldNames.IDORIGEN_FIELD);
		final String sTipo = (String) cv.get(ITGDFileConstants.FILEKIND_FIELD);
		final Number idFile = filelog.getIdFichero();

		String obsr = null;
		boolean assigned = false;
		// Si no viene el contrato lo asigno solo si el conductor existe sólo
		// en un contrato
		try {
			TachoFileContractService contractService = this.getService(TachoFileContractService.class);
			if (cgContrato == null) {
				// lo intento asociar a un contrato si no está asociado
				List<String> contratos = contractService.getContractToAssign(scif == null ? null : Arrays.asList(new String[] { scif }), sIdSource, sTipo, conn);
				if (contratos.size() == 1) {
					cgContrato = contratos.get(0);
					if (!contractService.isFileAssignedToContract(idFile, cgContrato, conn)) {
						contractService.assignFileToContract(idFile, cgContrato, conn, sesionId);
					}
					cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
					assigned = true;
				} else {
					obsr = "Fichero con varios posibles contratos";
				}
			} else {
				cgContrato = ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sesionId, conn);

				if (!contractService.isFileAssignedToContract(idFile, cgContrato, conn)) {

					// Viene el contrato en la solicitud de inserción.
					// En el cv viene el contrato asigno el fichero si el
					// conductor-veh pertenece al contrato.
					if (contractService.isSourceAssignedToContract(sIdSource, cgContrato, sTipo, conn)) {
						contractService.assignFileToContract(idFile, cgContrato, conn, sesionId);
						assigned = true;
					}
				} else {
					assigned = true;
				}
			}
		} catch (Exception error) {
			TGDFileReceptionDelegate.logger.error(null, error);
		}
		MapTools.safePut(filelog.getKeysValues(), ITGDFileConstants.OBSR_FIELD, obsr);

		return assigned;

	}

	public void saveRecord(InOutFileLog filelog, int sesionId, Connection conn) throws Exception {
		Hashtable<?, ?> cv = filelog.getKeysValues();
		final String nomb = (String) cv.get(OpentachFieldNames.FILENAME_FIELD);
		final Number idFile = filelog.getIdFichero();
		final String sMail = (String) cv.get(ITGDFileConstants.EMAIL_FIELD);
		final String sAnalize = (String) cv.get(ITGDFileConstants.ANALIZAR_FIELD);
		final String sNotifUrl = (String) cv.get(ITGDFileConstants.NOTIF_FIELD);
		final String obsr = (String) cv.get(ITGDFileConstants.OBSR_FIELD);
		final Number latitude = (Number) cv.get("LATITUDE");
		final Number longitude = (Number) cv.get("LONGITUDE");
		final Object idBlackberry = cv.get("IDBLACKBERRY");
		Object cgContrato = cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		UploadSourceType sourceType = (UploadSourceType) cv.get("SOURCE_TYPE");
		String isMovil = (String) cv.get("ISMOVIL");
		String uploadUser = (String) cv.get("USUARIO_ALTA");
		if (isMovil == null) {
			isMovil = "N";
		}
		boolean analize = false;
		if ("S".equals(sAnalize)) {
			analize = true;
		}
		this.getService(TachoFileRecordService.class).saveFileRecord(idFile, cgContrato, ITachoFileRecordService.UPLOAD, nomb, obsr, sourceType, sMail, analize, sNotifUrl,
				latitude, longitude, idBlackberry, isMovil, uploadUser, sesionId, conn);
	}

}
