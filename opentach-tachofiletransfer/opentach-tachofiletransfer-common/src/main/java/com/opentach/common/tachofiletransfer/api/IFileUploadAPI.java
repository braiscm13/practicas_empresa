package com.opentach.common.tachofiletransfer.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.opentach.common.rest.FeignConfiguration;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;

@FeignClient(url = "${feign.url}/public/rest/fileService", name = "SiiService", contextId = "sender-sii.sii", configuration = FeignConfiguration.class)
public interface IFileUploadAPI {

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public void uploadFile(//
			MultipartFile tachoFile, TachoFileUploadRequest inputParameters) throws TachoFileTransferException;

}
