package com.opentach.client.util.directorywatcher.ftp.sftp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.ftp.IFtpEngineStrategy;
import com.opentach.client.util.directorywatcher.ftp.IFtpFile;

public class SFTPFtpEngineStrategy implements IFtpEngineStrategy {

	private final ChannelSftp	channel;
	private final Session		session;

	public SFTPFtpEngineStrategy(ChannelSftp sftpChannel, Session session) {
		super();
		this.channel = sftpChannel;
		this.session = session;
	}

	@Override
	public void changeWorkingDirectory(String ftpFolder) throws IOException {
		try {
			this.channel.cd(ftpFolder);
		} catch (SftpException err) {
			throw new IOException(err.getMessage(), err);
		}
	}

	@Override
	public IFtpFile[] listDirectories() throws IOException {
		try {
			List<LsEntry> ls = this.channel.ls(".");
			return ls.stream().filter(f -> f.getAttrs().isDir() && !".".equals(f.getFilename()) && !"..".equals(f.getFilename())).map(f -> new SFTPFtpFile(f))
					.collect(Collectors.toList()).toArray(new IFtpFile[] {});
		} catch (SftpException err) {
			throw new IOException(err.getMessage(),err);
		}
	}

	@Override
	public boolean makeDirectory(String path) throws IOException {
		try {
			this.channel.mkdir(path);
			return true;
		} catch (SftpException err) {
			throw new IOException(err.getMessage(), err);
		}
	}

	@Override
	public void retrieveFile(String src, OutputStream os) throws IOException {
		try {
			this.channel.get(src, os);
		} catch (SftpException err) {
			throw new IOException(err.getMessage(), err);
		}

	}

	@Override
	public boolean rename(String from, String to) throws IOException {
		try {
			this.channel.rename(from, to);
		} catch (SftpException err) {
			throw new IOException(err.getMessage(), err);
		}
		return true;
	}

	@Override
	public IFtpFile[] listFiles() throws IOException {
		try {
			List<LsEntry> ls = this.channel.ls(".");
			return ls.stream().filter(f -> !f.getAttrs().isDir() && !".".equals(f.getFilename()) && !"..".equals(f.getFilename())).map(f -> new SFTPFtpFile(f))
					.collect(Collectors.toList()).toArray(new IFtpFile[] {});
		} catch (SftpException err) {
			throw new IOException(err.getMessage(), err);
		}
	}

	@Override
	public void logout() {
		this.session.disconnect();
	}

	@Override
	public boolean isConnected() {
		return this.session.isConnected();
	}

	@Override
	public void disconnect() throws IOException {
		this.channel.exit();
	}

	public static SFTPFtpEngineStrategy connect(FtpWatcherSettings info) throws JSchException {
		JSch jsch = new JSch();

		String[] split = info.getFtpServer().split(":");
		String host = split[0];
		int port = 22;
		if (split.length > 1) {
			port = Integer.valueOf(split[1]);
		}

		Session session = jsch.getSession(info.getFtpUser(), host, port);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.setPassword(info.getFtpPass());
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();

		return new SFTPFtpEngineStrategy((ChannelSftp) channel, session);
	}
}
