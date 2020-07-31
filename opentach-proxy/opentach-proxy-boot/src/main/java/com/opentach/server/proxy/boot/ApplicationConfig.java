package com.opentach.server.proxy.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ApplicationConfig {

	private static final Logger	logger	= LoggerFactory.getLogger(ApplicationConfig.class);

	@Autowired
	public ApplicationContext	context;

	public ApplicationConfig() {
		super();
	}

	@Bean
	public ConversionService conversionService() {
		return new DefaultConversionService();
	}

}