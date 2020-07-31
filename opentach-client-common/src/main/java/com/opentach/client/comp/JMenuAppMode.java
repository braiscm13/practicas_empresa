package com.opentach.client.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationMenuBar;
import com.ontimize.gui.Form;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.locator.ReferenceLocator;

/**
 * JMenu with all appMode options (Launchped, Navigator, combined)
 */
public class JMenuAppMode extends JMenu {

	private static final Logger	logger	= LoggerFactory.getLogger(JMenuAppMode.APP_MODE.class);

	public static enum APP_MODE {
		LAUNCHPAD, NAVIGATOR, COMBINED
	}

	public static final String	P_MINIMIZED_START				= "P_MINIMIZED_START";
	public static final String	P_LAUNCH_PAD					= "P_LAUNCH_PAD";

	private static final String	APP_MODE_MENU_ICON				= "com/opentach/client/rsc/window_star24.png";
	private static final String	APP_MODE_MENU_ICON_LAUNCHPAD	= "com/opentach/client/rsc/window_launchpad24.png";
	private static final String	APP_MODE_MENU_ICON_NAVIGATOR	= "com/opentach/client/rsc/window_navigator24.png";
	private static final String	APP_MODE_MENU_ICON_COMBINED		= "com/opentach/client/rsc/window_navLaunchpad24.png";
	private static String		managerName						= "manageracercade2";
	private static String		formName						= "formAcercaDe.xml";

	protected JMenuItem			appModeNavigator;
	protected JMenuItem			appModeLaunchpad;
	protected JMenuItem			appModeCombined;

	public JMenuAppMode() {
		super(ApplicationManager.getTranslation("AppMode", ApplicationManager.getApplicationBundle()));
		this.setIcon(ImageManager.getIcon(JMenuAppMode.APP_MODE_MENU_ICON));

		this.appModeLaunchpad = new JMenuItem(ApplicationManager.getTranslation("AppMode.LaunchPad", ApplicationManager.getApplicationBundle()));
		this.appModeLaunchpad.setIcon(ImageManager.getIcon(JMenuAppMode.APP_MODE_MENU_ICON_LAUNCHPAD));
		this.appModeLaunchpad.addActionListener(new AppModeActionListener(APP_MODE.LAUNCHPAD));
		this.add(this.appModeLaunchpad);

		this.appModeNavigator = new JMenuItem(ApplicationManager.getTranslation("AppMode.Navigator", ApplicationManager.getApplicationBundle()));
		this.appModeNavigator.setIcon(ImageManager.getIcon(JMenuAppMode.APP_MODE_MENU_ICON_NAVIGATOR));
		this.appModeNavigator.addActionListener(new AppModeActionListener(APP_MODE.NAVIGATOR));
		this.add(this.appModeNavigator);

		this.appModeCombined = new JMenuItem(ApplicationManager.getTranslation("AppMode.NavLaunchpad", ApplicationManager.getApplicationBundle()));
		this.appModeCombined.setIcon(ImageManager.getIcon(JMenuAppMode.APP_MODE_MENU_ICON_COMBINED));
		this.appModeCombined.addActionListener(new AppModeActionListener(APP_MODE.COMBINED));
		this.add(this.appModeCombined);
	}

	public void addaptPopupMenu() {
		APP_MODE mode = this.getCurrentAppMode();
		this.appModeNavigator.setEnabled((mode != null) && (mode != APP_MODE.NAVIGATOR));
		this.appModeLaunchpad.setEnabled((mode == null) || (mode != APP_MODE.LAUNCHPAD));
		this.appModeCombined.setEnabled((mode == null) || (mode != APP_MODE.COMBINED));
	}

	protected APP_MODE getCurrentAppMode() {
		ReferenceLocator bref = (ReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
		final String user = bref.getUser();

		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		String launchPreference = prefs.getPreference(user, JMenuAppMode.P_LAUNCH_PAD);

		if ((null == launchPreference) || "false".equals(launchPreference) || APP_MODE.NAVIGATOR.toString().equals(launchPreference)) {
			return APP_MODE.NAVIGATOR;
		} else if ("true".equals(launchPreference) || APP_MODE.LAUNCHPAD.toString().equals(launchPreference)) {
			return APP_MODE.LAUNCHPAD;
		} else {
			return APP_MODE.COMBINED;
		}
	}

	/**
	 * Configure App mode(between launchpad, navigator or combined).
	 *
	 * @param launchPad
	 */
	public static void setAppModePreference(APP_MODE mode) {
		Application app = ApplicationManager.getApplication();

		// Save preference
		ReferenceLocator brefs = (ReferenceLocator) app.getReferenceLocator();
		String user = brefs.getUser();
		JMenuAppMode.logger.info("Setting AppMode Start Preference to " + mode.toString());
		ApplicationManager.getApplication().getPreferences().setPreference(user, JMenuAppMode.P_LAUNCH_PAD, mode.toString());

		// Addapt menu items
		ApplicationMenuBar menu = (ApplicationMenuBar) app.getMenu();
		JMenuItem item = menu.getMenuItem("AppMode.LaunchPad");
		if (item == null) {
			return;
		}
		item.setSelected(mode == APP_MODE.LAUNCHPAD);
		JMenuItem item2 = menu.getMenuItem("AppMode.Navigator");
		item2.setSelected(mode == APP_MODE.NAVIGATOR);
		JMenuItem item3 = menu.getMenuItem("AppMode.NavLaunchpad");
		item3.setSelected(mode == APP_MODE.COMBINED);

		// Addapt main form
		IFormManager gfMenu = app.getFormManager(JMenuAppMode.managerName);
		if (gfMenu != null) {
			Form f = gfMenu.getFormReference(JMenuAppMode.formName);
			if (f != null) {
				((IAppModeConfiguration) f.getInteractionManager()).configureAppMode(mode);
			}
		}
	}

	public static APP_MODE getCurrentMode() {
		ReferenceLocator brefs = (ReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
		String user = brefs.getUser();
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		String launchPreference = prefs.getPreference(user, JMenuAppMode.P_LAUNCH_PAD);
		APP_MODE mode = null;
		if ((launchPreference == null) || "false".equals(launchPreference) || APP_MODE.NAVIGATOR.toString().equals(launchPreference)) {
			mode = APP_MODE.NAVIGATOR;
		} else if ("true".equals(launchPreference) || APP_MODE.LAUNCHPAD.toString().equals(launchPreference)) {
			mode = APP_MODE.LAUNCHPAD;
		} else {
			mode = APP_MODE.COMBINED;
		}
		return mode;
	}

	public static void loadAppModePreference() {
		ReferenceLocator bref = (ReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
		final String user = bref.getUser();

		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		String launchPreference = prefs.getPreference(user, JMenuAppMode.P_LAUNCH_PAD);

		if ((null == launchPreference) || "false".equals(launchPreference) || APP_MODE.NAVIGATOR.toString().equals(launchPreference)) {
			JMenuAppMode.setAppModePreference(APP_MODE.NAVIGATOR);
		} else if ("true".equals(launchPreference) || APP_MODE.LAUNCHPAD.toString().equals(launchPreference)) {
			JMenuAppMode.setAppModePreference(APP_MODE.LAUNCHPAD);
		} else {
			JMenuAppMode.setAppModePreference(APP_MODE.COMBINED);
		}
	}

	public static void setMinimizedStartPreference(boolean minimized) {
		ReferenceLocator brefs = (ReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
		String user = brefs.getUser();
		JMenuAppMode.logger.info("Setting Minimized Start Preference to " + minimized);
		ApplicationManager.getApplication().getPreferences().setPreference(user, JMenuAppMode.P_MINIMIZED_START, Boolean.toString(minimized));
	}

	public static interface IAppModeConfiguration {
		void configureAppMode(APP_MODE mode);
	}

	public static class AppModeActionListener implements ActionListener {
		protected APP_MODE	appMode;

		public AppModeActionListener(APP_MODE appMode) {
			this.appMode = appMode;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuAppMode.setAppModePreference(this.appMode);
		}
	}
}
