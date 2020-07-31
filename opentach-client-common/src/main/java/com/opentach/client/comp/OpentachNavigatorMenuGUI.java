package com.opentach.client.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import com.imlabs.client.laf.common.components.CustomNavigatorMenuGUI;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.NavigatorMenuGUI;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.user.IUserData;

public class OpentachNavigatorMenuGUI extends CustomNavigatorMenuGUI {

	/** The constant logger. */
	private static final Logger			logger		= Logger.getLogger(OpentachNavigatorMenuGUI.class.getName());

	private static final String			ICON_DEFAULTS	= "com/opentach/client/rsc/window_equalizer24.png";
	private static final String			ICON_SETUP		= "com/opentach/client/rsc/window_gear24.png";

	protected static final Dimension	minSize		= new Dimension(800, 324);
	protected static final String		IMAGENAME	= "imagename";
	protected static final String		IMAGEPATH	= "imagepath";

	static {
		try {
			MenuGroup.DEFAULT_SELECTIONCOLOR = new Color(0x4C, 0x4C, 0x4C);
			MenuGroup.DEFAULT_FGSELECTIONCOLOR = Color.white;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected JLabel					jlText;
	protected JLabel					jImage;
	protected String					imagename;
	protected String					imagepath;
	protected IUserData					ud;

	/** Popup AppMode options */
	protected JMenuAppMode	jMenuAppMode;
	protected boolean		menuAppMode;

	public OpentachNavigatorMenuGUI(Hashtable params) throws Exception {
		super(params);
		this.jlText = this.addWarnLabel();
		this.jImage = this.addImage();
		this.imagename = (String) params.get(OpentachNavigatorMenuGUI.IMAGENAME);
		this.imagepath = (String) params.get(OpentachNavigatorMenuGUI.IMAGEPATH);
		this.menuAppMode = ParseUtilsExtended.getBoolean((String) params.get("menuappmode"), true);
	}

	private JLabel addImage() {
		this.setLayout(new BorderLayout());
		JLabel lImage = new JLabel();
		lImage.setHorizontalAlignment(SwingConstants.CENTER);
		lImage.setBorder(BorderFactory.createEmptyBorder());
		this.add(lImage, BorderLayout.CENTER);
		return lImage;
	}

	private JLabel addWarnLabel() {
		this.setLayout(new BorderLayout());
		JLabel jlText = new JLabel();

		jlText.setHorizontalAlignment(SwingConstants.CENTER);
		jlText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		this.add(jlText, BorderLayout.SOUTH);
		return jlText;
	}

	public void setWarnText(String text) {
		if (this.jlText != null) {
			this.jlText.setText(text);
		}
	}

	public void setImage() {
		if (IUserData.NIVEL_BASIC.equals(this.ud.getLevel())) {
			if ((this.jImage != null) && (this.imagepath != null) && (this.imagename != null)) {
				String ipath = this.imagepath + super.getLocale() + "/" + this.imagename;
				ImageIcon ic = ImageManager.getIcon(ipath);
				if (ic != null) {
					this.jImage.setIcon(ic);
				}
			}
		}
	}

	@Override
	public void setParentForm(Form form) {
		super.setParentForm(form);
		UserInfoProvider locator = (UserInfoProvider) form.getFormManager().getReferenceLocator();
		try {
			this.ud = locator.getUserData();
			this.setImage();
		} catch (Exception e) {
			OpentachNavigatorMenuGUI.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	protected void showPopupMenu(MouseEvent e) {
		// super.showPopupMenu(e);
		if (this.popupmenu == null) {
			this.builPopupMenu();
		}

		this.addaptPopupMenu();

		super.showPopupMenu(e);
	}

	protected void builPopupMenu() {
		// Default (override from super) ----------------------------
		this.popupmenu = new JPopupMenu();
		JMenuItem defaultValueMenu = new JMenuItem(ApplicationManager.getTranslation(NavigatorMenuGUI.RESTORE_DEFAULTS, this.bundle));
		defaultValueMenu.setIcon(ImageManager.getIcon(OpentachNavigatorMenuGUI.ICON_DEFAULTS));
		defaultValueMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OpentachNavigatorMenuGUI.this.setDefaultValues();
			}
		});
		this.popupmenu.add(defaultValueMenu);

		JMenuItem visibleItemsMenu = new JMenuItem(ApplicationManager.getTranslation(NavigatorMenuGUI.VISIBLE_ITEMS, this.bundle));
		visibleItemsMenu.setIcon(ImageManager.getIcon(OpentachNavigatorMenuGUI.ICON_SETUP));
		visibleItemsMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ReflectionTools.invoke(OpentachNavigatorMenuGUI.this, "configureMenuGroupItems", new Object[0]);
			}
		});
		this.popupmenu.add(visibleItemsMenu);

		// New menu option "AppMode" ----------------------------
		this.builPopupMenuAppMode();
	}

	private void builPopupMenuAppMode() {
		if (this.menuAppMode) {
			this.jMenuAppMode = new JMenuAppMode();
			this.popupmenu.add(this.jMenuAppMode);
		}
	}

	protected void addaptPopupMenu() {
		if (this.jMenuAppMode != null) {
			this.jMenuAppMode.addaptPopupMenu();
		}
	}
}