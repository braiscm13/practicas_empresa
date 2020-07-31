package com.opentach.server.proxy.tachofiletransfer.restcontrollers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ontimize.jee.common.tools.FileTools;
import com.opentach.common.tachofiletransfer.api.IFileUploadAPI;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.ErrorCode;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;
import com.opentach.server.proxy.tachofiletransfer.FileRedirectorComponent;

@RestController
@RequestMapping("openservices/public/rest/fileService")
public class OpentachFileUploadRestController implements IFileUploadAPI {
	private static final Logger		logger	= LoggerFactory.getLogger(OpentachFileUploadRestController.class);

	@Autowired
	private FileRedirectorComponent	fileRedirector;

	@Override
	public void uploadFile(@RequestPart("tachoFile") MultipartFile tachoFile, @RequestPart("inputParameters") TachoFileUploadRequest inputParameters)
			throws TachoFileTransferException {
		OpentachFileUploadRestController.logger.info("Received opentach file {} for cif {} of type {},", tachoFile.getOriginalFilename(),
				inputParameters.getCif(),
				inputParameters.getSourceType());
		byte[] file;
		try {
			file = FileTools.getBytesFromFile(tachoFile.getInputStream());
		} catch (IOException err) {
			throw TachoFileTransferException.fromCode(ErrorCode.INVALID_FILE_LENGTH);
		}
		this.fileRedirector.onNewFileReceived(file, tachoFile.getOriginalFilename(), inputParameters);
	}

}
