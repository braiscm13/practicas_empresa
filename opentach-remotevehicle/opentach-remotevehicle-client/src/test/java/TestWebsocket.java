import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ClientEndpointConfig.Configurator;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.common.remotevehicle.provider.opentach.WebsocketSerializableCoder;

public class TestWebsocket {
	private final static Logger	logger	= LoggerFactory.getLogger(TestWebsocket.class);

	public TestWebsocket() {}

	public static void main(String[] args) {
		try {
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
			// URI uri = new
			// URI("ws://10.7.0.227:8080/seirco-base/services/remoteOperation");
			URI uri = new URI("ws://10.7.0.144:8080/opentach-war/websocket/remoteDownload");
			TestWebsocket.logger.info("Connecting to remote operation manager on " + uri);

			ClientManager client = ClientManager.createClient();

			Session session = client.connectToServer(new Endpoint() {

				@Override
				public void onOpen(Session session, EndpointConfig config) {
					session.addMessageHandler(new MessageHandler.Whole<Object>() {
						@Override
						public void onMessage(Object text) {
							TestWebsocket.logger.debug(text.toString());
						}
					});
					TestWebsocket.logger.debug("on open " + session);
					try {
						session.getBasicRemote().sendObject(Integer.valueOf(1));
					} catch (Exception e) {
						TestWebsocket.logger.error(null, e);
						try {
							session.close(new CloseReason(CloseCodes.PROTOCOL_ERROR, e.getMessage()));
						} catch (IOException e1) {
							TestWebsocket.logger.error(null, e1);
						}
					}
				}

				@Override
				public void onClose(Session session, CloseReason closeReason) {
					TestWebsocket.logger.debug("onClose " + session);
					super.onClose(session, closeReason);
				}

				@Override
				public void onError(Session session, Throwable thr) {
					TestWebsocket.logger.debug("onError " + session);
					super.onError(session, thr);
					thr.printStackTrace();
				}

			}, cec, uri);
			TestWebsocket.logger.debug("handler: " + session);
			Thread.sleep(3000000);
		} catch (Throwable e) {
			TestWebsocket.logger.error(null, e);
		}
	}
}
