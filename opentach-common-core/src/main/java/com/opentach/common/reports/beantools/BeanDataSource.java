package com.opentach.common.reports.beantools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ReflectionTools;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class BeanDataSource implements JRDataSource {
	private final ListIterator<?>	content;
	private Object						current;

	private static Map<Pair<Class<?>, String>, Method>	cache	= new HashMap<>();

	public BeanDataSource(List<?> content) {
		super();
		this.content = content.listIterator();
	}

	@Override
	public boolean next() throws JRException {
		if (this.content.hasNext()) {
			this.current = this.content.next();
			return true;
		}
		return false;
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		Pair<Class<?>, String> key = new Pair<Class<?>, String>(this.current.getClass(), jrField.getName());
		Method method = BeanDataSource.cache.get(key);
		if (method == null) {
			method = ReflectionTools.getMethodByName(this.current.getClass(), "get" + jrField.getName());
			BeanDataSource.cache.put(key, method);
		}
		try {
			return method.invoke(this.current);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
