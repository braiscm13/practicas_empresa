package com.opentach.server.util.jee;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.services.ServiceTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.common.exception.OpentachException;
import com.opentach.server.AbstractOpentachServerLocator;


public class TemporaryOJeeEntity implements Entity {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(TemporaryOJeeEntity.class);

	/** The service name. */
	protected String			serviceName;

	/** The method prefix of the server. */
	protected String			serviceMethodPrefix;

	public TemporaryOJeeEntity(String serviceMethod) {
		this.serviceName = ServiceTools.extractServiceFromEntityName(serviceMethod);
		this.serviceMethodPrefix = ServiceTools.extractServiceMethodPrefixFromEntityName(serviceMethod);
	}

	@Override
	public EntityResult query(Hashtable keysValues, Vector attributes, int sessionId) throws Exception {
		Method method = this.getServiceMethod(this.serviceMethodPrefix + "Query", 2);

		return (EntityResult) this.invoke(method, this.getServiceBean(), keysValues, attributes);
	}

	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId) throws Exception {
		throw new OpentachException("NOT_IMPLEMENTED");
	}

	@Override
	public EntityResult update(Hashtable attributesValues, Hashtable keysValues, int sessionId) throws Exception {
		throw new OpentachException("NOT_IMPLEMENTED");
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId) throws Exception {
		throw new OpentachException("NOT_IMPLEMENTED");
	}

	private Object getServiceBean() {
		return AbstractOpentachServerLocator.getLocator().getBean(this.serviceName);
	}

	private Method getServiceMethod(String methodName, int numArgs) throws OpentachException {
		try {
			return ReflectionTools.getMethodByNameAndParatemerNumber(this.getServiceBean().getClass(), methodName, numArgs);
		} catch (Exception err) {
			TemporaryOJeeEntity.logger.trace(null, err);
			try {
				return ReflectionTools.getMethodByName(this.getServiceBean().getClass(), methodName);
			} catch (Exception err2) {
				TemporaryOJeeEntity.logger.trace(null, err2);
			}
		}
		throw new OpentachException("E_SERVICE_OR_METHOD_NOT_FOUND");
	}

	private Object invoke(Method method, Object paramObject, Object... paramArrayOfObject) throws Exception {
		try {
			return method.invoke(paramObject, paramArrayOfObject);
		} catch (IllegalArgumentException e) {
			TemporaryOJeeEntity.logger.error("The invoked method's declaration does not match given parameters", e);
			throw e;
		} catch (InvocationTargetException e) {
			TemporaryOJeeEntity.logger.trace(null, e);
			if (e.getCause() instanceof Exception) {
				throw (Exception) e.getCause();
			} else {
				throw new Exception(null, e.getCause());
			}
		}
	}
}
