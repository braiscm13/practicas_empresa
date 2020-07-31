package com.opentach.server.tachofiles;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.tachofiles.ITachoFileRecordService;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class TachoFileService.
 */
public class TachoFileRecordService extends UAbstractService implements ITachoFileRecordService {

	private static final Logger logger = LoggerFactory.getLogger(TachoFileRecordService.class);

	/**
	 * Instantiates a new tacho file service.
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
	public TachoFileRecordService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public void saveFileRecord(final Object idFile, final String cgContrato, final String type, final String name, final String obsr, final UploadSourceType sourceType,
			final String mail, final boolean analize, final String notifUrl, final Number latitude, final Number longitude, final Object idBlackberry, final String ismovil,
			final int sessionID) throws Exception {
		new OntimizeConnectionTemplate<Void>() {

			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					TachoFileRecordService.this.saveFileRecord(idFile, cgContrato, type, name, obsr, sourceType, mail, analize, notifUrl, latitude, longitude, idBlackberry,
							ismovil, null, sessionID, con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);

	}

	public void saveFileRecord(Object idFile, Object cgContrato, String type, String name, String obsr, UploadSourceType sourceType, String mail, boolean analize, String notifUrl,
			Number latitude, Number longitude, Object idBlackberry, String ismovil, String uploadUser, int sessionId, Connection conn) throws Exception {

		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put(ITGDFileConstants.FILEKIND_FIELD, type);
		av.put(OpentachFieldNames.FILENAME_FIELD, name);
		MapTools.safePut(av, ITGDFileConstants.OBSR_FIELD, obsr);
		MapTools.safePut(av, ITGDFileConstants.EMAIL_FIELD, mail);
		MapTools.safePut(av, OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
		MapTools.safePut(av, ITGDFileConstants.ANALIZAR_FIELD, analize ? "S" : "N");
		MapTools.safePut(av, OpentachFieldNames.IDFILE_FIELD, idFile);
		MapTools.safePut(av, ITGDFileConstants.NOTIF_FIELD, notifUrl);
		MapTools.safePut(av, "USUARIO_ALTA", uploadUser);
		MapTools.safePut(av, "SOURCE_TYPE", sourceType == null ? null : sourceType.toString());
		if ((latitude != null) && (longitude != null)) {
			av.put("LATITUDE", latitude);
			av.put("LONGITUDE", longitude);
		}
		MapTools.safePut(av, "IDBLACKBERRY", idBlackberry);
		MapTools.safePut(av, "ISMOVIL", ismovil);

		TransactionalEntity entity = this.getEntity(ITGDFileConstants.FILERECORD_ENTITY);
		EntityResult res = entity.insert(av, this.getSessionId(sessionId, entity), conn);
		if ((res == null) || (res.getCode() != EntityResult.OPERATION_SUCCESSFUL)) {
			throw new Exception("M_ERROR_INSERTAR_FILE_RECORD");
		}
	}

	public void saveFileRecord(Object idFile, String cgContrato, String type, String name, String obsr, UploadSourceType sourceType, int sessionId, Connection conn)
			throws Exception {
		// intentamos cogernos el resto de valores de CDFICHEROS_REGISTRO
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues(OpentachFieldNames.IDFILE_FIELD, idFile, OpentachFieldNames.CG_CONTRATO_FIELD,
				new SearchValue(SearchValue.NULL, null));
		TransactionalEntity entity = this.getEntity(ITGDFileConstants.FILERECORD_ENTITY);
		EntityResult er = entity.query(kv, EntityResultTools.attributes(ITGDFileConstants.EMAIL_FIELD, ITGDFileConstants.ANALIZAR_FIELD, ITGDFileConstants.NOTIF_FIELD, "LATITUDE",
				"LONGITUDE", "IDBLACKBERRY", "ISMOVIL", "SOURCE_TYPE"), this.getSessionId(sessionId, entity), conn);
		Map<String, Object> record = null;
		if (er.calculateRecordNumber() > 0) {
			record = er.getRecordValues(0);
		} else {
			record = new HashMap<String, Object>();
		}

		String mail = (String) record.get(ITGDFileConstants.EMAIL_FIELD);
		boolean analize = "S".equals(record.get(ITGDFileConstants.ANALIZAR_FIELD));
		String notifUrl = (String) record.get(ITGDFileConstants.NOTIF_FIELD);
		Number latitude = (Number) record.get("LATITUDE");
		Number longitude = (Number) record.get("LONGITUDE");
		Object idBlackberry = record.get("IDBLACKBERRY");
		String isMovil = (String) record.get("ISMOVIL");
		if ((sourceType == null) && ITachoFileRecordService.UPLOAD.equals(type)) {
			sourceType = UploadSourceType.fromString((String) record.get("SOURCE_TYPE"));
		}
		this.saveFileRecord(idFile, cgContrato, type, name, obsr, sourceType, mail, analize, notifUrl, latitude, longitude, idBlackberry, isMovil, null, sessionId, conn);
	}
}
