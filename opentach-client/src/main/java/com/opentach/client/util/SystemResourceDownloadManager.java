package com.opentach.client.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.OpentachClientApplication;


public class SystemResourceDownloadManager {

	private static final Logger	logger	= LoggerFactory.getLogger(SystemResourceDownloadManager.class);

	public final static String	TACHO	= "tachored.pdf";
	public final static String	SOLRED	= "solred.pdf";
	public final static String	HELP	= "help.pdf";

	protected Map<Locale, File>	tachofiles;
	protected Map<Locale, File>	solredfiles;
	protected Map<Locale, File>	helpfiles;
	protected String			path;

	private static SystemResourceDownloadManager	instance;

	public static SystemResourceDownloadManager getInstance() {
		if (SystemResourceDownloadManager.instance == null) {
			String path = ((OpentachClientApplication) ApplicationManager.getApplication()).getWebDocPath();
			if (path != null) {
				SystemResourceDownloadManager.instance = new SystemResourceDownloadManager(path);
			}
		}
		return SystemResourceDownloadManager.instance;
	}

	private SystemResourceDownloadManager(String path) {
		this.path = path;
		this.tachofiles = new HashMap<Locale, File>();
		this.solredfiles = new HashMap<Locale, File>();
		this.helpfiles = new HashMap<Locale, File>();
	}

	public File getResource(String name, Locale l) {
		File f = null;
		if (SystemResourceDownloadManager.TACHO.equals(name)) {
			f = this.tachofiles.get(l);
		} else if (SystemResourceDownloadManager.SOLRED.equals(name)) {
			f = this.solredfiles.get(l);
		} else {
			f = this.helpfiles.get(l);
		}
		if (f == null) {
			InputStream is = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try {
				String urlpath = this.path + l + "/" + name;
				URL url = new URL(urlpath);
				URLConnection urlConn = url.openConnection();
				is = urlConn.getInputStream();
				f = File.createTempFile("Fichero", ".pdf");
				fos = new FileOutputStream(f);
				bos = new BufferedOutputStream(fos);
				int c;
				while ((c = is.read()) >= 0) {
					bos.write(c);
				}

			} catch (Exception e) {
				SystemResourceDownloadManager.logger.error(null, e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
				if (bos != null) {
					try {
						bos.close();
					} catch (Exception e) {
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
			}
			if (f != null) {
				f.deleteOnExit();
			}
			if (SystemResourceDownloadManager.TACHO.equals(name)) {
				this.tachofiles.put(l, f);
			} else if (SystemResourceDownloadManager.SOLRED.equals(name)) {
				this.solredfiles.put(l, f);
			} else {
				this.helpfiles.put(l, f);
			}
		}
		return f;
	}

}
