package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.screenreport.ReportEvent;

public class GeneratingReportState extends AbstractDownCenterState {
	/** The logger. */
	private static final Logger	logger									= LoggerFactory.getLogger(GeneratingReportState.class);

	/** Time to wait for report build */
	protected static final int	GENERATINGREPORTMILLIS					= 60000;

	/** Time to wait for report build on second time, when timout received for the first one (now required with less info). */
	protected static final int	GENERATINGREPORTMILLIS_SECOND_ATTEMP	= 30000;

	/** Time showing report. */
	protected static final int	REPORTMILLIS							= 60000;

	protected boolean			secondAttemp;

	public GeneratingReportState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent e) {
		if (!this.secondAttemp) {
			GeneratingReportState.logger.info("Timeout in GeneratingReportState-> retry with non expensive tasks!!!!");
			// Consider to get less information, avoiding expensive tasks
			this.secondAttemp = true;
			this.getController().setTimerToValue(GeneratingReportState.GENERATINGREPORTMILLIS_SECOND_ATTEMP);
			this.getController().generateReport(true);
			return this;
		} else {
			GeneratingReportState.logger.info("Timeout in GeneratingReportState->goto Thanks");
			return this.getState(StateFactoryType.THANKS);
		}
	}

	@Override
	public IState executeReportEvent(ReportEvent event) {
		GeneratingReportState.logger.info("Execute reportEvent {}", event);
		Object reportInfo = event.getReportInfo();
		if (reportInfo != null) {
			this.getController().setTimerToValue(GeneratingReportState.REPORTMILLIS);
			this.getController().showReport(reportInfo, this.secondAttemp);
			ShowingReportState state = (ShowingReportState) this.getState(StateFactoryType.REPORTVIEWING);
			return state;
		}
		this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
		return this.getState(StateFactoryType.THANKS);
	}

	@Override
	public void handle() {
		GeneratingReportState.logger.info("handle reports");
		this.secondAttemp = false;
		this.getController().setTimerToValue(GeneratingReportState.GENERATINGREPORTMILLIS);
		this.getController().generateReport(false);
	}
}