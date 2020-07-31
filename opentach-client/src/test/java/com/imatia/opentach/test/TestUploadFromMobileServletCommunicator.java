package com.imatia.opentach.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.extractor.tacho.TachoException;

public class TestUploadFromMobileServletCommunicator {

	private static final Logger logger = LoggerFactory.getLogger(TestUploadFromMobileServletCommunicator.class);

	public TestUploadFromMobileServletCommunicator() {
		super();
	}

	/**
	 *
	 * @param con
	 *            la conexion
	 * @param parameters
	 *            los parametros para la cabecera
	 * @param attachments
	 *            los adjuntos
	 * @return un vector con el orden en que se deben escribir los adjuntos
	 * @throws IOException
	 */
	protected List<byte[]> establishHeaders(HttpURLConnection con, Map<String, String> parameters, Map<String, byte[]> attachments) throws IOException {
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if (parameters != null) {
			for (Entry<String, String> entry : parameters.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				con.setRequestProperty(key, value);
			}
		}
		List<byte[]> attachmentOrder = null;
		if ((attachments != null) && !attachments.isEmpty()) {
			attachmentOrder = new ArrayList<byte[]>();
			int fullSize = 0;
			String attachNames = "";
			String attachSizes = "";
			for (Entry<String, byte[]> entry : attachments.entrySet()) {
				String key = entry.getKey();
				byte[] value = entry.getValue();
				if (fullSize != 0) {
					attachNames += ";";
					attachSizes += ";";
				}
				attachNames += key;
				attachSizes += "" + value.length;
				fullSize += value.length;
				attachmentOrder.add(value);
			}
			con.setRequestProperty("Content-Length", "" + fullSize);
			con.setRequestProperty("attchnames", attachNames);
			con.setRequestProperty("attchsizes", attachSizes);
		}
		return attachmentOrder;
	}

	protected void writeFile(OutputStream os, InputStream is) throws IOException {
		byte[] buffer = new byte[256];
		int readed = 0;
		while ((readed = is.read(buffer, 0, 256)) >= 0) {
			os.write(buffer, 0, readed);
		}
	}

	/**
	 *
	 * @param timeout
	 *            timeout de la peticion
	 * @param parameters
	 *            parametros para la cabecera
	 * @param attachments
	 *            hashtable de adjuntos, espera un <String,byte[]> con <nombre fichero,contenido>
	 * @return
	 * @throws IOException
	 * @throws TachoException
	 * @throws Exception
	 */
	public String request(String sUrl, int timeout, Map<String, String> parameters, Map<String, byte[]> attachments) throws IOException, TachoException {
		URL url = new URL(sUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		// establecemos metodo POST
		con.setDoOutput(true);

		List<byte[]> attachmentOrder = this.establishHeaders(con, parameters, attachments);

		OutputStream os = null;
		try {
			if (attachmentOrder != null) {
				os = con.getOutputStream();
				for (byte[] fullData : attachmentOrder) {
					os.write(fullData);
				}
				os.flush();
			}
			int responseCode = con.getResponseCode();
			String responseMessage = con.getResponseMessage();
			String resultString = con.getHeaderField("RESULT");

			if ((responseCode != 200) || (resultString == null)) {
				if ((resultString != null) && resultString.startsWith("ERROR: ")) {
					resultString = resultString.substring("ERROR: ".length());
				}
				TestUploadFromMobileServletCommunicator.logger.error("ERROR: " + (resultString == null ? responseMessage : resultString));
				TestUploadFromMobileServletCommunicator.logger.info("ResponseMessage: " + responseCode + ":" + responseMessage);
				throw new TachoException(resultString == null ? responseMessage : resultString);
			}
			return resultString;
		} finally {
			con.disconnect();
		}
	}

	/**
	 *
	 * @param timeout
	 *            timeout de la peticion
	 * @param parameters
	 *            parametros para la cabecera
	 * @param attachments
	 *            hashtable de adjuntos, espera un <String,byte[]> con <nombre fichero,contenido>
	 * @return
	 * @throws IOException
	 * @throws TachoException
	 * @throws Exception
	 */
	public String request(String sUrl, int timeout, Map<String, String> parameters, File attachment) throws IOException, TachoException {
		if ((attachment == null) || (attachment.length() == 0)) {
			throw new IOException("NO_ATTACHMENT");
		}
		long attachLength = attachment.length();
		String attachName = attachment.getName();
		InputStream attachIs = new FileInputStream(attachment);

		return this.request(sUrl, timeout, parameters, attachLength, attachName, attachIs);

	}

	public String request(String sUrl, int timeout, Map<String, String> parameters, long attachLength, String attachName, InputStream attachIs) throws IOException, TachoException {
		if ((attachName == null) || (attachIs == null) || (attachLength == 0)) {
			throw new IOException("NO_ATTACHMENT");
		}
		URL url = new URL(sUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		try {
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			// establecemos metodo POST
			con.setDoOutput(true);

			this.establishHeaders(con, parameters, null);

			con.setRequestProperty("Content-Length", "" + attachLength);
			con.setRequestProperty("attchname", attachName);

			con.setFixedLengthStreamingMode(attachLength);
			try {
				OutputStream os = con.getOutputStream();
				byte[] buf = new byte[8192];
				int c = 0;
				while ((c = attachIs.read(buf, 0, buf.length)) > 0) {
					os.write(buf, 0, c);
					os.flush();
				}
				os.flush();
			} finally {
				if (attachIs != null) {
					attachIs.close();
				}
			}
			int responseCode = con.getResponseCode();
			String responseMessage = con.getResponseMessage();
			String resultString = con.getHeaderField("RESULT");

			if ((responseCode != 200) || (resultString == null)) {
				if ((resultString != null) && resultString.startsWith("ERROR: ")) {
					resultString = resultString.substring("ERROR: ".length());
				}
				TestUploadFromMobileServletCommunicator.logger.error("ERROR: " + (resultString == null ? responseMessage : resultString));
				TestUploadFromMobileServletCommunicator.logger.info("ResponseMessage: " + responseCode + ":" + responseMessage);
				throw new TachoException(resultString == null ? responseMessage : resultString);
			}
			return resultString;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}
}