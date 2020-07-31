package com.opentach.server.remotevehicle.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.db.Entity;
import com.ontimize.db.FileManagementEntity;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.settings.ISettingsHelper;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.rest.JSONTools;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;
import com.opentach.server.filereception.TGDFileReceptionService;
import com.opentach.server.util.spring.ILocatorReferencer;

@Component("ProxyUploader")
public class ProxyUploader implements IUploader {

	private static final String	DEFAULT_VALUE_FILE_PROXY_URL	= "http://localhost:8080/openservices/openservices/public/rest/fileService/uploadFile";
	private static final String	SET_KEY_FILE_PROXY_URL			= "file_proxy.url";
	private static final Logger logger = LoggerFactory.getLogger(ProxyUploader.class);
	@Autowired
	private ISettingsHelper		settings;
	@Autowired
	private ILocatorReferencer	locator;

	@Override
	public void uploadFile(byte[] file, String originalFilename, TachoFileUploadRequest parameters) {
		String proxyUrl = this.settings.getString(ProxyUploader.SET_KEY_FILE_PROXY_URL, ProxyUploader.DEFAULT_VALUE_FILE_PROXY_URL);
		try {
			ProxyUploader.logger.trace("Recirecting to proxy {}", proxyUrl);
			Resource fileResource = new ByteArrayResource(file) {
				@Override
				public String getFilename() {
					return originalFilename;
				}
			};
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("inputParameters", parameters);
			body.add("tachoFile", fileResource);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(proxyUrl, requestEntity, String.class);
			if (!HttpStatus.OK.equals(response.getStatusCode())) {
				throw new TachoFileTransferException(proxyUrl + ":" + response.getStatusCodeValue() + ":" + response.getBody());
			}
			ProxyUploader.logger.info("file {} uploaded ok to {}", originalFilename, proxyUrl);
		} catch (Exception err) {
			ProxyUploader.logger.error("Error uploading file {} of {} to proxy {}, trying local upload", originalFilename, parameters.getCif(), proxyUrl, err);
			// si falla el proxy lo subimos al servidor local
			this.uploadLocal(file, originalFilename, parameters);
		}
	}

	private void uploadLocal(byte[] file, String originalFilename, TachoFileUploadRequest parameters) {
		try {
			TachoFile tachofile = TachoFile.readTachoFile(file);
			FileManagementEntity entF = (FileManagementEntity) this.locator.getLocator().getEntityReferenceFromServer(ITGDFileConstants.FILE_ENTITY);
			this.locator.getLocator().getService(TGDFileReceptionService.class).uploadFile(tachofile, file, parameters.getCif(), JSONTools.str2dateTime(parameters
					.getDownloadDateTime()),
					UploadSourceType.fromString(parameters.getSourceType()), false, originalFilename, parameters.getReportMail(), false, parameters.isAnalyze(),
					parameters.getReportMail(), null, null, null, null, null, TableEntity.getEntityPrivilegedId((Entity) entF));
		} catch (Exception err) {
			ProxyUploader.logger.error("Error uploading file {} of {} to local", originalFilename, parameters.getCif(), err);
		}
	}
}
