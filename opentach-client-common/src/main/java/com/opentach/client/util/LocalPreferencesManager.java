package com.opentach.client.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.tools.FileTools;
import com.utilmize.tools.exception.UException;

public class LocalPreferencesManager {

	public static String								LOCAL_FOLDER_PATH						= ".opentach";
	public static String								DEFAULT_APP_BACKGROUND_IMAGE			= "com/opentach/client/rsc/AppBackground.png";
	private static final String							APP_BACKGROUND_MODE_PREFERENCE			= "APP_BACKGROUND_MODE";
	private static final String							APP_BACKGROUND_MODE_PREFERENCE_NONE		= "none";
	private static final String							APP_BACKGROUND_MODE_PREFERENCE_CUSTOM	= "custom";
	private static final String							APP_BACKGROUND_MODE_PREFERENCE_DEFAULT	= "default";

	public final static String							PROP_TGD_WATCH_FOLDER					= "TGD_WATCH_FOLDER";
	public final static String							PROP_AGREEMENT_READED					= "AGREEMENT_READ";

	private static final Logger							logger									= LoggerFactory.getLogger(LocalPreferencesManager.class);

	private static LocalPreferencesManager				instance;

	private Properties									prop;
	private Path										file;

	private final List<ILocalPreferenceChangeListener>	listeners;

	public static LocalPreferencesManager getInstance() {
		if (LocalPreferencesManager.instance == null) {
			LocalPreferencesManager.instance = new LocalPreferencesManager();
			try {
				LocalPreferencesManager.instance.load();
			} catch (IOException e) {
				LocalPreferencesManager.logger.error(null, e);
			}
		}
		return LocalPreferencesManager.instance;
	}

	protected LocalPreferencesManager() {
		super();
		this.listeners = new ArrayList<ILocalPreferenceChangeListener>();
	}

	private void load() throws IOException {
		String path = this.getPathAppdata();
		this.file = Paths.get(path, LocalPreferencesManager.LOCAL_FOLDER_PATH);
		if (!Files.exists(this.file)) {
			this.file = Files.createDirectories(this.file);
		}
		this.file = this.file.resolve("local.pref");
		this.prop = new Properties();
		try (InputStream fis = Files.newInputStream(this.file)) {
			this.prop.load(fis);
		} catch (Exception e) {
			LocalPreferencesManager.logger.error(null, e);
		}
	}

	private String getPathAppdata() {
		String path = System.getenv("APPDATA");
		if ((path == null) || ("".equals(path))) {
			path = System.getProperty("user.home");
		}
		return path;
	}

	public Image getImageBackground() {
		try {
			String appBackgroundMode = LocalPreferencesManager.getInstance().getPreference(LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE);
			if ((appBackgroundMode == null) || "default".equals(appBackgroundMode)) {
				return ImageManager.getIcon(LocalPreferencesManager.DEFAULT_APP_BACKGROUND_IMAGE).getImage();
			} else if ("none".equals(appBackgroundMode)) {
				return null;
			} else {
				Path file = this.getBackgroundImagePath(false);
				if ((file == null) || !Files.exists(file)) {
					return null;
				}
				BufferedImage img = null;
				try (InputStream fis = Files.newInputStream(file)) {
					img = ImageIO.read(fis);
					return img;
				}
			}
		} catch (Exception e) {
			LocalPreferencesManager.logger.error(null, e);
		}
		return null;
	}

	public void defaultImageBackground() throws UException, IOException {
		LocalPreferencesManager.getInstance().setPreference(LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE, LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE_DEFAULT);
	}

	public void setImageBackground(File imageFile) throws UException, IOException {
		if (!imageFile.exists()) {
			throw new UException("FILE_NOT_EXISTS");
		}
		Path file = this.getBackgroundImagePath(true);
		FileTools.copyFile(imageFile, file.toFile());
		LocalPreferencesManager.getInstance().setPreference(LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE, LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE_CUSTOM);
	}

	public void restoreImageBackground() throws UException {
		String path = this.getPathAppdata();
		Path file = Paths.get(path, LocalPreferencesManager.LOCAL_FOLDER_PATH);
		if (!Files.exists(file)) {
			return;
		}
		file = file.resolve("backgroundImage.jpg");
		if (!file.toFile().delete()) {
			// throw new UException("E_RESTORE_BACKGROUND");
		}
		LocalPreferencesManager.getInstance().setPreference(LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE, LocalPreferencesManager.APP_BACKGROUND_MODE_PREFERENCE_NONE);
	}

	public Path getBackgroundImagePath(boolean createPathWhenNotExists) throws IOException {
		String path = this.getPathAppdata();
		Path file = Paths.get(path, LocalPreferencesManager.LOCAL_FOLDER_PATH);
		if (!Files.exists(file)) {
			if (createPathWhenNotExists) {
				file = Files.createDirectories(file);
			} else {
				return null;
			}
		}
		file = file.resolve("backgroundImage.jpg");
		if (createPathWhenNotExists) {
			return file;
		} else if (!Files.exists(file)) {
			return null;
		}
		return file;
	}

	public String getPreference(String key) {
		return (String) this.prop.get(key);
	}

	public void setPreference(String key, String value) {
		String oldValue = (String) this.prop.put(key, value);
		this.firePropertyChangeListener(key, oldValue, value);
		this.save();
	}

	protected void firePropertyChangeListener(String key, String oldValue, String newValue) {
		for (ILocalPreferenceChangeListener listener : this.listeners) {
			listener.onPropertyChanged(key, oldValue, newValue);
		}
	}

	public void addPropertyChangeListener(ILocalPreferenceChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	private void save() {
		try (OutputStream out = Files.newOutputStream(this.file)) {
			this.prop.store(out, null);
		} catch (IOException error) {
			LocalPreferencesManager.logger.error(null, error);
		}
	}
}
