package com.ontimize.util.rmitunneling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JNLPNatServletHandler extends HttpServlet {

	private static final String JNLP_MIME_TYPE = "application/x-java-jnlp-file";

	private static final String URI_KEY = "$$uri";

	private static final String CODEBASE_KEY = "$$codebase";

	private static final String HOSTNAME_KEY = "$$hostname";

	private static final String TUNNEL_PATH_KEY = "$$tunnel_path";

	private static final String TUNNEL_PORT_KEY = "$$tunnel_port";

	private static final String DEBUG_KEY = "$$debug";

	private String codebase = null;

	private String hostname = null;

	private String tunnelPath = null;

	private String tunnelPort = null;

	private String debug = null;

	private String uri = null;

	public JNLPNatServletHandler() {}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private String parserHost(String h) {
		String host = h;
		try {
			if (host.indexOf("[") != -1) {
				host = host.substring(host.indexOf("[") + 1);
			}
			if (host.indexOf("]") != -1) {
				host = host.substring(0, host.indexOf("]"));
			}
			int p = host.indexOf(":");
			if (p != -1) {
				host = host.substring(0, p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return h;
		}
		return host;
	}

	private String parserTunnelingPort(String h, HttpServletRequest request) {
		String host = h;
		try {
			if (host.indexOf("[") != -1) {
				host = host.substring(host.indexOf("[") + 1);
			}
			if (host.indexOf("]") != -1) {
				host = host.substring(0, host.indexOf("]"));
			}
			int p = host.indexOf(":");
			if (p != -1) {
				host = host.substring(p + 1);
			} else if (request.isSecure()) {
				host = "443";
			} else {
				host = "80";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return h;
		}
		return host;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {

		if ((request.getHeader("host") != null) && (!request.getHeader("host").equals(""))) {
			this.hostname = request.getHeader("host");
		} else {
			this.hostname = request.getServerName();
		}

		StringBuffer codebaseBuffer = new StringBuffer();
		codebaseBuffer.append(!request.isSecure() ? "http://" : "https://");
		codebaseBuffer.append(this.hostname);
		String s = request.getRequestURI();
		int k = s.lastIndexOf("/");
		if (k != -1) {
			s = s.substring(0, k);
		}
		codebaseBuffer.append(s);

		String h = this.hostname;
		this.hostname = this.parserHost(this.hostname);
		this.tunnelPort = this.parserTunnelingPort(h, request);
		this.codebase = codebaseBuffer.toString();

		if ((request.getParameter("hostnameValue") != null) && (!request.getParameter("hostnameValue").equals(""))) {
			this.hostname = request.getParameter("hostnameValue");
		}
		if ((request.getParameter("codebaseValue") != null) && (!request.getParameter("codebaseValue").equals(""))) {
			this.codebase = request.getParameter("codebaseValue");
		}
		if ((request.getParameter("tunnelPathValue") != null) && (!request.getParameter("tunnelPathValue").equals(""))) {
			this.tunnelPath = request.getParameter("tunnelPathValue");
		}
		if ((request.getParameter("tunnelPortValue") != null) && (!request.getParameter("tunnelPortValue").equals(""))) {
			this.tunnelPort = request.getParameter("tunnelPortValue");
		}

		if ((request.getParameter("debugValue") != null) && (!request.getParameter("debugValue").equals(""))) {
			this.debug = request.getParameter("debugValue");
		}

		String contextPath = request.getContextPath();
		String requestURI = request.getRequestURI();

		if (requestURI != null) {
			int last = requestURI.lastIndexOf("/");
			if (last >= 0) {
				this.uri = requestURI.substring(0, last);
			} else {
				this.uri = requestURI;
			}
		}

		if ((request.getParameter("uri") != null) && (!request.getParameter("uri").equals(""))) {
			this.uri = request.getParameter("uri");
		}

		String relativePath = null;
		if (contextPath != null) {
			relativePath = requestURI.substring(contextPath.length());
		}
		if (relativePath == null) {
			relativePath = request.getServletPath();
		}
		if (relativePath == null) {
			relativePath = "/";
		}

		relativePath = relativePath.trim();

		this.log("JNLPServletHandler -> Resource request: " + relativePath);
		InputStream is = this.getServletContext().getResourceAsStream(relativePath);
		if (is == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found " + relativePath);
			return;
		}


		// Put the modification date
		// response.setDateHeader("Last-Modified", f.lastModified());

		// Write the file
		String mime = this.getServletContext().getMimeType(relativePath);
		response.setContentType(mime);

		if (JNLPNatServletHandler.JNLP_MIME_TYPE.equalsIgnoreCase(mime)) {
			// Replace occurrences of $$codebase
			OutputStream out = response.getOutputStream();
			OutputStreamWriter writer = null;
			Reader reader = null;
			BufferedReader bR = null;
			try {
				writer = new OutputStreamWriter(out);
				BufferedWriter bW = new BufferedWriter(writer);
				reader = new InputStreamReader(is);
				bR = new BufferedReader(reader);
				String sLine = null;
				int l = 0;
				while ((sLine = bR.readLine()) != null) {
					l++;
					if (sLine.indexOf(JNLPNatServletHandler.CODEBASE_KEY) >= 0) {
						this.log("JNLPServletHandler -> " + JNLPNatServletHandler.CODEBASE_KEY + " found in the line: " + l + " -> " + sLine);
						sLine = sLine.substring(0, sLine.indexOf(JNLPNatServletHandler.CODEBASE_KEY)) + this.codebase
								+ sLine.substring(sLine.indexOf(JNLPNatServletHandler.CODEBASE_KEY) + JNLPNatServletHandler.CODEBASE_KEY.length());
					}

					if (sLine.indexOf(JNLPNatServletHandler.HOSTNAME_KEY) >= 0) {
						this.log("JNLPServletHandler -> " + JNLPNatServletHandler.HOSTNAME_KEY + " found in the line: " + l + " -> " + sLine);
						sLine = sLine.substring(0, sLine.indexOf(JNLPNatServletHandler.HOSTNAME_KEY)) + this.hostname
								+ sLine.substring(sLine.indexOf(JNLPNatServletHandler.HOSTNAME_KEY) + JNLPNatServletHandler.HOSTNAME_KEY.length());
					}

					if (sLine.indexOf(JNLPNatServletHandler.TUNNEL_PATH_KEY) >= 0) {
						this.log("JNLPServletHandler -> " + JNLPNatServletHandler.TUNNEL_PATH_KEY + " found in the line: " + l + " -> " + sLine);
						sLine = sLine.substring(0, sLine.indexOf(JNLPNatServletHandler.TUNNEL_PATH_KEY)) + this.tunnelPath
								+ sLine.substring(sLine.indexOf(JNLPNatServletHandler.TUNNEL_PATH_KEY) + JNLPNatServletHandler.TUNNEL_PATH_KEY.length());
					}

					if (sLine.indexOf(JNLPNatServletHandler.TUNNEL_PORT_KEY) >= 0) {
						this.log("JNLPServletHandler -> " + JNLPNatServletHandler.TUNNEL_PORT_KEY + " found in the line: " + l + " -> " + sLine);
						sLine = sLine.substring(0, sLine.indexOf(JNLPNatServletHandler.TUNNEL_PORT_KEY)) + this.tunnelPort
								+ sLine.substring(sLine.indexOf(JNLPNatServletHandler.TUNNEL_PORT_KEY) + JNLPNatServletHandler.TUNNEL_PORT_KEY.length());
					}

					if (sLine.indexOf(JNLPNatServletHandler.DEBUG_KEY) >= 0) {
						this.log("JNLPServletHandler -> " + JNLPNatServletHandler.DEBUG_KEY + " found in the line: " + l + " -> " + sLine);
						sLine = sLine.substring(0, sLine.indexOf(JNLPNatServletHandler.DEBUG_KEY)) + this.debug
								+ sLine.substring(sLine.indexOf(JNLPNatServletHandler.DEBUG_KEY) + JNLPNatServletHandler.DEBUG_KEY.length());
					}

					if (sLine.indexOf(JNLPNatServletHandler.URI_KEY) >= 0) {
						this.log("JNLPServletHandler -> " + JNLPNatServletHandler.URI_KEY + " found in the line: " + l + " -> " + sLine);
						sLine = sLine.substring(0, sLine.indexOf(JNLPNatServletHandler.URI_KEY)) + this.uri
								+ sLine.substring(sLine.indexOf(JNLPNatServletHandler.URI_KEY) + JNLPNatServletHandler.URI_KEY.length());
					}

					bW.write(sLine);
					bW.newLine();
				}
				bW.flush();
			} catch (IOException e) {
				throw e;
			} finally {
				if (bR != null) {
					bR.close();
				}
			}
		} else {
			response.setContentLength(is.available());
			OutputStream out = response.getOutputStream();
			BufferedInputStream bIn = null;
			try {
				BufferedOutputStream bOut = new BufferedOutputStream(out);
				bIn = new BufferedInputStream(is);
				int i = -1;
				while ((i = bIn.read()) != -1) {
					bOut.write(i);
				}
				bOut.flush();
			} catch (IOException e) {
				throw e;
			} finally {
				if (bIn != null) {
					bIn.close();
				}
			}
		}
	}

}