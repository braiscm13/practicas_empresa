package com.opentach.downclient;

import java.awt.Frame;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;
import com.opentach.model.scard.CardListener;
import com.utilmize.client.UClientApplication;

public class DownCenterClientApplication extends UClientApplication implements CardListener {

	private static final Logger	logger			= LoggerFactory.getLogger(DownCenterClientApplication.class);
	// JNLP parameters
	private static final String	LAFNAME			= "lafName";
	private static final String	WEBDOCPATH		= "webDocPath";
	private static final String	LOCALE			= "locale";
	private static final String	TACHOREDURL		= "tachoredurl";

	private static final String	OPENTACH_TITLE	= "OPENTACH_TITLE";

	private String				webDocPath;
	private Locale				paramLocale;

	public DownCenterClientApplication(Hashtable p) throws Exception {
		super(p);
		if ((System.getProperty("screen-width") == null) || (System.getProperty("screen-height") == null)) {
			this.setUndecorated(true);
		}
		ActionListener alExit = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DownCenterClientApplication.this.exit();
			}
		};
		KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK, false);
		this.getRootPane().registerKeyboardAction(alExit, keystroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		DownCenterClientApplication.logger.info("App Version:   " + this.getVersion());
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if ((System.getProperty("screen-width") != null) && (System.getProperty("screen-height") != null)) {
					DownCenterClientApplication.this.setExtendedState(DownCenterClientApplication.this.getExtendedState() & ~Frame.MAXIMIZED_BOTH);
					DownCenterClientApplication.this.setSize(Integer.parseInt(System.getProperty("screen-width", "1035")),
							Integer.parseInt(System.getProperty("screen-height", "800")));
					DownCenterClientApplication.this.setLocationRelativeTo(null);
				} else {
					DownCenterClientApplication.this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
					DownCenterClientApplication.this.setExtendedState(DownCenterClientApplication.this.getExtendedState() | Frame.MAXIMIZED_BOTH);
				}

			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void init(Hashtable params) {
		this.paramLocale = DownCenterClientApplication.getLocaleParameter();
		this.webDocPath = DownCenterClientApplication.getWebDocPathParameter();

		Object splash = this.setSplash(params);
		if (splash != null) {
			params.put("splash", splash);
		}
		super.init(params);
	}

	protected Object setSplash(Hashtable<String, Object> params) {
		String splash = (String) params.get("splash");
		if (splash != null) {
			if (this.paramLocale != null) {
				splash = splash.replace("%LOCALE%", this.paramLocale.toString());
			} else {
				String locale = (String) params.get("locale");
				if (locale != null) {
					splash = splash.replaceAll("%LOCALE%", locale);
				}
			}
			return splash;
		}
		return null;
	}

	@Override
	protected void initPreloginPreferences() {
		super.initPreloginPreferences();
		if (this.paramLocale != null) {
			this.setComponentLocale(this.paramLocale);
			this.setResourceBundle(ExtendedPropertiesBundle.getExtendedBundle(this.resourcesFileName, this.paramLocale));
		}
	}

	@Override
	protected void initStaticPreferences() {
		if (this.paramLocale != null) {
			this.preferences.setPreference(this.getUser(), MainApplication.APP_LOCALE, this.paramLocale.toString());
		}
		super.initStaticPreferences();
	}

	@Override
	public void setResourceBundle(ResourceBundle resources) {
		super.setResourceBundle(resources);
		Locale locale = resources.getLocale();
		Locale.setDefault(locale);
		if (this.locator != null) {
			try {
				((DownCenterClientLocator) this.locator).setLocale(locale);
			} catch (Exception e) {
				DownCenterClientApplication.logger.error(null, e);
			}
		}
	}

	public String getWebDocPath() {
		return this.webDocPath;
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		int wid = e.getID();
		if ((wid == WindowEvent.WINDOW_CLOSING)) {
			DownCenterClientApplication.logger.info("Ignorando cierre de aplicación");
		} else {
			super.processWindowEvent(e);
		}
	}

	@Override
	public void show() {
		if (this.loggedIn) {
			super.show();
		} else {
			super.show();
		}
	}

	@Override
	public void setTitle(String s) {
		boolean isAplicacion = ApplicationManager.getApplication() != null;
		String title = null;
		if (isAplicacion) {
			title = ApplicationManager.getTranslation(DownCenterClientApplication.OPENTACH_TITLE);
		} else {
			title = " OPENTACH: Plataforma servicios tacógrafo";
		}
		super.setTitle(title);
	}

	private static final String getWebDocPathParameter() {
		return System.getProperty(DownCenterClientApplication.WEBDOCPATH);
	}

	private static final Locale getLocaleParameter() {
		Locale locale = null;
		String sLocale = System.getProperty(DownCenterClientApplication.LOCALE);
		if (sLocale != null) {
			try {
				final StringTokenizer st = new StringTokenizer(sLocale, "_");
				String sCountry = null;
				String language = null;
				String variant = "";
				if (st.hasMoreTokens()) {
					language = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					sCountry = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					variant = st.nextToken();
				}
				locale = new Locale(language, sCountry, variant);
			} catch (Exception e) {
				DownCenterClientApplication.logger.error(null, e);
			}
		}
		return locale;
	}

	@Override
	public void cardStatusChange(CardEvent ce) {
		// Para despertar el ordenador en caso de que entrara el salvapantallas
		// en los terminales
		if (ce.getType() == CardEventType.CARD_INSERTED) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						Robot rob = new Robot();
						rob.keyPress(KeyEvent.VK_CONTROL);
						rob.keyRelease(KeyEvent.VK_CONTROL);
					} catch (Exception e) {
						DownCenterClientApplication.logger.error(null, e);
					}
					// if (DownCenterClientApplication.this.downloadFrame == null) {
					DownCenterClientApplication.this.showApplication();
					DownCenterClientApplication.this.toFront();
					// } else {
					// DownCenterClientApplication.this.downloadFrame.toFront();
					// }
				}
			});
		}
	}

	@Override
	protected void decorateStatusBar() {
	}
}
