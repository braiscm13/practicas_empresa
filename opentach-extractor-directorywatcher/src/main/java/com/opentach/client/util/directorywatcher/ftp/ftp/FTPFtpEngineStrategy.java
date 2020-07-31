package com.opentach.client.util.directorywatcher.ftp.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.directorywatcher.DirectoryWatcherException;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.ftp.IFtpEngineStrategy;
import com.opentach.client.util.directorywatcher.ftp.IFtpFile;

public class FTPFtpEngineStrategy implements IFtpEngineStrategy {

	private static final Logger	logger	= LoggerFactory.getLogger(FTPFtpEngineStrategy.class);
	private final FTPClient		ftp;

	public FTPFtpEngineStrategy(FTPClient ftp) {
		super();
		this.ftp = ftp;
	}

	@Override
	public void changeWorkingDirectory(String ftpFolder) throws IOException {
		this.ftp.changeWorkingDirectory(ftpFolder);

	}

	@Override
	public IFtpFile[] listDirectories() throws IOException {
		return Arrays.asList(this.ftp.listDirectories()).stream().map(f -> new FTPFtpFile(f)).collect(Collectors.toList()).toArray(new IFtpFile[] {});
	}

	@Override
	public boolean makeDirectory(String pathname) throws IOException {
		return this.ftp.makeDirectory(pathname);
	}

	@Override
	public void retrieveFile(String remote, OutputStream os) throws IOException {
		this.ftp.retrieveFile(remote, os);
	}

	@Override
	public boolean rename(String from, String to) throws IOException {
		return this.ftp.rename(from, to);
	}

	@Override
	public IFtpFile[] listFiles() throws IOException {
		return Arrays.asList(this.ftp.listFiles()).stream().map(f -> new FTPFtpFile(f)).collect(Collectors.toList()).toArray(new IFtpFile[] {});
	}

	@Override
	public void logout() {
		try {
			this.ftp.logout();
		} catch (IOException err) {
			FTPFtpEngineStrategy.logger.error(null, err);
		}

	}

	@Override
	public boolean isConnected() {
		return this.ftp.isConnected();
	}

	@Override
	public void disconnect() throws IOException {
		this.ftp.disconnect();
	}

	public static FTPFtpEngineStrategy connect(boolean secure, FtpWatcherSettings info) throws SocketException, UnknownHostException, IOException, DirectoryWatcherException {
		// si no funciona con secure=true --> System.setProperty("jdk.tls.useExtendedMasterSecret", "false");
		// https://stackoverflow.com/questions/32398754/how-to-connect-to-ftps-server-with-data-connection-using-same-tls-session
		FTPClient ftp = secure ? new FTPSClient() {
			@Override
			protected void _prepareDataSocket_(final Socket socket) throws IOException {
				// if(preferences.getBoolean("ftp.tls.session.requirereuse")) {
				if (true) {
					if (socket instanceof SSLSocket) {
						// Control socket is SSL
						final SSLSession session = ((SSLSocket) this._socket_).getSession();
						if (session.isValid()) {
							final SSLSessionContext context = session.getSessionContext();
							// context.setSessionCacheSize(preferences.getInteger("ftp.ssl.session.cache.size"));
							try {
								final Field sessionHostPortCache = context.getClass().getDeclaredField("sessionHostPortCache");
								sessionHostPortCache.setAccessible(true);
								final Object cache = sessionHostPortCache.get(context);
								final Method method = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
								method.setAccessible(true);
								method.invoke(cache, String.format("%s:%s", socket.getInetAddress().getHostName(), String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT),
										session);
								method.invoke(cache, String.format("%s:%s", socket.getInetAddress().getHostAddress(), String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT),
										session);
							} catch (NoSuchFieldException e) {
								// Not running in expected JRE
								FTPFtpEngineStrategy.logger.warn("No field sessionHostPortCache in SSLSessionContext", e);
							} catch (Exception e) {
								// Not running in expected JRE
								FTPFtpEngineStrategy.logger.warn(e.getMessage());
							}
						} else {
							FTPFtpEngineStrategy.logger.warn(String.format("SSL session %s for socket %s is not rejoinable", session, socket));
						}
					}
				}
			}
		} : new FTPClient();

		// Connect and logon to FTP Server
		int reply;
		String[] split = info.getFtpServer().split(":");
		String host = split[0];
		int port = 21;
		if (split.length > 1) {
			port = Integer.valueOf(split[1]);
		}
		ftp.connect(InetAddress.getByName(host), port);
		FTPFtpEngineStrategy.logger.info("Connected to {}.", info.getFtpServer());

		// After connection attempt, you should check the reply code to
		// verify success.
		reply = ftp.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			FTPFtpEngineStrategy.logger.error("FTP server refused connection.");
			throw new DirectoryWatcherException("FTP server refused connection.");
		}

		if (!ftp.login(info.getFtpUser(), info.getFtpPass())) {
			ftp.logout();
			FTPFtpEngineStrategy.logger.error("FTP server fails on logon: {}", info.getFtpUser());
			throw new DirectoryWatcherException("FTP server fails on logon: " + info.getFtpUser());
		}
		if (secure) {
			((FTPSClient) ftp).execPBSZ(0);
			((FTPSClient) ftp).execPROT("P");
		}

		FTPFtpEngineStrategy.logger.trace("Remote system is {}", ftp.getSystemType());

		ftp.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

		// Use passive mode as default because most of us are
		// behind firewalls these days.
		ftp.enterLocalPassiveMode();
		return new FTPFtpEngineStrategy(ftp);
	}
}
