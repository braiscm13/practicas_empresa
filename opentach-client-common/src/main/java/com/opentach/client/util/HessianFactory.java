package com.opentach.client.util;

import java.util.HashMap;
import java.util.Map;

import com.ontimize.jee.common.hessian.CustomSerializerFactory;
import com.ontimize.jee.common.hessian.OntimizeHessianHttpClientConnectionFactory;
import com.ontimize.jee.common.hessian.OntimizeHessianProxyFactory;
import com.ontimize.jee.common.hessian.OntimizeHessianProxyFactoryBean;

public class HessianFactory {
	private static HessianFactory	factory;

	public static HessianFactory getFactory() {
		if (HessianFactory.factory == null) {
			HessianFactory.factory = new HessianFactory();
		}
		return HessianFactory.factory;

	}

	/** The pf. */
	private final OntimizeHessianProxyFactory					pf;
	/** The hcf. */
	private final OntimizeHessianHttpClientConnectionFactory	hcf;
	/** The serializer. */
	private final CustomSerializerFactory						serializer;

	private final Map<String, OntimizeHessianProxyFactoryBean>	serviceCache;

	private HessianFactory() {
		this.serviceCache = new HashMap<>();
		this.serializer = new CustomSerializerFactory();
		this.pf = new OntimizeHessianProxyFactory();
		this.pf.setSerializerFactory(this.serializer);
		// TestLoginProvider loginProvider = new TestLoginProvider();
		// this.pf.setLoginProvider(loginProvider);
		this.hcf = (OntimizeHessianHttpClientConnectionFactory) this.pf.getConnectionFactory();
		// loginProvider.doLogin(new URI(this.getServiceBaseUrl()), this.getUser(), this.getPass());
		// this.doTest();
		// AbstractOntimizeTest.logger.error("Test finalizado en {}", System.currentTimeMillis() - startTime);
	}

	/**
	 * Creates the service.
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceInterface
	 *            the service interface
	 * @param relativeUrl
	 *            the relative url
	 * @return the t
	 */
	public <T> T getService(Class<T> serviceInterface, String relativeUrl) {
		OntimizeHessianProxyFactoryBean service = this.serviceCache.get(relativeUrl);
		if (service == null) {
			service = new OntimizeHessianProxyFactoryBean();
			service.setProxyFactory(this.pf);
			service.setServiceRelativeUrl(relativeUrl);
			service.setServiceInterface(serviceInterface);
			service.afterPropertiesSet();
			this.serviceCache.put(relativeUrl, service);
		}
		return (T) service.getObject();
	}

}
