package com.opentach.client.comp;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.util.swing.EJFrame;
import com.utilmize.client.gui.UTabPanel;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.buttons.advanced.USearchPanel;
import com.utilmize.client.listeners.UShowFormListener;

public class ShowFormManagerButton extends UButton {

	public ShowFormManagerButton(Hashtable params) {
		super(params);
	}

	@Override
	public void init(Hashtable params) {
		super.init(this.completeParameters(params));
	}

	/**
	 * Complete parameters.
	 *
	 * @param params
	 *            the params
	 * @return the hashtable
	 */
	private Hashtable<?, ?> completeParameters(Hashtable<Object, Object> params) {
		MapTools.safePut(params, "key", params.get("attr"), true);
		MapTools.safePut(params, "text", params.get("attr"), true);
		MapTools.safePut(params, "title", params.get("attr"), true);
		MapTools.safePut(params, "align", "left", true);
		// MapTools.safePut(params, "fixedwidth", "200", true);
		MapTools.safePut(params, "fixedheight", "30", true);
		MapTools.safePut(params, "textalign", "left", true);
		MapTools.safePut(params, "iconalign", "left", true);
		MapTools.safePut(params, "dim", "text", true);
		MapTools.safePut(params, "name", "ShowFormManagerButton", true);
		MapTools.safePut(params, "listener", ShowFormManagerListener.class.getName(), true);
		return params;
	}

	public static class ShowFormManagerListener extends AbstractUpdateModeActionListenerButton {
		/** The CONSTANT logger */
		private static final Logger						logger			= LoggerFactory.getLogger(ShowFormManagerButton.class);

		protected String				title;
		protected String				formManagerName;
		protected String				formName;
		protected boolean				isDetail;
		protected boolean				clean;
		protected boolean				autoSearch;

		protected String							tabPanelAttr;
		protected String							tabAttr;

		/** Pass values to new form (equivalences) */
		protected Map<String, String>	hAttributesToPassEquivalences;

		/** Fixed pass values to new form (equivalences) */
		protected Map<String, String>	hAttributesToFixEquivalences;

		/** Attributes that must contain non-null values */
		protected String[]							requiredFilterFields;

		protected Map<String, Pair<Window, Form>>	instancesLoaded	= new HashMap<String, Pair<Window, Form>>();

		/**
		 * Instantiates a new listener.
		 *
		 * @param button
		 *            the button
		 * @param params
		 *            the params
		 * @throws Exception
		 *             the exception
		 */
		public ShowFormManagerListener(UButton button, Hashtable params) throws Exception {
			this(button, button, params);
		}

		public ShowFormManagerListener(AbstractButton button, IUFormComponent comp, Hashtable params) throws Exception {
			super(button, comp, ShowFormManagerListener.completeParams(params));
		}

		private static Hashtable completeParams(Hashtable params) {
			MapTools.safePut(params, AbstractActionListenerButton.PARAM_ENABLE_FIM_UPDATE, "yes", true);
			MapTools.safePut(params, AbstractActionListenerButton.PARAM_ENABLE_FIM_QUERY, "yes", true);
			MapTools.safePut(params, AbstractActionListenerButton.PARAM_ENABLE_FIM_QUERY_INSERT, "yes", true);
			MapTools.safePut(params, AbstractActionListenerButton.PARAM_ENABLE_FIM_INSERT, "yes", true);
			return params;
		}

		@Override
		protected void init(Map<?, ?> params) throws Exception {
			super.init(params);
			this.title = ParseUtilsExtended.getString((String) params.get("title"), null);
			this.formManagerName = ParseUtilsExtended.getString((String) params.get("formmanager"), null);
			this.formName = ParseUtilsExtended.getString((String) params.get("form"), null);
			this.isDetail = ParseUtilsExtended.getBoolean((String) params.get("detail"), false);
			this.clean = ParseUtilsExtended.getBoolean((String) params.get("clean"), false);
			this.autoSearch = ParseUtilsExtended.getBoolean((String) params.get("autosearch"), false);

			Object passattr = params.get("passattr");
			if (passattr != null) {
				this.hAttributesToPassEquivalences = UShowFormListener.getTokensAt((String) passattr, ";", ":");
			}
			Object fixattr = params.get("fixattr");
			if (fixattr != null) {
				this.hAttributesToFixEquivalences = UShowFormListener.getTokensAt((String) fixattr, ";", ":");
			}
			Object notnullattr = params.get("requiredfilterfields");
			if (notnullattr != null) {
				this.requiredFilterFields = ((String) notnullattr).split(";");
			}

			this.tabPanelAttr = ParseUtilsExtended.getString((String) params.get("tabpanel"), null);
			this.tabAttr = ParseUtilsExtended.getString((String) params.get("tab"), null);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// Check required fields:
			if (!this.checkRequiredFilterFields(this.requiredFilterFields, this.getForm())) {
				return;
			}
			// Create form + window container
			Pair<Window, Form> windowToShow = this.getWindowToShow();
			if (windowToShow == null) {
				return;
			}
			// Set values
			this.completeFormWithValues(windowToShow.getSecond());

			// Show window container
			this.showWindow(windowToShow.getFirst());
		}

		protected Pair<Window, Form> getWindowToShow() {
			Pair<Window, Form> toReturn = null;

			if (this.formManagerName == null) {
				ShowFormManagerListener.logger.warn("E_INVALID_FORM_MANAGER__{}", this.formManagerName);
				return null;
			}
			if (this.instancesLoaded.containsKey(this.formManagerName)) {
				toReturn = this.instancesLoaded.get(this.formManagerName);
			} else {
				IFormManager formManagerToUse = ApplicationManager.getApplication().getFormManager(this.formManagerName);
				if (formManagerToUse == null) {
					ShowFormManagerListener.logger.warn("E_INVALID_FORM_MANAGER__{}", this.formManagerName);
					return null;
				}
				formManagerToUse.load();
				if (formManagerToUse.showFrame()) {
					if ((this.formName != null) && !ObjectTools.safeIsEquals(this.formName, formManagerToUse.getActiveForm().getName())) {
						formManagerToUse.showForm(this.formName);
					}
					EJFrame frame = this.getFrameToShow(this.formManagerName, formManagerToUse);
					toReturn = new Pair(frame, this.lookForForm(frame));
				} else {
					Form detailForm = formManagerToUse.getFormCopy(this.formName);
					JDialog detailDialog = detailForm.putInModalDialog(this.title, this.getForm());
					toReturn = new Pair(detailDialog, detailForm);
				}
				this.instancesLoaded.put(this.formManagerName, toReturn);
			}

			// Ensure to re-title (several menu entries uses same formManager with different titles)
			if (toReturn.getFirst() instanceof JDialog) {
				((JDialog) toReturn.getFirst()).setTitle(ApplicationManager.getTranslation(this.title));
			} else if (toReturn.getFirst() instanceof JFrame) {
				((JFrame) toReturn.getFirst()).setTitle(ApplicationManager.getTranslation(this.title));
			}

			return toReturn;
		}

		protected void completeFormWithValues(Form targetForm) {
			ShowFormManagerListener.logger.warn("Setting values...");
			// Case 1: Detail -> Current record from fixed key
			if (this.isDetail) {
				ShowFormManagerListener.logger.warn("Setting values. Its a detail form, loading...");
				targetForm.getInteractionManager().setQueryInsertMode();

				if ((this.tabPanelAttr != null) && (this.tabAttr != null)) {
					// Try to locate this tab
					UTabPanel tabPanel = (UTabPanel) targetForm.getElementReference(this.tabPanelAttr);
					if (tabPanel == null) {
						ShowFormManagerListener.logger.warn("The tabpanel '{}' does not exists in this form", this.tabPanelAttr);
					} else {
						tabPanel.setSelectedTab(ApplicationManager.getTranslation(this.tabAttr));
					}
				}

				targetForm.updateDataFields(this.getDetailKeys(), 0);
				targetForm.getInteractionManager().setUpdateMode();
				return;
			}

			// Case 2: Simple case, only show FormManager and set values
			if (this.clean) {
				ShowFormManagerListener.logger.warn("Setting values. Cleaning.");
				AbstractButton cleanButton = this.lookForCleanButton(targetForm);
				if (cleanButton != null) {
					cleanButton.doClick();
				} else {
					targetForm.deleteDataFields();
				}
			}

			ShowFormManagerListener.logger.warn("Setting values. Setting all new values.");
			this.setValues(this.hAttributesToPassEquivalences, this.getForm(), targetForm, false);
			this.setValues(this.hAttributesToFixEquivalences, this.getForm(), targetForm, true);

			if (this.autoSearch) {
				ShowFormManagerListener.logger.warn("Setting values. AutoSearch.");
				AbstractButton searchButton = this.lookForSearchButton(targetForm);
				if (searchButton != null) {
					searchButton.doClick();
				}
			}
		}

		protected boolean checkRequiredFilterFields(String[] attributes, Form currentForm) {
			boolean valid = true;

			StringBuilder stringBuilder = new StringBuilder(ApplicationManager.getTranslation("W_REQUIRED_FILTER_FIELDS"));

			if (attributes != null) {
				for (String attribute : attributes) {
					DataComponent elementReference = (DataComponent) currentForm.getElementReference(attribute);
					if ((elementReference != null) && (elementReference.getValue() == null)) {
						// First missing attribute: add period. Else, add comma.
						stringBuilder.append(valid ? ": " : ", ");
						valid = false;
						stringBuilder.append(elementReference.getLabelComponentText());
					}
				}

				if (!valid) {
					currentForm.message(stringBuilder.toString(), JOptionPane.WARNING_MESSAGE);
				}
			}

			return valid;
		}

		protected void setValues(Map<String, String> mappings, Form currentForm, Form targetForm, boolean fix) {
			if (mappings != null) {
				for (Entry<String, String> entry : mappings.entrySet()) {
					this.setValue(entry.getKey(), currentForm.getDataFieldValue(entry.getValue()), targetForm, fix);
				}
			}
		}

		protected void setValue(String fieldAttr, Object fieldValueToSet, Form targetForm, boolean fix) {
			if (fieldAttr == null) {
				return;
			}

			// Special case with checkdatafields
			FormComponent targetField = targetForm.getElementReference(fieldAttr);
			if (targetField instanceof CheckDataField) {
				((CheckDataField) targetField).getDataField().setEnabled(fieldValueToSet != null);
				try {
					JCheckBoxMenuItem includeMenu = (JCheckBoxMenuItem) ReflectionTools.getFieldValue(targetField, "includeMenu");
					includeMenu.setSelected(fieldValueToSet != null);
				} catch (Exception err) {
					ShowFormManagerListener.logger.trace("W_CANNOT_SET_MENU_ITEM_INCLUDE", err);
				}
			}

			targetForm.setDataFieldValue(fieldAttr, fieldValueToSet);
			if (fix) {
				DataComponent comp = targetForm.getDataFieldReference(fieldAttr);
				if (comp != null) {
					comp.setModifiable(false);
				}
			}
		}

		protected Hashtable getDetailKeys() {
			Hashtable keys = new Hashtable();
			if (this.hAttributesToFixEquivalences != null) {
				for (Entry<String, String> entry : this.hAttributesToFixEquivalences.entrySet()) {
					Object dataFieldValue = this.getForm().getDataFieldValue(entry.getValue());
					MapTools.safePut(keys, entry.getKey(), new Vector(Arrays.asList(new Object[] { dataFieldValue })));
				}
			}
			return keys;
		}

		protected void showWindow(Window window) {
			if (window.isVisible()) {
				if ((window instanceof Frame) && (((Frame) window).getState() == Frame.ICONIFIED)) {
					int state = ((Frame) window).getExtendedState();
					int newState = state & Frame.MAXIMIZED_BOTH;
					((Frame) window).setExtendedState(newState);
				}
				window.toFront();
			} else {
				window.setVisible(true);
			}
		}

		protected EJFrame getFrameToShow(String idPanel, IFormManager formManager) {
			EJFrame frame = null;

			Hashtable<String, JFrame> formManagerFrames = (Hashtable<String, JFrame>) ReflectionTools.getFieldValue(ApplicationManager.getApplication(), "formManagerFrames");

			if (formManagerFrames.containsKey(idPanel)) {
				// Rescue current formManager
				frame = (EJFrame) formManagerFrames.get(idPanel);
				frame.setSizePositionPreference(this.getSizePreferenceKey(idPanel));
			} else {
				frame = new EJFrame(this.title);
				frame.setSizePositionPreference(this.getSizePreferenceKey(idPanel));

				Image currentImage = null;
				if (formManager.getIcon() != null) {
					ImageIcon icon = ImageManager.getIcon(formManager.getIcon());
					currentImage = icon.getImage();
				}
				if (currentImage == null) {
					currentImage = ApplicationManager.getApplication().getFrame().getIconImage();
				}
				frame.setIconImage(currentImage);

				Container cont = formManager.getContainer();
				if (cont != null) {
					frame.setContentPane(cont);
				} else {
					ShowFormManagerListener.logger.warn("The container of the form manager {} is null", idPanel);
					frame.setContentPane((Container) formManager);
				}
				frame.pack();

				formManagerFrames.put(idPanel, frame);
			}

			frame.setTitle(ApplicationManager.getTranslation(this.title, formManager.getResourceBundle()));
			return frame;
		}

		protected String getSizePreferenceKey(String idPanel) {
			StringBuilder builder = new StringBuilder();
			builder.append("frameFormManager");
			builder.append("_");
			builder.append(idPanel);
			return builder.toString();
		}

		protected AbstractButton lookForCleanButton(Form form) {
			// First try: SearchPanel component
			Button button = form.getButton("clean");
			if (button != null) {
				return button;
			}

			// Second try: SearchPanel component
			USearchPanel searchPanel = (USearchPanel) form.getElementReference("searchPanel");
			if ((searchPanel != null) && (searchPanel.getCleanFiltersButton() != null)) {
				return searchPanel.getCleanFiltersButton();
			}
			return null;
		}

		protected AbstractButton lookForSearchButton(Form form) {
			// First try: SearchPanel component
			Button button = form.getButton("search");
			if (button != null) {
				return button;
			}

			// Second try: SearchPanel component
			USearchPanel searchPanel = (USearchPanel) form.getElementReference("searchPanel");
			if ((searchPanel != null) && (searchPanel.getSearchButton() != null)) {
				return searchPanel.getSearchButton();
			}
			return null;
		}

		protected Form lookForForm(Container container) {
			if (container instanceof Form) {
				return (Form) container;
			}
			for (Component c : container.getComponents()) {
				if (c instanceof Container) {
					Form lookForForm = this.lookForForm((Container) c);
					if (lookForForm != null) {
						return lookForForm;
					}
				}
			}
			return null;
		}

	}

}
