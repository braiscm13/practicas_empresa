package com.opentach.server.remotevehicle.provider.man.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.settings.AbstractSettingsHelper;
import com.ontimize.jee.common.settings.ISettingsHelper;

@Configuration
@ComponentScan(basePackages = { "com.opentach.server.remotevehicle.provider.man" })
public class ManTestConfig {

	@Bean
	public ISettingsHelper settings() {
		Map<String,String> params = new HashMap<>();
		params.put("remote_vehicle.man.client_id", "5a6097a8-c25f-4842-b239-ffa8f9179a45");
		params.put("remote_vehicle.man.grant_type", "partner_integration");
		params.put("remote_vehicle.man.client_secret", "MjAwMDhjOWYtZjFmOC00NWM3LWI3MzItNDkxNTQ3MTgyNGI2");
		params.put("remote_vehicle.man.integration_id", "9aa6990a-33f6-47c1-897a-cdefce07defd");
		return new AbstractSettingsHelper() {

			@Override
			protected String query(String key) throws OntimizeJEEException {
				return params.get(key);
			}
		};
	}

}
