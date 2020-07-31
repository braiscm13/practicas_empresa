package com.opentach.server.util.jee;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ontimize.jee.common.tools.ReflectionTools;

/**
 * Temporary class that will delegate call to OJEE Services, emulating an Entity Required for some actions like queryOtherEntities.
 */
public class TemporaryOJeeEntityInvocationHandler implements InvocationHandler {
	/** ProxyEntity */
	protected TemporaryOJeeEntity entity;

	public TemporaryOJeeEntityInvocationHandler(String serviceMethod) {
		this.entity = new TemporaryOJeeEntity(serviceMethod);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return ReflectionTools.invoke(this.entity, method.getName(), args);
	}

}
