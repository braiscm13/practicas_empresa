package com.opentach.client.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.Tab;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.UTab;
import com.utilmize.client.gui.UTabPanel;

public class TabVisibleUtil {

	private TabVisibleUtil() {
		super();
	}

	/**
	 * Sets the tabs visibles.
	 *
	 * @param form
	 *            the form
	 * @param visible
	 *            set visible or not
	 * @param elementsAttrs
	 *            the tabs attributes
	 */
	public static void setTabVisible(Form form, boolean visible, String... elementsAttrs) {
		for (String elementAttr : elementsAttrs) {
			TabVisibleUtil.setTabVisible(form, visible, elementAttr);
		}
	}

	/**
	 * Sets the tab visible.
	 *
	 * @param visible
	 *            Set visible or not.
	 * @param elementAttr
	 *            The tab attribute.
	 */
	public static void setTabVisible(Form form, boolean visible, String elementAttr) {
		UTab tab = (UTab) form.getElementReference(elementAttr);
		if (tab == null) {
			return;
		}

		UTabPanel tabPanel = TabVisibleUtil.getParentTabPanel(form, tab);
		if (tabPanel == null) {
			return;
		}
		TabVisibleUtil.setTabVisible(form, tabPanel, tab, visible);

		boolean someChildVisible = false;
		for (Component tabChild : tabPanel.getComponents()) {
			if ((tabChild instanceof Tab) && ((Tab) tabChild).isVisible()) {
				someChildVisible = true;
			}
		}
		tabPanel.setVisible(someChildVisible);
		Tab tabParent = (Tab) SwingUtilities.getAncestorOfClass(Tab.class, tabPanel);
		if ((tabParent != null) && someChildVisible) {
			TabVisibleUtil.setTabVisible(form, someChildVisible, (String) tabParent.getAttribute());
		}
	}

	protected static void setTabVisible(Form form, UTabPanel tabPanel, UTab tab, boolean visible) {
		if ((tabPanel == null) || (tab == null)) {
			return;
		}
		String title = ObjectTools.coalesce(tab.getTitleKey(), tab.getAttribute().toString());
		if (visible) {
			tabPanel.showTab(ApplicationManager.getTranslation(title, form.getResourceBundle()));
		} else {
			tabPanel.hideTabs(ApplicationManager.getTranslation(title, form.getResourceBundle()));
		}
	}

	public static UTabPanel getParentTabPanel(Form form, UTab tab) {
		if (tab == null) {
			return null;
		}
		UTabPanel parentTabPanel = (UTabPanel) SwingUtilities.getAncestorOfClass(UTabPanel.class, tab);
		if (parentTabPanel != null) {
			return parentTabPanel;
		}
		// Complex search (of already hidden tab)
		List<UTabPanel> tabPanels = FIMUtils.getComponentsOfType(form, UTabPanel.class);
		UTabPanel tabPanelFound = TabVisibleUtil.searchTabPanel(tabPanels, tab);
		if (tabPanelFound != null) {
			return tabPanelFound;
		}

		// Last chance: Iterate around Form.components list (because childs under hidden tabs does not exist)
		tabPanels = new ArrayList<UTabPanel>();
		for (Object comp : form.getComponentList()) {
			if (comp instanceof UTabPanel) {
				tabPanels.add((UTabPanel) comp);
			}
		}
		tabPanelFound = TabVisibleUtil.searchTabPanel(tabPanels, tab);
		return tabPanelFound;
	}

	private static UTabPanel searchTabPanel(List<UTabPanel> tabPanels, UTab tab) {
		for (UTabPanel tabpanel : tabPanels) {
			List<?> innerTabs = (List<?>) ReflectionTools.getFieldValue(tabpanel, "tabs");
			if (innerTabs == null) {
				continue;
			}
			for (Object innerTab : innerTabs) {
				if (((Tab) innerTab).getConstraints(null).equals(tab.getConstraints(null))) {
					return tabpanel;
				}
			}
		}
		return null;
	}
}
