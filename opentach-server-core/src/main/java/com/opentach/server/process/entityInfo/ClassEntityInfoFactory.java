package com.opentach.server.process.entityInfo;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.server.process.FileProcessTaskInsertDbHelper;

public class ClassEntityInfoFactory {

	private static final Logger								logger	= LoggerFactory.getLogger(ClassEntityInfoFactory.class);
	private static final List<AbstractClassEntityInfo<?>>	entitiesInfo;

	static {
		entitiesInfo = new ArrayList<AbstractClassEntityInfo<?>>();
		try (InputStream in = FileProcessTaskInsertDbHelper.class.getClassLoader().getResourceAsStream("process/ParserProcesado.properties")) {
			Properties fieldColumMap = new Properties();
			fieldColumMap.load(in);
			String className = "";
			for (int i = 1; className != null; i++) {
				className = (String) fieldColumMap.get("insert." + i);
				if (className != null) {
					ClassEntityInfoFactory.entitiesInfo.add(ClassEntityInfoFactory.getInstance(className, fieldColumMap));
				}
			}
		} catch (Exception error) {
			ClassEntityInfoFactory.logger.error(null, error);
		}
	}

	/**
	 * Return list of columns and type to store the obj parameter.
	 *
	 * @param obj
	 * @return
	 */
	private static Map<String, String> getColumField(Class<?> clazz, Properties fieldColumMap) {
		Map<String, String> colfield = new HashMap<String, String>();
		String name, pstr;
		/** @todo Mirar de optimizar el acceso ( sacar bucles, etc.) */
		try {
			Field fields[] = clazz.getDeclaredFields();
			for (Field f : fields) {
				try {
					String fName = f.getName();
					name = clazz.getName() + "." + fName;
					pstr = fieldColumMap.getProperty(name);
					if (pstr != null) {
						colfield.put(pstr, fName);
					}
				} catch (Exception ex) {
					ClassEntityInfoFactory.logger.error(null, ex);
				}
			}
			return colfield;
		} catch (Exception ex) {
			ClassEntityInfoFactory.logger.error(null, ex);
			return null;
		}
	}

	private static AbstractClassEntityInfo<?> getInstance(String className, Properties prop) throws ClassNotFoundException {
		String entityInfoClassName = prop.getProperty(className + ".entityInfoClass");
		AbstractClassEntityInfo<?> cei = (AbstractClassEntityInfo<?>) ReflectionTools.newInstance(entityInfoClassName, //
				new Object[] { //
						Class.forName(className), //
						prop.getProperty(className + ".autonumeric"), //
						prop.getProperty(className + ".usercol"), //
						prop.getProperty(className + ".timestampcol"), //
						prop.getProperty(className + ".table"), //
						ClassEntityInfoFactory.getColumField(Class.forName(className), prop)//
		});
		return cei;
	}

	public static List<AbstractClassEntityInfo<?>> getEntitiesInfo() {
		return ClassEntityInfoFactory.entitiesInfo;
	}
}
