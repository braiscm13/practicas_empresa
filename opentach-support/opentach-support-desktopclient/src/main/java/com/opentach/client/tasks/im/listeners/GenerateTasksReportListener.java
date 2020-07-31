package com.opentach.client.tasks.im.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.tasks.ITasksReport;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.gui.tasks.WorkerStatusInfo;
import com.utilmize.client.listeners.UForceQueryWithFiltersListener;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class GenerateTasksReportListener extends UForceQueryWithFiltersListener {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(GenerateTasksReportListener.class);

	public GenerateTasksReportListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Form form = this.getForm();
		new USwingWorker<JasperPrint, Void>() {

			private Hashtable<Object, Object> catchFilters() {
				Hashtable<Object, Object> filters = new Hashtable<>();
				MapTools.safePut(filters, ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, GenerateTasksReportListener.this.composeFilter());
				MapTools.safePut(filters, "OPENTACH_SYSTEM_DEFAULT_TIMEZONE", System.getProperty("OPENTACH_SYSTEM_DEFAULT_TIMEZONE"));
				return filters;
			}

			@Override
			protected JasperPrint doInBackground() throws Exception {
				this.fireStatusUpdate(new WorkerStatusInfo("Consultando...", null, null));
				Entity eTasks = GenerateTasksReportListener.this.getReferenceLocator().getEntityReference("ETasks");
				Hashtable<Object, Object> kv = this.catchFilters();
				JasperPrint generateReport = ((ITasksReport) eTasks).generateReport(kv, GenerateTasksReportListener.this.getReferenceLocator().getSessionId());
				this.fireStatusUpdate(new WorkerStatusInfo("Abriendo informe...", null, null));
				return generateReport;
			}

			@Override
			protected void done() {
				super.done();
				try {
					JasperPrint jp = this.uget();
					if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
						JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("Informe_de_incidencias"), form.getParentFrame(), jp);
						jv.setVisible(true);
					} else {
						form.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
					}
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, GenerateTasksReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());

	}

	@Override
	protected void launchSearch() {
		// Do nothing
	}
}
