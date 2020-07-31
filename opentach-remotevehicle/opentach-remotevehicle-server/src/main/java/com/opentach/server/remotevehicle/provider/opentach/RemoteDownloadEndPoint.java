package com.opentach.server.remotevehicle.provider.opentach;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ServerLauncherServlet;
import com.opentach.common.remotevehicle.provider.opentach.WebsocketSerializableCoder;
import com.opentach.common.remotevehicle.provider.opentach.messages.AbstractMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.ApduRequestMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.ApduResponseMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.CheckMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.OkMessage;
import com.opentach.common.remotevehicle.provider.opentach.messages.RegisterMessage;
import com.opentach.server.IOpentachServerLocator;

/**
 * The Class RemoteDownloadEndPoint.
 */
@ServerEndpoint(
		value = "/websocket/remoteDownload",
		encoders = { WebsocketSerializableCoder.class },
		decoders = { WebsocketSerializableCoder.class },
		configurator = RemoteDownloadConfigurator.class)
public class RemoteDownloadEndPoint {

	/** The Constant ILLEGAL_RESPONSE_RECEIVED. */
	private static final String	ERROR_ILLEGAL_RESPONSE_RECEIVED	= "ILLEGAL_RESPONSE_RECEIVED";

	/** The Constant NO_RESPONSE_RECEIVED. */
	private static final String	ERROR_NO_RESPONSE_RECEIVED		= "NO_RESPONSE_RECEIVED";

	/** The Constant logger. */
	private static final Logger	logger							= LoggerFactory.getLogger(RemoteDownloadEndPoint.class);

	/** The Constant TIME_OUT. */
	private static final long	TIME_OUT						= 30000;

	/** The http session. */
	private HttpSession			httpSession;

	/** The session. */
	private Session				session;

	/** The company id. */
	private String				companyId;

	/** The last received message. */
	private AbstractMessage		lastReceivedMessage;

	/** The atr. */
	private byte[]				atr;

	/**
	 * Open.
	 *
	 * @param session
	 *            the session
	 * @param config
	 *            the config
	 */
	@OnOpen
	public void open(Session session, EndpointConfig config) {
		this.session = session;
		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
	}

	/**
	 * On message.
	 *
	 * @param session
	 *            the session
	 * @param ob
	 *            the ob
	 */
	@OnMessage
	public void onMessage(Session session, Object ob) {
		try {
			if (ob instanceof RegisterMessage) {
				this.onRegister(session, (RegisterMessage) ob);
			} else {
				this.lastReceivedMessage = (AbstractMessage) ob;
				synchronized (session) {
					this.session.notify();
				}
			}
		} catch (Exception e) {
			RemoteDownloadEndPoint.logger.error(null, e);
			this.getRegister().unregister(this.companyId);
		}
	}

	/**
	 * On register.
	 *
	 * @param session
	 *            the session
	 * @param message
	 *            the message
	 * @throws IOException
	 *             the IO exception
	 * @throws EncodeException
	 *             the encode exception
	 */
	public void onRegister(Session session, RegisterMessage message) throws IOException, EncodeException {
		CompanyCardRegister register = this.getRegister();
		register.register(message.getCompanyCode(), this);
		this.companyId = message.getCompanyCode();
		this.atr = message.getAtr();
		session.getBasicRemote().sendObject(new OkMessage(message.getSessionId()));
	}

	/**
	 * Gets the register.
	 *
	 * @return the register
	 */
	private CompanyCardRegister getRegister() {
		IOpentachServerLocator locator = (IOpentachServerLocator) this.httpSession.getServletContext()
				.getAttribute(
				ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
		return locator.getService(OpentachRemoteDownloadService.class).getRegister();
	}

	/**
	 * Close.
	 *
	 * @throws IOException
	 *             the IO exception
	 */
	public void close() throws IOException {
		this.session.close();
	}

	/**
	 * Do check.
	 *
	 * @return true, if do check
	 * @throws Exception
	 *             the exception
	 */
	public Boolean doCheck() throws Exception {
		return new InvokeTemplate<Boolean>() {
			@Override
			Boolean invoke() throws IOException, EncodeException, InterruptedException {
				AbstractMessage res = RemoteDownloadEndPoint.this.sendMessage(new CheckMessage(0));
				return res instanceof OkMessage;
			}
		}.doit();
	}

	/**
	 * Do send apdu.
	 *
	 * @param apdu
	 *            the apdu
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	public byte[] doSendApdu(final byte[] apdu) throws Exception {
		return new InvokeTemplate<byte[]>() {
			@Override
			byte[] invoke() throws IOException, EncodeException, InterruptedException {
				AbstractMessage res = RemoteDownloadEndPoint.this.sendMessage(new ApduRequestMessage(0, apdu));
				if (res instanceof ApduResponseMessage) {
					return ((ApduResponseMessage) res).getApdu();
				}
				return null;
			}
		}.doit();
	}

	/**
	 * Send message.
	 *
	 * @param message
	 *            the check message
	 * @return the abstract message
	 * @throws IOException
	 *             the IO exception
	 * @throws EncodeException
	 *             the encode exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private AbstractMessage sendMessage(AbstractMessage message) throws IOException, EncodeException, InterruptedException {
		synchronized (this.session) {
			this.lastReceivedMessage = null;
			this.session.getBasicRemote().sendObject(message);
			this.session.wait(RemoteDownloadEndPoint.TIME_OUT);
			if (this.lastReceivedMessage != null) {
				return this.lastReceivedMessage;
			}
			throw new IOException(RemoteDownloadEndPoint.ERROR_NO_RESPONSE_RECEIVED);
		}
	}

	/**
	 * Gets the atr.
	 *
	 * @return the atr
	 */
	public byte[] getAtr() {
		return this.atr;
	}

	/**
	 * The Class InvokeTemplate.
	 *
	 * @param <T>
	 *            the generic type
	 */
	private abstract class InvokeTemplate<T> {

		/**
		 * Invoke.
		 *
		 * @return the t
		 * @throws Exception
		 *             the exception
		 */
		abstract T invoke() throws Exception;

		/**
		 * Doit.
		 *
		 * @return the t
		 * @throws Exception
		 *             the exception
		 */
		public T doit() throws Exception {
			try {
				return this.invoke();
			} catch (Exception error) {
				RemoteDownloadEndPoint.this.getRegister().unregister(RemoteDownloadEndPoint.this.companyId);
				throw error;
			}
		}
	}


}
