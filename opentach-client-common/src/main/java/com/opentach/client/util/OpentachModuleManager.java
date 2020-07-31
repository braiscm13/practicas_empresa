package com.opentach.client.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ontimize.builder.xml.CustomNode;
import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.module.ModuleManager;
import com.ontimize.module.OModule;

public class OpentachModuleManager extends ModuleManager {

	private static final Logger				logger						= LoggerFactory.getLogger(OpentachModuleManager.class);

	private static final String				CLIENT_TAG					= "Client";
	private static final String				REFERENCES_TAG				= "References";
	private static final String				REFERENCE_LOCATOR			= "ReferenceLocator";

	private static final String				ID_ATTR						= "id";
	private static final String				PACKAGE_ATTR				= "package";
	private static final String				ARCHIVE_ATTR				= "archive";
	private static final String				LOCAL_ENTITY_PACKAGE_ATTR	= "localentitypackage";
	private static final String				LOCAL_ENTITIES_ATTR			= "localentities";

	protected List<IApplicationListener>	applicationListeners;

	public OpentachModuleManager(ModuleType type) {
		super(type);
		this.applicationListeners = new ArrayList<>();
	}

	public void onApplicationBuilt(Application application) {
		for (IApplicationListener apl : this.applicationListeners) {
			apl.onApplicationBuilt(application);
		}
	}

	private void processApplicationListener(OModule currentModule, Hashtable<String, String> hashtableAttribute, DocumentBuilder dBuilder) {
		try {
			IApplicationListener applicationListener = ParseUtilsExtended.getClazzInstance(hashtableAttribute.get("class"), null);
			this.applicationListeners.add(applicationListener);
		} catch (Exception error) {
			OpentachModuleManager.logger.error(null, error);
		}
	}

	@Override
	protected void processClientModule(Element parentElement, OModule currentModule) {
		NodeList clientList = parentElement.getElementsByTagName(OpentachModuleManager.CLIENT_TAG);
		if (clientList.getLength() != 1) {
			OpentachModuleManager.logger.warn("Found {} {} tags.", clientList.getLength(), OpentachModuleManager.CLIENT_TAG);
		}
		for (int i = 0; i < clientList.getLength(); i++) {
			CustomNode clientNode = new CustomNode(clientList.item(i));
			if (clientNode.isTag()) {
				Hashtable<String, String> attributes = clientNode.hashtableAttribute();
				if (attributes.containsKey(OpentachModuleManager.PACKAGE_ATTR)) {
					currentModule.setClientBaseClasspath(attributes.get(OpentachModuleManager.PACKAGE_ATTR));
				}
				int number = clientNode.getChildrenNumber();
				for (int j = 0; j < number; j++) {
					CustomNode node = clientNode.child(j);
					if (node.isTag()) {
						String tag = node.getNodeInfo();
						if (XMLApplicationBuilder.TOOLBAR.equalsIgnoreCase(tag)) {
							Hashtable<String, String> toolBarAttrs = node.hashtableAttribute();
							if (toolBarAttrs.containsKey(OpentachModuleManager.ARCHIVE_ATTR)) {
								OpentachModuleManager.logger.debug("Process toolbar menu archive : {}", toolBarAttrs.get(OpentachModuleManager.ARCHIVE_ATTR));
								this.processToolbar(currentModule, toolBarAttrs.get(OpentachModuleManager.ARCHIVE_ATTR), this.dBuilder);
							} else {
								OpentachModuleManager.logger.warn("Toolbar menu archive is required");
							}
						} else if (XMLApplicationBuilder.TOOLBARLISTENER.equalsIgnoreCase(tag)) {
							Hashtable<String, String> param = node.hashtableAttribute();
							String classTListener = param.get("class");
							if (classTListener == null) {
								OpentachModuleManager.logger.warn("Toolbar Listener not specified in {} module", currentModule.getId());
							} else {
								currentModule.setToolbarListener(classTListener);
							}
						} else if (XMLApplicationBuilder.MENU.equalsIgnoreCase(tag)) {
							Hashtable<String, String> menuAttrs = node.hashtableAttribute();
							if (menuAttrs.containsKey(OpentachModuleManager.ARCHIVE_ATTR)) {
								OpentachModuleManager.logger.debug("Process menu archive : {}", menuAttrs.get(OpentachModuleManager.ARCHIVE_ATTR));
								this.processMenu(currentModule, menuAttrs.get(OpentachModuleManager.ARCHIVE_ATTR), this.dBuilder);
							} else {
								OpentachModuleManager.logger.warn("Toolbar menu archive is required");
							}
						} else if (XMLApplicationBuilder.MENULISTENER.equalsIgnoreCase(tag)) {
							Hashtable<String, String> param = node.hashtableAttribute();
							String classMListener = param.get("class");
							if (classMListener == null) {
								OpentachModuleManager.logger.warn("Toolbar Listener not specified in {} module", currentModule.getId());
							} else {
								currentModule.setMenuListener(classMListener);
							}
						} else if (OpentachModuleManager.REFERENCES_TAG.equalsIgnoreCase(tag)) {
							Hashtable<String, String> referencesAttrs = node.hashtableAttribute();
							if (referencesAttrs.containsKey(OpentachModuleManager.ARCHIVE_ATTR)) {
								OpentachModuleManager.logger.debug("Process remote references archive : {}", referencesAttrs.get(OpentachModuleManager.ARCHIVE_ATTR));
								this.processReferences(currentModule, referencesAttrs.get(OpentachModuleManager.ARCHIVE_ATTR), this.dBuilder);
							} else {
								OpentachModuleManager.logger.warn("References archive is required");
							}
						} else if (OpentachModuleManager.REFERENCE_LOCATOR.equalsIgnoreCase(tag)) {
							Hashtable<String, String> referencesAttrs = node.hashtableAttribute();
							if (referencesAttrs.containsKey(OpentachModuleManager.LOCAL_ENTITY_PACKAGE_ATTR)) {
								currentModule.setLocalEntityPackage(referencesAttrs.get(OpentachModuleManager.LOCAL_ENTITY_PACKAGE_ATTR));
							}
							if (referencesAttrs.containsKey(OpentachModuleManager.LOCAL_ENTITIES_ATTR)) {
								String localEntities = referencesAttrs.get(OpentachModuleManager.LOCAL_ENTITIES_ATTR);
								Vector<String> entitiesProperties = ApplicationManager.getTokensAt(localEntities, ";");
								currentModule.setLocalEntities(entitiesProperties);
							}
						} else if ("ApplicationListener".equals(tag)) {
							this.processApplicationListener(currentModule, node.hashtableAttribute(), this.dBuilder);
						} else {
							// FormManagers
							Hashtable<String, String> nodeAttrs = node.hashtableAttribute();
							if (nodeAttrs.containsKey(OpentachModuleManager.ID_ATTR)) {
								OpentachModuleManager.logger.trace("Add {} tag with id: {}", tag, nodeAttrs.get(OpentachModuleManager.ID_ATTR));
								currentModule.getFormManagers().add(node);
							} else {
								OpentachModuleManager.logger.warn("FormManager hasn't been added because {} tag is required", OpentachModuleManager.ID_ATTR);
							}
						}
					}
				}
			}
		}
	}

}
