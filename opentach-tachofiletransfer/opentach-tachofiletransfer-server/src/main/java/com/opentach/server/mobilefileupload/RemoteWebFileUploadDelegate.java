package com.opentach.server.mobilefileupload;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.FileManagementEntity;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.mobilefileupload.IRemoteWebFileUpload;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.filereception.TGDFileReceptionService;

public class RemoteWebFileUploadDelegate implements IRemoteWebFileUpload, ITGDFileConstants {

	private static final Logger			logger	= LoggerFactory.getLogger(RemoteWebFileUploadDelegate.class);

	private final AbstractOpentachServerLocator	locator;

	public RemoteWebFileUploadDelegate(AbstractOpentachServerLocator brefs) {
		super();
		this.locator = brefs;
	}

	@Override
	public String uploadFileByPhone(byte[] file, String pinNumber, String user, String password, boolean analize, Map<?, ?> otherParams, Date downloadDate) throws Exception {
		// Compruebo si el fichero es un ficherto de tarjeta o tacografo
		// valido.
		TachoFile tachofile = TachoFile.readTachoFile(file);
		RemoteWebFileUploadDelegate.logger.info("COMPROBANDO SI ES DE TARJETA O TACOGRAFO");
		if (tachofile == null) {
			throw new Exception("UNKNOW_FILE_FORMAT");
		}
		SmartPhoneInfo smartphoneInfo = null;
		try {
			smartphoneInfo = this.getSmartPhoneInfo(pinNumber, analize, otherParams);
		} catch (Exception ex) {
			// Si no encontramos el smarphone, en caso de subida de
			// fichero, seguimos igualmente
			smartphoneInfo = new SmartPhoneInfo(pinNumber, null, null, null, null, null, null, analize);
		}
		this.saveFile(tachofile, file, smartphoneInfo, downloadDate);
		return "OK";
	}

	private void saveFile(TachoFile tachofile, byte[] file, SmartPhoneInfo spInfo, Date downloadDate) throws Exception {
		FileManagementEntity entF = (FileManagementEntity) this.locator.getEntityReferenceFromServer(ITGDFileConstants.FILE_ENTITY);

		this.locator.getService(TGDFileReceptionService.class).uploadFile(tachofile, file, spInfo.getCif(), downloadDate, UploadSourceType.OPENTACH_MOVIL, true,
				null,
				spInfo.getBlackberryEmail(), true, spInfo.isAnalize(), spInfo.getBlackberryEmail(),
				spInfo.getBlackberryEmail() == null ? spInfo.getPin() : (String) spInfo.getBlackberryEmail(), spInfo.getLatitude(), spInfo.getLongitude(), spInfo.getId(),
				null, TableEntity.getEntityPrivilegedId((Entity) entF));
	}

	private SmartPhoneInfo getSmartPhoneInfo(String pinNumber, boolean analize, Map<?, ?> otherParams) throws Exception {
		Double latitude = null;
		Double longitude = null;
		try {
			String sLatitude = (String) otherParams.get("latitude");
			latitude = new Double(sLatitude);
			String sLongitude = (String) otherParams.get("longitude");
			longitude = new Double(sLongitude);
		} catch (Exception e) {
		}
		if (pinNumber != null) {
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put("PIN", pinNumber.toUpperCase());
			Entity entity = this.locator.getEntityReferenceFromServer("EBlackberry");
			EntityResult res = entity.query(cv, new Vector<String>(Arrays.asList(new String[] { "EMAIL", "MAILINFORMEANALISIS", "CIF", "IDBLACKBERRY" })),
					TableEntity.getEntityPrivilegedId(entity));
			if (res.calculateRecordNumber() > 0) {
				Hashtable record = res.getRecordValues(0);
				String mail = (String) record.get("EMAIL");
				String mailToReport = (String) record.get("MAILINFORMEANALISIS");
				String cif = (String) record.get("CIF");
				Object id = record.get("IDBLACKBERRY");
				return new SmartPhoneInfo(pinNumber, id, cif, mailToReport, mail, latitude, longitude, analize);
			}
			return new SmartPhoneInfo(pinNumber, null, null, (String) otherParams.get("report_mail"), null, latitude, longitude, analize);
		}
		return new SmartPhoneInfo(pinNumber, null, null, (String) otherParams.get("report_mail"), null, latitude, longitude, analize);
		// throw new Exception("NO_REGISTERED_SMARTPHONE");
	}

	@Override
	public String checkCredentials(String user, String password, Map<?, ?> parameters) {
		return "ok";
	}

	@Override
	public String saveMailExtraction(Map<?, ?> parameters) throws Exception {
		SimpleDateFormat sdfMailExtraction = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Entity entity = this.locator.getEntityReferenceFromServer("EMobileFileExtraction");
		String sDownloadTime = this.doNullTrimUpper((String) parameters.get("donwloadtime"));
		String userMail = this.doNullTrimUpper((String) parameters.get("mail"));
		String company = this.doNullTrimUpper((String) parameters.get("company"));
		String vehicle = this.doNullTrimUpper((String) parameters.get("vehicle"));

		String licenseAcepted = this.doNullTrimUpper((String) parameters.get("licenseacepted")).equals("yes") ? "S" : "N";
		String authAcepted = this.doNullTrimUpper((String) parameters.get("authorizationacepted")).equals("yes") ? "S" : "N";
		Date licenseAceptedDate = null;
		try {
			licenseAceptedDate = sdfMailExtraction.parse(this.doNullTrimUpper((String) parameters.get("dateacepted")));
		} catch (ParseException e1) {
		}

		Date downloadTime = null;
		if (sDownloadTime != null) {
			try {
				downloadTime = sdfMailExtraction.parse(sDownloadTime);
			} catch (ParseException e) {
				downloadTime = new Date();
			}
		}
		Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put("DOWNLOADTIME", downloadTime);
		MapTools.safePut(av, "USERMAIL", userMail);
		MapTools.safePut(av, "COMPANY", company);
		MapTools.safePut(av, "VEHICLE", vehicle);
		MapTools.safePut(av, "LICENSEACEPTED", licenseAcepted);
		MapTools.safePut(av, "AUTHORIZATIONACEPTED", authAcepted);
		MapTools.safePut(av, "LICENSEACEPTEDDATE", licenseAceptedDate);

		EntityResult rs = entity.insert(av, TableEntity.getEntityPrivilegedId(entity));
		if (rs.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
			throw new Exception(rs.getMessage());
		}
		return "OK";
	}

	@Override
	public String checkVipCode(String code, String deviceId) throws Exception {
		if ((code == null) || (code.length() == 0)) {
			throw new Exception("INVALID_CODE");
		}
		if (deviceId == null) {
			throw new Exception("INVALID_DEVICE_ID");
		}
		Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put("CODE", code.toUpperCase());
		Vector<Object> av = new Vector<Object>();
		av.add("CODE");
		av.add("DEVICE_ID");
		Entity ent = this.locator.getEntityReferenceFromServer("ESmartphoneVipCodes");
		EntityResult res = ent.query(cv, av, TableEntity.getEntityPrivilegedId(ent));
		if (res.calculateRecordNumber() == 0) {
			throw new Exception("INVALID_CODE");
		} else if (res.calculateRecordNumber() > 1) {
			throw new Exception("INVALID_CODE");
		}
		Hashtable recordValues = res.getRecordValues(0);
		Object dbDeviceId = recordValues.get("DEVICE_ID");
		if ((dbDeviceId != null) && (!dbDeviceId.equals(deviceId))) {
			throw new Exception("INVALID_CODE");
		}
		if (dbDeviceId == null) {
			Hashtable<String, Object> avUpdate = new Hashtable<String, Object>();
			avUpdate.put("DEVICE_ID", deviceId);
			avUpdate.put("ACQUISITION_DATE", new Date());
			ent.update(avUpdate, cv, TableEntity.getEntityPrivilegedId(ent));
		}
		return "OK";
	}

	private String doNullTrimUpper(String s) {
		if (s == null) {
			return null;
		} else if ("".equals(s.trim())) {
			return null;
		} else {
			return s.trim().toUpperCase();
		}
	}

}
