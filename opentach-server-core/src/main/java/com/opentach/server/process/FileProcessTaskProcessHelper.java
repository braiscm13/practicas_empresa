package com.opentach.server.process;

import java.io.File;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.IElementaryFile;
import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.security.TachoFileVerifier;
import com.ontimize.jee.common.tools.Chronometer;
import com.opentach.common.tacho.FileParser;
import com.opentach.common.tacho.IFileParser;
import com.opentach.common.tacho.data.AbstractData;
import com.opentach.common.tacho.data.Actividad;
import com.opentach.common.tacho.data.Conductor;
import com.utilmize.server.tools.sqltemplate.FunctionJdbcTemplate;

public class FileProcessTaskProcessHelper {

	private static final Logger logger = LoggerFactory.getLogger(FileProcessTaskProcessHelper.class);

	public ProcessedRecord process(String user, FileInfo fInfo, File file, ErrorLogTableManagementThread errorLogTableThread, Connection conn)
			throws Exception {
		Chronometer chrono = new Chronometer().start();
		// Inicio el procesado de los ficheros
		ProcessedRecord processedRecord = this.procesTgdDataFile(fInfo, file, conn);
		FileProcessTaskProcessHelper.logger.info("\tProcessTgdDataFile in {} ms", chrono.elapsedMs());
		// Vuelco info a la BD.
		List<String> errors = new FileProcessTaskInsertDbHelper().execute(processedRecord.data, user, errorLogTableThread, conn);
		FileProcessTaskProcessHelper.logger.info("\tInsert into DB in {} ms", chrono.elapsedMs());
		// Postvolcado
		this.procPostVolcado(fInfo, processedRecord, conn);
		FileProcessTaskProcessHelper.logger.info("\tProcPostVolcado in {} ms", chrono.elapsedMs());
		return processedRecord;
	}

	/**
	 * Analiza la información del fichero
	 *
	 * @param fInfo
	 *            ,
	 * @param file
	 * @param certstore
	 * @return
	 */
	private ProcessedRecord procesTgdDataFile(FileInfo fInfo, File file, Connection conn) {
		String regtext = "NO_PROCESS_INCIDENT";
		Map<String, List<? extends AbstractData>> parserResult = Collections.EMPTY_MAP;
		ProcessedRecord processedRecord = null;
		try {
			TachoFile tachofile = TachoFile.readTachoFile(file);
			// Compruebo integridad del fichero
			new TachoFileVerifier().validateFile(tachofile, TachoCertificateStoreManager.getStore());
			if (tachofile.isCertificateCorrupted()) {
				FileProcessTaskProcessHelper.logger.error("No se ha encontrado certificados válidos para el fichero");
				regtext = "No se ha podido abrir los certificados del fichero. No se dispone de los certificados raiz necesarios o el fichero está corrupto";
			} else {
				List<IElementaryFile> corruptedblocks = tachofile.getCorruptesEfs();
				if (!corruptedblocks.isEmpty()) {
					FileProcessTaskProcessHelper.logger.error("Error procesando fichero {}: Detectados bloques de datos con error de firma", file.getName());
					StringBuilder sb = new StringBuilder();
					sb.append("Detectados bloques de datos con error de firma");
					for (Iterator<IElementaryFile> iter = corruptedblocks.iterator(); iter.hasNext();) {
						IElementaryFile db = iter.next();
						sb.append(", ").append(db.getClass().getName().substring(db.getClass().getName().lastIndexOf(".") + 1));
					}
					regtext = sb.toString();
					FileProcessTaskProcessHelper.logger.error(regtext);
				}
			}
			IFileParser parser = FileParser.createParserFor(tachofile);
			FileProcessTaskProcessHelper.logger.info("File parser: {}", parser);
			parserResult = parser.parseFile();
			if (FileProcessTaskProcessHelper.logger.isDebugEnabled()) {
				FileProcessTaskProcessHelper.logger.debug("File information:");
				for (Entry<String, List<? extends AbstractData>> entry : parserResult.entrySet()) {
					FileProcessTaskProcessHelper.logger.debug("{} records {}", entry.getKey(), entry.getValue().size());
				}
			}
			this.insertarConductorDesconocido(parserResult);

			// Informacion del procesado incluyo fechas de rango de actividades.
			processedRecord = new ProcessedRecord(fInfo.getIdFile(), file.getName(), regtext, parserResult);
			List<Actividad> activs = (List<Actividad>) parserResult.get(Actividad.class.getName());
			if ((activs != null) && !activs.isEmpty()) {
				processedRecord.setOldestActivityTimeStamp(activs.get(0).fecComienzo);
				processedRecord.setNewestActivityTimeStamp(activs.get(activs.size() - 1).fecFin);
			} else {
				FileProcessTaskProcessHelper.logger.warn("Fichero sin actividades");
			}
			processedRecord.setExtractTimestamp(tachofile.getExtractTime(null));
			processedRecord.setOwner(tachofile.getOwnerId());
		} catch (Exception ex) {
			FileProcessTaskProcessHelper.logger.error(null, ex);
			String exmsg = "Error en la lectura del fichero " + file.getName();
			if (ex.getMessage() != null) {
				exmsg += " :" + ex.getMessage();
			} else {
				exmsg += ex;
			}
			processedRecord = new ProcessedRecord(fInfo.getIdFile(), file.getName(), exmsg, parserResult);
		}
		return processedRecord;
	}


	private void procPostVolcado(FileInfo fInfo, ProcessedRecord reg, Connection conn) throws Exception {
		if ((reg.idfile != null) && reg.idfile.equals(fInfo.getIdFile())) {
			Object procPostVolcado = this.procPostVolcado(fInfo, reg.owner, reg.text, reg.oldestActivityTimestamp, reg.newestActivityTimestamp, reg.extractTimestamp, conn);
			if (!"OK".equals(procPostVolcado)) {
				FileProcessTaskProcessHelper.logger.error("Error in postVolcado {}", procPostVolcado);
			}
		}
	}

	private Object procPostVolcado(FileInfo fInfo, String idOrigen, String obsr, Date dIni, Date dFin, Date dExtract, Connection con) throws Exception {
		final Number idFile = fInfo.getIdFile();
		final Set<String> sContract = fInfo.getContracts();
		StringBuilder sbContracts = new StringBuilder();
		List<String> lContracts = new ArrayList<>(sContract);
		Collections.sort(lContracts);
		for (int i = 0; i < lContracts.size(); i++) {
			sbContracts.append(lContracts.get(i));
			if (i != (lContracts.size() - 1)) {
				sbContracts.append(';');
			}
		}
		if (obsr != null) {
			obsr = obsr.substring(0, obsr.length() > 254 ? 254 : obsr.length());
		}

		java.sql.Date dExtr = dExtract == null ? null : new java.sql.Date(dExtract.getTime());
		try {
			return new FunctionJdbcTemplate<String>().execute(con, "FNC_INFRAC_POS_VOLCADO", Types.VARCHAR, idFile.intValue(), sbContracts.toString(), idOrigen, obsr, dIni, dFin,
					dExtr);
		} catch (Exception e) {
			FileProcessTaskProcessHelper.logger.error(null, e);
			return null;
		}
	}

	private void insertarConductorDesconocido(Map mData) {
		Conductor driver = new Conductor();
		driver.setIdConductor(Conductor.UNKNOWN_DRIVER);
		List list = (List) mData.get(Conductor.class.getName());
		if (list != null) {
			driver.setNombre("CONDUCTOR");
			driver.setApellidos("DESCONOCIDO");
			driver.setEquipmentType(1);// Ponerle tipo conductor desconocido
			list.add(driver);
		}
	}


}
