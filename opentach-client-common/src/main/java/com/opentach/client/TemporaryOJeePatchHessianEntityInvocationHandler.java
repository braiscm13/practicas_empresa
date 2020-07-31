package com.opentach.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.field.ReferenceFieldAttribute;
import com.ontimize.gui.table.TableAttribute;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.desktopclient.hessian.HessianEntityInvocationHandler;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

public class TemporaryOJeePatchHessianEntityInvocationHandler extends HessianEntityInvocationHandler {

	private static final Logger logger = LoggerFactory.getLogger(TemporaryOJeePatchHessianEntityInvocationHandler.class);

	private static final String	OJEE_PREFIX	= "ojee.";

	public TemporaryOJeePatchHessianEntityInvocationHandler(String entityName) {
		super(entityName);
	}

	@Override
	public EntityResult query(Hashtable keysValues, Vector attributes, int sessionId) throws Exception {
		Object hessianProxy = this.getHessianService();
		Method hessianMethod = null;
		String methodName = this.serviceMethodPrefix + "Query";
		hessianMethod = ReflectionTools.getMethodByName(hessianProxy.getClass(), methodName);
		this.checkHessianMethodExists(hessianMethod, methodName);
		Map<String, Object> replacements = this.prepareAttributes(attributes);
		EntityResult res = null;
		if (this.queryId == null) {
			res = (EntityResult) this.invoke(hessianMethod, hessianProxy, keysValues, attributes);
		} else {
			res = (EntityResult) this.invoke(hessianMethod, hessianProxy, keysValues, attributes, this.queryId);
		}
		this.restoreAttributes(res, replacements);
		return res;
	}

	private void restoreAttributes(EntityResult res, Map<String, Object> replacements) {
		for (Object key : res.keySet().toArray()) {
			if (key instanceof TableAttribute) {
				TableAttribute tAttr = (TableAttribute) key;
				String replacement = tAttr.getEntity();
				if (replacements.containsKey(replacement)) {
					res.put(replacements.get(replacement), res.remove(tAttr));
				}
			} else if (key instanceof ReferenceFieldAttribute) {
				ReferenceFieldAttribute rfAttr = (ReferenceFieldAttribute) key;
				String replacement = rfAttr.getEntity();
				if (replacements.containsKey(replacement)) {
					res.put(replacements.get(replacement), res.remove(rfAttr));
				}
			}
		}
	}

	private Map<String, Object> prepareAttributes(Vector attributes) {
		Map<String, Object> replacements = new HashMap<>();
		for (int i = 0; i < attributes.size(); i++) {
			Object x = attributes.remove(i);
			Object target = x;
			if ((x instanceof TableAttribute) && ((TableAttribute) x).getEntity().startsWith(TemporaryOJeePatchHessianEntityInvocationHandler.OJEE_PREFIX)) {
				TableAttribute originalAttr = (TableAttribute) x;
				String originalEntity = originalAttr.getEntity();
				String targetEntity = originalEntity.substring(TemporaryOJeePatchHessianEntityInvocationHandler.OJEE_PREFIX.length());

				TableAttribute newTableAttribute = this.cloneTableAttribute(originalAttr);
				newTableAttribute.setEntityAndAttributes(targetEntity, originalAttr.getAttributes());
				replacements.put(targetEntity, originalAttr);
				target = newTableAttribute;
			} else if ((x instanceof ReferenceFieldAttribute) && ((ReferenceFieldAttribute) x).getEntity()
					.startsWith(TemporaryOJeePatchHessianEntityInvocationHandler.OJEE_PREFIX)) {
				ReferenceFieldAttribute originalAttr = (ReferenceFieldAttribute) x;
				String originalEntity = originalAttr.getEntity();
				String targetEntity = originalEntity.substring(TemporaryOJeePatchHessianEntityInvocationHandler.OJEE_PREFIX.length());

				ReferenceFieldAttribute targetAttr = new ReferenceFieldAttribute(originalAttr.getAttr(), targetEntity, originalAttr.getCod(), originalAttr.getCols());
				replacements.put(targetEntity, originalAttr);
				target = targetAttr;
			}
			attributes.add(i, target);
		}
		return replacements;
	}

	private TableAttribute cloneTableAttribute(TableAttribute attr) {
		TableAttribute newTableAttribute = new TableAttribute();
		newTableAttribute.setEntityAndAttributes(attr.getEntity(), attr.getAttributes());
		newTableAttribute.setKeysParentkeysOtherkeys(attr.getKeys(), attr.getParentKeys());
		newTableAttribute.setOrderBy(attr.getOrderBy());
		newTableAttribute.setParentkeyEquivalences(attr.getParentkeyEquivalences());
		newTableAttribute.setQueryFilter(attr.getQueryFilter());
		newTableAttribute.setTotalRecordNumberInQuery(attr.getQueryRecordNumber());
		newTableAttribute.setRecordNumberToInitiallyDownload(attr.getRecordNumberToInitiallyDownload());
		return newTableAttribute;
	}

	private Object getHessianService() {
		return BeansFactory.getBean(this.serviceName);
	}

	private Object invoke(Method method, Object paramObject, Object... paramArrayOfObject) throws Exception {
		try {
			return method.invoke(paramObject, paramArrayOfObject);
		} catch (IllegalArgumentException ex) {
			TemporaryOJeePatchHessianEntityInvocationHandler.logger.error("The invoked method's declaration does not match given parameters", ex);
			throw ex;
		} catch (InvocationTargetException ex) {
			TemporaryOJeePatchHessianEntityInvocationHandler.logger.trace(null, ex);
			if (ex.getCause() instanceof Exception) {
				throw (Exception) ex.getCause();
			}
			throw new Exception(null, ex.getCause());
		}
	}

}
