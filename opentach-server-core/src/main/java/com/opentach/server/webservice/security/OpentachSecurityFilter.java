package com.opentach.server.webservice.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;

import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.exceptions.InvalidCredentialsException;
import com.opentach.server.AbstractOpentachServerLocator;

public class OpentachSecurityFilter implements Filter {

	private static final Logger	logger				= LoggerFactory.getLogger(OpentachSecurityFilter.class);
	private final static String	CREDENTIALS_CHARSET	= "UTF-8";
	public final static String	CONTEXT_SESSION_ID	= "context-ontimize-session-id";

	private ServletContext		context;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.context = filterConfig.getServletContext();
	}

	@Override
	public void destroy() {
		this.context = null;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		if (this.isExclussion((HttpServletRequest) servletRequest)) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String header = request.getHeader("Authorization");

		if ((header == null) || !header.startsWith("Basic ")) {
			OpentachSecurityFilter.logger.info("No Authorization header");
			response.addHeader("WWW-Authenticate", "Basic realm=\"Opentach\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String[] tokens = this.extractAndDecodeHeader(header, request);
		assert tokens.length == 2;

		String username = tokens[0];

		OpentachSecurityFilter.logger.debug("Basic Authentication Authorization header found for user '{}'", username);
		final AbstractOpentachServerLocator locator = (AbstractOpentachServerLocator) this.context.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);

		int startSession = -1;
		try {
			startSession = locator.startSession(username, tokens[1], null);
			SecurityContextHolder.setContext(new SecurityContextImpl(startSession));
			if (startSession >= 0) {
				chain.doFilter(request, response);
			}
		} catch (InvalidCredentialsException err) {
			OpentachSecurityFilter.logger.info(null, err);
			response.addHeader("WWW-Authenticate", "Basic realm=\"Opentach\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception err) {
			if ("E_UNAUTHORIZED_USER".equals(err.getMessage()) || "unauthorized_user".equals(err.getMessage())) {
				OpentachSecurityFilter.logger.info(null, err);
				response.addHeader("WWW-Authenticate", "Basic realm=\"Opentach\"");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			OpentachSecurityFilter.logger.error(null, err);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, err.getMessage());
		} finally {
			SecurityContextHolder.clearContext();
			if (startSession >= 0) {
				try {
					locator.endSession(startSession);
				} catch (Exception ex) {
					OpentachSecurityFilter.logger.error(null, ex);
				}
			}
		}
	}

	private final String[] WHITE_LIST = new String[] { //
			"/driverAnalysis", //
			"/restStationsService", //
			"/restLoginService", //
			"/restCompanyService", //
			"/restDriverService", //
			"/restVehicleService", //
			"/restInfractionService", //
			"/restExpressReportService", //
			"/restFilesService", //
			"/restSurveyService", //
			"/restActivityChartService", //
	"/restGraphicsSurveyService" };

	private boolean isExclussion(HttpServletRequest servletRequest) {
		// servicios que estaban antes de meter este filtro de seguridad
		String pathInfo = servletRequest.getPathInfo();
		for (String st : this.WHITE_LIST) {
			if (pathInfo.startsWith(st)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Decodes the header into a username and password.
	 *
	 * @throws BadCredentialsException
	 *             if the Basic header is not present or is not valid Base64
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) {

		try {
			byte[] base64Token = header.substring(6).getBytes("UTF-8");
			byte[] decoded;
			decoded = Base64.decodeBase64(base64Token);

			String token = new String(decoded, this.getCredentialsCharset(request));

			int delim = token.indexOf(":");

			if (delim == -1) {
				throw new InvalidCredentialsException("Invalid basic authentication token");
			}
			return new String[] { token.substring(0, delim), token.substring(delim + 1) };
		} catch (Exception error) {
			throw new InvalidCredentialsException("Failed to decode basic authentication token", error);
		}
	}

	protected String getCredentialsCharset(HttpServletRequest request) {
		return OpentachSecurityFilter.CREDENTIALS_CHARSET;
	}

}
