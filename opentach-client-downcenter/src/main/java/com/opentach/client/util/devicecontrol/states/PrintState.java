package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.printer.PrintEvent;
import com.opentach.common.user.ICDOUserData;
import com.opentach.common.user.IUserData;
import com.opentach.downclient.AnonymousDeviceController;


public class PrintState extends AbstractDownCenterState {

	private static final Logger	logger	= LoggerFactory.getLogger(PrintState.class);
	public PrintState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent e) {
		PrintState.logger.info("execute ActionEvent in PrintState->gotoinit");
		return this.getState(StateFactoryType.INIT);
	}

	@Override
	public IState executePrintEvent(PrintEvent event) {
		PrintState.logger.info("Execute printEvent: {}", event);
		((AnonymousDeviceController) this.getController()).printTicket(event);
		String hasReports = "";
		try {
			IUserData ud = this.getController().getUserData();
			hasReports = ((ICDOUserData) ud).getExpressReport();// VU;TC;AMBOS
		} catch (Exception ex) {
			PrintState.logger.error(null, ex);
		}
		if (("TC".equals(hasReports) || "AMBOS".equals(hasReports)) && this.getController().isCardDownloading()) {
			return this.getState(StateFactoryType.REPORTGENERATING);
		} else if (("VU".equals(hasReports) || "AMBOS".equals(hasReports)) && this.getController().isKeyDownloading()) {
			return this.getState(StateFactoryType.REPORTGENERATING);
		} else {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			return this.getState(StateFactoryType.THANKS);
		}
	}

	@Override
	public void handle() {
		PrintState.logger.info("handle printstate");
		this.getController().print();
	}
}