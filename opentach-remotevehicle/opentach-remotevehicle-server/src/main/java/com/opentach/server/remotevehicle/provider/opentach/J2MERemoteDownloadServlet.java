package com.opentach.server.remotevehicle.provider.opentach;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ServerLauncherServlet;
import com.opentach.server.IOpentachServerLocator;

/**
 * Servlet de carga de ficheros en Novadis y envío de mails.<br> Extiende del servlet estandar de envío de mails.<br> Añade estos parámetros en la cabecera post: <ul>
 * <li><b>phoneNumber</b>: Url para coger la referencia al objecto remoto IRemoteWebFileUpload.</li> </ul
 */
public class J2MERemoteDownloadServlet extends HttpServlet {

	private static final Logger		logger									= LoggerFactory.getLogger(J2MERemoteDownloadServlet.class);

	private static final String		OPERATION_KEY							= "app.operation";
	private static final String		OPERATION_SEND_APDU						= "app.sendapdu";
	private static final String		OPERATION_CHECK_REMOTE_CARD_AVAILABLE	= "app.checkremotecardavailable";
	/** URL del objeto remoto */

	private IOpentachServerLocator	locator;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.locator = (IOpentachServerLocator) this.getServletContext().getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
	}

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		arg1.getOutputStream().println("Esta rulando, pero solo se acepta POST");
		super.doGet(arg0, arg1);
	}

	/**
	 * Method to Service the input requests. The input Requests are redirected to the Server. <br> The output from the Server is sent back as the response.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse p_res) throws ServletException, IOException {
		// Extracción de parámetros
		String user = this.getHeader(request, "app.user");
		String password = this.getHeader(request, "app.password");
		String companyId = this.getHeader(request, "app.company");
		Map<String, String> parameters = this.extractParameters(request);
		String pinNumber = this.getHeader(request, "pin");

		try {
			String res = "Error";
			String operation = parameters.get(J2MERemoteDownloadServlet.OPERATION_KEY);
			if (operation != null) {
				operation = operation.toLowerCase();
			}

			if (J2MERemoteDownloadServlet.OPERATION_CHECK_REMOTE_CARD_AVAILABLE.equals(operation)) {
				res = this.locator.getService(OpentachRemoteDownloadService.class).checkRemoteCardAvailable(user, password, companyId);

			} else if (J2MERemoteDownloadServlet.OPERATION_SEND_APDU.equals(operation)) {
				res = this.locator.getService(OpentachRemoteDownloadService.class).sendApdu(user, password, companyId, parameters.get("apdu"));
			} else {
				throw new Exception("E_INVALID_OPERATION");
			}
			p_res.addHeader("RESULT", res);

		} catch (Throwable t) {
			if (t.getMessage().equals("M_NO_SE_ENCUENTRA_CONTRATO")) {
				J2MERemoteDownloadServlet.logger.error(t.getMessage());
			} else {
				J2MERemoteDownloadServlet.logger.error(null, t);
			}
			p_res.addHeader("RESULT", this.getExceptionMessage(t));
			p_res.sendError(500, t.getMessage());
		}
	}

	private String getExceptionMessage(Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage();
	}

	private Map<String, String> extractParameters(HttpServletRequest pReq) {
		Enumeration headerNames = pReq.getHeaderNames();
		Map<String, String> parameters = new HashMap<String, String>();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = pReq.getHeader(key);
			if (value != null) {
				parameters.put(key, value);
			}
		}
		return parameters;
	}

	private String getHeader(HttpServletRequest request, String name) {
		String value = request.getHeader(name);
		if (value == null) {
			value = request.getHeader(name.toUpperCase());
		}
		return value;
	}

}