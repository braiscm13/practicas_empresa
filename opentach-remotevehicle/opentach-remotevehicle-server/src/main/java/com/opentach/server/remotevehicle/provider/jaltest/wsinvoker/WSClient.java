package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

/**
 * The Class WSClient.
 *
 * @param <T>
 *            the generic type
 */
public class WSClient<T> {

	/** The service. */
	private final Service	service;

	/** The port. */
	private final T			port;

	/**
	 * Instantiates a new WS client.
	 *
	 * @param service
	 *            the service
	 * @param port
	 *            the port
	 */
	public WSClient(Service service, T port) {
		super();
		this.service = service;
		this.port = port;
	}

	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	public Service getService() {
		return this.service;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public T getPort() {
		return this.port;
	}

	/**
	 * Gets the binding provider.
	 *
	 * @return the binding provider
	 */
	public BindingProvider getBindingProvider() {
		return (BindingProvider) this.port;
	}


}
