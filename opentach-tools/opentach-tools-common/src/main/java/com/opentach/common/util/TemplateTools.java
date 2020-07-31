package com.opentach.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.visitor.BaseVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.StringTools;

/**
 * Consider to move to common place all facts of fill templates. See moreover TemplateManager, consider to unificate.
 */
@Component
public class TemplateTools {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(TemplateTools.class);

	private VelocityEngine		velocityEngine;

	public TemplateTools() {
		super();
	}

	protected VelocityEngine getVelocityEngine() {
		if (this.velocityEngine == null) {
			// Initialize the engine.
			this.velocityEngine = new VelocityEngine();
			this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
			this.velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", TemplateTools.logger.getName());
			this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
			this.velocityEngine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
			this.velocityEngine.addProperty("string.resource.loader.repository.static", "false");
			// engine.addProperty("string.resource.loader.modificationCheckInterval", "1");
			this.velocityEngine.init();
		}
		return this.velocityEngine;
	}

	protected Template getTemplate(String strVelocityTemplate) {
		VelocityEngine engine = this.getVelocityEngine();
		// Initialize my template repository. You can replace the "Hello $w" with your String.
		StringResourceRepository repo = (StringResourceRepository) engine.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource("temp", strVelocityTemplate);
		return engine.getTemplate("temp", "UTF-8");
	}

	public String velocityWithStringTemplateExample(String strVelocityTemplate, Map<?, ?> data) {
		if (StringTools.isEmpty(strVelocityTemplate)) {
			return null;
		}
		Template template = this.getTemplate(strVelocityTemplate);
		// Set parameters for my template.
		Map<?, ?> velocityData = data != null ? new HashMap<>(data) : new HashMap<>();
		VelocityContext context = new VelocityContext(velocityData);
		// Get and merge the template with my parameters.
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

	public String fillTemplateByClasspath(final String classpathResource, final Map<String, Object> parameters) throws IOException {
		String templateStr = null;
		try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathResource)) {
			templateStr = new String(FileTools.getBytesFromFile(in));
		}
		return this.velocityWithStringTemplateExample(templateStr, parameters);
	}

	public List<String> getKeys(String... strVelocityTemplates) {
		List<String> result = new ArrayList<>();
		for (String strVelocityTemplate : strVelocityTemplates) {
			List<String> keys = this.getKeys(strVelocityTemplate);
			ListTools.ensureAllValues(result, keys);
		}
		return result;
	}

	public List<String> getKeys(String strVelocityTemplate) {
		Template template = this.getTemplate(strVelocityTemplate);
		List<String> keys = new ArrayList<>();
		SimpleNode sn = (SimpleNode) template.getData();
		sn.jjtAccept(new BaseVisitor() {
			@Override
			public Object visit(ASTReference node, Object data) {
				keys.add(node.literal().replace("$", ""));
				return super.visit(node, data);
			}
		}, new Object());
		return keys;
	}
}

