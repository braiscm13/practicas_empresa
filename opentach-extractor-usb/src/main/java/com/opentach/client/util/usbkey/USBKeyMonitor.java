package com.opentach.client.util.usbkey;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.FileTools;
import com.opentach.client.util.usbkey.USBEvent.USBEventType;
import com.utilmize.tools.usbdrivemonitor.USBDeviceMonitor;
import com.utilmize.tools.usbdrivemonitor.USBStorageDevice;
import com.utilmize.tools.usbdrivemonitor.events.DeviceEventType;
import com.utilmize.tools.usbdrivemonitor.events.IUSBDriveListener;
import com.utilmize.tools.usbdrivemonitor.events.USBStorageEvent;

public final class USBKeyMonitor {

	private static final Logger logger = LoggerFactory.getLogger(USBKeyMonitor.class);

	private volatile boolean downloading;
	private volatile boolean autodownload;
	private volatile boolean enabled;
	private File repository;
	protected USBDeviceMonitor usbMonitor;

	private final EventListenerList elListeners;
	private final USBInfoProvider infoProv;

	public USBKeyMonitor(USBInfoProvider infoProv) {
		this.elListeners = new EventListenerList();
		this.infoProv = infoProv;
		this.downloading = false;
		this.autodownload = false;
		this.enabled = false;
		this.usbMonitor = new USBDeviceMonitor(1000l);
		this.usbMonitor.addDriveListener(new IUSBDriveListener() {

			@Override
			public void usbDriveEvent(USBStorageEvent event) {
				if (DeviceEventType.CONNECTED.equals(event.getEventType()) && USBKeyMonitor.this.enabled
						&& USBKeyMonitor.this.autodownload) {
					USBKeyMonitor.this.downloadUSBKeyFiles(null,
							Arrays.asList(new USBStorageDevice[] { event.getStorageDevice() }));
				}
			}
		});
	}

	public void setRepositoryDir(File dir) throws IllegalArgumentException {
		if ((dir != null) && !dir.isDirectory()) {
			throw new IllegalArgumentException("Repository argument must be a valid DIRECTORY");
		}
		this.repository = dir;
	}

	public void setAutoDownload(boolean autodownload) {
		this.autodownload = autodownload;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void downloadUSBKeyFiles(final File repDir) {
		Collection<USBStorageDevice> removableDevices = null;
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			removableDevices = this.getAllDevices();
		} else {
			removableDevices = this.usbMonitor.getRemovableDevices();
		}
		this.downloadUSBKeyFiles(repDir, removableDevices);
	}

	private Collection<USBStorageDevice> getAllDevices() {
		List<USBStorageDevice> usbs = new ArrayList<USBStorageDevice>();
		List<File> roots = Arrays.asList(File.listRoots());

		final FileSystemView fsView = FileSystemView.getFileSystemView();
		for (File file : roots) {
			String name = fsView.getSystemDisplayName(file);
			try {
				usbs.add(new USBStorageDevice(file.toPath(), name));
			} catch (Exception ex) {
				USBKeyMonitor.logger.error("{},{} error selecting", file.toPath(), name);
			}
		}
		return usbs;
	}

	private boolean isValidName(String name, String templateName) {
		templateName = templateName.trim();
		if ((templateName == null) || (templateName.length() == 0)) {
			return false;
		}
		try {
			name = name.toUpperCase().trim();
			templateName = templateName.toUpperCase();
			// barras de windows
			templateName = templateName.replaceAll(Matcher.quoteReplacement("\\"), "/");
			String root = templateName.replaceAll("\\*", "(.)+");
			if (!templateName.startsWith("(.)+")) {
				root = "(.)*" + root;
			}
			if (!templateName.endsWith("(.)+")) {
				root = root + "(.)*";
			}
			if (Pattern.matches(root, name)) {
				return true;
			}
		} catch (Exception e) {
			USBKeyMonitor.logger.error(null, e);
		}
		return false;
	}

	private void downloadUSBKeyFiles(final File repDir, final Collection<USBStorageDevice> removableDevices) {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				USBKeyMonitor.this.downloadFiles(repDir, removableDevices);
			}
		};
		Thread thr = new Thread(runnable, this.getClass().getName());
		thr.start();
	}

	private void downloadFiles(final File pRepDir, Collection<USBStorageDevice> removableDevices) {

		synchronized (this) {
			if (this.downloading) {
				return;
			}
			this.downloading = true;
		}

		try {
			Collection<Path> usbRoots = this.getUsbRoots(removableDevices);
			if (usbRoots.isEmpty()) {
				USBKeyMonitor.logger.error("Ningun dispositivo detectado como llave");
				USBEvent event = new USBEvent(this, USBEventType.USB_NOT_FOUND);
				this.notifyUSBStatusChange(event);
				return;
			}
			USBEvent us = new USBEvent(this, USBEventType.USB_DOWNLOAD_START);
			this.notifyUSBStatusChange(us);

			File repDir = pRepDir == null ? this.repository : pRepDir;
			if (!repDir.exists()) {
				if (!repDir.mkdirs()) {
					USBKeyMonitor.logger.error("Error creating repository path {}", repDir);
					USBEvent ue = new USBEvent(this, USBEventType.USB_DOWNLOAD_ERROR, USBEvent.M_ERROR_INTERNAL);
					this.notifyUSBStatusChange(ue);
					return;
				}
			}
			File[] fList = this.listFiles(usbRoots);
			if (fList.length == 0) {
				USBKeyMonitor.logger.info("Llave detectada pero no tiene ficheros");
				USBEvent ue = new USBEvent(this, USBEventType.USB_DOWNLOAD_ERROR, USBEvent.M_KEY_EMPTY);
				this.notifyUSBStatusChange(ue);
				return;
			}
			Set<USBFileInfo> sCopiedFiles = new HashSet<>();
			USBFileInfo tinfo;
			for (File usbFile : fList) {
				try {
					File fDest = new File(repDir, usbFile.getName());
					FileTools.copyFile(usbFile, fDest);
					tinfo = new USBFileInfo(usbFile, fDest);
					sCopiedFiles.add(tinfo);
				} catch (Exception e) {
					USBKeyMonitor.logger.error(null, e);
					USBEvent ue = new USBEvent(this, USBEventType.USB_DOWNLOAD_ERROR, USBEvent.M_ERROR_COPYING_DATA);
					this.notifyUSBStatusChange(ue);
					break;
				}
			}
			// FIXME: arreglar este evento
			USBEvent ue = new USBEvent(this, USBEventType.USB_DOWNLOAD_END, null,
					new ArrayList<USBFileInfo>(sCopiedFiles));
			this.notifyUSBStatusChange(ue);
		} catch (Exception ex) {
			USBEvent ue = new USBEvent(this, USBEventType.USB_DOWNLOAD_ERROR, USBEvent.M_ERROR_INTERNAL);
			this.notifyUSBStatusChange(ue);
		} finally {
			this.downloading = false;
		}
	}

	private File[] listFiles(Collection<Path> fDirSet) throws IOException {
		Set<File> sFiles = new HashSet<File>();
		for (Path f1 : fDirSet) {
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(f1)) {
				for (Path path : directoryStream) {
					if (!Files.isDirectory(path)){
						sFiles.add(path.toFile());
					}
				}
			}
		}
		return sFiles.toArray(new File[0]);
	}

	private Collection<Path> getUsbRoots(Collection<USBStorageDevice> removableDevices) throws IOException {
		Collection<Path> usbRoots = new HashSet<>();
		if (removableDevices == null) {
			return usbRoots;
		}

		List<USBInfo> usbInfos = this.infoProv.getUSBInfo();
		for (USBStorageDevice device : removableDevices) {
			for (USBInfo info : usbInfos) {

				USBKeyMonitor.logger.info("Dispositivo [{}], probando con [{}] ...", device.getDeviceName(),
						info.getDeviceName());
				if (this.isValidName(device.getDeviceName(), info.getDeviceName())) {
					USBKeyMonitor.logger.info("Llave [{}] detectada", device.getDeviceName());

					Path rootDirectory = device.getRootDirectory();

					boolean exists = false;
					try {
						exists = Files.exists(rootDirectory.resolve(info.getPath()));
					} catch (InvalidPathException err) {
						// do nothing
					}
					if (exists) {
						USBKeyMonitor.logger.info("Llave [{}] detectada , path [{}] detectado.", device.getDeviceName(),
								info.getPath());
						usbRoots.add(rootDirectory.resolve(info.getPath()));

					} else {
						USBKeyMonitor.logger.info("Llave [{}] detectada , path [{}] NO detectado.",
								device.getDeviceName(), info.getPath());
						USBKeyMonitor.logger.info("Llave [{}] detectada , path [{}], probando patrón {}",
								device.getDeviceName(), info.getPath(), info.getPath());
						Set<Path> fDir = this.listDirsRecursive(device.getRootDirectory());
						usbRoots.addAll(this.filterDirs(device.getRootDirectory(), fDir, info.getPath()));
					}
				}
			}
		}
		return usbRoots;
	}

	private final Set<Path> listDirsRecursive(Path froot) throws IOException {
		HashSet<Path> fileNames = new HashSet<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(froot, new DirFileFilter())) {
			for (Path path : directoryStream) {
				fileNames.add(path);
				fileNames.addAll(this.listDirsRecursive(path));
			}
		}
		return fileNames;
	}

	private String removeRootName(Path fRoot, Path subPath) {
		String subdirName = subPath.toString();
		String name = subdirName.replace(fRoot.toString(), "");
		return name;
	}

	private Set<Path> filterDirs(Path fRoot, Set<Path> sDirs, String pattern) {
		String p = pattern.toUpperCase();
		if (p.startsWith("/")) {
			p = p.substring(1);
		}
		p = p.replaceAll(Matcher.quoteReplacement("\\"), "/");
		String root = p.replaceAll("\\*", "(.)+");
		if (!p.startsWith("(.)+")) {
			root = "(.)*" + root;
		}
		if (!p.endsWith("(.)+")) {
			root = root + "(.)*";
		}
		String sPattern = root;
		Set<Path> sReturn = new HashSet<>();
		for (Path path : sDirs) {
			String s = this.removeRootName(fRoot, path);
			s = (s.replace('\\', '/') + "/").toUpperCase();
			if (Pattern.matches(sPattern, s)) {
				sReturn.add(path);
			}
		}
		return sReturn;
	}

	public void addUSBListener(USBListener ic) {
		this.elListeners.add(USBListener.class, ic);
	}

	protected void notifyUSBStatusChange(final USBEvent ce) {
		USBListener[] elArray = this.elListeners.getListeners(USBListener.class);
		for (USBListener el : elArray) {
			try {
				el.usbStatusChange(ce);
			} catch (Exception e) {
				USBKeyMonitor.logger.error(null, e);
			}
		}
	}

	private static class DirFileFilter implements Filter<Path> {

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry);
		}
	}

}
