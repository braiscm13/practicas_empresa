package com.opentach.server.dao.autofill;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.server.dao.AbstractOpentachJdbcDao;

public class AutoFillHelper {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(AutoFillHelper.class);

	public static enum AUTOFILL {
		GETUSER("getUser"), GETCDATETIME("getCDateTime");

		private String method;

		private AUTOFILL(String method) {
			this.method = method;
		}

		@Override
		public String toString() {
			return this.method;
		}
	}

	private AutoFillHelper() {
		// Nothing
	}

	public static void autoFillCols2Hash(Map<?, ?> cv, Map<String, String> autofillcols) {
		AutoFillHelper.autoFillCols2Hash(cv, autofillcols, null);
	}

	public static void autoFillCols2Hash(Map<?, ?> cv, Map<String, String> autofillcols, AbstractOpentachJdbcDao dao) {
		if (autofillcols == null) {
			return;
		}
		for (Object key : autofillcols.keySet()) {
			if (!cv.containsKey(key)) {
				MapTools.safePut((Map) cv, key, AutoFillHelper.getAutoFillValue(autofillcols.get(key), dao));
			}
		}
	}

	private static Object getAutoFillValue(String methodName, AbstractOpentachJdbcDao dao) {
		// First search in source dao (overridable function)
		if (dao != null) {
			try {
				Method method = ReflectionTools.getMethodByName(dao.getClass(), methodName);
				return method.invoke(dao);
			} catch (Exception ex1) {
				AutoFillHelper.logger.trace("E_AUTOFILL_COLUMN__DAO", ex1);
			}
		}
		// Second search here (default functions)
		try {
			Method method = ReflectionTools.getMethodByName(AutoFillHelper.class, methodName);
			return method.invoke(null);
		} catch (Exception ex1) {
			AutoFillHelper.logger.trace("E_AUTOFILL_COLUMN__AUTOFILL_HELPER", ex1);
		}
		return null;
	}

	// Some autofill default methods ////////////////////////////////////////////
	public static String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public static Date getCDateTime() {
		return new Date();
	}

}
