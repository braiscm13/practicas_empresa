package com.opentach.client.remotevehicle.provider.opentach;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.swing.JOptionPane;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ClientEndpointConfig.Configurator;
import javax.websocket.CloseReason;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.opentach.common.remotevehicle.provider.opentach.WebsocketSerializableCoder;
import com.opentach.common.remotevehicle.provider.opentach.messages.AbstractMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.ApduRequestMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.ApduResponseMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.CheckMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.KoMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.OkMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.RegisterMessage;

public class ClientEndPoint {

	private static final Logger							logger	= LoggerFactory.getLogger(ClientEndPoint.class);

	private final Session								session;
	private final RemoteDownloadAuthenticationSession	smartCardConnector;
	private final String								companyId;

	public ClientEndPoint(String companyId) throws DeploymentException, IOException, URISyntaxException {
		super();
		this.companyId = companyId;
		this.smartCardConnector = new RemoteDownloadAuthenticationSession();
		this.session = this.createEndPoint();
		this.doRegister();
	}

	private Session createEndPoint() throws DeploymentException, IOException, URISyntaxException {
		List<Class<? extends Decoder>> decoders = new ArrayList<Class<? extends Decoder>>();
		decoders.add(WebsocketSerializableCoder.class);
		List<Class<? extends Encoder>> encoders = new ArrayList<Class<? extends Encoder>>();
		encoders.add(WebsocketSerializableCoder.class);
		Configurator configurator = new Configurator() {
			@Override
			public void beforeRequest(Map<String, List<String>> headers) {
				// final String basicAuth = "Basic " +
				// CodingTools.encodeBase64("prueba" + ":" + "pass");
				// headers.put("Authorization", Arrays.asList(new String[] {
				// basicAuth }));
				super.beforeRequest(headers);
			}
		};
		ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().decoders(decoders).encoders(encoders).configurator(configurator).build();
		URI uri = this.getRmoteServiceUrl();

		ClientEndPoint.logger.info("Connecting to remote operation manager on " + uri);

		ClientManager client = ClientManager.createClient();

		return client.connectToServer(new Endpoint() {

			@Override
			public void onOpen(Session session, EndpointConfig config) {
				session.addMessageHandler(new MessageHandler.Whole<Object>() {
					@Override
					public void onMessage(Object ob) {
						ClientEndPoint.this.onMessage(ob);
					}
				});
				ClientEndPoint.logger.debug("on open " + session);
			}

			@Override
			public void onClose(Session session, CloseReason closeReason) {
				ClientEndPoint.logger.debug("onClose " + session);
				super.onClose(session, closeReason);
				JOptionPane.showMessageDialog(ApplicationManager.getApplication().getFrame(),
						ApplicationManager.getTranslation("REMOTE_DOWNLOAD_FINISHED"));
			}

			@Override
			public void onError(Session session, Throwable thr) {
				ClientEndPoint.logger.debug("onError " + session);
				super.onError(session, thr);
			}

		}, cec, uri);
	}

	private URI getRmoteServiceUrl() {
		String urlPathTemplate = "%s/websocket/remoteDownload";
		try {
			return new URI(String.format(urlPathTemplate, System.getProperty("remotedownload.base.url")));
		} catch (URISyntaxException e) {
			return null;
		}
	}

	private void doRegister() {
		this.sendMessage(new RegisterMessage(this.getSessionId(), this.companyId, this.smartCardConnector.getAtr()));
	}

	protected void onMessage(Object ob) {
		if (ob instanceof CheckMessage) {
			ClientEndPoint.logger.info("check message received: " + ob.getClass().getName());
			this.onCheckMessage(ob);
		} else if (ob instanceof ApduRequestMessage) {
			ClientEndPoint.logger.info("apdu request received: " + ob.getClass().getName());
			this.onApduRequestMessage((ApduRequestMessage) ob);
		} else if ((ob instanceof OkMessage) || (ob instanceof KoMessage)) {
			ClientEndPoint.logger.info("message received: " + ob.getClass().getName());
		} else {
			ClientEndPoint.logger.error("invalid object received: {}", ob.getClass().getName());
		}
	}

	private void onApduRequestMessage(ApduRequestMessage ob) {
		try {
			CommandAPDU apdu = new CommandAPDU(ob.getApdu());
			ResponseAPDU response = this.smartCardConnector.sendCommandAPDU(apdu);
			if (response.getBytes() != null) {
				ClientEndPoint.logger.info("Responsing with {} bytes apdu", response.getBytes().length);
			} else {
				ClientEndPoint.logger.error("Null response");
			}
			this.sendMessage(new ApduResponseMessage(this.getSessionId(), response.getBytes()));
		} catch (Exception e) {
			ClientEndPoint.logger.error(null, e);
			this.sendMessage(new KoMessage(this.getSessionId()));
		}
	}

	private void onCheckMessage(Object ob) {
		if (this.smartCardConnector.isCompanyCardInserted()) {
			try {
				this.smartCardConnector.begin();
			} catch (Exception e) {
				ClientEndPoint.logger.error(null, e);
			}
			this.sendMessage(new OkMessage(this.getSessionId()));
		} else {
			this.sendMessage(new KoMessage(this.getSessionId()));
		}

	}

	private void sendMessage(AbstractMessage message) {
		try {
			this.session.getBasicRemote().sendObject(message);
		} catch (Exception e) {
			ClientEndPoint.logger.error(null, e);
			this.unregister();
		}
	}

	private int getSessionId() {
		try {
			return ApplicationManager.getApplication().getReferenceLocator().getSessionId();
		} catch (Exception e) {
			ClientEndPoint.logger.error(null, e);
			return 0;
		}
	}

	public void unregister() {
		try {
			this.session.close();
			this.smartCardConnector.end();
		} catch (IOException e) {
			ClientEndPoint.logger.error(null, e);
		} catch (CardException e) {
			ClientEndPoint.logger.error(null, e);
		}
	}

}
