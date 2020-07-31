package com.opentach.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.gui.Application;
import com.ontimize.module.ModuleManager.ModuleType;

public class OpentachXMLApplicationBuilder extends XMLApplicationBuilder {

	private static final Logger logger = LoggerFactory.getLogger(OpentachXMLApplicationBuilder.class);

	public OpentachXMLApplicationBuilder(String labelsFileURI, String packageName) throws Exception {
		super(labelsFileURI, packageName);
		this.moduleManager = new OpentachModuleManager(ModuleType.CLIENT);
	}

	public OpentachXMLApplicationBuilder(String labelsFileURI) throws Exception {
		super(labelsFileURI);
		this.moduleManager = new OpentachModuleManager(ModuleType.CLIENT);
	}

	public void onApplicationBuilt(Application application) {
		((OpentachModuleManager) this.moduleManager).onApplicationBuilt(application);

	}

}
