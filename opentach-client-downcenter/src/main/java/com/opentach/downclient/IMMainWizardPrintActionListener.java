package com.opentach.downclient;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.printer.PrintEvent;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMMainWizardPrintActionListener extends AbstractActionListenerButton {
	private int	copies;


	public IMMainWizardPrintActionListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(null, formComponent, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.copies = ParseUtilsExtended.getInteger((String) params.get("copies"), 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final UserInfoProvider uinfo = (UserInfoProvider) this.getReferenceLocator();
		final String dscr = uinfo.getUserDescription();
		IMMainWizard im = this.getInteractionManager(IMMainWizard.class);
		final PrintEvent pe = new PrintEvent(this, this.copies, dscr, im.getFiles());
		((AnonymousDeviceController) im.getDeviceController()).printAction(pe);
	}

}
