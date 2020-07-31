package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.soap.SOAPException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaltestSecurityInterceptor extends AbstractSoapInterceptor {

	private static final Logger	logger	= LoggerFactory.getLogger(JaltestSecurityInterceptor.class);
	private final String		secretKey;
	private final String		apiKey;

	public JaltestSecurityInterceptor(String secretKey, String apiKey) {
		super(Phase.USER_PROTOCOL);
		this.secretKey = secretKey;
		this.apiKey = apiKey;
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		OutputStream out = message.getContent(OutputStream.class);
		final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(out);
		message.setContent(OutputStream.class, newOut);
		newOut.registerCallback(new JaltestSecurityCallback(message));
	}

	@Override
	public void handleFault(SoapMessage message) {
		// do nothing
	}

	public class JaltestSecurityCallback implements CachedOutputStreamCallback {
		private final Message message;

		public JaltestSecurityCallback(Message message) {
			this.message = message;
		}


		@Override
		public void onClose(CachedOutputStream cos) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(JaltestInvoker.FORMAT_DATETIME);
				String date = sdf.format(new Date());

				String method = "POST";
				String queryString = "";
				String url = String.valueOf(((URLConnection) this.message.get("http.connection")).getURL());
				String contentHash = this.getBodyHash(cos);
				String stringToSign = method + "\n" + url + "\n" + queryString + "\n" + contentHash + "\n" + date;

				String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, JaltestSecurityInterceptor.this.secretKey).hmacHex(stringToSign);

				Map<String, List> headers = (Map<String, List>) this.message.get(Message.PROTOCOL_HEADERS);
				headers.put("DateRequest", Collections.singletonList(date));
				headers.put("Authorization", Collections.singletonList("CWS " + JaltestSecurityInterceptor.this.apiKey + ":" + signature));
			} catch (Exception err) {
				JaltestSecurityInterceptor.logger.error(null, err);
				throw new Fault(err.getMessage(), (ResourceBundle) null);
			}
		}

		public String getBodyHash(CachedOutputStream cos) throws IOException, SOAPException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			cos.writeCacheTo(baos);
			byte[] bytesBody = baos.toByteArray();
			if (bytesBody.length > 0) {
				return Base64.encodeBase64String(DigestUtils.sha1(bytesBody));
			}
			return "";
		}

		@Override
		public void onFlush(CachedOutputStream os) {
			// do nothing
		}
	}
}
