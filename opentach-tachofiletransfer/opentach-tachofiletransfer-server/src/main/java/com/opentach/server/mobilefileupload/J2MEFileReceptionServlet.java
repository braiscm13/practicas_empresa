package com.opentach.server.mobilefileupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.tools.FileTools;
import com.opentach.common.user.Company;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.QueueNames;
import com.opentach.messagequeue.api.messages.CloudFileReceivedQueueMessage;
import com.opentach.messagequeue.api.messages.LocationChangeQueueMessage;
import com.opentach.messagequeue.api.messages.LocationIdentifyQueueMessage;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.companies.ContractService;

/**
 * Servlet de carga de ficheros en Novadis y envío de mails.<br> Extiende del servlet estandar de envío de mails.<br> Añade estos parámetros en la cabecera post: <ul>
 * <li><b>phoneNumber</b>: Url para coger la referencia al objecto remoto IRemoteWebFileUpload.</li> </ul
 */
public class J2MEFileReceptionServlet extends HttpServlet {

	private static final Logger				logger							= LoggerFactory.getLogger(J2MEFileReceptionServlet.class);

	private static final String				OPERATION_KEY					= "app.operation";
	private static final String				OPERATION_SAVE_MAIL_EXTRACTION	= "app.savemailextraction";
	private static final String				OPERATION_CHECK_CREDENTIALS		= "app.checkcredentials";
	private static final String				OPERATION_CHECK_VIP_CODE		= "app.checkvipcode";
	private static final String				OPERATION_GET_USER_COMPANIES	= "app.getusercompanies";
	private static final String				OPERATION_IDENTIFY_SMARTPHONE	= "app.identifysmartphone";
	private static final String				OPERATION_LOCALIZE_SMARTPHONE	= "app.localizesmartphone";
	private static final String				OPERATION_CLOUD_FILE			= "app.cloudfile";
	/** URL del objeto remoto */

	private RemoteWebFileUploadDelegate		delegate;
	private AbstractOpentachServerLocator	locator;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.locator = (AbstractOpentachServerLocator) this.getServletContext().getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
		this.delegate = new RemoteWebFileUploadDelegate(this.locator);
	}

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		arg1.getOutputStream().println("Está rulando, pero sólo se acepta POST");
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
		Map<String, String> parameters = this.extractParameters(request);
		String pinNumber = this.getHeader(request, "pin");

		try {
			String res = "Error";
			String operation = parameters.get(J2MEFileReceptionServlet.OPERATION_KEY);
			if (operation != null) {
				operation = operation.toLowerCase();
			}
			if (J2MEFileReceptionServlet.OPERATION_CHECK_CREDENTIALS.equals(operation)) {
				res = this.delegate.checkCredentials(user, password, parameters);
			} else if (J2MEFileReceptionServlet.OPERATION_SAVE_MAIL_EXTRACTION.equals(operation)) {
				res = this.delegate.saveMailExtraction(parameters);

			} else if (J2MEFileReceptionServlet.OPERATION_CHECK_VIP_CODE.equals(operation)) {
				String deviceId = parameters.get("pin");
				String code = parameters.get("code");
				res = this.delegate.checkVipCode(code, deviceId);
			} else if (J2MEFileReceptionServlet.OPERATION_GET_USER_COMPANIES.equals(operation)) {
				res = this.getUserCompanies(user, password);
			} else if (J2MEFileReceptionServlet.OPERATION_IDENTIFY_SMARTPHONE.equals(operation)) {
				String smartphoneId = parameters.get("smartphoneid");
				String tokenId = parameters.get("tokenid");
				J2MEFileReceptionServlet.logger.trace("========>identifySmartphone: smartphoneId=" + smartphoneId + "   tokenId=" + tokenId);
				try {
					this.locator.getBean(IMessageQueueManager.class).pushEvent(QueueNames.LOCATION_IDENTIFY_SMARTPHONE, new LocationIdentifyQueueMessage() {
						@Override
						public String getSmartphoneId() {
							return smartphoneId;
						}

						@Override
						public String getTokenId() {
							return tokenId;
						}
					});

				} catch (Exception ex) {
					J2MEFileReceptionServlet.logger.warn("Device not registered for push {}", smartphoneId, ex);
					throw new SilentException();
				}
				res = "OK";
			} else if (J2MEFileReceptionServlet.OPERATION_LOCALIZE_SMARTPHONE.equals(operation)) {
				String smartphoneId = parameters.get("smartphoneid");
				double latitude = Double.parseDouble(parameters.get("latitude"));
				double longitude = Double.parseDouble(parameters.get("longitude"));
				double accuracy = this.parseDouble(parameters.get("accuracy"));
				Date date = new Date();
				J2MEFileReceptionServlet.logger
				.trace("========>localizeSmartphone: smartphoneId=" + smartphoneId + "   latitude=" + latitude + "   longitude=" + longitude + "   accuracy=" + accuracy);
				this.locator.getBean(IMessageQueueManager.class).pushEvent(QueueNames.LOCATION_CHANGE_SMARTPHONE, new LocationChangeQueueMessage() {
					@Override
					public String getSmartphoneId() {
						return smartphoneId;
					}

					@Override
					public double getLatitude() {
						return latitude;
					}

					@Override
					public double getLongitude() {
						return longitude;
					}

					@Override
					public double getAccuracy() {
						return accuracy;
					}

					@Override
					public Date getDate() {
						return date;
					}

				});
				res = "OK";
			} else if (J2MEFileReceptionServlet.OPERATION_CLOUD_FILE.equals(operation)) {
				String devicePin = parameters.get("pin");
				String fileName = parameters.get("attchname");
				String notes = parameters.get("note");
				InputStream is = request.getInputStream();
				this.locator.getBean(IMessageQueueManager.class).pushEvent(QueueNames.CLOUDFILE_FILE_RECEIVED, new CloudFileReceivedQueueMessage() {
					@Override
					public String getDevicePin() {
						return devicePin;
					}

					@Override
					public String getFileName() {
						return fileName;
					}

					@Override
					public String getNotes() {
						return notes;
					}

					@Override
					public InputStream getInputStream() {
						return is;
					}

				});
			} else {
				File attchs[] = this.extractAttachmentFiles(request);
				boolean analize = "true".equalsIgnoreCase(this.getHeader(request, "analize"));
				Date downloadDate = null;
				try {
					downloadDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(this.getHeader(request, "download_date"));
				} catch (Exception ex) {
					// do nothing
				}
				res = this.loadFilesInApp(new String[] { pinNumber }, attchs, user, password, analize, parameters, downloadDate);
				if (!res.toLowerCase().startsWith("ok")) {
					throw new Exception(res);
				}
			}
			p_res.addHeader("RESULT", res);

		} catch (Throwable t) {
			if (!(t instanceof SilentException)) {
				if (t.getMessage().equals("M_NO_SE_ENCUENTRA_CONTRATO")) {
					J2MEFileReceptionServlet.logger.error(t.getMessage());
				} else {
					J2MEFileReceptionServlet.logger.error(null, t);
				}
			}
			p_res.addHeader("RESULT", this.getExceptionMessage(t));
			p_res.sendError(500, t.getMessage());
		}
	}

	private String getUserCompanies(String user, String password) throws Exception {
		List<Company> companies = this.locator.getService(ContractService.class).getUserCompanies(user, password);
		ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return mapper.writeValueAsString(companies);
	}

	private String getHeader(HttpServletRequest request, String name) {
		String value = request.getHeader(name);
		if (value == null) {
			value = request.getHeader(name.toUpperCase());
		}
		return value;
	}

	private String getExceptionMessage(Throwable err) {
		while (err.getCause() != null) {
			err = err.getCause();
		}
		return err.getMessage();
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

	private File[] extractAttachmentFiles(HttpServletRequest p_req) throws IOException {
		int[] attachSizes = this.getAttchSizes(p_req.getHeader("attchsizes"));
		File attchs[] = new File[attachSizes.length];
		InputStream isPda = p_req.getInputStream();
		for (int i = 0; i < attachSizes.length; i++) {
			attchs[i] = this.createFile(isPda, attachSizes[i]);
		}

		return attchs;
	}

	private int[] getAttchSizes(String header) {
		String[] sSizes = this.extractTokens(header, ";");
		int[] sizes = new int[sSizes.length];
		for (int i = 0; i < sSizes.length; i++) {
			sizes[i] = Integer.parseInt(sSizes[i]);
		}
		return sizes;
	}

	private String loadFilesInApp(String[] mailto, File[] files, String user, String password, boolean analize, Map<?, ?> parameters, Date downloadDate) throws Exception {
		try {
			byte[] bytes = this.readBytes(files[0]);
			// Look up remote object
			if (mailto.length <= 0) {
				throw new IOException("NO_PIN");
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mailto.length; i++) {
				sb.append(mailto[i]);
				if (i < (mailto.length - 1)) {
					sb.append(";");
				}
			}
			return this.delegate.uploadFileByPhone(bytes, sb.toString(), user, password, analize, parameters, downloadDate);
		} finally {
			// Los ficheros temporales han de ser borrados
			this.deleteTemporaryFiles(files);
		}
	}

	private File createFile(InputStream is, int size) throws IOException {
		File file = File.createTempFile("toSend", "data");
		try (FileOutputStream fos = new FileOutputStream(file)) {
			int readed;
			for (int counter = 0; counter != size; counter += readed) {
				byte buffer[] = new byte[size - counter];
				readed = is.read(buffer, 0, size - counter);
				fos.write(buffer, 0, readed);
			}
		}
		return file;
	}

	private void deleteTemporaryFiles(File attchs[]) {
		for (int i = 0; i < attchs.length; i++) {
			attchs[i].delete();
		}
	}

	private String[] extractTokens(String s, String separator) {
		if (s == null) {
			return new String[0];
		}
		if (separator == null) {
			return (new String[] { s });
		}
		List<String> v = new Vector<>();
		for (StringTokenizer st = new StringTokenizer(s, separator); st.hasMoreTokens(); v.add(st.nextToken())) {
			;
		}
		return v.toArray(new String[0]);
	}

	private byte[] readBytes(File file) throws IOException {
		return FileTools.getBytesFromFile(file);
	}

	private Double parseDouble(String string) {
		if (string == null) {
			return null;
		}
		return Double.parseDouble(string);
	}

	static class SilentException extends Exception {

	}
}