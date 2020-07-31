package com.opentach.adminclient.modules.reports;

import java.awt.Window;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataLabel;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.FullInfractionReportStatus;

public class IMInformeFullInfractions extends OpentachBasicInteractionManager {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeFullInfractions.class);

	@FormComponent(attr = "ire.status")
	private DataLabel			labelStatus;
	@FormComponent(attr = "ire.numdrivers")
	private DataLabel			labelNumDrivers;
	@FormComponent(attr = "ire.numdprocessed")
	private DataLabel			labelNumProcessed;

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setQueryMode();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.startStatusCheckThread();
	}

	private void startStatusCheckThread() {
		Thread thread = new Thread(new StatusCheckTask(), "Maintentance status thread");
		thread.setDaemon(true);
		thread.start();
	}

	public class StatusCheckTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				OpentachClientLocator ocl = (OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
				try {
					Form form = IMInformeFullInfractions.this.managedForm;
					Window window = SwingUtilities.getWindowAncestor(form);
					if ((window != null) && window.isVisible()) {
						FullInfractionReportStatus status = ocl.getRemoteService(IInfractionService.class).getFullInfractionReportStatus(ocl.getSessionId());
						this.updateInfo(status);
					}
				} catch (Exception error) {
					IMInformeFullInfractions.logger.error(null, error);
				} finally {
					try {
						Thread.sleep(500);
					} catch (InterruptedException error) {
						IMInformeFullInfractions.logger.error(null, error);
					}
				}
			}
		}

		private void updateInfo(FullInfractionReportStatus status) {
			IMInformeFullInfractions.this.labelStatus.setValue(status.isRunning() ? "Ejecutando:                  si" : "Ejecutando:                   no");
			IMInformeFullInfractions.this.labelNumDrivers.setValue("Conductores a analizar:  " + status.getTotalCount());
			IMInformeFullInfractions.this.labelNumProcessed.setValue("Conductores analizados: " + status.getProcessed());
		}
	}
}
