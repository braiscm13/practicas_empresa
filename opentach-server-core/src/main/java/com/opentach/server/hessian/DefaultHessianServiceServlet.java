package com.opentach.server.hessian;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.ontimize.gui.ServerLauncherServlet;
import com.utilmize.server.UReferenceSeeker;
import com.utilmize.server.services.UAbstractService;

public class DefaultHessianServiceServlet extends com.caucho.hessian.server.HessianServlet {

	/**
	 * Instantiates a new hessian servlet.
	 */
	public DefaultHessianServiceServlet() {
		super();
	}

	/**
	 * Inits the.
	 *
	 * @param config
	 *            the config
	 * @throws ServletException
	 *             the servlet exception
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.setSerializerFactory(new com.ontimize.jee.common.hessian.CustomSerializerFactory());
	}

	/**
	 * Gets the reference locator.
	 *
	 * @return the reference locator
	 */
	protected UReferenceSeeker getReferenceLocator() {
		return (UReferenceSeeker) this.getServletContext().getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
	}

	protected <T extends UAbstractService> T getService(Class<T> clazz) {
		return this.getReferenceLocator().getService(clazz);
	}

}
