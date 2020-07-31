package com.opentach.server.tachofiletransfer.restcontrollers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ontimize.db.Entity;
import com.ontimize.db.FileManagementEntity;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.rest.JSONTools;
import com.opentach.common.tachofiletransfer.api.IFileUploadAPI;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.ErrorCode;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;
import com.opentach.server.filereception.TGDFileReceptionService;
import com.opentach.server.util.spring.OpentachLocatorReferencer;

@RestController
@RequestMapping("fileService")
public class FileUploadRestController implements IFileUploadAPI {
	private static final Logger			logger	= LoggerFactory.getLogger(FileUploadRestController.class);

	@Autowired
	private OpentachLocatorReferencer	locator;

	@Override
	public void uploadFile(@RequestPart("tachoFile") MultipartFile tachoFile, @RequestPart("inputParameters") TachoFileUploadRequest inputParameters)
			throws TachoFileTransferException {
		try {
			FileUploadRestController.logger.info("Received file {} for cif {} of type {},", tachoFile.getOriginalFilename(), inputParameters.getCif(),
					inputParameters.getSourceType());
			FileManagementEntity entF = (FileManagementEntity) this.locator.getLocator().getEntityReferenceFromServer(ITGDFileConstants.FILE_ENTITY);
			byte[] file = FileTools.getBytesFromFile(tachoFile.getInputStream());
			Date downloadDate = null;
			try {
				downloadDate = JSONTools.str2dateTime(inputParameters.getDownloadDateTime());
			} catch (Exception ex) {
				FileUploadRestController.logger.trace(null, ex);
			}

			Double latitude = null;
			Double longitude = null;
			try {
				latitude = StringTools.isEmpty(inputParameters.getLatitude()) ? null : Double.valueOf(inputParameters.getLatitude());
				longitude = StringTools.isEmpty(inputParameters.getLongitude()) ? null : Double.valueOf(inputParameters.getLongitude());
			} catch (Exception ex) {
				FileUploadRestController.logger.trace(null, ex);
			}
			String reportMail = inputParameters.getReportMail();
			String fileName = tachoFile.getOriginalFilename();

			UploadSourceType uploadType = UploadSourceType.fromString(inputParameters.getSourceType());

			this.locator.getLocator().getService(TGDFileReceptionService.class).uploadFile( //
					null, //
					file, //
					inputParameters.getCif(), //
					downloadDate, //
					uploadType, //
					true, //
					fileName, //
					reportMail, //
					this.checkIsMobile(uploadType), //
					inputParameters.isAnalyze(), //
					reportMail, //
					reportMail, //
					latitude, //
					longitude, //
					null, //
					inputParameters.getUser(), //
					TableEntity.getEntityPrivilegedId((Entity) entF));
		} catch (Exception er) {
			throw TachoFileTransferException.fromCode(ErrorCode.ERR_CODE_GENERAL, er);
		}
	}

	private boolean checkIsMobile(UploadSourceType uploadType) {
		return ObjectTools.isIn(uploadType, UploadSourceType.OPENTACH_MOVIL, UploadSourceType.TACHOCABLE);
	}

}
