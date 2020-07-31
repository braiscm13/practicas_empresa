package com.opentach.client.util.directorywatcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.MapTools;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.local.LocalWatcherSettings;

/**
 * The Class AbstractWatchFolderInfo.
 */
public class AbstractWatcherSettings implements IWatcherSettings {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractWatcherSettings.class);

	/** The company id. */
	private String				companyId;

	/** The mode. */
	private WatchFolderMode		mode;

	/**
	 * Instantiates a new abstract watch folder info.
	 */
	public AbstractWatcherSettings() {
		super();
	}

	public AbstractWatcherSettings(String companyId, WatchFolderMode mode) {
		super();
		this.setCompanyId(companyId);
		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.client.util.directorywatcher.IWatchFolderInfo#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 * Sets the company id.
	 *
	 * @param companyId
	 *            the new company id
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.client.util.directorywatcher.IWatchFolderInfo#getMode()
	 */
	@Override
	public WatchFolderMode getMode() {
		return this.mode;
	}

	public static IWatcherSettings fromPreference(String preference) {
		if (preference != null) {
			if (preference.indexOf('=') < 0) {
				// compatibilidad hacia atras
				return new LocalWatcherSettings(preference);
			}
			Properties prop = new Properties();
			try {
				prop.load(new ByteArrayInputStream(preference.getBytes()));
			} catch (IOException e) {
				AbstractWatcherSettings.logger.error(null, e);
			}
			switch (WatchFolderMode.fromString(prop.getProperty("mode"), WatchFolderMode.LOCAL)) {
				case LOCAL:
					return AbstractWatcherSettings.parseLocal(prop);
				case FTP:
					return AbstractWatcherSettings.parseFTP(prop);
			}
		}
		return new LocalWatcherSettings(null);
	}

	private static IWatcherSettings parseFTP(Properties prop) {
		return new FtpWatcherSettings(prop.getProperty("ftpServer"), prop.getProperty("ftpPass"), prop.getProperty("ftpUser"), prop.getProperty("ftpFolder"),
				FtpWatcherSettings.parseFtpConnectionType(prop.getProperty("ftpConnectionType")));
	}

	private static IWatcherSettings parseLocal(Properties prop) {
		return new LocalWatcherSettings(prop.getProperty("localFolder"));
	}

	public static String toPreference(IWatcherSettings info) {
		Properties prop = new Properties();
		MapTools.safePut(prop, "mode", info.getMode().toString());
		if (info instanceof LocalWatcherSettings) {
			MapTools.safePut(prop, "localFolder", ((LocalWatcherSettings) info).getLocalFolder());
		} else if (info instanceof FtpWatcherSettings) {
			MapTools.safePut(prop, "ftpServer", ((FtpWatcherSettings) info).getFtpServer());
			MapTools.safePut(prop, "ftpPass", ((FtpWatcherSettings) info).getFtpPass());
			MapTools.safePut(prop, "ftpUser", ((FtpWatcherSettings) info).getFtpUser());
			MapTools.safePut(prop, "ftpFolder", ((FtpWatcherSettings) info).getFtpFolder());
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			prop.store(baos, "");
		} catch (IOException e) {
			AbstractWatcherSettings.logger.error(null, e);
		}
		return new String(baos.toByteArray());
	}
}
