package com.opentach.server.proxy.tachofiletransfer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.common.tachofiletransfer.exception.ErrorCode;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;

@Component("FileRedirector")
public class FileRedirectorComponent {

	private static final Logger logger = LoggerFactory.getLogger(FileRedirectorComponent.class);

	@Value("${app.redirections.filseService}")
	private List<String>		redirections;
	@Autowired
	private FileStoreComponent	fileStore;

	public void onNewFileReceived(byte[] file, String originalFilename, TachoFileUploadRequest parameters) throws TachoFileTransferException {
		try {
			FileRedirectorComponent.logger.info("On new file {} for cif {} of type {},", originalFilename, parameters.getCif(), parameters.getSourceType());
			this.fileStore.onNewFileReceived(file, originalFilename, parameters);
			Resource fileResource = new ByteArrayResource(file) {
				@Override
				public String getFilename() {
					return originalFilename;
				}
			};

			int oks = 0;
			for (String serverUrl : this.redirections) {
				try {
					FileRedirectorComponent.logger.trace("Recirecting to {}", serverUrl);
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.MULTIPART_FORM_DATA);
					// headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));

					MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
					body.add("inputParameters", parameters);
					body.add("tachoFile", fileResource);

					HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
					if (!HttpStatus.OK.equals(response.getStatusCode())) {
						throw new TachoFileTransferException(serverUrl + ":" + response.getStatusCodeValue() + ":" + response.getBody());
					}
					FileRedirectorComponent.logger.info("file {} uploaded ok to {}",originalFilename,serverUrl);
					oks++;
				} catch (Exception err) {
					FileRedirectorComponent.logger.error(null, err);
				}
			}
			if (oks == 0) {
				throw TachoFileTransferException.fromCode(ErrorCode.NO_VALID_SERVER_FOUND);
			}
		} catch (TachoFileTransferException err) {
			throw err;
		} catch (Exception er) {
			throw TachoFileTransferException.fromCode(ErrorCode.ERR_CODE_GENERAL, er);
		}
	}
}
