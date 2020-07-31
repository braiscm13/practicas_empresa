package com.opentach.server.proxy.tachofiletransfer.restcontrollers;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;
import com.opentach.server.proxy.tachofiletransfer.FileRedirectorComponent;
import com.opentach.server.proxy.tachofiletransfer.model.telepass.TelepassRequest;

@RestController
@RequestMapping("openservices/public/rest/telepassFileService")
public class TelepassFileUploadRestController {
	private static final Logger		logger	= LoggerFactory.getLogger(TelepassFileUploadRestController.class);

	@Autowired
	private FileRedirectorComponent	fileRedirector;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public void uploadFile(@RequestBody TelepassRequest inputParameters) throws TachoFileTransferException {
		TelepassFileUploadRestController.logger.info("Received telepass file {} for cif {} of type RTP,", inputParameters.getFileName(), inputParameters.getVat());
		byte[] file = Base64.decodeBase64(inputParameters.getContent());
		TachoFileUploadRequest tachoRequest = new TachoFileUploadRequest();
		tachoRequest.setAnalyze(false);
		tachoRequest.setCif(inputParameters.getVat());
		tachoRequest.setSourceType(UploadSourceType.REMOTA_TELEPASS.toString());
		this.fileRedirector.onNewFileReceived(file, inputParameters.getFileName(), tachoRequest);
	}

}
