package com.opentach.client.util.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.locator.ClientReferenceLocator;
import com.opentach.client.util.upload.TGDFileSendThread.ProgressNotifier;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;

public class UploadFileStrategyRestClient {

	private static final Logger logger = LoggerFactory.getLogger(UploadFileStrategyRestClient.class);

	public void upload(int sesId, String filename, String scif, byte[] fileData, ProgressNotifier progressNotifier) throws Exception {
		String proxyUrl = System.getProperty("opentach.proxy.url");
		proxyUrl += "/openservices/public/rest/fileService/uploadFile";

		TachoFileUploadRequest inputParameters = new TachoFileUploadRequest();
		inputParameters.setCif(scif);
		if ((ApplicationManager.getApplication() != null) && ApplicationManager.getApplication().getClass().getName().contains("DownCenterClientApplication")) {
			inputParameters.setSourceType(UploadSourceType.CENTRO_DESCARGAS.toString());
		} else {
			inputParameters.setSourceType(UploadSourceType.ESCRITORIO.toString());
		}
		inputParameters.setUser(((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser());
		Resource file = new ByteArrayResource(fileData) {
			@Override
			public String getFilename() {
				return filename;
			}
		};
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("inputParameters", inputParameters);
		body.add("tachoFile", file);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		if (progressNotifier.isCancelled()) {
			return;
		}

		progressNotifier.updateStatus("EnviandoFichero " + filename);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(proxyUrl, requestEntity, String.class);
		progressNotifier.setCurrentPosition(fileData.length);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			throw new TachoFileTransferException(proxyUrl + ":" + response.getStatusCodeValue() + ":" + response.getBody());
		}
	}
}
