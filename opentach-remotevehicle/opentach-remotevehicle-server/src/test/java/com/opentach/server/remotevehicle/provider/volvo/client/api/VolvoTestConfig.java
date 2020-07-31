package com.opentach.server.remotevehicle.provider.volvo.client.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.settings.AbstractSettingsHelper;
import com.ontimize.jee.common.settings.ISettingsHelper;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.server.remotevehicle.uploader.IUploader;

@Configuration
@ComponentScan(basePackages = { "com.opentach.server.remotevehicle.provider.volvo" })
public class VolvoTestConfig {

	private static final Logger logger = LoggerFactory.getLogger(VolvoTestConfig.class);

	@Bean
	public ISettingsHelper settings() {
		Map<String, String> params = new HashMap<>();
		params.put("file_proxy.url", "http://localhost:8081/openservices/openservices/public/rest/fileService/uploadFile");
		return new AbstractSettingsHelper() {

			@Override
			protected String query(String key) throws OntimizeJEEException {
				return params.get(key);
			}
		};
	}

	@Bean
	public IUploader uploader() {
		return new IUploader() {

			@Override
			public void uploadFile(byte[] file, String originalFilename, TachoFileUploadRequest parameters) {
				VolvoTestConfig.logger.info("file uploaded :P");
			}
		};
	}

}
