package com.opentach.client.modules.admin;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import com.imlabs.client.laf.common.UStyleUtil;
import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.TextComboDataField;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.laf.LafController;
import com.opentach.client.modules.admin.IMLafChooser.LafListElement;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UListDataField;

public class IMLafChooserSaveListener extends AbstractActionListenerButton {

	@FormComponent(attr = "lafList")
	private UListDataField		list;
	@FormComponent(attr = "fontSize")
	private TextComboDataField	fontSize;

	public IMLafChooserSaveListener() throws Exception {
		super();
	}

	public IMLafChooserSaveListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMLafChooserSaveListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMLafChooserSaveListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LafListElement selectedObject = (LafListElement) this.list.getSelectedObject();
		boolean showMessage = false;
		if (selectedObject != null) {
			ApplicationManager.getApplication().getPreferences().setPreference(
					((OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser(), LafController.PROPERTY_LAF_STYLE,
					selectedObject.getLafName());
			showMessage = true;
		}

		String vFontSize = (String) this.fontSize.getValue();
		if (vFontSize != null) {
			ApplicationManager.getApplication().getPreferences().setPreference(
					((OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser(), UStyleUtil.FONT_SIZE_PROPERTY,
					vFontSize);
			showMessage = true;
		}
		if (showMessage) {
			this.getForm().message("M_LAF_APPLY_NEXT_TIME", Form.INFORMATION_MESSAGE);
		}

		SwingUtilities.getAncestorOfClass(Window.class, this.getForm()).setVisible(false);

	}

}
