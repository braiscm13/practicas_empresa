package com.opentach.client.modules.admin.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.UApplicationMenuBar;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.table.editor.UXMLButtonCellEditor;
import com.utilmize.client.gui.menu.UMenu;
import com.utilmize.tools.exception.UException;

/**
 * Escuchador para la tabla de productos. Mostrará un diálogo con las opciones disponibles para cada fila.
 *
 */
public class PreferenceActionSelectionListener extends AbstractActionListenerButton {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(PreferenceActionSelectionListener.class);
	protected String			menu	= null;

	public PreferenceActionSelectionListener(UXMLButtonCellEditor editor, Hashtable parameters) throws Exception {
		super(editor, editor, parameters);
	}

	public PreferenceActionSelectionListener(UButton ubutton, Hashtable parameters) throws Exception {
		super(ubutton, parameters);
		this.menu = (String) parameters.get("menu");
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			this.getListMenuPreferences();
		} catch (UException ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, PreferenceActionSelectionListener.LOGGER);
		}
	}

	private void getListMenuPreferences() throws UException {
		UApplicationMenuBar menuBar = (UApplicationMenuBar) ApplicationManager.getApplication().getMenu();
		UMenu menuPreferences = (UMenu) menuBar.getMenuItem(this.menu);
		if (menuPreferences == null) {
			throw new UException("E_NO_FOUND_MENU");
		}

		Integer widthPopupSize = ((Number) menuPreferences.getPopupMenu().getSize().getWidth()).intValue();
		if (widthPopupSize == 0) {
			widthPopupSize = ((Number) menuPreferences.getPopupMenu().getPreferredSize().getWidth()).intValue();
		}
		Integer heightPopupSize = ((Number) menuPreferences.getPopupMenu().getSize().getHeight()).intValue();
		if (heightPopupSize == 0) {
			heightPopupSize = ((Number) menuPreferences.getPopupMenu().getPreferredSize().getHeight()).intValue();
		}
		menuPreferences.getPopupMenu().show(this.getButton(), this.getButton().getWidth() - widthPopupSize, 0 - heightPopupSize);
	}

}
