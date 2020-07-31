package com.opentach.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.CheckMenuItem;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MenuListener;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.UtilReferenceLocator;
import com.ontimize.plaf.utils.StyleUtil;
import com.opentach.client.comp.JMenuAppMode;
import com.opentach.client.comp.LicenseAgreementDialog;
import com.opentach.client.modules.news.NewsDialog;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.UserTools;
import com.opentach.client.util.password.PasswordChanger;
import com.opentach.common.user.IUserData;
import com.utilmize.client.UClientApplication;

public class OpentachClientApplication extends UClientApplication {

	private static final Logger	logger				= LoggerFactory.getLogger(OpentachClientApplication.class);
	// JNLP parameters
	private static final String	WEBDOCPATH			= "webDocPath";
	private static final String	LOCALE				= "locale";
	private static final String	TACHOREDURL			= "tachoredurl";

	private static final String	OPENTACH_TITLE		= "OPENTACH_TITLE";
	private static final String	APPDATE				= "APPDATE";

	public static Color			colorUserLabel		= Color.decode("#FFFFFF");
	public static Color			colorAppVersion		= Color.decode("#fe9292");
	public static Color			colorPhoneNumber	= Color.decode("#99ff00");

	// private String appVersion;
	private String				appDate;
	private String				tachoredUrl;

	private String				webDocPath;
	private String				sStatusBarText		= null;
	protected boolean			undecorate			= false;
	private Locale				paramLocale;

	public OpentachClientApplication(Hashtable p) throws Exception {
		super(p);
		this.loadClientConfig();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void init(Hashtable params) {
		this.paramLocale = OpentachClientApplication.getLocaleParameter();
		this.webDocPath = OpentachClientApplication.getWebDocPathParameter();
		this.tachoredUrl = OpentachClientApplication.getTachoRedUrl();

		Object splash = this.setSplash(params);
		if (splash != null) {
			params.put("splash", splash);
		}

		super.init(params);
		StatusBar statusBar = this.getStatusBar();
		try {
			JLabel label = (JLabel) ReflectionTools.getFieldValue(statusBar, "statusText");
			label.setBorder(BorderFactory.createLineBorder(new Color(0x00000055, true), 1));
			JPanel panel = (JPanel) ReflectionTools.getFieldValue(statusBar, "iconPanel");
			panel.setBorder(BorderFactory.createLineBorder(new Color(0x00000055, true), 1));
		} catch (Exception ex) {
			OpentachClientApplication.logger.error("no se pudo cambiar el borde de la status bar");
		}
		// UApplicationMenuBar menuBar = (UApplicationMenuBar) ApplicationManager.getApplication().getMenu();
		// UMenu menuPreferences = (UMenu) ((UApplicationMenuBar) this.menu).getMenuItem("PREFERENCIAS");

	}

	public boolean isIconNoConvert(String iconName) {
		String noconverticons = StyleUtil.getProperty("Constants", "noconverticons", "");
		if (!noconverticons.isEmpty()) {
			StringTokenizer tokens = new StringTokenizer(noconverticons, " ");
			while (tokens.hasMoreTokens()) {
				String iconConf = tokens.nextToken();
				if (iconName.contains(iconConf)) {
					return true;
				}
			}
		}
		return false;
	}

	private void changeProgressBarPosition() {
		JProgressBar progressBar = (JProgressBar) this.getStatusBar().getComponent(0);
		this.getStatusBar().remove(progressBar);
		progressBar = new JProgressBar();
		ReflectionTools.setFieldValue(this.getStatusBar(), "progressBar", progressBar);
		progressBar.setStringPainted(false);
		progressBar.setPreferredSize(new Dimension(1, 1));
		progressBar.setMinimumSize(new Dimension(1, 1));
		progressBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		progressBar.setBorderPainted(false);
		progressBar.setUI(new BasicProgressBarUI());
		this.panelAuxCardLayout.remove(this.getStatusBar());
		JPanel panelAux = new JPanel(new BorderLayout(0, 0));
		panelAux.add(this.getStatusBar(), BorderLayout.CENTER);
		panelAux.add(progressBar, BorderLayout.NORTH);
		this.panelAuxCardLayout.add(panelAux, BorderLayout.SOUTH);
	}

	@Override
	public void setToolBar(JToolBar applicationToolBar) {
		super.setToolBar(applicationToolBar);
		this.changeProgressBarPosition();
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

	private void loadClientConfig() {
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream("com/opentach/client/conf/OpentachClientConf.properties");
			prop.load(is);
			this.appDate = prop.getProperty(OpentachClientApplication.APPDATE);
		} catch (Exception e) {
			OpentachClientApplication.logger.error(null, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ee) {
				}
			}
		}
	}

	public String getAppDate() {
		return this.appDate;
	}

	public String getTachoredUrl() {
		return this.tachoredUrl;
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
				((OpentachClientLocator) this.locator).setLocale(locale);
				IUserData du = ((OpentachClientLocator) this.locator).getUserData();
				if (du != null) {
					this.setUserInfo(du);
				}
			} catch (Exception e) {
				OpentachClientApplication.logger.error(null, e);
			}
		}
	}

	public String getWebDocPath() {
		return this.webDocPath;
	}

	@Override
	public void show() {
		if (this.loggedIn) {
			if (LicenseAgreementDialog.needToShow()) {
				try {
					if (UserTools.isEmpresa()) {
						IUserData userData = ((UserInfoProvider) this.getReferenceLocator()).getUserData();
						new LicenseAgreementDialog(this, userData.getContractCompany()).setVisible(true);
					}
				} catch (Exception error) {
					OpentachClientApplication.logger.error("E_SHOWING_AGREEMENT", error);
				}
			}

			try {
				if (((UtilReferenceLocator) this.getReferenceLocator()).supportChangePassword(this.getUser(), this.getReferenceLocator().getSessionId())) {
					MessageManager.getMessageManager().showMessage(this, "M_MUST_CHANGE_PASSWORD", MessageType.INFORMATION, new Object[] {});
					PasswordChanger.doChangePassword(false, this);
				}
			} catch (Exception error) {
				OpentachClientApplication.logger.error("Error to obtain the reference to LoginEntity", error);
			}

			super.show();

			try {
				NewsDialog newsDialog = new NewsDialog(ApplicationManager.getApplication().getFrame());
				newsDialog.showNews(false);
			} catch (Exception ex) {
				OpentachClientApplication.logger.error(null, ex);
			}
		} else {
			super.show();
			this.initializeChecksMenu();
		}
	}

	private void initializeChecksMenu() {
		boolean bStartMinimized = false;
		String sStartMinimized = this.getPreferences().getPreference(((OpentachClientLocator) this.locator).getUser(), JMenuAppMode.P_MINIMIZED_START);
		if ((sStartMinimized == null) || (sStartMinimized.length() == 0)) {
			bStartMinimized = Boolean.valueOf(sStartMinimized).booleanValue();
			this.selectMinimizedMenu(bStartMinimized);
		}
		if (bStartMinimized) {
			this.sendToTray();
		}
	}

	@Override
	public void setMenu(JMenuBar jMenuBar) {
		super.setMenu(jMenuBar);
		this.initializeChecksMenu();
	}

	@Override
	public void setMenuListener(MenuListener l) {
		super.setMenuListener(l);
		JMenuAppMode.loadAppModePreference();
	}

	public void setUserInfo(IUserData du) {
		String hexColorUseLabel = String.format("#%02X%02X%02X", OpentachClientApplication.colorUserLabel.getRed(), OpentachClientApplication.colorUserLabel.getGreen(),
				OpentachClientApplication.colorUserLabel.getBlue());
		String hexColorAppVersion = String.format("#%02X%02X%02X", OpentachClientApplication.colorAppVersion.getRed(), OpentachClientApplication.colorAppVersion.getGreen(),
				OpentachClientApplication.colorAppVersion.getBlue());

		String version = this.getVersion();
		StringBuffer buffer = new StringBuffer("<HTML><font color=\"").append(hexColorUseLabel).append("\">").append(ApplicationManager.getTranslation("Usuario")).append(":");
		buffer.append(" <B> ");
		buffer.append(du.getLogin());
		buffer.append(" </B>  ");
		buffer.append(ApplicationManager.getTranslation("Nivel")).append(":<B>  ");
		buffer.append(du.getLevelDscr());
		buffer.append("</B>");
		buffer.append("&nbsp &nbsp &nbsp &nbsp &nbsp &nbsp ").append(ApplicationManager.getTranslation("versionC")).append(": ").append(" </font><B>[ <FONT COLOR=\"")
		.append(hexColorAppVersion).append("\"> ").append(version).append(" </FONT> ]</B>");
		buffer.append("</HTML>");
		this.sStatusBarText = buffer.toString();
		this.setStatusBarText(this.sStatusBarText);
	}

	private void selectMinimizedMenu(boolean bStartMinimized) {
		if (this.menu != null) {
			int count = this.menu.getMenuCount();
			for (int i = 0; i < count; i++) {
				JMenu jm = this.menu.getMenu(i);
				int icount = jm.getItemCount();
				for (int j = 0; j < icount; j++) {
					if (jm.getItem(j) instanceof CheckMenuItem) {
						CheckMenuItem itm = (CheckMenuItem) jm.getItem(j);
						if ("ArrancarMinimizado".equals(itm.getAttribute())) {
							itm.setSelected(bStartMinimized);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	protected void decorateStatusBarVersion() {
		// version is decorate above
	}

	@Override
	public void setStatusBarText(String txt) {
		super.setStatusBarText(this.sStatusBarText);
	}

	@Override
	public void setTitle(String s) {
		boolean isAplicacion = ApplicationManager.getApplication() != null;
		String title = null;
		if (isAplicacion) {
			title = ApplicationManager.getTranslation(OpentachClientApplication.OPENTACH_TITLE);
		} else {
			title = " OPENTACH: Plataforma servicios tacógrafo";
		}
		super.setTitle(title);
	}

	private static final String getWebDocPathParameter() {
		return System.getProperty(OpentachClientApplication.WEBDOCPATH);
	}

	private static final String getTachoRedUrl() {
		return System.getProperty(OpentachClientApplication.TACHOREDURL);
	}

	private static final Locale getLocaleParameter() {
		Locale locale = null;
		String sLocale = System.getProperty(OpentachClientApplication.LOCALE);
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
				OpentachClientApplication.logger.error(null, e);
			}
		}
		return locale;
	}
}
