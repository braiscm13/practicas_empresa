package com.imlabs.client.laf.common.components;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationMenuBar;
import com.ontimize.gui.field.NavigatorMenuGUI.MenuItemGUI;

/**
 * This class integrate Ontimize characteristics into the MenuItem.
 *
 * @author Imatia Innovation
 *
 */
public class CustomMenuItemGUI extends MenuItemGUI {

	private static final Logger logger = LoggerFactory.getLogger(CustomMenuItemGUI.class);

	/**
	 * Constructs a new MenuItemGUI specifying the identifier and the icon of the MenuItemGUI.
	 *
	 * @param manager
	 *            String with the identifier of the MenuItemGUI. That is, the manager associated to this MenuItem.
	 * @param icon
	 *            ImageIcon with the icon of the MenuItemGUI.
	 */
	public CustomMenuItemGUI(String manager, ImageIcon icon, String tooltip) {
		super(manager, icon, tooltip);
	}

	/**
	 * Call doClick MenuItem
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		JMenuItem menuItem = ((ApplicationMenuBar) ApplicationManager.getApplication().getMenu()).getMenuItem(actionCommand);
		menuItem.doClick();
	}

}