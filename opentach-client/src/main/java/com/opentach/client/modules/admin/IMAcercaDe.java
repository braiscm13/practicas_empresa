package com.opentach.client.modules.admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.JMenuAppMode;
import com.opentach.client.comp.JMenuAppMode.APP_MODE;
import com.opentach.client.comp.JMenuAppMode.IAppModeConfiguration;
import com.opentach.client.comp.OpentachNavigatorMenuGUI;
import com.opentach.client.util.LocalPreferencesManager;
import com.opentach.common.user.IUserData;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.UApplicationMenuBar;
import com.utilmize.client.gui.UCardPanel;
import com.utilmize.client.gui.launchpad.LaunchPad;
import com.utilmize.client.gui.menu.UMenu;

public class IMAcercaDe extends UBasicFIM implements IAppModeConfiguration {
	/** The logger. */
	private static final Logger			logger			= LoggerFactory.getLogger(IMAcercaDe.class);

	/** Default app dimension for Navigator mode */
	private static final Dimension		dimensionNav	= new Dimension(1250, 760);
	/** Default app dimension for Launchpad mode */
	private static final Dimension		dimensionPad	= new Dimension(610, 760);

	@FormComponent(attr = "menu")
	private OpentachNavigatorMenuGUI	onm;
	@FormComponent(attr = "bpref")
	private Button						bPref;
	@FormComponent(attr = "binfo")
	private Button						bInfo;

	@FormComponent(attr = "AppMode.cardPanel")
	private UCardPanel					cardPanelAppMode;
	@FormComponent(attr = "launchpad")
	private LaunchPad					launchPad;

	@FormComponent(attr = "launchpadHeader")
	private Container					launchpadHeader;
	@FormComponent(attr = "launchpadFooter")
	private Container					launchpadFooter;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		OpentachClientLocator ocl = (OpentachClientLocator) formManager.getReferenceLocator();
		try {
			IUserData ud = ocl.getUserData();
			if ((ud != null) && (this.onm != null)) {
				Date dMax = ud.getDMaxLogin();
				if (dMax != null) {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					String sMax = df.format(dMax);
					this.onm.setWarnText(ApplicationManager.getTranslation("M_DEMO_MENU_EXPIRED_WARN_TEXT", ApplicationManager.getApplicationBundle(), new Object[] { sMax }));
				}
			}
		} catch (Exception e) {
			IMAcercaDe.logger.error(null, e);
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.configureBackground();
		this.configureAppMode(JMenuAppMode.getCurrentMode());
		this.bPref.setEnabled(true);
		this.bInfo.setEnabled(true);
	}

	public void configureBackground() {
		LocalPreferencesManager prefManager = LocalPreferencesManager.getInstance();
		Image img = prefManager.getImageBackground();
		if (img != null) {
			this.cardPanelAppMode.setOpaque(true);
			this.cardPanelAppMode.setBackgroundImage(img);
			this.launchPad.setLaunchPadOpaque(false);
		} else {
			this.cardPanelAppMode.setOpaque(false);
			this.cardPanelAppMode.setBackgroundImage(null);
			this.launchPad.setLaunchPadOpaque(true);
		}
	}

	/**
	 * Configure main form of app according AppMode
	 */
	@Override
	public void configureAppMode(APP_MODE mode) {
		// Catch values according app mode --------------------------
		boolean toolbarMenuVisible = true;
		boolean resetInvoker = true;
		boolean launchpadHeaderFooter = false;
		String panelToShow = "AppMode.Navigator";
		Dimension dimension = IMAcercaDe.dimensionNav;

		OpentachClientLocator bref = (OpentachClientLocator) this.formManager.getReferenceLocator();
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		final String user = bref.getUser();
		switch (mode) {
			case NAVIGATOR:
				toolbarMenuVisible = true;
				resetInvoker = true;
				launchpadHeaderFooter = false;
				panelToShow = "AppMode.Navigator";
				dimension = IMAcercaDe.dimensionNav;
				break;
			case LAUNCHPAD:
				toolbarMenuVisible = false;
				resetInvoker = false;
				launchpadHeaderFooter = true;
				panelToShow = "AppMode.Launchpad";
				dimension = IMAcercaDe.dimensionPad;
				break;
			case COMBINED:
			default:
				// Combined mode
				toolbarMenuVisible = true;
				resetInvoker = true;
				launchpadHeaderFooter = false;
				panelToShow = "AppMode.Launchpad";
				dimension = IMAcercaDe.dimensionNav;
				break;
		}

		// Now configure accord values --------------------------
		this.cardPanelAppMode.show(panelToShow);
		this.launchpadHeader.setVisible(launchpadHeaderFooter);
		this.launchpadFooter.setVisible(launchpadHeaderFooter);
		ApplicationManager.getApplication().getToolBar().setVisible(toolbarMenuVisible);
		ApplicationManager.getApplication().getMenu().setVisible(toolbarMenuVisible);
		ApplicationManager.getApplication().getFrame().setSize(dimension);
		prefs.setPreference(user, MainApplication.APP_WINDOW_SIZE, ((Number) dimension.getWidth()).intValue() + ";" + ((Number) dimension.getHeight()).intValue());
		if (resetInvoker) {
			// Esto es necesario porque si no al volver al menú navegación, no se abre el popup
			this.restoreInvoker();
		}
	}

	private void restoreInvoker() {
		UApplicationMenuBar menu = (UApplicationMenuBar) ApplicationManager.getApplication().getMenu();
		Vector allItems = menu.getAllItems();
		for (int i = 0; i < allItems.size(); i++) {
			Object ele = allItems.get(i);
			if (ele instanceof UMenu) {
				((UMenu) ele).getPopupMenu().setInvoker((UMenu) ele);
			}
		}
	}

}
